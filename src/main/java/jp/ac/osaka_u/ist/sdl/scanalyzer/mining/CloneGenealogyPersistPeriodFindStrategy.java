package jp.ac.osaka_u.ist.sdl.scanalyzer.mining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneClassMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBCloneGenealogy;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRevision;

public class CloneGenealogyPersistPeriodFindStrategy<E extends IProgramElement>
		implements MiningStrategy<DBCloneGenealogy, CloneGenealogy<E>> {

	private final String outputFilePath;

	private final Map<Long, Integer> persistPeriods;

	public CloneGenealogyPersistPeriodFindStrategy(final String outputFilePath) {
		this.outputFilePath = outputFilePath;
		this.persistPeriods = new TreeMap<>();
	}

	@Override
	public boolean requiresVolatileObjects() {
		return true;
	}

	@Override
	public void mine(Collection<CloneGenealogy<E>> genealogies)
			throws Exception {
		for (final CloneGenealogy<E> genealogy : genealogies) {
			Set<DBRevision> revisions = new HashSet<>();
			for (final DBCloneClassMapping mapping : genealogy.getCore()
					.getCloneClassMappings()) {
				revisions.add(mapping.getOldCloneClass().getVersion()
						.getRevision());
				revisions.add(mapping.getNewCloneClass().getVersion()
						.getRevision());
			}
			persistPeriods.put(genealogy.getId(), revisions.size());
		}
	}

	@Override
	public void writeResult() throws Exception {
		try (final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(outputFilePath))))) {
			pw.println("ID,#_REVS");
			for (final Map.Entry<Long, Integer> entry : persistPeriods
					.entrySet()) {
				pw.println(entry.getKey() + "," + entry.getValue());
			}
		}
	}

}
