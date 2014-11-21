package jp.ac.osaka_u.ist.sdl.scanalyzer.genealogy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

import org.junit.BeforeClass;
import org.junit.Test;

public class CloneGenealogyFindHelperTest {

	private static final Map<Integer, DBCloneClass> imitationCloneClasses = new TreeMap<>();

	private static DBVersion ver1 = new DBVersion(1, new DBRevision(1, "1",
			null), null, null, new TreeSet<DBCloneClass>(
			new DBElementComparator()), null, new TreeSet<DBCloneClassMapping>(
			new DBElementComparator()));

	private static DBVersion ver2 = new DBVersion(2, new DBRevision(2, "2",
			null), null, null, new TreeSet<DBCloneClass>(
			new DBElementComparator()), null, new TreeSet<DBCloneClassMapping>(
			new DBElementComparator()));

	private static DBVersion ver3 = new DBVersion(3, new DBRevision(3, "3",
			null), null, null, new TreeSet<DBCloneClass>(
			new DBElementComparator()), null, new TreeSet<DBCloneClassMapping>(
			new DBElementComparator()));

	private static DBVersion ver4 = new DBVersion(4, new DBRevision(4, "4",
			null), null, null, new TreeSet<DBCloneClass>(
			new DBElementComparator()), null, new TreeSet<DBCloneClassMapping>(
			new DBElementComparator()));

	private static Method mCategorizeWithOldClone;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		for (int i = 1; i < 105; i++) {
			imitationCloneClasses
					.put(i, new DBCloneClass((long) i, null, null));
		}

		long count = 0;

