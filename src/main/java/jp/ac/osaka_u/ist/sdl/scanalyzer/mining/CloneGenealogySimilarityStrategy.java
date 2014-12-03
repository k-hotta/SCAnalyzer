package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is an implementation of {@link MiningStrategy} to get how similar a
 * clone class is during its evolution.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneGenealogySimilarityStrategy<E extends IProgramElement>
		implements MiningStrategy<DBCloneGenealogy, CloneGenealogy<E>> {

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneGenealogySimilarityStrategy.class);

	private final Set<Long> versionsUnderConsideration;

	private final ConcurrentMap<Long, Map<Long, Double>> commonInCloned;

	private final ConcurrentMap<Long, Map<Long, Double>> commonInAll;

	private final String outputFilePath;

	public CloneGenealogySimilarityStrategy(final String outputFilePath) {
		this.versionsUnderConsideration = new ConcurrentSkipListSet<>();
		this.commonInCloned = new ConcurrentSkipListMap<>();
		this.commonInAll = new ConcurrentSkipListMap<>();
		this.outputFilePath = outputFilePath;
	}

	@Override
	public boolean requiresVolatileObjects() {
		return false;
	}

	@Override
	public void mine(Collection<CloneGenealogy<E>> genealogies)
			throws Exception {
		final ExecutorService pool = Executors.newCachedThreadPool();

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
			for (final Map.Entry<Long, Map<Long, Double>> entry : commonInCloned
					.entrySet()) {
				final long genealogyId = entry.getKey();
				final Map<Long, Double> commonInClonedInGenealogy = entry
						.getValue();
				final Map<Long, Double> commonInAllInGenealogy = commonInAll
						.get(genealogyId);

				pw.println(buildRow(genealogyId, "C", commonInClonedInGenealogy));
				pw.println(buildRow(genealogyId, "A", commonInAllInGenealogy));
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

	private String buildRow(final long genealogyId, final String kind,
			final Map<Long, Double> values) {
		final StringBuilder builder = new StringBuilder();

		builder.append(genealogyId + "," + kind + ",");
		for (final long versionId : versionsUnderConsideration) {
			final Double value = values.get(versionId);
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
			final Map<Long, Set<DBCloneClass>> cloneClassesByVersions = new TreeMap<>();
			for (final DBCloneClassMapping mapping : genealogy
					.getCloneClassMappings()) {
				if (mapping.getOldCloneClass() != null) {
					addCloneClass(cloneClassesByVersions,
							mapping.getOldCloneClass());
				}
				if (mapping.getNewCloneClass() != null) {
					addCloneClass(cloneClassesByVersions,
							mapping.getNewCloneClass());
				}
			}

			final Map<Long, Double> commonInClonedInGenealogy = new TreeMap<>();
			final Map<Long, Double> commonInAllInGenealogy = new TreeMap<>();
			for (final Map.Entry<Long, Set<DBCloneClass>> entry : cloneClassesByVersions
					.entrySet()) {
				final long versionId = entry.getKey();
				versionsUnderConsideration.add(versionId);

				double numCommonInCloned = 0.0;
				double numCommonInAll = 0.0;

				for (final DBCloneClass cloneClass : entry.getValue()) {
					numCommonInCloned += cloneClass
							.getNumCommonClonedElements();
					numCommonInAll += cloneClass.getNumCommonAllElements();
				}

				numCommonInCloned /= entry.getValue().size();
				numCommonInAll /= entry.getValue().size();

				commonInClonedInGenealogy.put(versionId, numCommonInCloned);
				commonInAllInGenealogy.put(versionId, numCommonInAll);
			}
			commonInCloned.put(genealogy.getId(), commonInClonedInGenealogy);
			commonInAll.put(genealogy.getId(), commonInAllInGenealogy);

			logger.info("[" + count.incrementAndGet() + "/" + numGenealogies
					+ "] complete mining genealogy " + genealogy.getId());
		}

		private void addCloneClass(
				final Map<Long, Set<DBCloneClass>> cloneClassesByVersions,
				final DBCloneClass cloneClass) {
			final long versionId = cloneClass.getVersion().getId();

			Set<DBCloneClass> cloneClassesInVersion = cloneClassesByVersions
					.get(versionId);

			if (cloneClassesInVersion == null) {
				cloneClassesInVersion = new HashSet<>();
				cloneClassesByVersions.put(versionId, cloneClassesInVersion);
			}

			cloneClassesInVersion.add(cloneClass);
		}
	}

}
