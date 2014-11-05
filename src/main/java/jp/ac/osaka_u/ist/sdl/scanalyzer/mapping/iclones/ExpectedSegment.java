package jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.iclones;

/**
 * This class represents segments that are expected to be based on mapping
 * information of program elements. A segment in before version will be updated
 * based on mapping information of program elements, and then the updated
 * segment will be compared to the segments in the next version. This class is
 * just an intermediate representation of updated segments. The segments in
 * before version will be updated and instances of this class will be provided.
 * The instances will be, then, used to detect the matching segments in the next
 * version. Note that this intermediate class has only the information about
 * positions. It has no information about the contents in the segments.
 * 
 * @author k-hotta
 *
 */
public class ExpectedSegment {

	/**
	 * The path of the owner file of the expected segment
	 */
	private final String path;

	/**
	 * The expected start position of the updated segment
	 */
	private final int startPosition;

	/**
	 * The expected end position of the updated segment
	 */
	private final int endPosition;

	public ExpectedSegment(final String path, final int startPosition,
			final int endPosition) {
		this.path = path;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	/**
	 * Get the path of the owner file of this expected segment
	 * 
	 * @return the path of the owner file
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * Get the start position of this expected segment.
	 * 
	 * @return the expected start position
	 */
	public final int getStartPosition() {
		return startPosition;
	}

	/**
	 * Get the end position of this expected segment.
	 * 
	 * @return the expected end position
	 */
	public final int getEndPosition() {
		return endPosition;
	}

}
