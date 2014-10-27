package jp.ac.osaka_u.ist.sdl.scanalyzer.data;

import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class maps each segment and its contents.
 * 
 * @author k-hotta
 * 
 */
public class SegmentContent<E extends IAtomicElement> {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The segment
	 */
	private Segment segment;

	/**
	 * The contents as a map. The key is the position of each content and the
	 * value is the content itself.
	 */
	private Map<Integer, E> contents;

	/**
	 * Construct the object. This constructor automatically detects contents of
	 * this segment with given segment and file contents.
	 * 
	 * @param segment
	 *            the segment
	 * @param contentsOfFile
	 *            the contents of source file
	 */
	public SegmentContent(final Segment segment,
			final SourceFileContent<E> contentsOfFile) {
		if (segment == null) {
			eLogger.fatal("segment is null");
			throw new IllegalArgumentException("segment must not be null");
		}
		if (contentsOfFile == null) {
			eLogger.fatal("contentsOfFile is null");
			throw new IllegalArgumentException(
					"contentsOfFile must not be null");
		}

		if (!segment.getSourceFile().equals(contentsOfFile.getSourceFile())) {
			eLogger.fatal(segment.getSourceFile() + " doesn't match to "
					+ contentsOfFile.getSourceFile());
			throw new IllegalArgumentException(
					"the owner file of segment doesn't match to the owner file of the specified contents");
		}

		this.segment = segment;
		final Map<Integer, E> fileContents = contentsOfFile.getContents();

		if (fileContents == null || fileContents.size() == 0) {
			eLogger.fatal("no contents were found in "
					+ contentsOfFile.getSourceFile());
			throw new IllegalArgumentException(
					"the specified contentsOfFile doesn't have any contents");
		}

		this.contents = new TreeMap<Integer, E>();
		for (int index = segment.getStartPosition(); index <= segment
				.getEndPosition(); index++) {
			final E content = fileContents.get(index);
			if (content == null) {
				eLogger.fatal(index + " in " + contentsOfFile.getSourceFile()
						+ " doesn't exist");
				throw new IllegalStateException("cannot find the element "
						+ index + " in " + contentsOfFile.getSourceFile());
			}

			contents.put(index, content);
		}
	}

	/**
	 * Get the segment
	 * 
	 * @return the segment
	 */
	public final Segment getSegment() {
		return segment;
	}

	/**
	 * Get the contents as a map
	 * 
	 * @return the contents
	 */
	public final Map<Integer, E> getContents() {
		return contents;
	}

}
