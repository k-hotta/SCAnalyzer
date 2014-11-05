package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

/**
 * DBÇ©ÇÁâêÕëŒè€ÇÃÉäÉrÉWÉáÉìÇì¡íËÇ∑ÇÈ
 * 
 * @author k-hotta
 * 
 */
public class RevisionsDetector {

	private final List<Integer> revisions;

	private final ConcurrentMap<Integer, Integer> nextRevisions;

	private final DBConnection connection;
	
	public RevisionsDetector(final int startRev, final int endRev) {
		this.revisions = new ArrayList<Integer>();
		this.nextRevisions = new ConcurrentHashMap<Integer, Integer>();
		this.connection = DBConnection.getInstance();
		detect(startRev, endRev);
	}
	
	private void detect(final int startRev, final int endRev) {
		try {
			connection.setAutoCommit(true);
			final Statement stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("select REVISION_NUM from REVISION ORDER BY REVISION_NUM ASC");
			connection.setAutoCommit(false);
			
			int previous = -1;
			while (rs.next()) {
				final int revisionNum = rs.getInt(1);
				if (revisionNum < startRev || endRev < revisionNum) {
					continue;
				}
				revisions.add(revisionNum);
				if (previous >= 0) {
					nextRevisions.put(previous, revisionNum);
				}
				previous = revisionNum;
			}
			
			revisions.remove(revisions.size() - 1);
			
			rs.close();
			stmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("cannot detect target revisions!!");
		}
	}

	public int[] getRevisionsArray() {
		final int[] result = new int[revisions.size()];
		
		for (int i = 0; i < revisions.size(); i++) {
			result[i] = revisions.get(i);
		}
		
		return result;
	}
	
	public ConcurrentMap<Integer, Integer> getNextRevisions() {
		return nextRevisions;
	}
}
