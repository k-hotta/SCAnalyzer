package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IAtomicElement;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class CloneClassBuildTaskTest {

	private static Method mTraceBack;

	private static Method mSearchPositionWithLine;

	private static IAtomicElement mock1;

	private static IAtomicElement mock2;

	private static IAtomicElement mock3_1;

	private static IAtomicElement mock3_2;

	private static IAtomicElement mock3_3;

	private static IAtomicElement mock4;

	private static IAtomicElement mock5;

	private static IAtomicElement mock6;

	private static IAtomicElement mock7;

	private static IAtomicElement mock8;

	private static IAtomicElement mock9;

	private static IAtomicElement mock10;

	private static SortedMap<Integer, IAtomicElement> mockElements;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		mTraceBack = CloneClassBuildTask.class.getDeclaredMethod("traceBack",
				SortedMap.class, int.class);
		mTraceBack.setAccessible(true);

		mSearchPositionWithLine = CloneClassBuildTask.class.getDeclaredMethod(
				"searchPositionWithLine", SortedMap.class, int.class);
		mSearchPositionWithLine.setAccessible(true);

		mock1 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock1.getLine()).andStubReturn(1);

		mock2 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock2.getLine()).andStubReturn(2);

		mock3_1 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock3_1.getLine()).andStubReturn(3);

		mock3_2 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock3_2.getLine()).andStubReturn(3);

		mock3_3 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock3_3.getLine()).andStubReturn(3);

		mock4 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock4.getLine()).andStubReturn(4);

		mock5 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock5.getLine()).andStubReturn(5);

		mock6 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock6.getLine()).andStubReturn(6);

		mock7 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock7.getLine()).andStubReturn(7);

		mock8 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock8.getLine()).andStubReturn(8);

		mock9 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock9.getLine()).andStubReturn(9);

		mock10 = EasyMock.createMock(IAtomicElement.class);
		EasyMock.expect(mock10.getLine()).andStubReturn(10);

		EasyMock.replay(mock1, mock2, mock3_1, mock3_2, mock3_3, mock4, mock5,
				mock6, mock7, mock8, mock9, mock10);

		mockElements = new TreeMap<Integer, IAtomicElement>();
		int count = 1;
		mockElements.put(count++, mock1);
		mockElements.put(count++, mock2);
		mockElements.put(count++, mock3_1);
		mockElements.put(count++, mock3_2);
		mockElements.put(count++, mock3_3);
		mockElements.put(count++, mock4);
		mockElements.put(count++, mock5);
		mockElements.put(count++, mock6);
		mockElements.put(count++, mock7);
		mockElements.put(count++, mock8);
		mockElements.put(count++, mock9);
		mockElements.put(count++, mock10);
	}

	@Test
	public void testTraceBack1() throws Exception {
		int result = (int) mTraceBack.invoke(
				new CloneClassBuildTask(null, null), mockElements, 5);
		assertTrue(result == 3);
	}

	@Test
	public void testSearchPositionWithLine1() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 3);
		assertTrue(result == 3);
	}
	
	@Test
	public void testSearchPositionWithLine2() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 8);
		assertTrue(result == 10);
	}
	
	@Test
	public void testSearchPositionWithLine3() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock5);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 4);
		assertTrue(result == 6);
	}
	
	@Test
	public void testSearchPositionWithLine4() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(6);
		elements.put(6, mock3_3);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 4);
		assertTrue(result == 7);
	}
	
	@Test
	public void testSearchPositionWithLine5() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(9);
		elements.put(9, mock6);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 7);
		assertTrue(result == 10);
	}
	
	@Test
	public void testSearchPositionWithLine6() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		elements.remove(9);
		elements.put(9, mock8);
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 7);
		assertTrue(result == 9);
	}
	
	@Test
	public void testSearchPositionWithLine7() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		for (int i = 4; i <= 12; i++) {
			elements.remove(i);
		}
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, 7);
		assertTrue(result == -1);
	}
	
	@Test
	public void testSearchPositionWithLine8() throws Exception {
		final SortedMap<Integer, IAtomicElement> elements = new TreeMap<Integer, IAtomicElement>(
				mockElements);
		for (int i = 4; i <= 12; i++) {
			elements.remove(i);
		}
		int result = (int) mSearchPositionWithLine.invoke(
				new CloneClassBuildTask(null, null), elements, -1);
		assertTrue(result == -1);
	}
	
}
