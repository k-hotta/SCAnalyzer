package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.RetrievedFileInfo;

public class FileInfoRetriever extends
		AbstractElementRetriever<RetrievedFileInfo> {

	private final SortedSet<Long> toRetrieve;

	public FileInfoRetriever(final long revisionId,
			final SortedSet<Long> toRetrieve) {
		super(revisionId);
		this.toRetrieve = toRetrieve;
	}

	@Override
	protected String getSelectStatement(long revisionId) {
		StringBuilder builder = new StringBuilder();
		builder.append("select * from FILE");

		boolean firstElement = true;

		for (final long id : toRetrieve) {
			if (firstElement) {
				builder.append(" where FILE_ID = " + ((Long) id).toString());
				firstElement = false;
			} else {
				builder.append(" OR FILE_ID = " + ((Long) id).toString());
			}
		}

		return builder.toString();
	}

	protected String getSelectStatement(long revisionId, long fileId) {
		return "select * from FILE where FILE_ID = "
				+ ((Long) fileId).toString();
	}

	public SortedSet<RetrievedFileInfo> retrieveAll() {
		final SortedSet<RetrievedFileInfo> result = new TreeSet<RetrievedFileInfo>();

		try {

			for (final long tmpId : toRetrieve) {
				final Statement stmt = connection.createStatement();
				final ResultSet rs = stmt.executeQuery(getSelectStatement(
						revisionId, tmpId));
				//connection.commit();
				// connection.setAutoCommit(false);

				while (rs.next()) {
					result.add(createElement(rs));
				}

				rs.close();
				stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSortedSet(result);
	}

	@Override
	protected RetrievedFileInfo createElement(ResultSet rs) throws Exception {
		final long fileId = rs.getLong(1);
		final String fileName = rs.getString(2);
		final String filePath = rs.getString(3);

		return new RetrievedFileInfo(fileId, fileName, filePath);
	}

}
