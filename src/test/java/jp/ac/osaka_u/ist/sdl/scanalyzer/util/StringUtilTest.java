package jp.ac.osaka_u.ist.sdl.scanalyzer.util;

import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;

import org.junit.Test;

public class StringUtilTest {

	@Test
	public void testGuessEncoding1() {
		final String str = "test";
		final Charset guess = StringUtil.guessEncoding(str.getBytes());
		
		assertTrue(guess == Charset.forName("UTF-8"));
	}
	
	@Test
	public void testGuessEncoding2() {
		final String str = "テスト";
		final Charset guess = StringUtil.guessEncoding(str.getBytes());
		
		assertTrue(guess == Charset.forName("UTF-8"));
	}

	@Test
	public void testGuessEncoding3() throws Exception {
		final String str = "てすと";
		final Charset guess = StringUtil.guessEncoding(str.getBytes("UTF-16"));
		
		assertTrue(guess == Charset.forName("UTF-16BE"));
	}
	
}
