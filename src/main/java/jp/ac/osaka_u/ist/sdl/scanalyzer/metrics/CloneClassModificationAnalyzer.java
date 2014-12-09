package jp.ac.osaka_u.ist.sdl.scanalyzer.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Segment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneModification.Type;
import difflib.myers.Equalizer;

/**
 * This class is for analyzing changes on clone classes.
 * 
 * @author k-hotta
 *
 */
public class CloneClassModificationAnalyzer<E extends IProgramElement> {

	private final ConcurrentMap<Long, Map<Long, List<E>>> allElements;

	private final ConcurrentMap<Long, Map<Long, List<E>>> lcsInCloned;

	private final ConcurrentMap<Long, Map<Long, List<E>>> lcsInAll;

	private final Collection<CloneClassMapping<E>> mappings;

	private final Equalizer<E> equalizer;

	public CloneClassModificationAnalyzer(
			final ConcurrentMap<Long, Map<Long, List<E>>> allElements,
			final ConcurrentMap<Long, Map<Long, List<E>>> lcsInCloned,
			final ConcurrentMap<Long, Map<Long, List<E>>> lcsInAll,
			final Collection<CloneClassMapping<E>> mappings,
			final Equalizer<E> equalizer) {
		this.allElements = allElements;
		this.lcsInCloned = lcsInCloned;
		this.lcsInAll = lcsInAll;
		this.mappings = mappings;
		this.equalizer = equalizer;
	}

