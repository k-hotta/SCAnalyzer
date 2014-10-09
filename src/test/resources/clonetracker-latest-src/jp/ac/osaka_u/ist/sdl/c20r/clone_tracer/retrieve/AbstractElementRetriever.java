package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

/**
 * DBから要素を取り出すための抽象クラス
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractElementRetriever<T extends AbstractRetrievedElementInfo> {

	/**
	 * DBとのコネクション管理クラス
	 */
	protected final DBConnection connection;

	/**
	 * 着目中のリビジョンID
	 */
	protected final long revisionId;

	/**
	 * コンストラクタ
	 * 
	 * @param revisionId
	 */
	public AbstractElementRetriever(final long revisionId) {
		this.connection = DBConnection.getInstance();
		this.revisionId = revisionId;
	}

	/**
	 * DBから要素を取り出す
	 * 
	 * @return
	 */
	public SortedSet<T> retrieveAll() {
		final SortedSet<T> result = new TreeSet<T>();

		try {

			//connection.setAutoCommit(true);
			final Statement stmt = connection.createStatement();
			final ResultSet rs = stmt
					.executeQuery(getSelectStatement(revisionId));
			//connection.commit();
			//connection.setAutoCommit(false);

			while (rs.next()) {
				result.add(createElement(rs));
			}

			rs.close();
			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.unmodifiableSortedSet(result);
	}

	/**
	 * テーブルから要素を取り出すための select 命令を記述する
	 * 
	 * @param revisionId
	 * @return
	 */
	protected abstract String getSelectStatement(final long revisionId);

	/**
	 * テーブルの各行の情報から各要素のインスタンスを生成する
	 * 
	 * @param rs
	 * @return
	 */
	protected abstract T createElement(ResultSet rs) throws Exception;

}
