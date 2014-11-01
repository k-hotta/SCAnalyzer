package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBSourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.DBVersion;
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
	public Collection<DBRawCloneClass> detectClones(DBVersion version) {
		try {
			final String targetPath = String.format(format, version
					.getRevision().getIdentifier());
			final File targetFile = new File(targetPath);

			return read(targetFile, version);
		} catch (Exception e) {
			eLogger.fatal("fail to detect clones in version " + version.getId());
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Collection<DBRawCloneClass> read(File file, DBVersion version)
			throws IOException, IllegalCloneResultFileFormatException {
		BufferedReader br = null;
		Collection<DBRawCloneClass> result = null;

		try {
			br = new BufferedReader(new FileReader(file));
			result = read(br, version);
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

	private Collection<DBRawCloneClass> read(final BufferedReader br,
			final DBVersion version) throws IOException,
			IllegalCloneResultFileFormatException {
		final Collection<DBRawCloneClass> result = new HashSet<DBRawCloneClass>();
		final Map<String, DBSourceFile> sourceFiles = getSourceFilesAsMapWithPath(version
				.getSourceFiles());

		String line;
		try {
			while ((line = br.readLine()) != null) {
				final String[] splitLine = line.split("\t");

				final String path1 = splitLine[0];
				final int startLine1 = Integer.parseInt(splitLine[1]);
				final int endLine1 = Integer.parseInt(splitLine[2]);
				final String path2 = splitLine[3];
				final int startLine2 = Integer.parseInt(splitLine[4]);
				final int endLine2 = Integer.parseInt(splitLine[5]);

				final DBSourceFile sourceFile1 = sourceFiles.get(path1);
				if (sourceFile1 == null) {
					eLogger.fatal("cannot find " + path1 + " in this version");
					throw new IllegalStateException(path1
							+ " doesn't exist in the version");
				}
				final DBSourceFile sourceFile2 = sourceFiles.get(path2);
				if (sourceFile2 == null) {
					eLogger.fatal("cannot find " + path2 + " in this version");
					throw new IllegalStateException(path2
							+ " doesn't exist in the version");
				}

				final DBRawClonedFragment fragment1 = new DBRawClonedFragment(
						IDGenerator.generate(DBRawClonedFragment.class), version,
						sourceFile1, startLine1, endLine1 - startLine1 + 1,
						null);
				final DBRawClonedFragment fragment2 = new DBRawClonedFragment(
						IDGenerator.generate(DBRawClonedFragment.class), version,
						sourceFile2, startLine2, endLine2 - startLine2 + 1,
						null);

				final DBRawCloneClass cloneClass = new DBRawCloneClass(
						IDGenerator.generate(DBRawCloneClass.class), version,
						new TreeSet<DBRawClonedFragment>(
								new DBElementComparator()));
				cloneClass.getElements().add(fragment1);
				cloneClass.getElements().add(fragment2);

				fragment1.setCloneClass(cloneClass);
				fragment2.setCloneClass(cloneClass);

				result.add(cloneClass);
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

	private static Map<String, DBSourceFile> getSourceFilesAsMapWithPath(
			final Collection<DBSourceFile> sourceFiles) {
		final Map<String, DBSourceFile> result = new TreeMap<String, DBSourceFile>();

		for (final DBSourceFile sourceFile : sourceFiles) {
			result.put(sourceFile.getPath(), sourceFile);
		}

		return result;
	}

}
