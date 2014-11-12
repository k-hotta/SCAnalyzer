package jp.ac.osaka_u.ist.sdl.scanalyzer.config;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ICloneDetector;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileChangeEntryDetector;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IFileContentProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IRelocationFinder;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.IRevisionProvider;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.in.ISourceFileParser;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.ICloneClassMapper;
import jp.ac.osaka_u.ist.sdl.scanalyzer.mapping.IProgramElementMapper;
import difflib.myers.Equalizer;

/**
 * This class manages <b>worker</b>s to which tasks will be assigned. The worker
 * will vary because of the configuration values.
 * 
 * @author k-hotta
 *
 * @param <E>
 *            the type of element
 * 
 * @see IProgramElement
 * @see IRevisionProvider
 * @see IFileChangeEntryDetector
 * @see IRelocationFinder
 * @see ICloneDetector
 * @see IFileContentProvider
 * @see ISourceFileParser
 */
public class WorkerManager<E extends IProgramElement> {

	/**
	 * The revision provider
	 */
	private IRevisionProvider revisionProvider;

	/**
	 * The file change entry detector
	 */
	private IFileChangeEntryDetector fileChangeEntryDetector;

	/**
	 * The additional relocation finder
	 */
	private IRelocationFinder relocationFinder;

	/**
	 * The clone detector
	 */
	private ICloneDetector<E> cloneDetector;

	/**
	 * The file content provider
	 */
	private IFileContentProvider<E> fileContentProvider;

	/**
	 * The source file parser
	 */
	private ISourceFileParser<E> fileParser;

	/**
	 * The equalizer for elements
	 */
	private Equalizer<E> equalizer;

	/**
	 * The element mapper
	 */
	private IProgramElementMapper<E> elementMapper;

	/**
	 * The clone mapper
	 */
	private ICloneClassMapper<E> cloneMapper;

	public final IRevisionProvider getRevisionProvider() {
		return revisionProvider;
	}

	public final IFileChangeEntryDetector getFileChangeEntryDetector() {
		return fileChangeEntryDetector;
	}

	public final IRelocationFinder getRelocationFinder() {
		return relocationFinder;
	}

	public final ICloneDetector<E> getCloneDetector() {
		return cloneDetector;
	}

	public final IFileContentProvider<E> getFileContentProvider() {
		return fileContentProvider;
	}

	public final ISourceFileParser<E> getFileParser() {
		return fileParser;
	}

	public final Equalizer<E> getEqualizer() {
		return equalizer;
	}

	public final IProgramElementMapper<E> getElementMapper() {
		return elementMapper;
	}

	public final ICloneClassMapper<E> getCloneMapper() {
		return cloneMapper;
	}

	/**
	 * Set up all the workers with the specified configuration
	 * 
	 * @param config
	 *            the configuration
	 */
	public void setup(final Config config) {

	}

	private IRevisionProvider setupRevisionProvider(
			final VersionControlSystem vcs) {
		switch (vcs) {
		case SVN:
			// TODO implement
		}
		return null;
	}

}
