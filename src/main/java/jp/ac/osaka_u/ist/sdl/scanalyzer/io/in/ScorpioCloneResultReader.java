package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.zip.DataFormatException;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.exception.IllegalCloneResultFileFormatException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class reads the result file reported by Scorpio. <br>
 * 
 * @author k-hotta
 * 
 */
public class ScorpioCloneResultReader implements ICloneResultReader {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The format of the files of clones in each revision
	 */
	private final String format;

	/**
	 * The constructor with the format of files of clones in each revision. <br>
	 * The format must have at least one "%s" to specify the identifier of each
	 * revision.
	 * 
	 * @param format
	 *            the format to specify a file having clone result for each of
	 *            revisions
	 */
	public ScorpioCloneResultReader(final String format) {
		this.format = format;
	}

	@Override
	public Collection<RawCloneClass> detectClones(Version version) {
		try {
			final String targetPath = String.format(format, version
					.getRevision().getIdentifier());
			final File targetFile = new File(targetPath);

			return read(targetFile);
		} catch (Exception e) {
			eLogger.fatal("fail to detect clones in version " + version.getId());
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Collection<RawCloneClass> read(File file) throws IOException,
			IllegalCloneResultFileFormatException {
		BufferedReader br = null;
		Collection<RawCloneClass> result = null;

		try {
			br = new BufferedReader(new FileReader(file));
			result = read(br);
		} catch (FileNotFoundException e) {
			eLogger.fatal("cannot find " + file.getAbsolutePath());
			throw e;
		} catch (IOException e) {
			eLogger.fatal("an error occurred when reading "
					+ file.getAbsolutePath());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					eLogger.warn("cannot close the input stream for "
							+ file.getAbsolutePath());
					throw e;
				}
			}
		}

		return result;
	}

	private Collection<RawCloneClass> read(final BufferedReader br)
			throws IOException, IllegalCloneResultFileFormatException {
		final Collection<RawCloneClass> result = new HashSet<RawCloneClass>();

		String line;
		try {
			while ((line = br.readLine()) != null) {
				final String[] splitLine = line.split("\t");

				final String path1 = splitLine[0];
				final int startLine1 = Integer.parseInt(splitLine[1]);
				final int endLineStr1 = Integer.parseInt(splitLine[2]);
				final String path2 = splitLine[3];
				final int startLineStr2 = Integer.parseInt(splitLine[4]);
				final int endLine2 = Integer.parseInt(splitLine[5]);

			}
		} catch (ArrayIndexOutOfBoundsException be) {
			eLogger.fatal("the given file seems to have illegal format");
			throw new IllegalCloneResultFileFormatException(be);
		} catch (NumberFormatException ne) {
			eLogger.fatal("the given file seems to have illegal format");
			throw new IllegalCloneResultFileFormatException(ne);
		} catch (IOException ioe) {
			throw ioe;
		}

		return Collections.unmodifiableCollection(result);
	}

}
