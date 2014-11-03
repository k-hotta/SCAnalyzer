package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Type1TokenEqualizer;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.db.DBXmlParser;

import org.junit.BeforeClass;
import org.junit.Test;

public class TraditionalDiffTokenMapperTest {

	private static final String TEST_DB_XML_PATH = "src/test/resources/test-db.xml";

	private static DBXmlParser parser;

	private static Class<?> cTaskClass;

	private static Class<?> cType;

	private static Constructor<?> taskConstructor;

	private static Method mPrepareTasks;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// parse data
		parser = new DBXmlParser(TEST_DB_XML_PATH);
		parser.parse();

		cTaskClass = TraditionalDiffTokenMapper.class.getDeclaredClasses()[0];
		cType = TraditionalDiffTokenMapper.class.getDeclaredClasses()[1];

		taskConstructor = cTaskClass.getDeclaredConstructor(
				TraditionalDiffTokenMapper.class, SourceFile.class,
				SourceFile.class, cType);
		taskConstructor.setAccessible(true);

		mPrepareTasks = TraditionalDiffTokenMapper.class.getDeclaredMethod(
				"prepareTasks", Version.class, Version.class);
		mPrepareTasks.setAccessible(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMappingDeletedFiles1() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final Callable<Map<Token, Token>> task = (Callable<Map<Token, Token>>) taskConstructor
				.newInstance(mapper,
						parser.getVolatileSourceFiles().get((long) 4), null,
						cType.getDeclaredField("DELETE").get(null));
		ExecutorService pool = Executors.newSingleThreadExecutor();

		try {
			final Future<Map<Token, Token>> future = pool.submit(task);
			final Map<Token, Token> result = future.get();

			assertEquals(result.size(), 0);

			for (final Token token : parser.getVolatileSourceFiles()
					.get((long) 4).getContents().values()) {
				assertNull(result.get(token));
			}
		} finally {
			pool.shutdown();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMappingStableFiles1() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final Callable<Map<Token, Token>> task = (Callable<Map<Token, Token>>) taskConstructor
				.newInstance(mapper,
						parser.getVolatileSourceFiles().get((long) 1), parser
								.getVolatileSourceFiles().get((long) 1), cType
								.getDeclaredField("STABLE").get(null));
		ExecutorService pool = Executors.newSingleThreadExecutor();

		try {
			final Future<Map<Token, Token>> future = pool.submit(task);
			final Map<Token, Token> result = future.get();

			assertEquals(result.size(),
					parser.getVolatileSourceFiles().get((long) 1).getContents()
							.size());

			for (final Token token : result.keySet()) {
				assertEquals(result.get(token), token);
			}
		} finally {
			pool.shutdown();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMappingModifiedFiles1() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final Callable<Map<Token, Token>> task = (Callable<Map<Token, Token>>) taskConstructor
				.newInstance(mapper,
						parser.getVolatileSourceFiles().get((long) 1), parser
								.getVolatileSourceFiles().get((long) 3), cType
								.getDeclaredField("MODIFY").get(null));
		ExecutorService pool = Executors.newSingleThreadExecutor();

		try {
			final Future<Map<Token, Token>> future = pool.submit(task);
			final Map<Token, Token> result = future.get();

			assertEquals(result.size(),
					parser.getVolatileSourceFiles().get((long) 1).getContents()
							.size());

			for (final Token token : result.keySet()) {
				assertEquals(result.get(token).getValue(), token.getValue());
			}
		} finally {
			pool.shutdown();
		}
	}

	@Test
	public void testPrepareTasks1() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final List<?> tasks = (List<?>) mPrepareTasks.invoke(mapper, parser
				.getVolatileVersions().get((long) 1), parser
				.getVolatileVersions().get((long) 2));

		assertTrue(tasks.size() == 1);
	}

	@Test
	public void testPrepareTasks2() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final List<?> tasks = (List<?>) mPrepareTasks.invoke(mapper, parser
				.getVolatileVersions().get((long) 2), parser
				.getVolatileVersions().get((long) 3));

		assertTrue(tasks.size() == 2);
	}

	@Test
	public void testPrepareTasks3() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final List<?> tasks = (List<?>) mPrepareTasks.invoke(mapper, parser
				.getVolatileVersions().get((long) 3), parser
				.getVolatileVersions().get((long) 4));

		assertTrue(tasks.size() == 3);
	}

	@Test
	public void testPrepare1() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final Version<Token> previous = parser.getVolatileVersions().get(
				(long) 1);
		final Version<Token> next = parser.getVolatileVersions().get((long) 2);

		mapper.prepare(previous, next);

		for (final Token token : previous.getSourceFiles().get((long) 1)
				.getContents().values()) {
			assertEquals(mapper.getNext(token), token);
		}
	}

	@Test
	public void testPrepare2() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final Version<Token> previous = parser.getVolatileVersions().get(
				(long) 2);
		final Version<Token> next = parser.getVolatileVersions().get((long) 3);

		mapper.prepare(previous, next);

		for (final Token token : previous.getSourceFiles().get((long) 1)
				.getContents().values()) {
			assertEquals(
					mapper.getNext(token).getValue(),
					next.getSourceFiles().get((long) 3).getContents()
							.get(token.getPosition()).getValue());
		}

		for (final Token token : previous.getSourceFiles().get((long) 2)
				.getContents().values()) {
			assertEquals(mapper.getNext(token), token);
		}
	}

	@Test
	public void testPrepare3() throws Exception {
		final TraditionalDiffTokenMapper mapper = new TraditionalDiffTokenMapper(
				new Type1TokenEqualizer());
		final Version<Token> previous = parser.getVolatileVersions().get(
				(long) 3);
		final Version<Token> next = parser.getVolatileVersions().get((long) 4);

		mapper.prepare(previous, next);

		for (final Token token : previous.getSourceFiles().get((long) 3)
				.getContents().values()) {
			assertEquals(mapper.getNext(token), token);
		}

		for (final Token token : previous.getSourceFiles().get((long) 2)
				.getContents().values()) {
			assertEquals(mapper.getNext(token), token);
		}

		for (final Token token : previous.getSourceFiles().get((long) 4)
				.getContents().values()) {
			assertNull(mapper.getNext(token));
		}

	}

}
