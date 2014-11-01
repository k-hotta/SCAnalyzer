package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ScorpioCloneResultReaderTest {

	private static ScorpioCloneResultReader reader;

	/*
	 * file name is "clonetracker-scorpio-rev***.txt"
	 */
	private static final String FORMAT = "src/test/resources/clonetracker-scorpio-rev%s.txt";

	private static DBVersion dummyVersion;

	private static Collection<DBSourceFile> dummySourceFiles;

	private BufferedReader br;

	private static Method mPrivateRead;

	private static Method mGetSourceFilesAsMapWithPath;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		reader = new ScorpioCloneResultReader(FORMAT);
		final Collection<String> fileList = makeFileList(
				"src/test/resources/clonetracker-latest-src/",
				"/c20r_main/src/");
		dummyVersion = new DBVersion();
		DBRevision dummyRevision = new DBRevision(421, "421", null);
		dummySourceFiles = makeDummySourceFiles(fileList);
		dummyVersion.setRevision(dummyRevision);
		dummyVersion.setSourceFiles(dummySourceFiles);

		mPrivateRead = ScorpioCloneResultReader.class.getDeclaredMethod("read",
				BufferedReader.class, DBVersion.class);
		mPrivateRead.setAccessible(true);
		mGetSourceFilesAsMapWithPath = ScorpioCloneResultReader.class
				.getDeclaredMethod("getSourceFilesAsMapWithPath",
						Collection.class);
		mGetSourceFilesAsMapWithPath.setAccessible(true);
	}

	@Before
	public void setUp() throws Exception {
		br = new BufferedReader(new FileReader(new File(
				"src/test/resources/clonetracker-scorpio-rev421.txt")));
	}

	@After
	public void tearDown() throws Exception {
		br.close();
	}

	private static Collection<DBSourceFile> makeDummySourceFiles(
			final Collection<String> list) {
		final Collection<DBSourceFile> result = new TreeSet<DBSourceFile>(
				new DBElementComparator());

		int count = 0;
		for (final String path : list) {
			final DBSourceFile dummy = new DBSourceFile(++count, path);
			result.add(dummy);
		}

		return result;
	}

	private static Collection<String> makeFileList(final String dir,
			final String head) throws Exception {
		final Collection<String> result = new TreeSet<String>();

		Path dirPath = Paths.get(dir);
		final String dirUriStr = dirPath.toUri().getPath();
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				final String uriStr = file.toUri().getPath();

				if (Language.JAVA.isRelevantFile(uriStr)) {
					final String replaced = head
							+ uriStr.substring(dirUriStr.length());

					result.add(replaced);
				}
				return FileVisitResult.CONTINUE;
			}
		};

		Files.walkFileTree(dirPath, EnumSet.noneOf(FileVisitOption.class),
				Integer.MAX_VALUE, visitor);

		return result;
	}

	@Test
	public void testGetSourceFilesAsMapWithPath1() throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, DBSourceFile> results = (Map<String, DBSourceFile>) mGetSourceFilesAsMapWithPath
				.invoke(null, dummySourceFiles);

		assertTrue(results.size() == dummySourceFiles.size());
		for (final DBSourceFile dummy : dummySourceFiles) {
			DBSourceFile result = results.get(dummy.getPath());
			assertTrue(result != null);
			assertTrue(result.equals(dummy));
		}
	}

	@Test
	public void testRead1() throws Exception {
		@SuppressWarnings("unchecked")
		Collection<DBRawCloneClass> result = (Collection<DBRawCloneClass>) mPrivateRead
				.invoke(reader, br, dummyVersion);

		assertTrue(result.size() == 1059);
	}

	@Test
	public void testDetectClones1() throws Exception {
		Collection<DBRawCloneClass> result = reader.detectClones(dummyVersion);

		assertTrue(result.size() == 1059);
	}

}
