package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Comparator;

/**
 * This class compares tokens based on their positions.
 * 
 * @author k-hotta
 *
 */
public class PositionTokenComparator implements Comparator<Token> {

	private final PathSourceFileComparator fileComparator = new PathSourceFileComparator();

	@Override
	public int compare(Token o1, Token o2) {
		final int comparedFile = fileComparator.compare(
				o1.getOwnerSourceFile(), o2.getOwnerSourceFile());
		if (comparedFile != 0) {
			return comparedFile;
		}

		return Integer.compare(o1.getPosition(), o2.getPosition());
	}

}
