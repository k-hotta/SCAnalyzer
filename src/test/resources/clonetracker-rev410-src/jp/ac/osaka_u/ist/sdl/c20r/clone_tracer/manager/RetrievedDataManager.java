package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.manager;

public class RetrievedDataManager {

	private final RetrievedBlockInfoManager blockManager;
	
	private final RetrievedFileInfoManager fileManager;
	
	private final RetrievedCloneSetInfoManager cloneManager;
	
	public RetrievedDataManager() {
		this.blockManager = new RetrievedBlockInfoManager();
		this.fileManager = new RetrievedFileInfoManager();
		this.cloneManager = new RetrievedCloneSetInfoManager();
	}
	
	public RetrievedBlockInfoManager getBlockManager() {
		return blockManager;
	}
	
	public RetrievedFileInfoManager getFileManager() {
		return fileManager;
	}
	
	public RetrievedCloneSetInfoManager getCloneManager() {
		return cloneManager;
	}
	
}
