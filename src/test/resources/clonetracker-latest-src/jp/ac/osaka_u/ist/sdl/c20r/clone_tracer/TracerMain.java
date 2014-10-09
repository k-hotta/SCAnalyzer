package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.clone.MatchedCloneSetPairInfo;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager.RevisionManager;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.CRDMode;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve.Retriever;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.settings.TracerSettings;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer.ResultStringCreator;
import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.writer.ResultWriter;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

public class TracerMain {

	private static int beforeRevisionNum = 270153;

	private static int afterRevisionNum = 270155;

	private static String dbPath = "F:\\dbfiles\\ant.db";

	private static String resultDir = "C:\\workingdir\\c20r-resulttest";

	private static String workingDir = "C:\\workingdir\\ant";

	public static void main(String[] args) {
		DBConnection.createInstance(dbPath);

		TracerSettings settings = TracerSettings.getInstance();
		settings.setResultDir(resultDir);
		settings.setWorkingDir(workingDir);

		Retriever retriever = new Retriever(beforeRevisionNum,
				afterRevisionNum, CRDMode.ENHANCED);
		retriever.retrieve();

		RevisionManager manager = retriever.getRevisionManager();

		final String resultStr = ResultStringCreator.create(manager, 0);
		ResultWriter.getInstance().pool(beforeRevisionNum, afterRevisionNum,
				resultStr);
		ResultWriter.getInstance().writeAll();

		DBConnection.getInstance().close();

		int matchedCount = manager.getCloneManager().getMatchedCloneSetPairs()
				.size();
		int deletedCount = manager.getCloneManager().getDeletedCloneSetPairs()
				.size();
		int addedCount = manager.getCloneManager().getAddedCloneSetPairs()
				.size();

		int havingThreeOrMoreCount = 0;
		int containsHashChangeCount = 0;
		int containsIncreaseCount = 0;
		int containsDecreaseCount = 0;

		for (MatchedCloneSetPairInfo pair : manager.getCloneManager()
				.getMatchedCloneSetPairs()) {
			if (pair.getAllBlockPairs().size() > 2) {
				havingThreeOrMoreCount++;
			}
			if (pair.containsHashChange()) {
				containsHashChangeCount++;
			}
			if (pair.containsIncrease()) {
				containsIncreaseCount++;
			}
			if (pair.containsDecrease()) {
				containsDecreaseCount++;
			}
		}

		System.out.println("finish");
		System.out.println("\tadd: " + addedCount);
		System.out.println("\tdelete: " + deletedCount);
		System.out.println("\tmatch: " + matchedCount);
		System.out.println("\t\tthree or more: " + havingThreeOrMoreCount);
		System.out.println("\t\thash change: " + containsHashChangeCount);
		System.out.println("\t\tincrease: " + containsIncreaseCount);
		System.out.println("\t\tdecrease: " + containsDecreaseCount);
	}

}
