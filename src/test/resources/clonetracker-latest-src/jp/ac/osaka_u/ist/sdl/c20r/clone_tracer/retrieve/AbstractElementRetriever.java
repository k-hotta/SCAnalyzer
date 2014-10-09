package jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.retrieve;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.c20r.clone_tracer.data.AbstractRetrievedElementInfo;
import jp.ac.osaka_u.ist.sdl.c20r.db.DBConnection;

/**
 * DB����v�f�����o�����߂̒��ۃN���X
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractElementRetriever<T extends AbstractRetrievedElementInfo> {

	/**
	 * DB�Ƃ̃R�l�N�V�����Ǘ��N���X
	 */
	protected final DBConnection connection;

	/**
	 * ���ڒ��̃��r�W����ID
	 */
	protected final long revisionId;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param revisionId
	 */
	public AbstractElementRetriever(final long revisionId) {
		this.connection = DBConnection.getInstance();
		this.revisionId = revisionId;
	}

	/**
	 * DB����v�f�����o��
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
	 * �e�[�u������v�f�����o�����߂� select ���߂��L�q����
	 * 
	 * @param revisionId
	 * @return
	 */
	protected abstract String getSelectStatement(final long revisionId);

	/**
	 * �e�[�u���̊e�s�̏�񂩂�e�v�f�̃C���X�^���X�𐶐�����
	 * 
	 * @param rs
	 * @return
	 */
	protected abstract T createElement(ResultSet rs) throws Exception;

}