	public void run() {
		final ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final List<Future<?>> futures = new ArrayList<>();
			for (final CloneClassMapping<E> mapping : mappings) {
				final CloneClassModificationAnalyzeTask task = new CloneClassModificationAnalyzeTask(
						mapping);
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

	private class CloneClassModificationAnalyzeTask implements Runnable {

		private final CloneClassMapping<E> mapping;

		private CloneClassModificationAnalyzeTask(final CloneClassMapping<E> mapping) {
			this.mapping = mapping;
		}

		@Override
		public void run() {
			analyzeNonLcs();
			analyzeLcsInCloned();
			analyzeLcsInAll();
		}

		private void analyzeNonLcs() {
			analyze((lcsElements, cloneClassId) -> {
				lcsElements.putAll(allElements.get(cloneClassId));
			});
		}

		private void analyzeLcsInCloned() {
			// XXX currently do nothing
			// analyze((lcsElements, cloneClassId) -> {
			// lcsElements.putAll(lcsInCloned.get(cloneClassId));
			// });
		}

		private void analyzeLcsInAll() {
			analyze((lcsElements, cloneClassId) -> {
				lcsElements.putAll(lcsInAll.get(cloneClassId));
			});
		}

		/**
		 * Analyze the changes
		 * 
		 * @param function
		 *            a function to retrieve corresponding LCSs
		 */
		private void analyze(final LCSRetrieveFunction<E> function) {
			final Map<Long, List<E>> lcsElements = new TreeMap<>();
			final Map<Long, CodeFragment<E>> oldCodeFragments = new TreeMap<>();
			final Map<Long, CodeFragment<E>> newCodeFragments = new TreeMap<>();

			if (mapping.getOldCloneClass() != null) {
				function.retrieve(lcsElements, mapping.getOldCloneClass()
						.getId());
				oldCodeFragments.putAll(mapping.getOldCloneClass()
						.getCodeFragments());
				oldCodeFragments.putAll(mapping.getOldCloneClass()
						.getGhostFragments());
			}
			if (mapping.getNewCloneClass() != null) {
				function.retrieve(lcsElements, mapping.getNewCloneClass()
						.getId());
				newCodeFragments.putAll(mapping.getNewCloneClass()
						.getCodeFragments());
				newCodeFragments.putAll(mapping.getNewCloneClass()
						.getGhostFragments());
			}

			// ask another method to perform the procedure
			for (final CodeFragmentMapping<E> fragmentMapping : mapping
					.getCodeFragmentMappings().values()) {
				processFragmentMapping(fragmentMapping, lcsElements,
						oldCodeFragments, newCodeFragments);
				if (fragmentMapping.getOldCodeFragment() != null) {
					oldCodeFragments.remove(fragmentMapping
							.getOldCodeFragment().getId());
				}
				if (fragmentMapping.getNewCodeFragment() != null) {
					newCodeFragments.remove(fragmentMapping
							.getNewCodeFragment().getId());
				}
			}

			for (final CodeFragment<E> deletedFragment : oldCodeFragments
					.values()) {
				for (final Segment<E> deletedSegment : deletedFragment
						.getSegments()) {
					processSegmentDeletion(deletedSegment, lcsElements, null);
				}
			}

			for (final CodeFragment<E> addedFragment : newCodeFragments
					.values()) {
				for (final Segment<E> addedSegment : addedFragment
						.getSegments()) {
					processSegmentAddition(addedSegment, lcsElements, null);
				}
			}
		}

		/**
		 * Analyze code fragment mapping and detect changes among them.
		 * 
		 * @param mapping
		 *            code fragment mapping
		 * @param lcsElements
		 *            LCSs
		 * @param oldCodeFragments
		 *            code fragments in old version
		 * @param newCodeFragments
		 *            code fragments in new version
		 */
		private void processFragmentMapping(
				final CodeFragmentMapping<E> fragmentMapping,
				final Map<Long, List<E>> lcsElements,
				final Map<Long, CodeFragment<E>> oldCodeFragments,
				final Map<Long, CodeFragment<E>> newCodeFragments) {
			final CodeFragment<E> oldCodeFragment = fragmentMapping
					.getOldCodeFragment();
			final CodeFragment<E> newCodeFragment = fragmentMapping
					.getNewCodeFragment();

			if (oldCodeFragment == null && newCodeFragment == null) {
				throw new IllegalStateException("both fragments are null");
			}

			final Map<Integer, Segment<E>> oldSegmentsByPathHash = (oldCodeFragment == null) ? new TreeMap<>()
					: getSegmentsByPathHash(oldCodeFragment);

			final Map<Integer, Segment<E>> newSegmentsByPathHash = (newCodeFragment == null) ? new TreeMap<>()
					: getSegmentsByPathHash(newCodeFragment);

			for (final int key : oldSegmentsByPathHash.keySet()) {
				final Segment<E> oldSegment = oldSegmentsByPathHash.get(key);
				final Segment<E> newSegment = newSegmentsByPathHash.remove(key);

				if (newSegment == null) {
					// this is segment deletion
					processSegmentDeletion(oldSegment, lcsElements,
							fragmentMapping);
				} else {
					// this is segment change
					processSegmentChange(oldSegment, newSegment, lcsElements,
							fragmentMapping);
				}
			}

			for (final Segment<E> newSegment : newSegmentsByPathHash.values()) {
				processSegmentAddition(newSegment, lcsElements, fragmentMapping);
			}
		}

		/**
		 * Get the segments in the given code fragment as a map whose key is the
		 * hash code value calculated from the path of the owner file of each
		 * segment, and whose value is the segment itself.
		 * 
		 * @param codeFragment
		 * @return
		 */
		private Map<Integer, Segment<E>> getSegmentsByPathHash(
				final CodeFragment<E> codeFragment) {
			/*
			 * XXX the followings suppose that each code fragment has only one
			 * segment in a single source file
			 */
			final Map<Integer, Segment<E>> result = new TreeMap<>();

			for (final Map.Entry<SourceFile<E>, SortedSet<Segment<E>>> entry : codeFragment
					.getSegmentsAsMap().entrySet()) {
				if (entry.getValue().size() > 1) {
					throw new IllegalStateException(
							"there are two or more segments in a file");
				}
				result.put(entry.getKey().getHashOfPath(), entry.getValue()
						.first());
			}

			return result;
		}

		private void processSegmentAddition(final Segment<E> newSegment,
				final Map<Long, List<E>> lcsElements,
				final CodeFragmentMapping<E> fragmentMapping) {
			// final List<E> contents = new ArrayList<E>();
			// contents.addAll(newSegment.getContents());
			// ModificationAnalyzeHelper.registerModification(Type.ADD,
			// contents,
			// -1, newSegment.getFirstElement().getPosition(), null,
			// newSegment, null);

			final List<E> lcsElementsList = lcsElements.get(newSegment
					.getCodeFragment().getId());
			final List<List<E>> divided = ModificationAnalyzeHelper
					.divide(lcsElementsList);

			for (final List<E> sequential : divided) {
				ModificationAnalyzeHelper.registerModification(Type.ADD,
						sequential, -1, sequential.get(0).getPosition(), null,
						newSegment, fragmentMapping);
			}
		}

		private void processSegmentDeletion(final Segment<E> oldSegment,
				final Map<Long, List<E>> lcsElements,
				final CodeFragmentMapping<E> fragmentMapping) {
			// final List<E> contents = new ArrayList<E>();
			// contents.addAll(oldSegment.getContents());
			// ModificationAnalyzeHelper.registerModification(Type.REMOVE,
			// contents, oldSegment.getFirstElement().getPosition(), -1,
			// oldSegment, null, null);

			final List<E> lcsElementsList = lcsElements.get(oldSegment
					.getCodeFragment().getId());
			final List<List<E>> divided = ModificationAnalyzeHelper
					.divide(lcsElementsList);

			for (final List<E> sequential : divided) {
				ModificationAnalyzeHelper.registerModification(Type.REMOVE,
						sequential, sequential.get(0).getPosition(), -1,
						oldSegment, null, fragmentMapping);
			}
		}

		private void processSegmentChange(final Segment<E> oldSegment,
				final Segment<E> newSegment,
				final Map<Long, List<E>> lcsElements,
				final CodeFragmentMapping<E> fragmentMapping) {
			final List<List<E>> dividedOld = ModificationAnalyzeHelper
					.divide(lcsElements.get(oldSegment.getCodeFragment()
							.getId()));
			final ModificationFinder<E> finder = new ModificationFinder<>(
					dividedOld, equalizer, oldSegment, newSegment,
					fragmentMapping);

			finder.findAndRegisterModifications();
		}

	}

	@FunctionalInterface
	private interface LCSRetrieveFunction<E extends IProgramElement> {

		public void retrieve(final Map<Long, List<E>> lcsElements,
				final long cloneClassId);

	}

}
