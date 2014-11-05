package jp.ac.osaka_u.ist.sdl.scanalyzer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.FileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.NearMissTokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Revision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBFileChange.Type;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.CloneClassBuildTask;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.FileChangeEntry;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ScorpioCloneResultReader;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.TokenSourceFileParser;

public class TestVersionBuilder {

	private String beforeSrcDir = "src/test/resources/clonetracker-rev410-src/";

	private String afterSrcDir = "src/test/resources/clonetracker-latest-src/";

	private String beforeCloneFile = "src/test/resources/clonetracker-scorpio-rev410.txt";

	private String afterCloneFile = "src/test/resources/clonetracker-scorpio-rev421.txt";

	private String diffFile = "src/test/resources/clonetracker-diff-summarize-rev410-rev419.txt";

	private long beforeRevisionNum = 410;

	private long afterRevisionNum = 419;

	private static final TokenSourceFileParser parser = new TokenSourceFileParser(
			Language.JAVA);

	private static Method mRead;

	static {
		try {
			mRead = ScorpioCloneResultReader.class.getDeclaredMethod("read",
					File.class, Version.class);
			mRead.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TestVersionBuilder() {

	}

	public TestVersionBuilder(final String beforeSrcDir,
			final String afterSrcDir, final String beforeCloneFile,
			final String afterCloneFile, final String diffFile,
			final long beforeRevisionNum, final long afterRevisionNum) {
		this.beforeSrcDir = beforeSrcDir;
		this.afterSrcDir = afterSrcDir;
		this.beforeCloneFile = beforeCloneFile;
		this.afterCloneFile = afterCloneFile;
		this.diffFile = diffFile;
		this.beforeRevisionNum = beforeRevisionNum;
		this.afterRevisionNum = afterRevisionNum;
	}

	public Map<Long, Version<Token>> build() throws Exception {
		final Version<Token> beforeVersion = initializeVersion(
				beforeRevisionNum, new Date());
		final Version<Token> afterVersion = initializeVersion(afterRevisionNum,
				new Date());
		final Map<Long, SourceFile<Token>> filesInBefore = getSourceFileInBeforeVersion();
		for (final SourceFile<Token> beforeFile : filesInBefore.values()) {
			beforeVersion.getCore().getSourceFiles().add(beforeFile.getCore());
			beforeVersion.addSourceFile(beforeFile);
		}

		final Map<Long, SourceFile<Token>> filesInAfter = new TreeMap<>();
		final Map<Long, FileChange<Token>> fileChanges = new TreeMap<>();

		detectFileChange(filesInBefore, filesInAfter, fileChanges, afterVersion);

		for (final SourceFile<Token> afterFile : filesInAfter.values()) {
			afterVersion.getCore().getSourceFiles().add(afterFile.getCore());
			afterVersion.addSourceFile(afterFile);
		}

		final Map<Long, RawCloneClass<Token>> beforeRawCloneClasses = detectRawCloneClass(
				beforeCloneFile, beforeVersion);
		final Map<Long, RawCloneClass<Token>> afterRawCloneClasses = detectRawCloneClass(
				afterCloneFile, afterVersion);

		detectCloneClasses(beforeRawCloneClasses, beforeVersion);
		detectCloneClasses(afterRawCloneClasses, afterVersion);

		final Map<Long, Version<Token>> result = new TreeMap<Long, Version<Token>>();
		result.put(beforeVersion.getId(), beforeVersion);
		result.put(afterVersion.getId(), afterVersion);

		return result;
	}

	private Version<Token> initializeVersion(final long revisionNum,
			final Date date) {
		final DBRevision dbRevision = new DBRevision(revisionNum,
				String.valueOf(revisionNum), date);
		final Revision revision = new Revision(dbRevision);

		final DBVersion dbVersion = new DBVersion(revisionNum, dbRevision,
				new TreeSet<DBFileChange>(new DBElementComparator()),
				new TreeSet<DBRawCloneClass>(new DBElementComparator()),
				new TreeSet<DBCloneClass>(new DBElementComparator()),
				new TreeSet<DBSourceFile>(new DBElementComparator()));
		final Version<Token> version = new Version<>(dbVersion);
		version.setRevision(revision);

		return version;
	}

	private Map<Long, SourceFile<Token>> getSourceFileInBeforeVersion() {
		final Map<Long, SourceFile<Token>> result = new TreeMap<Long, SourceFile<Token>>();

		final List<File> listFiles = new ArrayList<>();
		getFiles(new File(beforeSrcDir), listFiles);

		long count = 0;

		for (final File file : listFiles) {
			final DBSourceFile dbSourceFile = new DBSourceFile(count++,
					formatPath(file, new File(beforeSrcDir)));
			final SourceFile<Token> sourceFile = new SourceFile<>(dbSourceFile);
			readAndSet(file, sourceFile);

			result.put(sourceFile.getId(), sourceFile);
		}

		return result;
	}

	private void getFiles(final File file, final List<File> result) {
		if (file.isDirectory()) {
			for (final File child : file.listFiles()) {
				getFiles(child, result);
			}
		} else if (file.isFile()
				&& Language.JAVA.isRelevantFile(file.getName())) {
			result.add(file);
		}
	}

	private void readAndSet(final File file, final SourceFile<Token> sourceFile) {
		final StringBuilder builder = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = null;

			while ((line = br.readLine()) != null) {
				builder.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		final Map<Integer, Token> contents = parser.parse(sourceFile,
				builder.toString());
		sourceFile.setContents(contents.values());
	}

	private String formatPath(final File file, final File root) {
		final URI uri = file.toURI();
		final URI rootUri = root.toURI();

		final String fixed = uri.toString().substring(
				rootUri.toString().length());

		return "/c20r_main/src/" + fixed;
	}

	private void detectFileChange(
			final Map<Long, SourceFile<Token>> filesInBefore,
			final Map<Long, SourceFile<Token>> filesInAfterResult,
			final Map<Long, FileChange<Token>> fileChangesResult,
			final Version<Token> afterVersion) {
		final Map<String, SourceFile<Token>> filesInBeforeCopy = new TreeMap<String, SourceFile<Token>>();
		for (final SourceFile<Token> beforeFile : filesInBefore.values()) {
			filesInBeforeCopy.put(beforeFile.getPath(), beforeFile);
		}

		final List<FileChangeEntry> fileChangeEntries = new ArrayList<FileChangeEntry>();
		long index = filesInBeforeCopy.size();
		long count = 0;

		final Map<String, File> filesInAfter = new TreeMap<String, File>();
		final List<File> listFiles = new ArrayList<>();
		getFiles(new File(afterSrcDir), listFiles);
		for (final File afterFile : listFiles) {
			filesInAfter.put(formatPath(afterFile, new File(afterSrcDir)),
					afterFile);
		}

		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				diffFile)))) {
			String line = null;

			while ((line = br.readLine()) != null) {
				final String[] split = line.split(" ");
				if (Language.JAVA.isRelevantFile(split[1])) {
					switch (split[0]) {
					case "A":
						fileChangeEntries.add(new FileChangeEntry(null,
								split[1], 'A'));
						break;
					case "D":
						fileChangeEntries.add(new FileChangeEntry(split[1],
								null, 'D'));
						break;
					case "M":
						fileChangeEntries.add(new FileChangeEntry(split[1],
								split[1], 'M'));
						break;
					default:
						break; // ignore
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (FileChangeEntry changeEntry : fileChangeEntries) {
			if (changeEntry.getType() == 'A') {
				final DBSourceFile newDbFile = new DBSourceFile(index++,
						changeEntry.getAfterPath());
				final SourceFile<Token> newFile = new SourceFile<Token>(
						newDbFile);
				readAndSet(filesInAfter.get(changeEntry.getAfterPath()),
						newFile);
				filesInAfterResult.put(newFile.getId(), newFile);

				final DBFileChange additionDb = new DBFileChange(count++, null,
						newDbFile, Type.ADD, afterVersion.getCore());
				final FileChange<Token> addition = new FileChange<Token>(
						additionDb);
				addition.setNewSourceFile(newFile);
				addition.setVersion(afterVersion);

				afterVersion.getCore().getFileChanges().add(addition.getCore());
				afterVersion.addFileChange(addition);
			} else if (changeEntry.getType() == 'D') {
				final SourceFile<Token> deleted = filesInBeforeCopy
						.remove(changeEntry.getBeforePath());

				final DBFileChange deletionDb = new DBFileChange(count++,
						deleted.getCore(), null, Type.DELETE,
						afterVersion.getCore());
				final FileChange<Token> deletion = new FileChange<Token>(
						deletionDb);
				deletion.setOldSourceFile(deleted);
				deletion.setVersion(afterVersion);

				afterVersion.getCore().getFileChanges().add(deletion.getCore());
				afterVersion.addFileChange(deletion);
			} else if (changeEntry.getType() == 'M') {
				final SourceFile<Token> deleted = filesInBeforeCopy
						.remove(changeEntry.getBeforePath());

				final DBSourceFile newDbFile = new DBSourceFile(index++,
						changeEntry.getAfterPath());
				final SourceFile<Token> newFile = new SourceFile<Token>(
						newDbFile);
				readAndSet(filesInAfter.get(changeEntry.getAfterPath()),
						newFile);
				filesInAfterResult.put(newFile.getId(), newFile);

				final DBFileChange modificationDb = new DBFileChange(count++,
						deleted.getCore(), newDbFile, Type.MODIFY,
						afterVersion.getCore());
				final FileChange<Token> modification = new FileChange<Token>(
						modificationDb);
				modification.setOldSourceFile(deleted);
				modification.setNewSourceFile(newFile);
				modification.setVersion(afterVersion);

				afterVersion.getCore().getFileChanges()
						.add(modification.getCore());
				afterVersion.addFileChange(modification);
			}
		}

		// process unchanged files
		for (final SourceFile<Token> unchangedFile : filesInBeforeCopy.values()) {
			filesInAfterResult.put(unchangedFile.getId(), unchangedFile);
		}
	}

	private Map<Long, RawCloneClass<Token>> detectRawCloneClass(
			final String cloneFile, final Version<Token> version)
			throws Exception {
		final Map<Long, RawCloneClass<Token>> result = new TreeMap<Long, RawCloneClass<Token>>();

		final ScorpioCloneResultReader<Token> reader = new ScorpioCloneResultReader<>(
				null);
		@SuppressWarnings("unchecked")
		final Collection<RawCloneClass<Token>> rawCloneClasses = (Collection<RawCloneClass<Token>>) mRead
				.invoke(reader, new File(cloneFile), version);

		for (final RawCloneClass<Token> rawCloneClass : rawCloneClasses) {
			version.getCore().getRawCloneClasses().add(rawCloneClass.getCore());
			version.addRawCloneClass(rawCloneClass);
			result.put(rawCloneClass.getId(), rawCloneClass);
		}

		return result;
	}

	private void detectCloneClasses(
			final Map<Long, RawCloneClass<Token>> rawCloneClasses,
			final Version<Token> version) {
		ExecutorService pool = Executors.newCachedThreadPool();
		final List<Future<CloneClass<Token>>> futures = new ArrayList<Future<CloneClass<Token>>>();

		try {
			for (final RawCloneClass<Token> rawCloneClass : rawCloneClasses
					.values()) {
				final CloneClassBuildTask<Token> task = new CloneClassBuildTask<Token>(
						rawCloneClass, version, new NearMissTokenEqualizer());
				futures.add(pool.submit(task));
			}

			final List<CloneClass<Token>> results = new ArrayList<CloneClass<Token>>();
			for (final Future<CloneClass<Token>> future : futures) {
				try {
					results.add(future.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			for (final CloneClass<Token> result : results) {
				version.getCore().getCloneClasses().add(result.getCore());
				version.addCloneClass(result);
			}
		} finally {
			pool.shutdown();
		}
	}

}
