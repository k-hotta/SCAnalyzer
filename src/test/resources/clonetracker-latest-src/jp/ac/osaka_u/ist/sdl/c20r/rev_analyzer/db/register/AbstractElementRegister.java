package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.db.register;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.data.AbstractElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer.settings.Settings;

/**
 * �v�f��DB�ɓo�^���邽�߂Ɏg�p���钊�ۃN���X
 * 
 * @author k-hotta
 * 
 * @param <T>
 *            �o�^����v�f�̌^
 */
public abstract class AbstractElementRegister<T extends AbstractElementInfo> {

	/**
	 * DB�Ƃ̃R�l�N�V�������Ǘ�����N���X
	 */
	protected final DBConnection dbConnection;

	public AbstractElementRegister() {
		this.dbConnection = DBConnection.getInstance();
	}

	/**
	 * PreparedStatement ���쐬����ۂɎg�p���镶������擾����
	 * 
	 * @return
	 */
	protected abstract String createPreparedStatementQueue();

	/**
	 * ������PreparedStatement�ɁC������element��o�^���邽�߂ɕK�v�ȏ����Z�b�g����
	 * 
	 * @param pstmt
	 * @param element
	 */
	protected abstract void setAttributesIntoPreparedStatement(
			PreparedStatement pstmt, T element) throws SQLException;

	/**
	 * �����Ŏ󂯎�����v�f�����ׂ�DB�ɓo�^����
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
