package jp.ac.osaka_u.ist.sdl.scanalyzer.genealogy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

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
public class CloneGenealogyFinder {

	public Map<DBCloneClass, DBCloneGenealogy> concatinate(
			final DBVersion nextVersion,
			final Collection<DBCloneClassMapping> nextMappings,
			final Map<DBCloneClass, DBCloneGenealogy> currentGenealogies) {
		final Map<DBCloneClass, DBCloneGenealogy> result = new TreeMap<>(
				new DBElementComparator());

		for (final DBCloneClassMapping nextMapping : nextMappings) {
			final DBCloneClass oldCloneClass = nextMapping.getOldCloneClass();
			final DBCloneClass newCloneClass = nextMapping.getNewCloneClass();

			if (oldCloneClass == null) {
				if (newCloneClass == null) {
					throw new IllegalStateException(
							"both of new/old clone classes are null");
				}

				final DBCloneGenealogy newGenealogy = new DBCloneGenealogy(
						IDGenerator.generate(DBCloneGenealogy.class),
						nextVersion, null, new ArrayList<DBCloneClassMapping>());
				newGenealogy.getCloneClassMappings().add(nextMapping);

				result.put(newCloneClass, newGenealogy);
			} else {
				if (!currentGenealogies.containsKey(oldCloneClass)) {
					throw new IllegalStateException("the old clone class "
							+ oldCloneClass.getId()
							+ " are not in any genealogies");
				}

				if (newCloneClass == null) {
					// this is clone removal
					currentGenealogies.get(oldCloneClass)
							.getCloneClassMappings().add(nextMapping);
				} else {
					final DBCloneGenealogy previousGenealogy = currentGenealogies
							.get(oldCloneClass);
					previousGenealogy.getCloneClassMappings().add(nextMapping);

					if (result.containsKey(newCloneClass)) {
						final DBCloneGenealogy alreadyRegisteredGenealogy = result
								.remove(newCloneClass);
						result.put(
								newCloneClass,
								merge(alreadyRegisteredGenealogy,
										previousGenealogy));
					}
				}
			}
		}

		return result;
	}

	private DBCloneGenealogy merge(final DBCloneGenealogy genealogy1,
			final DBCloneGenealogy genealogy2) {
		genealogy1.getCloneClassMappings().addAll(
				genealogy2.getCloneClassMappings());
		return genealogy1;
	}

}
