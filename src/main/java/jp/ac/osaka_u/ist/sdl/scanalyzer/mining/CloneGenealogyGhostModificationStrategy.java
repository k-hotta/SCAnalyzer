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

import jp.ac.osaka_u.ist.sdl.scanalyzer.config.AvailableMiningStrategy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification.Place;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCodeFragment;
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
public class CloneGenealogyGhostModificationStrategy<E extends IProgramElement>
		implements WriteFileMiningStrategy<DBCloneGenealogy, CloneGenealogy<E>> {

	private static final AvailableMiningStrategy CORRESPONDING_STRATEGY = AvailableMiningStrategy.GENEALOGY_GHOST_MODIFICATIONS;

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneGenealogyGhostModificationStrategy.class);

	private final Set<Long> versionsUnderConsideration;

	private final String outputFilePattern;

	private final String projectName;

	/*
	 * for the following fields ... // <GenealogyID => <VersionID => number>>
	 */

	private final ConcurrentMap<Long, Integer> numConsistent;

	private final ConcurrentMap<Long, Integer> numInconsistent;

	public CloneGenealogyGhostModificationStrategy(
			final String outputFilePattern, final String projectName) {
		this.versionsUnderConsideration = new ConcurrentSkipListSet<>();
		this.outputFilePattern = outputFilePattern;
		this.projectName = projectName;
		this.numConsistent = new ConcurrentSkipListMap<>();
		this.numInconsistent = new ConcurrentSkipListMap<>();
	}

	@Override
	public String getStrategyName() {
		return CORRESPONDING_STRATEGY.getShortName();
	}

	@Override
	public String getProjectName() {
		return projectName;
	}

	@Override
	public boolean requiresVolatileObjects() {
		return false;
	}

	@Override
	public void mine(Collection<CloneGenealogy<E>> genealogies)
			throws Exception {
		//final ExecutorService pool = Executors.newCachedThreadPool();
		 final ExecutorService pool = Executors.newSingleThreadExecutor();

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
				new FileWriter(new File(FileNameHelper.getFileName(this,
						outputFilePattern)))))) {
			pw.println(buildHeader());

			for (final long genealogyId : numConsistent.keySet()) {
				pw.println(buildRows(genealogyId,
						numConsistent.get(genealogyId),
						numInconsistent.get(genealogyId)));
			}
		}
	}

	private String buildHeader() {
		final StringBuilder builder = new StringBuilder();

		builder.append("GENEALOGY_ID,CONSISTENT,INCONSISTENT");

		return builder.toString();
	}

	private String buildRows(final long genealogyId, final int consistent,
			final int inconsistent) {
		final StringBuilder builder = new StringBuilder();

		builder.append(genealogyId + "," + consistent + "," + inconsistent);

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

			final Map<Long, Set<Integer>> contentHashesOnCloned = new TreeMap<>();
			final Map<Long, Set<Integer>> contentHashesOnGhost = new TreeMap<>();

			final Set<Long> versionIdsConsistent = new TreeSet<>();
			final Set<Long> versionIdsInconsistent = new TreeSet<>();

			for (final DBCloneClassMapping cloneClassMapping : cloneClassMappings) {
				final DBCloneClass oldCloneClass = cloneClassMapping
						.getOldCloneClass();
				final DBCloneClass newCloneClass = cloneClassMapping
						.getNewCloneClass();

				if (oldCloneClass == null || newCloneClass == null) {
					continue;
				}

				boolean hasGhost = oldCloneClass.getNumGhostFragments() > 0;

				if (!hasGhost) {
					continue;
				}

				boolean hasModification = false;
				for (final DBCodeFragmentMapping fragmentMapping : cloneClassMapping
						.getCodeFragmentMappings()) {
					if (fragmentMapping.getOldCodeFragment() == null
							|| fragmentMapping.getNewCodeFragment() == null) {
						continue;
					}

					final DBCodeFragment oldFragment = fragmentMapping
							.getOldCodeFragment();
					final Set<Integer> contentHashesOnClonedInMapping = new TreeSet<>();
					final Set<Integer> contentHashesOnGhostInMapping = new TreeSet<>();

					boolean hasModificationInMapping = false;
					for (final DBCloneModification modification : fragmentMapping
							.getModifications()) {
						if (modification.getPlace() != Place.COMMON_ALL) {
							continue;
						}

						hasModification = true;
						hasModificationInMapping = true;

						if (oldFragment.isGhost()) {
							contentHashesOnGhostInMapping.add(modification
									.getContentHash());
						} else {
							contentHashesOnClonedInMapping.add(modification
									.getContentHash());
						}
					}

					if (hasModificationInMapping) {
						contentHashesOnCloned.put(fragmentMapping.getId(),
								contentHashesOnClonedInMapping);
						contentHashesOnGhost.put(fragmentMapping.getId(),
								contentHashesOnGhostInMapping);
					}
				}

				if (!hasModification) {
					continue;
				}

				if (contentHashesOnGhost.isEmpty()) {
					continue;
				}

				boolean inconsistent = false;

				if (contentHashesOnGhost.size() == 1
						&& contentHashesOnCloned.isEmpty()) {
					inconsistent = true;
				}

				for (final Set<Integer> hashGhost : contentHashesOnGhost
						.values()) {
					for (final Set<Integer> another : contentHashesOnGhost
							.values()) {
						if (!hashGhost.equals(another)) {
							inconsistent = true;
							break;
						}
					}

					if (inconsistent) {
						break;
					}

					for (final Set<Integer> hashCloned : contentHashesOnCloned
							.values()) {
						if (!hashGhost.equals(hashCloned)) {
							inconsistent = true;
							break;
						}
					}

					if (inconsistent) {
						break;
					}
				}

				final long versionId = newCloneClass.getVersion().getId();

				if (inconsistent) {
					versionIdsInconsistent.add(versionId);
				} else {
					versionIdsConsistent.add(versionId);
				}
			}

			if (!versionIdsConsistent.isEmpty()
					|| !versionIdsInconsistent.isEmpty()) {
				numConsistent.put(genealogyId, versionIdsConsistent.size());
				numInconsistent.put(genealogyId, versionIdsInconsistent.size());
			}

			logger.info("[" + count.incrementAndGet() + "/" + numGenealogies
					+ "] complete mining genealogy " + genealogy.getId());
		}
	}

}
