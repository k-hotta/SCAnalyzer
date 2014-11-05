package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.settings;

/**
 * トラッキング機能用の設定情報
 * @author k-hotta
 *
 */
public class TracerSettings {

	private static TracerSettings SINGLETON = null;
	
	private int startRev;
	
	private int endRev;
	
	private String dbPath;
	
	private String resultDir;
	
	private String workingDir;
	
	private int threadsCount;
	
	private TracerSettings() {
		startRev = 0;
		endRev = Integer.MAX_VALUE;
		dbPath = null;
		resultDir = null;
		workingDir = null;
		threadsCount = 1;
	}
	
	public static TracerSettings getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new TracerSettings();
		}
		
		return SINGLETON;
	}

	public int getStartRev() {
		return startRev;
	}

	public void setStartRev(int startRev) {
		this.startRev = startRev;
	}

	public int getEndRev() {
		return endRev;
	}

	public void setEndRev(int endRev) {
		this.endRev = endRev;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public String getResultDir() {
		return resultDir;
	}

	public void setResultDir(String resultDir) {
		this.resultDir = resultDir;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}

	public int getThreadsCount() {
		return threadsCount;
	}

	public void setThreadsCount(int threadsCount) {
		this.threadsCount = threadsCount;
	}
	
}
