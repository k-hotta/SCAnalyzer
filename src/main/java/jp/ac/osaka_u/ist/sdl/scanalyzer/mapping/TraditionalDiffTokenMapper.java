package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.PositionTokenComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import difflib.myers.Equalizer;

/**
 * This class provides mapping of tokens between two consecutive versions with
 * traditional UNIX-Diff algorithm. This mapper never maps two tokens that are
 * in different source files. In other words, this mapper does not consider code
 * move between files.
 * 
 * @author k-hotta
 *
 */
public class TraditionalDiffTokenMapper implements IProgramElementMapper<Token> {

	/**
	 * This map contains the mapping of tokens. The key of this map is an ID of
	 * a file, which belongs to the previous version. The value of this map is
	 * the mapping of tokens in the file.
	 */
	private final ConcurrentMap<Long, Map<Token, Token>> mapping;

	/**
	 * The equalizer for tokens
	 */
	private final Equalizer<Token> equalizer;

	/**
	 * The constructor
	 */
	public TraditionalDiffTokenMapper(final Equalizer<Token> equalizer) {
		this.mapping = new ConcurrentSkipListMap<>();
		this.equalizer = equalizer;
	}

	@Override
	public Token getNext(Token previous) {
		if (previous == null) {
			throw new IllegalArgumentException("the given token is null");
		}

		return mapping.get(previous.getOwnerSourceFile().getId()).get(previous);
	}

