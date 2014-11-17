package jp.ac.osaka_u.ist.sdl.scanalyzer.genealogy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBVersion;

/**
 * This class finds clone genealogies from database.
 * 
 * @author k-hotta
 *
 */
public class CloneGenealogyFindHelper {

	public static void concatenate(final DBVersion previousVersion,
			final DBVersion nextVersion,
			final Collection<DBCloneClassMapping> nextMappings,
			final Map<DBCloneClass, DBCloneGenealogy> currentGenealogies,
			final Set<DBCloneGenealogy> disappearedGenealogies) {
		assert (disappearedGenealogies.isEmpty());

		final Map<DBCloneClass, DBCloneGenealogy> result = new TreeMap<>(
				new DBElementComparator());

		final Set<DBCloneGenealogy> aliveGenealogies = new HashSet<>();
		final Set<DBCloneGenealogy> mightDisappearedGenealogies = new HashSet<>();

		// sort the given mappings based on their old clone classes
		// if two or more mappings share the same old clone classes
		// they will be mapped to the same key
		final Map<DBCloneClass, List<DBCloneClassMapping>> sortedMappings = categorizeWithOldClone(nextMappings);

		for (final Map.Entry<DBCloneClass, List<DBCloneClassMapping>> entry : sortedMappings
				.entrySet()) {
			final DBCloneClass oldCloneClass = entry.getKey();

			if (oldCloneClass != null) {
				// old clone class must be in a genealogy
				if (!currentGenealogies.containsKey(oldCloneClass)) {
					throw new IllegalStateException("the old clone class "
							+ oldCloneClass.getId()
							+ " are not in any genealogies");
				}

				// get clone genealogy contains the old clone class
				final DBCloneGenealogy previousGenealogy = currentGenealogies
						.remove(oldCloneClass);

				if (previousGenealogy != null) {
					boolean mightDisappeare = true;
					for (final DBCloneClassMapping mapping : entry.getValue()) {
						previousGenealogy.getCloneClassMappings().add(mapping);

						if (mapping.getNewCloneClass() != null) {
							// the genealogy is still alive
							mightDisappeare = false;

							if (result.containsKey(mapping.getNewCloneClass())) {
								// in case where two genealogies are merged
								final DBCloneGenealogy merged = merge(
										result.get(mapping.getNewCloneClass()),
										previousGenealogy);
								result.put(mapping.getNewCloneClass(), merged);
								aliveGenealogies.add(merged);
							} else {
								result.put(mapping.getNewCloneClass(),
										previousGenealogy);
								aliveGenealogies.add(previousGenealogy);
							}
						}
					}

					if (mightDisappeare) {
						// the genealogy might have disappeared
						mightDisappearedGenealogies.add(previousGenealogy);
					}

				} else {
					throw new IllegalStateException(
							"previous genealogy is null");
				}

			} else {
				// old clone class is null
				// make new genealogies for each of the mappings
				for (final DBCloneClassMapping mapping : entry.getValue()) {
					final DBCloneGenealogy newGenealogy = new DBCloneGenealogy(
							IDGenerator.generate(DBCloneGenealogy.class),
							nextVersion, null, new TreeSet<>(
									new DBElementComparator()));
					newGenealogy.getCloneClassMappings().add(mapping);
					result.put(mapping.getNewCloneClass(), newGenealogy);
				}
			}
		}

		for (final DBCloneGenealogy mightDisappearedGenealogy : mightDisappearedGenealogies) {
			if (!aliveGenealogies.contains(mightDisappearedGenealogy)) {
				mightDisappearedGenealogy.setEndVersion(previousVersion);
				disappearedGenealogies.add(mightDisappearedGenealogy);
			}
		}

		currentGenealogies.clear();
		currentGenealogies.putAll(result);
	}

	private static Map<DBCloneClass, List<DBCloneClassMapping>> categorizeWithOldClone(
			final Collection<DBCloneClassMapping> mappings) {
		final Map<DBCloneClass, List<DBCloneClassMapping>> result = new HashMap<>();

		for (final DBCloneClassMapping mappingCore : mappings) {
			if (result.containsKey(mappingCore.getOldCloneClass())) {
				result.get(mappingCore.getOldCloneClass()).add(mappingCore);
			} else {
				final List<DBCloneClassMapping> newList = new ArrayList<>();
				newList.add(mappingCore);
				result.put(mappingCore.getOldCloneClass(), newList);
			}
		}

		return result;
	}

	private static DBCloneGenealogy merge(final DBCloneGenealogy genealogy1,
			final DBCloneGenealogy genealogy2) {
		genealogy1.getCloneClassMappings().addAll(
				genealogy2.getCloneClassMappings());
		return genealogy1;
	}

}