		ver1.getCloneClasses().add(imitationCloneClasses.get(11));
		ver1.getCloneClasses().add(imitationCloneClasses.get(21));
		ver1.getCloneClasses().add(imitationCloneClasses.get(31));
		ver1.getCloneClasses().add(imitationCloneClasses.get(32));
		ver1.getCloneClasses().add(imitationCloneClasses.get(41));
		ver1.getCloneClasses().add(imitationCloneClasses.get(51));
		// ver1.getCloneClasses().add(imitationCloneClasses.get(61));
		ver1.getCloneClasses().add(imitationCloneClasses.get(71));
		ver1.getCloneClasses().add(imitationCloneClasses.get(81));
		ver1.getCloneClasses().add(imitationCloneClasses.get(82));
		ver1.getCloneClasses().add(imitationCloneClasses.get(91));
		ver1.getCloneClasses().add(imitationCloneClasses.get(92));
		ver1.getCloneClasses().add(imitationCloneClasses.get(101));

		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(11), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(21), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(31), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(32), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(41), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(51), null, ver1));
		// ver1.getCloneClassMappings().add(
		// new DBCloneClassMapping(count++, null, imitationCloneClasses
		// .get(61), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(71), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(81), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(82), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(91), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(92), null, ver1));
		ver1.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(101), null, ver1));

		ver2.getCloneClasses().add(imitationCloneClasses.get(12));
		ver2.getCloneClasses().add(imitationCloneClasses.get(22));
		ver2.getCloneClasses().add(imitationCloneClasses.get(33));
		ver2.getCloneClasses().add(imitationCloneClasses.get(34));
		ver2.getCloneClasses().add(imitationCloneClasses.get(42));
		ver2.getCloneClasses().add(imitationCloneClasses.get(43));
		ver2.getCloneClasses().add(imitationCloneClasses.get(52));
		ver2.getCloneClasses().add(imitationCloneClasses.get(62));
		ver2.getCloneClasses().add(imitationCloneClasses.get(72));
		ver2.getCloneClasses().add(imitationCloneClasses.get(83));
		ver2.getCloneClasses().add(imitationCloneClasses.get(84));
		ver2.getCloneClasses().add(imitationCloneClasses.get(93));
		ver2.getCloneClasses().add(imitationCloneClasses.get(102));
		ver2.getCloneClasses().add(imitationCloneClasses.get(103));

		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(11),
						imitationCloneClasses.get(12), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(21),
						imitationCloneClasses.get(22), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(31),
						imitationCloneClasses.get(33), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(32),
						imitationCloneClasses.get(34), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(41),
						imitationCloneClasses.get(42), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(41),
						imitationCloneClasses.get(43), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(51),
						imitationCloneClasses.get(52), null, ver2));
		// ver2.getCloneClassMappings().add(
		// new DBCloneClassMapping(count++, imitationCloneClasses.get(61),
		// imitationCloneClasses.get(62), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, null, imitationCloneClasses
						.get(62), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(71),
						imitationCloneClasses.get(72), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(81),
						imitationCloneClasses.get(83), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(82),
						imitationCloneClasses.get(84), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(91),
						imitationCloneClasses.get(93), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(92),
						imitationCloneClasses.get(93), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++,
						imitationCloneClasses.get(101), imitationCloneClasses
								.get(102), null, ver2));
		ver2.getCloneClassMappings().add(
				new DBCloneClassMapping(count++,
						imitationCloneClasses.get(101), imitationCloneClasses
								.get(103), null, ver2));

		ver3.getCloneClasses().add(imitationCloneClasses.get(13));
		ver3.getCloneClasses().add(imitationCloneClasses.get(23));
		ver3.getCloneClasses().add(imitationCloneClasses.get(24));
		ver3.getCloneClasses().add(imitationCloneClasses.get(35));
		ver3.getCloneClasses().add(imitationCloneClasses.get(44));
		ver3.getCloneClasses().add(imitationCloneClasses.get(53));
		ver3.getCloneClasses().add(imitationCloneClasses.get(54));
		ver3.getCloneClasses().add(imitationCloneClasses.get(63));
		ver3.getCloneClasses().add(imitationCloneClasses.get(73));
		ver3.getCloneClasses().add(imitationCloneClasses.get(74));
		ver3.getCloneClasses().add(imitationCloneClasses.get(85));
		ver3.getCloneClasses().add(imitationCloneClasses.get(94));
		ver3.getCloneClasses().add(imitationCloneClasses.get(104));

		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(12),
						imitationCloneClasses.get(13), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(22),
						imitationCloneClasses.get(23), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(22),
						imitationCloneClasses.get(24), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(33),
						imitationCloneClasses.get(35), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(34),
						imitationCloneClasses.get(35), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(42),
						imitationCloneClasses.get(44), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(43),
						imitationCloneClasses.get(44), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(52),
						imitationCloneClasses.get(53), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(52),
						imitationCloneClasses.get(54), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(62),
						imitationCloneClasses.get(63), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(72),
						imitationCloneClasses.get(73), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(72),
						imitationCloneClasses.get(74), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(83),
						imitationCloneClasses.get(85), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(84),
						imitationCloneClasses.get(85), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(93),
						imitationCloneClasses.get(94), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++,
						imitationCloneClasses.get(102), imitationCloneClasses
								.get(104), null, ver3));
		ver3.getCloneClassMappings().add(
				new DBCloneClassMapping(count++,
						imitationCloneClasses.get(103), imitationCloneClasses
								.get(104), null, ver3));

		ver4.getCloneClasses().add(imitationCloneClasses.get(14));
		ver4.getCloneClasses().add(imitationCloneClasses.get(25));
		ver4.getCloneClasses().add(imitationCloneClasses.get(26));
		ver4.getCloneClasses().add(imitationCloneClasses.get(36));
		ver4.getCloneClasses().add(imitationCloneClasses.get(45));
		ver4.getCloneClasses().add(imitationCloneClasses.get(55));

		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(13),
						imitationCloneClasses.get(14), null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(23),
						imitationCloneClasses.get(25), null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(24),
						imitationCloneClasses.get(26), null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(35),
						imitationCloneClasses.get(36), null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(44),
						imitationCloneClasses.get(45), null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(53),
						null, null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(54),
						imitationCloneClasses.get(55), null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(63),
						null, null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(73),
						null, null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(74),
						null, null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(85),
						null, null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++, imitationCloneClasses.get(94),
						null, null, ver4));
		ver4.getCloneClassMappings().add(
				new DBCloneClassMapping(count++,
						imitationCloneClasses.get(104), null, null, ver4));

		mCategorizeWithOldClone = CloneGenealogyFindHelper.class
				.getDeclaredMethod("categorizeWithOldClone", Collection.class);
		mCategorizeWithOldClone.setAccessible(true);
	}

	@Test
	public void testCategorizeWithOldClone1() throws Exception {
		@SuppressWarnings("unchecked")
		Map<DBCloneClass, List<DBCloneClassMapping>> result = (Map<DBCloneClass, List<DBCloneClassMapping>>) mCategorizeWithOldClone
				.invoke(null, ver1.getCloneClassMappings());

		assertTrue(result.containsKey(null));
		assertEquals(result.size(), 1);
		assertEquals(result.get(null).size(), 12);
	}

	@Test
	public void testCategorizeWithOldClone2() throws Exception {
		@SuppressWarnings("unchecked")
		Map<DBCloneClass, List<DBCloneClassMapping>> result = (Map<DBCloneClass, List<DBCloneClassMapping>>) mCategorizeWithOldClone
				.invoke(null, ver2.getCloneClassMappings());

		assertTrue(result.containsKey(null));
		assertEquals(result.size(), 13);

		assertEquals(result.get(imitationCloneClasses.get(41)).size(), 2);
	}

	@Test
	public void testCategorizeWithOldClone3() throws Exception {
		@SuppressWarnings("unchecked")
		Map<DBCloneClass, List<DBCloneClassMapping>> result = (Map<DBCloneClass, List<DBCloneClassMapping>>) mCategorizeWithOldClone
				.invoke(null, ver3.getCloneClassMappings());

		assertFalse(result.containsKey(null));
		assertEquals(result.size(), 14);

		assertEquals(result.get(imitationCloneClasses.get(22)).size(), 2);
		assertEquals(result.get(imitationCloneClasses.get(33)).get(0)
				.getNewCloneClass().getId(), 35);
		assertEquals(result.get(imitationCloneClasses.get(34)).get(0)
				.getNewCloneClass().getId(), 35);
	}

	@Test
	public void testCategorizeWithOldClone4() throws Exception {
		@SuppressWarnings("unchecked")
		Map<DBCloneClass, List<DBCloneClassMapping>> result = (Map<DBCloneClass, List<DBCloneClassMapping>>) mCategorizeWithOldClone
				.invoke(null, ver4.getCloneClassMappings());

		assertFalse(result.containsKey(null));
		assertEquals(result.size(), 13);

		assertEquals(result.get(imitationCloneClasses.get(44)).size(), 1);
		assertNull(result.get(imitationCloneClasses.get(63)).get(0)
				.getNewCloneClass());
	}

	@Test
	public void testConcatenate1() throws Exception {
		final Map<DBCloneClass, DBCloneGenealogy> genealogies = new HashMap<DBCloneClass, DBCloneGenealogy>();
		final Set<DBCloneGenealogy> disappearedGenealogies = new HashSet<>();
		CloneGenealogyFindHelper.concatenate(null, ver1,
				ver1.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		assertEquals(genealogies.size(), 12);
		assertTrue(disappearedGenealogies.isEmpty());

		assertEquals(genealogies.get(imitationCloneClasses.get(11))
				.getStartVersion(), ver1);
		assertNull(genealogies.get(imitationCloneClasses.get(11))
				.getEndVersion());
	}

	@Test
	public void testConcatenate2() throws Exception {
		final Map<DBCloneClass, DBCloneGenealogy> genealogies = new HashMap<DBCloneClass, DBCloneGenealogy>();
		final Set<DBCloneGenealogy> disappearedGenealogies = new HashSet<>();

		CloneGenealogyFindHelper.concatenate(null, ver1,
				ver1.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		disappearedGenealogies.clear();

		// preparation finished

		CloneGenealogyFindHelper.concatenate(ver1, ver2,
				ver2.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		assertEquals(14, genealogies.size());
		assertTrue(disappearedGenealogies.isEmpty());

		assertEquals(genealogies.get(imitationCloneClasses.get(12))
				.getStartVersion(), ver1);
		assertNull(genealogies.get(imitationCloneClasses.get(12))
				.getEndVersion());

		assertEquals(genealogies.get(imitationCloneClasses.get(62))
				.getStartVersion(), ver2);
		assertNull(genealogies.get(imitationCloneClasses.get(62))
				.getEndVersion());

		assertEquals(genealogies.get(imitationCloneClasses.get(42)),
				genealogies.get(imitationCloneClasses.get(43)));
	}

	@Test
	public void testConcatenate3() throws Exception {
		final Map<DBCloneClass, DBCloneGenealogy> genealogies = new HashMap<DBCloneClass, DBCloneGenealogy>();
		final Set<DBCloneGenealogy> disappearedGenealogies = new HashSet<>();

		CloneGenealogyFindHelper.concatenate(null, ver1,
				ver1.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		disappearedGenealogies.clear();

		CloneGenealogyFindHelper.concatenate(ver1, ver2,
				ver2.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		disappearedGenealogies.clear();

		// preparation finished

		CloneGenealogyFindHelper.concatenate(ver2, ver3,
				ver3.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		assertEquals(13, genealogies.size());
		assertTrue(disappearedGenealogies.isEmpty());

		assertEquals(genealogies.get(imitationCloneClasses.get(35))
				.getStartVersion(), ver1);
		assertNull(genealogies.get(imitationCloneClasses.get(35))
				.getEndVersion());

		assertEquals(genealogies.get(imitationCloneClasses.get(53)),
				genealogies.get(imitationCloneClasses.get(54)));

		assertEquals(5, genealogies.get(imitationCloneClasses.get(104))
				.getCloneClassMappings().size());
	}

	@Test
	public void testConcatenate4() throws Exception {
		final Map<DBCloneClass, DBCloneGenealogy> genealogies = new HashMap<DBCloneClass, DBCloneGenealogy>();
		final Set<DBCloneGenealogy> disappearedGenealogies = new HashSet<>();

		CloneGenealogyFindHelper.concatenate(null, ver1,
				ver1.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		disappearedGenealogies.clear();

		CloneGenealogyFindHelper.concatenate(ver1, ver2,
				ver2.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		disappearedGenealogies.clear();

		CloneGenealogyFindHelper.concatenate(ver2, ver3,
				ver3.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		disappearedGenealogies.clear();

		final DBCloneGenealogy disappearedGenealogySample = genealogies
				.get(imitationCloneClasses.get(104));

		// preparation finished

		CloneGenealogyFindHelper.concatenate(ver3, ver4,
				ver4.getCloneClassMappings(), genealogies,
				disappearedGenealogies);

		assertEquals(6, genealogies.size());
		assertEquals(5, disappearedGenealogies.size());

		assertEquals(genealogies.get(imitationCloneClasses.get(55))
				.getStartVersion(), ver1);
		assertNull(genealogies.get(imitationCloneClasses.get(55))
				.getEndVersion());

		assertEquals(genealogies.get(imitationCloneClasses.get(25)),
				genealogies.get(imitationCloneClasses.get(26)));

		assertEquals(6, disappearedGenealogySample.getCloneClassMappings()
				.size());

		for (DBCloneGenealogy disappearedGenealogy : disappearedGenealogies) {
			assertEquals(disappearedGenealogy.getEndVersion().getId(),
					ver3.getId());
		}
	}

}