	@Override
	public boolean prepare(Version<Token> previousVersion,
			Version<Token> nextVersion) {
		if (previousVersion == null) {
			throw new IllegalArgumentException(
					"the given previous version is null");
		}

		if (nextVersion == null) {
			throw new IllegalArgumentException("the given next version is null");
		}

		// clear the existing mapping
		this.mapping.clear();

		final List<TraditionalDiffTokenMappingTask> tasks = prepareTasks(
				previousVersion, nextVersion);

		ExecutorService pool = Executors.newCachedThreadPool();

		try {
			final Map<Long, Future<Map<Token, Token>>> futures = new TreeMap<>();

			for (final TraditionalDiffTokenMappingTask task : tasks) {
				futures.put(task.getOldSourceFile().getId(), pool.submit(task));
			}

			for (final Map.Entry<Long, Future<Map<Token, Token>>> future : futures
					.entrySet()) {
				final long oldFileId = future.getKey();
				try {
					final Map<Token, Token> mappingInFile = future.getValue()
							.get();
					this.mapping.put(oldFileId, mappingInFile);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}

		} finally {
			pool.shutdown();
		}

		return true;
	}

	/**
	 * Construct tasks to detect token mapping. Each task performs mapping
	 * between a pair of files.
	 * 
	 * @param previousVersion
	 *            the previous version
	 * @param nextVersion
	 *            the next version
	 * @return a list of constructed tasks
	 */
	private List<TraditionalDiffTokenMappingTask> prepareTasks(
			final Version<Token> previousVersion,
			final Version<Token> nextVersion) {
		final List<TraditionalDiffTokenMappingTask> tasks = new ArrayList<>();

		final Map<Long, SourceFile<Token>> previousFiles = new TreeMap<Long, SourceFile<Token>>();
		previousFiles.putAll(previousVersion.getSourceFiles());

		final Map<Long, SourceFile<Token>> nextFiles = new TreeMap<Long, SourceFile<Token>>();
		nextFiles.putAll(nextVersion.getSourceFiles());

		for (final FileChange<Token> fileChange : nextVersion.getFileChanges()
				.values()) {
			switch (fileChange.getType()) {
			case ADD: {
				final SourceFile<Token> newSourceFile = nextFiles
						.remove(fileChange.getNewSourceFile().getId());
				if (newSourceFile == null) {
					throw new IllegalStateException("cannot find "
							+ fileChange.getNewSourceFile() + " in version "
							+ nextVersion.getId());
				}

				// no task is prepared here
				// because addition of file does not require any treats

				break;
			}
			case DELETE: {
				final SourceFile<Token> oldSourceFile = previousFiles
						.remove(fileChange.getOldSourceFile().getId());
				if (oldSourceFile == null) {
					throw new IllegalStateException("cannot find "
							+ fileChange.getOldSourceFile() + " in version "
							+ previousVersion.getId());
				}

				tasks.add(constructTask(oldSourceFile, null, Type.DELETE));

				break;
			}
			case MODIFY:
			case RELOCATE: {
				final SourceFile<Token> oldSourceFile = previousFiles
						.remove(fileChange.getOldSourceFile().getId());
				if (oldSourceFile == null) {
					throw new IllegalStateException("cannot find "
							+ fileChange.getOldSourceFile() + " in version "
							+ previousVersion.getId());
				}

				final SourceFile<Token> newSourceFile = nextFiles
						.remove(fileChange.getNewSourceFile().getId());
				if (newSourceFile == null) {
					throw new IllegalStateException("cannot find "
							+ fileChange.getNewSourceFile() + " in version "
							+ nextVersion.getId());
				}

				tasks.add(constructTask(oldSourceFile, newSourceFile,
						Type.MODIFY));

				break;
			} // this is the end of case MODIFY-RELOCATE
			} // this is the end of switch-statement
		}

		if (previousFiles.size() != nextFiles.size()) {
			// the remaining files are stable ones
			// the numbers of unchanged files must be same between two versions
			throw new IllegalStateException(
					"the number of remaining files in the previous version does not match to that in the next version");
		}

		// construct tasks for unchanged files
		final List<Long> stableFileIds = new ArrayList<Long>();
		stableFileIds.addAll(previousFiles.keySet());
		for (final long fileId : stableFileIds) {
			final SourceFile<Token> oldSourceFile = previousFiles
					.remove(fileId);
			final SourceFile<Token> newSourceFile = nextFiles.remove(fileId);

			if (oldSourceFile == null || newSourceFile == null) {
				throw new IllegalStateException("file " + fileId
						+ " does not seem to unstable");
			}

			tasks.add(constructTask(oldSourceFile, newSourceFile, Type.STABLE));
		}

		if (!nextFiles.isEmpty()) {
			throw new IllegalStateException("some files remain unmapped");
		}

		return tasks;
	}

	/**
	 * Construct a task with the specified values.
	 * 
	 * @param oldSourceFile
	 *            the old source file
	 * @param newSourceFile
	 *            the new source file
	 * @param type
	 *            the type
	 * @return a constructed task
	 */
	private TraditionalDiffTokenMappingTask constructTask(
			final SourceFile<Token> oldSourceFile,
			final SourceFile<Token> newSourceFile, final Type type) {
		return new TraditionalDiffTokenMappingTask(oldSourceFile,
				newSourceFile, type);
	}

	/**
	 * The internal representation of change type. Note that there does not
	 * exist RELOCATE, which will be treated in the same way of MODIFY. In
	 * addition, this enum has STABLE, which means that the file was not changed
	 * between the two versions.
	 * 
	 * @author k-hotta
	 *
	 */
	private enum Type {
		ADD, MODIFY, DELETE, STABLE;
	}

	/**
	 * This class represents a task of mapping. Each task performs mapping a
	 * source file.
	 * 
	 * @author k-hotta
	 *
	 */
	private class TraditionalDiffTokenMappingTask implements
			Callable<Map<Token, Token>> {

		private final SourceFile<Token> oldSourceFile;

		private final SourceFile<Token> newSourceFile;

		private final Type type;

		public TraditionalDiffTokenMappingTask(
				final SourceFile<Token> oldSourceFile,
				final SourceFile<Token> newSourceFile, final Type type) {
			this.oldSourceFile = oldSourceFile;
			this.newSourceFile = newSourceFile;
			this.type = type;
		}

		private SourceFile<Token> getOldSourceFile() {
			return oldSourceFile;
		}

		@Override
		public Map<Token, Token> call() throws Exception {
			final Map<Token, Token> mapping = new TreeMap<Token, Token>(
					new PositionTokenComparator());

			switch (type) {
			case ADD:
				// here shouldn't be reached
				assert false;
				break;
			case DELETE:
				break; // do nothing for file deletion
			case MODIFY:
				mappingModifiedFiles(mapping);
				break;
			case STABLE:
				mappingStableFiles(mapping);
				break;
			}

			return mapping;
		}

		/**
		 * Mapping for modified files. All the tokens in old source file will be
		 * mapped to tokens in new source file based on diff information between
		 * two files.
		 * 
		 * @param mapping
		 *            the mapping where the results stored
		 */
		private void mappingModifiedFiles(final Map<Token, Token> mapping) {
			final List<Token> oldTokens = new ArrayList<Token>();
			oldTokens.addAll(oldSourceFile.getContents().values());

			final List<Token> newTokens = new ArrayList<Token>();
			newTokens.addAll(newSourceFile.getContents().values());

			final Patch<Token> patch = DiffUtils.diff(oldTokens, newTokens,
					equalizer);
			for (final Delta<Token> delta : patch.getDeltas()) {
				final List<Token> deletedTokens = delta.getOriginal()
						.getLines();

				for (final Token deletedToken : deletedTokens) {
					mapping.put(deletedToken, null);
				}
				oldTokens.removeAll(deletedTokens);

				final List<Token> addedTokens = delta.getRevised().getLines();
				newTokens.removeAll(addedTokens);
			}

			if (oldTokens.size() != newTokens.size()) {
				// the remaining tokens are unchanged tokens
				// hence, the number of tokens must be the same
				throw new IllegalStateException(
						"the number of remaining tokens are not the same. Old: "
								+ oldTokens.size() + " New: "
								+ newTokens.size());
			}

			for (int index = 0; index < oldTokens.size(); index++) {
				mapping.put(oldTokens.get(index), newTokens.get(index));
			}
		}

		/**
		 * Mapping for stable files. Every token in old source file will be
		 * mapped to the token itself through this operation.
		 * 
		 * @param mapping
		 *            the mapping where the results stored
		 */
		private void mappingStableFiles(final Map<Token, Token> mapping) {
			for (final Token token : this.oldSourceFile.getContents().values()) {
				mapping.put(token, token);
			}
		}

	}

}
