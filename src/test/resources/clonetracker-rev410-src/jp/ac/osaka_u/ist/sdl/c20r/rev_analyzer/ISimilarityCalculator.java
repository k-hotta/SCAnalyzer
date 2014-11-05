package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.util.List;

/**
 * テキストの類似度計測器を表すインターフェース
 * @author k-hotta
 *
 */
public interface ISimilarityCalculator {

	public double calc(final List<Token> l1, final List<Token> l2);
	
}
