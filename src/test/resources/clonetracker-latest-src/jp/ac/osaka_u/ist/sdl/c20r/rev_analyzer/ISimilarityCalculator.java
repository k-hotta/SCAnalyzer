package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.List;

/**
 * �e�L�X�g�̗ގ��x�v�����\���C���^�[�t�F�[�X
 * @author k-hotta
 *
 */
public interface ISimilarityCalculator {

	public double calc(final List<Token> l1, final List<Token> l2);
	
}
