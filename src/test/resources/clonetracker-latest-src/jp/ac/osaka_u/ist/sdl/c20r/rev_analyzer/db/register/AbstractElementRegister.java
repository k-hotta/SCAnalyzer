package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

/**
 * 要素をDBに登録するために使用する抽象クラス
 * 
 * @author k-hotta
 * 
 * @param <T>
 *            登録する要素の型
 */
public abstract class AbstractElementRegister<T extends AbstractElementInfo> {

	/**
	 * DBとのコネクションを管理するクラス
	 */
	protected final DBConnection dbConnection;

	public AbstractElementRegister() {
		this.dbConnection = DBConnection.getInstance();
	}

	/**
	 * PreparedStatement を作成する際に使用する文字列を取得する
	 * 
	 * @return
	 */
	protected abstract String createPreparedStatementQueue();

	/**
	 * 引数のPreparedStatementに，引数のelementを登録するために必要な情報をセットする
	 * 
	 * @param pstmt
	 * @param element
	 */
	protected abstract void setAttributesIntoPreparedStatement(
			PreparedStatement pstmt, T element) throws SQLException;

	/**
	 * 引数で受け取った要素をすべてDBに登録する
	 * 
	 * @param elements
	 */
	public void registAll(Collection<T> elements) {
		try {
			final long start = System.currentTimeMillis();
			long lap = start;
			
			final PreparedStatement pstmt = dbConnection
					.createPreparedStatement(createPreparedStatementQueue());

			int count = 0;
			final int maxBatchCount = Settings.getIntsance().getMaxBatchCount();

			for (T element : elements) {
				setAttributesIntoPreparedStatement(pstmt, element);
				pstmt.addBatch();
				if ((++count % maxBatchCount) == 0) {
					pstmt.executeBatch();
					pstmt.clearBatch();
					//dbConnection.commit();
					final long now = System.currentTimeMillis();
					System.out.println("\t\t\t" + (count)
							+ " elements are registered (elapsed time "
							+ (now - lap) + "ms)");
					lap = now;
				}
			}

			pstmt.executeBatch();
			dbConnection.commit();
			final long end = System.currentTimeMillis();
			System.out.println("\t\t\t" + (count)
					+ " elements are registered (elapsed time " + (end - lap)
					+ "ms)");
			
			System.out.println("\t\ttotal elapsed time is " + (end - start) + "ms");

			pstmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
