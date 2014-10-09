package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data;

import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.manager.AbstractDBElementManager;

public class IdManager {

	protected final AtomicLong count;
	
	protected final long idStart;
	
	public IdManager(AbstractDBElementManager<?> dbManager) {
		this.count = new AtomicLong(0);
		this.idStart = dbManager.getMaxId() + 1;
	}
	
	public synchronized long getNextId() {
		return count.getAndIncrement() + idStart;
	}
	
}
