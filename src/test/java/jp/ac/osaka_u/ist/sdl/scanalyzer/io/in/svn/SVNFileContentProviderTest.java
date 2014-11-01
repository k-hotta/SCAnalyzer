package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.svn;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersion;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.junit.BeforeClass;
import org.junit.Test;

public class SVNFileContentProviderTest {

	private static final String PATH_OF_TEST_REPO = "src/test/resources/repository-clonetracker";

	private static final String RELATIVE_PATH_FOR_TEST = "/c20r_main/src";

	private static SVNRepositoryManager manager;

	private static SVNFileContentProvider provider;

	private static Method mPrivateGetFileContent;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		manager = new SVNRepositoryManager(PATH_OF_TEST_REPO,
				RELATIVE_PATH_FOR_TEST, Language.JAVA);
		provider = new SVNFileContentProvider(manager);

		mPrivateGetFileContent = SVNFileContentProvider.class
				.getDeclaredMethod("getFileContent", long.class, String.class);
		mPrivateGetFileContent.setAccessible(true);
	}

	@Test
	public void testGetFileContent1() throws Exception {
		boolean caughtException = false;

		try {
			provider.getFileContent(null, new DBSourceFile());
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testGetFileContent2() throws Exception {
		boolean caughtException = false;

		try {
			provider.getFileContent(new DBVersion(), null);
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testGetFileContent3() throws Exception {
		boolean caughtException = false;

		try {
			provider.getFileContent(new DBVersion(), new DBSourceFile());
		} catch (IllegalArgumentException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testGetFileContent4() throws Exception {
		boolean caughtException = false;

		try {
			final DBVersion version = new DBVersion();
			version.setRevision(new DBRevision(0, "-1", null));
			provider.getFileContent(version, new DBSourceFile());
		} catch (IllegalStateException e) {
			caughtException = true;
		}

		assertTrue(caughtException);
	}

	@Test
	public void testPrivateGetFileContent1() throws Exception {
		final long revNum = 419;
		final String path = "/c20r_main/src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/BlockDetectThread.java";

		final String result = (String) mPrivateGetFileContent.invoke(provider,
				revNum, path);

		URI uri = Paths
				.get("src/test/resources/clonetracker-latest-src/jp/ac/osaka_u/ist/sdl/c20r/rev_analyzer/BlockDetectThread.java")
				.toUri();

		final String reference = read(uri.getPath(),
				Charset.forName("JISAutoDetect"));

		assertTrue(result.equals(reference));
	}

	private static String read(final String path, final Charset charset)
			throws Exception {
		String result = null;

		try (InputStream is = new FileInputStream(new File(path));
				ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			while (true) {
				int b = is.read();
				if (b == -1) {
					break;
				}

				os.write(b);
			}

			result = new String(os.toByteArray(), charset);
		}

		return result;
	}

}
