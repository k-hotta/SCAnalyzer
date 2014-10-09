package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.revision.RevisionInfo;

public class DBRegister {
	
	private final long maxRevisionCount;
	
	public DBRegister(final long maxRevisionCount) {
		this.maxRevisionCount = maxRevisionCount;
	}

	public void regist() {
		System.out.println("\t\tregstering a revision ...");
		final RevisionRegister revisionRegister = new RevisionRegister();
		final RevisionInfo currentRevision = DataManagerManager.getInstance()
				.getRevisionManager().getCurrentRevision();
		revisionRegister.regist(currentRevision);

		System.out.println("\t\tregistering newly added files ...");
		final FileRegister fileRegister = new FileRegister();
		fileRegister.registAll(DataManagerManager.getInstance()
				.getFileManager().getAddedFiles());

		System.out.println("\t\tregistering all the blocks in modified files ...");
		final UnitRegister unitRegister = new UnitRegister(maxRevisionCount);
		unitRegister.registAll(DataManagerManager.getInstance()
				.getUnitManager().getAllElements());
		System.out.println("\t\tupdating all the blocks in previous revisions ... ");
		unitRegister.updatePreviousRevisionBlocks(currentRevision.getId());

		//System.out.println("\t\tdetecting clone sets ...");
		//final CloneSetRegister cloneRegister = new CloneSetRegister(
				//currentRevision.getId());
		//cloneRegister.detectCloneSets();
		
		//System.out.println("\t\tregistering all the clone sets ...");
		//cloneRegister.insertCloneSets();
	}
}
