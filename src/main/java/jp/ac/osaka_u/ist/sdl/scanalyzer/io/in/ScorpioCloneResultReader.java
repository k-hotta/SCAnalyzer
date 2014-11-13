package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IDGenerator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.IProgramElement;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.RawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Version;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBElementComparator;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawCloneClass;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.db.DBRawClonedFragment;
import jp.ac.osaka_u.ist.sdl.scanalyzer.exception.IllegalCloneResultFileFormatException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class reads the result file reported by Scorpio. <br>
 * 
 * @author k-hotta
 * 
 * @param <E>
 *            the type of program element
 */
public class ScorpioCloneResultReader<E extends IProgramElement> implements
		ICloneResultReader<E> {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The directory that contains the clone result files
	 */
	private final String dir;

	/**
	 * The format of the files of clones in each revision
	 */
	private final String format;

	/**
	 * The file system
	 */
	private final FileSystem fs;

	/**
	 * The constructor with the format of files of clones in each revision. <br>
	 * The format must have at least one "%s" to specify the identifier of each
	 * revision.
	 * 
	 * @param format
	 *            the format to specify a file having clone result for each of
	 *            revisions
	 */
	public ScorpioCloneResultReader(final String dir, final String format) {
		this.dir = dir;
		this.format = format;
		this.fs = FileSystems.getDefault();
	}

	@Override
	public Collection<RawCloneClass<E>> detectClones(Version<E> version) {
		try {
			final String targetFileName = String.format(format, version
					.getRevision().getIdentifier());
			final Path targetPath = fs.getPath(dir, targetFileName);
			final File targetFile = new File(targetPath.toString());

			return read(targetFile, version);
		} catch (Exception e) {
			eLogger.fatal("fail to detect clones in version " + version.getId());
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Collection<RawCloneClass<E>> read(File file, Version<E> version)
			throws IOException, IllegalCloneResultFileFormatException {
		BufferedReader br = null;
		Collection<RawCloneClass<E>> result = null;

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

	private Collection<RawCloneClass<E>> read(final BufferedReader br,
			final Version<E> version) throws IOException,
			IllegalCloneResultFileFormatException {
		final Collection<RawCloneClass<E>> result = new HashSet<RawCloneClass<E>>();
		final Map<String, SourceFile<E>> sourceFiles = getSourceFilesAsMapWithPath(version
				.getSourceFiles().values());

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

				final SourceFile<E> sourceFile1 = sourceFiles.get(path1);
				if (sourceFile1 == null) {
					eLogger.fatal("cannot find " + path1 + " in this version");
					throw new IllegalStateException(path1
							+ " doesn't exist in the version");
				}
				final SourceFile<E> sourceFile2 = sourceFiles.get(path2);
				if (sourceFile2 == null) {
					eLogger.fatal("cannot find " + path2 + " in this version");
					throw new IllegalStateException(path2
							+ " doesn't exist in the version");
				}

				final DBRawClonedFragment dbFragment1 = new DBRawClonedFragment(
						IDGenerator.generate(DBRawClonedFragment.class),
						version.getCore(), sourceFile1.getCore(), startLine1,
						endLine1 - startLine1 + 1, null);
				final RawClonedFragment<E> fragment1 = new RawClonedFragment<E>(
						dbFragment1);
				fragment1.setSourceFile(sourceFile1);

				final DBRawClonedFragment dbFragment2 = new DBRawClonedFragment(
						IDGenerator.generate(DBRawClonedFragment.class),
						version.getCore(), sourceFile2.getCore(), startLine2,
						endLine2 - startLine2 + 1, null);
				final RawClonedFragment<E> fragment2 = new RawClonedFragment<E>(
						dbFragment2);
				fragment2.setSourceFile(sourceFile2);

				final DBRawCloneClass dbCloneClass = new DBRawCloneClass(
						IDGenerator.generate(DBRawCloneClass.class),
						version.getCore(), new TreeSet<DBRawClonedFragment>(
								new DBElementComparator()));
				dbCloneClass.getElements().add(dbFragment1);
				dbCloneClass.getElements().add(dbFragment2);

				dbFragment1.setCloneClass(dbCloneClass);
				dbFragment2.setCloneClass(dbCloneClass);

				final RawCloneClass<E> cloneClass = new RawCloneClass<E>(
						dbCloneClass);
				cloneClass.addRawClonedFragment(fragment1);
				cloneClass.addRawClonedFragment(fragment2);
				fragment1.setRawCloneClass(cloneClass);
				fragment2.setRawCloneClass(cloneClass);

				cloneClass.setVersion(version);

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

	private static <E extends IProgramElement> Map<String, SourceFile<E>> getSourceFilesAsMapWithPath(
			final Collection<SourceFile<E>> sourceFiles) {
		final Map<String, SourceFile<E>> result = new TreeMap<String, SourceFile<E>>();

		for (final SourceFile<E> sourceFile : sourceFiles) {
			result.put(sourceFile.getPath(), sourceFile);
		}

		return result;
	}

}
