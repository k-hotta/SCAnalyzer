package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragmentMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is an implementation of {@link MiningStrategy} to get how many times a
 * clone class are modified during its evolution.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneGenealogyModificationStrategy<E extends IProgramElement>
		implements MiningStrategy<DBCloneGenealogy, CloneGenealogy<E>> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneGenealogyModificationStrategy.class);

	private final Set<Long> versionsUnderConsideration;

	private final String outputFilePath;

	/*
	 * for the following fields ... // <GenealogyID => <VersionID => number>>
	 */

	private final ConcurrentMap<Long, Map<Long, Integer>> codeFragments;

	private final ConcurrentMap<Long, Map<Long, Integer>> addedFragments;

	private final ConcurrentMap<Long, Map<Long, Integer>> deletedFragments;

	private final ConcurrentMap<Long, Map<Long, Integer>> changedFragments;

	private final ConcurrentMap<Long, Map<Long, Integer>> changePatterns;

	public CloneGenealogyModificationStrategy(final String outputFilePath) {
		this.versionsUnderConsideration = new ConcurrentSkipListSet<>();
		this.outputFilePath = outputFilePath;
		this.codeFragments = new ConcurrentSkipListMap<>();
		this.addedFragments = new ConcurrentSkipListMap<>();
		this.deletedFragments = new ConcurrentSkipListMap<>();
		this.changedFragments = new ConcurrentSkipListMap<>();
		this.changePatterns = new ConcurrentSkipListMap<>();
	}

	@Override
	public boolean requiresVolatileObjects() {
		return false;
	}

	@Override
	public void mine(Collection<CloneGenealogy<E>> genealogies)
			throws Exception {
		final ExecutorService pool = Executors.newCachedThreadPool();
//		final ExecutorService pool = Executors.newSingleThreadExecutor();

		try {
			final List<Future<?>> futures = new ArrayList<>();
			final AtomicInteger count = new AtomicInteger(0);
			final int numGenealogies = genealogies.size();

			for (final CloneGenealogy<E> genealogy : genealogies) {
				final MiningTask task = new MiningTask(genealogy.getCore(),
						count, numGenealogies);
				futures.add(pool.submit(task));
			}

			for (final Future<?> future : futures) {
				try {
					future.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			pool.shutdown();
		}
	}

	@Override
	public void writeResult() throws Exception {
		try (final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(outputFilePath))))) {
			pw.println(buildHeader());

			for (final long genealogyId : codeFragments.keySet()) {
				final Map<Long, Integer> codeFragmentsInGenealogy = codeFragments
						.get(genealogyId);
				final Map<Long, Integer> addedFragmentsInGenealogy = addedFragments
						.get(genealogyId);
				final Map<Long, Integer> deletedFragmentsInGenealogy = deletedFragments
						.get(genealogyId);
				final Map<Long, Integer> changedFragmentsInGenealogy = changedFragments
						.get(genealogyId);
				final Map<Long, Integer> changePatternsInGenealogy = changePatterns
						.get(genealogyId);

				pw.println(buildRows(genealogyId, "TOTAL",
						codeFragmentsInGenealogy));
				pw.println(buildRows(genealogyId, "A",
						addedFragmentsInGenealogy));
				pw.println(buildRows(genealogyId, "D",
						deletedFragmentsInGenealogy));
				pw.println(buildRows(genealogyId, "C",
						changedFragmentsInGenealogy));
				pw.println(buildRows(genealogyId, "Pattern",
						changePatternsInGenealogy));
			}
		}
	}

	private String buildHeader() {
		final StringBuilder builder = new StringBuilder();

		builder.append("GENEALOGY_ID,KIND,");
		for (final long versionId : versionsUnderConsideration) {
			builder.append("v." + versionId + ",");
		}
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	private String buildRows(final long genealogyId, final String kind,
			final Map<Long, Integer> values) {
		final StringBuilder builder = new StringBuilder();

		builder.append(genealogyId + "," + kind + ",");
		for (final long versionId : versionsUnderConsideration) {
			final Integer value = values.get(versionId);
			if (value == null) {
				builder.append("-1");
			} else {
				builder.append(value);
			}
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	private class MiningTask implements Runnable {

		private final DBCloneGenealogy genealogy;

		private final AtomicInteger count;

		private final int numGenealogies;

		private MiningTask(final DBCloneGenealogy genealogy,
				final AtomicInteger count, final int numGenealogies) {
			this.genealogy = genealogy;
			this.count = count;
			this.numGenealogies = numGenealogies;
		}

		@Override
		public void run() {
			final long genealogyId = genealogy.getId();

			final Collection<DBCloneClassMapping> cloneClassMappings = genealogy
					.getCloneClassMappings();

			final Map<Long, Integer> codeFragmentsInGenealogy = new TreeMap<>();
			final Map<Long, Integer> addedFragmentsInGenealogy = new TreeMap<>();
			final Map<Long, Integer> deletedFragmentsInGenealogy = new TreeMap<>();
			final Map<Long, Integer> changedFragmentsInGenealogy = new TreeMap<>();
			final Map<Long, Integer> changePatternsInGenealogy = new TreeMap<>();

			final Set<Long> consideredCloneClassIds = new TreeSet<>();

			for (final DBCloneClassMapping cloneClassMapping : cloneClassMappings) {
				final DBCloneClass newCloneClass = cloneClassMapping
						.getNewCloneClass();

				if (newCloneClass == null) {
					continue;
				}

				versionsUnderConsideration.add(newCloneClass.getVersion()
						.getId());

				final DBCloneClass oldCloneClass = cloneClassMapping
						.getOldCloneClass();

				calcNumFragments(codeFragmentsInGenealogy,
						consideredCloneClassIds, newCloneClass);
				analyzeModifications(cloneClassMapping, oldCloneClass,
						newCloneClass, addedFragmentsInGenealogy,
						deletedFragmentsInGenealogy,
						changedFragmentsInGenealogy, changePatternsInGenealogy);
			}

			codeFragments.put(genealogyId, codeFragmentsInGenealogy);
			addedFragments.put(genealogyId, addedFragmentsInGenealogy);
			deletedFragments.put(genealogyId, deletedFragmentsInGenealogy);
			changedFragments.put(genealogyId, changedFragmentsInGenealogy);
			changePatterns.put(genealogyId, changePatternsInGenealogy);

			logger.info("[" + count.incrementAndGet() + "/" + numGenealogies
					+ "] complete mining genealogy " + genealogy.getId());
		}

		private void calcNumFragments(
				final Map<Long, Integer> codeFragmentsInGenealogy,
				final Set<Long> consideredCloneClassIds,
				final DBCloneClass cloneClass) {
			if (cloneClass != null
					&& !consideredCloneClassIds.contains(cloneClass.getId())) {
				consideredCloneClassIds.add(cloneClass.getId());

				int numFragments = cloneClass.getCodeFragments().size();
				if (codeFragmentsInGenealogy.containsKey(cloneClass.getId())) {
					numFragments += codeFragmentsInGenealogy.get(cloneClass
							.getId());
				}
				codeFragmentsInGenealogy.put(cloneClass.getVersion().getId(),
						numFragments);
			}
		}

		private void analyzeModifications(
				final DBCloneClassMapping cloneClassMapping,
				final DBCloneClass oldCloneClass,
				final DBCloneClass newCloneClass,
				final Map<Long, Integer> addedFragmentsInGenealogy,
				final Map<Long, Integer> deletedFragmentsInGenealogy,
				final Map<Long, Integer> changedFragmentsInGenealogy,
				final Map<Long, Integer> changePatternsInGenealogy) {
			final int mapped = cloneClassMapping.getCodeFragmentMappings()
					.size();
			int added = newCloneClass.getCodeFragments().size() - mapped;
			int deleted = (oldCloneClass == null) ? 0 : oldCloneClass
					.getCodeFragments().size() - mapped;
			int changed = 0;
			int patterns = 0;

			for (final DBCodeFragmentMapping fragmentMapping : cloneClassMapping
					.getCodeFragmentMappings()) {
				if (fragmentMapping.getOldCodeFragment() == null) {
					added++;
				}

				else if (fragmentMapping.getNewCodeFragment() == null) {
					deleted++;
				}

				else {
					for (final DBCloneModification modification : fragmentMapping
							.getModifications()) {
						changed++;
						// TODO implement
					}
				}
			}

			final long versionId = newCloneClass.getVersion().getId();

			if (addedFragmentsInGenealogy.containsKey(versionId)) {
				added += addedFragmentsInGenealogy.get(versionId);
			}

			if (deletedFragmentsInGenealogy.containsKey(versionId)) {
				deleted += deletedFragmentsInGenealogy.get(versionId);
			}

			if (changedFragmentsInGenealogy.containsKey(versionId)) {
				changed += changedFragmentsInGenealogy.get(versionId);
			}

			addedFragmentsInGenealogy.put(versionId, added);
			deletedFragmentsInGenealogy.put(versionId, deleted);
			changedFragmentsInGenealogy.put(versionId, changed);
			changePatternsInGenealogy.put(versionId, patterns);
		}

	}

}
