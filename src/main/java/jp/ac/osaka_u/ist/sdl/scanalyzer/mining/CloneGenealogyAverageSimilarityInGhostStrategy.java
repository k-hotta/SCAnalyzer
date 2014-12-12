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
import java.util.SortedMap;
import java.util.TreeMap;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is an implementation of {@link MiningStrategy} to get how similar a
 * clone class is during its ghost period.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class CloneGenealogyAverageSimilarityInGhostStrategy<E extends IProgramElement>
		implements WriteFileMiningStrategy<DBCloneGenealogy, CloneGenealogy<E>> {

	private static final AvailableMiningStrategy CORRESPONDING_STRATEGY = AvailableMiningStrategy.GENEALOGY_SIMILARITY_GHOST_AVERAGE;

	/**
	 * The logger
	 */
	private static Logger logger = LogManager
			.getLogger(CloneGenealogyAverageSimilarityInGhostStrategy.class);

	private final Set<Long> versionsUnderConsideration;

	private final ConcurrentMap<Long, Double> averageSimilarities;

	private final String outputFilePattern;

	private final String projectName;

	public CloneGenealogyAverageSimilarityInGhostStrategy(
			final String outputFilePattern, final String projectName) {
		this.versionsUnderConsideration = new ConcurrentSkipListSet<>();
		this.averageSimilarities = new ConcurrentSkipListMap<>();
		this.outputFilePattern = outputFilePattern;
		this.projectName = projectName;
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
				new FileWriter(new File(FileNameHelper.getFileName(this,
						outputFilePattern)))))) {
			pw.println(buildHeader());
			for (final Map.Entry<Long, Double> entry : averageSimilarities
					.entrySet()) {
				pw.println(buildRow(entry.getKey(), entry.getValue()));
			}
		}
	}

	private String buildHeader() {
		final StringBuilder builder = new StringBuilder();

		builder.append("GENEALOGY_ID,AVR_SIM");

		return builder.toString();
	}

	private String buildRow(final long genealogyId, final Double value) {
		final StringBuilder builder = new StringBuilder();

		builder.append(genealogyId + "," + value);

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

			final SortedMap<Long, Double> commonInClonedInGenealogy = new TreeMap<>();
			final SortedMap<Long, Double> commonInAllInGenealogy = new TreeMap<>();
			final SortedMap<Long, Boolean> hasGhost = new TreeMap<>();
			for (final Map.Entry<Long, Set<DBCloneClass>> entry : cloneClassesByVersions
					.entrySet()) {
				final long versionId = entry.getKey();
				versionsUnderConsideration.add(versionId);

				double numCommonInCloned = 0.0;
				double numCommonInAll = 0.0;

				boolean hasGhostInVersion = false;
				for (final DBCloneClass cloneClass : entry.getValue()) {
					numCommonInCloned += cloneClass
							.getNumCommonClonedElements();
					numCommonInAll += cloneClass.getNumCommonAllElements();

					if (cloneClass.getNumGhostFragments() > 0) {
						hasGhostInVersion = true;
					}
				}

				numCommonInCloned /= entry.getValue().size();
				numCommonInAll /= entry.getValue().size();

				commonInClonedInGenealogy.put(versionId, numCommonInCloned);
				commonInAllInGenealogy.put(versionId, numCommonInAll);
				hasGhost.put(versionId, hasGhostInVersion);
			}

			final SortedMap<Long, Double> similaritiesInGenealogy = new TreeMap<>();
			double lastCloned = 0.0;
			for (final long versionId : commonInClonedInGenealogy.keySet()) {
				final double inCloned = commonInClonedInGenealogy
						.get(versionId);
				final double inAll = commonInAllInGenealogy.get(versionId);
				final boolean hasGhostInVersion = hasGhost.get(versionId);

				if (hasGhostInVersion) {
					final double denominator = (inCloned > 0) ? inCloned
							: lastCloned;
					final double similarity = inAll / denominator;
					similaritiesInGenealogy.put(versionId, similarity);
				} else {
					// 100% similar if there is no ghosts
					similaritiesInGenealogy.put(versionId, 1.0);
				}

				if (inCloned > 0) {
					lastCloned = inCloned;
				}
			}

			double total = 0.0;
			int ghostCount = 0;

			for (final Map.Entry<Long, Double> entry : similaritiesInGenealogy
					.entrySet()) {
				long versionId = entry.getKey();
				double sim = entry.getValue();
				boolean ghost = hasGhost.get(versionId);

				if (ghost) {
					total += sim;
					ghostCount++;
				}
			}

			if (ghostCount > 0) {
				final double average = total / (double) ghostCount;
				averageSimilarities.put(genealogy.getId(), average);
			}

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
