package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.CodeFragmentMapping;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.ICodeFragmentMapper;

/**
 * This class represents the protocol of detecting code fragment mapping based
 * on the algorithm used in <i>iClones</i>. This class adopts the algorithm for
 * Type-3 clones.
 * <p>
 * literature: S. Bazrafshan "Evolution of Near-miss Clones", in Proceedings of
 * the 12th International Working Conference on Source Code Analysis and
 * Manipulation (SCAM'12)
 * </p>
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of program element
 */
public class IClonesCodeFragmentMapper<E extends IProgramElement> implements
		ICodeFragmentMapper<E> {

	@Override
	public Collection<CodeFragmentMapping<E>> detectMapping(
			Version<E> previousVersion, Version<E> nextVersion) {
		// TODO Auto-generated method stub
		return null;
	}

}
