package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * This class manages the subversion repository under investigation. <br>
 * 
 * @author k-hotta
 * 
 */
public class SVNRepositoryManager {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(SVNRepositoryManager.class);

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The path to a SVN repository <br>
	 * e.g. C:/repository, http://sdl.ist.osaka-u.ac.jp/svn/ScorpioTM
	 */
	private final String path;

	/**
	 * The relative path to the SVN project <br>
	 * e.g. /trunk
	 */
	private final String relativePath;

	/**
	 * The url of the repository under managed
	 */
	private final SVNURL url;

	/**
	 * The instance of repository under managed
	 */
	private final SVNRepository repository;

	/**
	 * The language to be analyzed
	 */
	private final Language language;

	/**
	 * The constructor. <br>
	 * The given path and relative path are checked whether they are valid or
	 * not. Then this constructor initializes SVNURL and SVNRepository with the
	 * specified values. <br>
	 * The supported schemes of URL are file, http, https, and svn. If the
	 * specified path corresponds to the above schemes, this constructor regards
	 * the path as a URL of the scheme. This constructor otherwise regards the
	 * given path as a path to local file and tries to connect to the local file
	 * with the file: protocol.
	 * 
	 * <p>
	 * Note: the URL of the repository will be initialized with the one for the
	 * root of the repository even if the relative path is specified
	 * </p>
	 * 
	 * @param path
	 *            the URL to the target repository (e.g. file:///***), or the
	 *            path of the target local repository (e.g.
	 *            C:\work\local-repository); in the former case the URL
	 *            shouldn't end with "/" (if so, the last "/" will be removed)
	 * @param relativePath
	 *            the relative path of the position under considered to the SVN
	 *            root (e.g. /trunk), which should start with "/" otherwise the
	 *            additional "/" will be inserted in the head of the specified
	 *            string
	 * @param language
	 *            the programming language to be analyzed
	 * @throws SVNException
	 *             If any error occurred when connecting and initializing the
	 *             given repository
	 */
	public SVNRepositoryManager(final String path, final String relativePath,
			final Language language) throws SVNException {
		logger.trace("start initializing the manager of svn repository");

		if (path == null) {
			throw new IllegalArgumentException(
					"the path of the repository must not be null");
		}

		if (!path.endsWith("/")) {
			this.path = path;
		} else {
			eLogger.warn("the specified path ends with \"/\"");
			eLogger.warn("the path will be replaced by the one without the last \"/\"");
			this.path = path.substring(0, path.lastIndexOf("/"));
		}

		final String fixedRelativePath;
		if (relativePath == null) {
			fixedRelativePath = "";
		} else if (relativePath.startsWith("/")) {
			fixedRelativePath = relativePath;
		} else {
			eLogger.warn("the relative path doesn't start with \"/\"");
			eLogger.warn("the relative path will be replaced by the one having \"/\" in the head");
			fixedRelativePath = "/" + relativePath;
		}

		this.language = language;

		URI pathUriWithRelative = null;
		URI pathUriWithoutRelative = null;
		try {
			pathUriWithRelative = new URI(this.path + fixedRelativePath);
			pathUriWithoutRelative = new URI(this.path);
		} catch (URISyntaxException e) {
			eLogger.warn(this.path + this.relativePath + " is not a valid URI");
			eLogger.warn("try to find " + this.path + fixedRelativePath
					+ " as a repository in local");
		}

		if (pathUriWithRelative == null
				|| pathUriWithRelative.getScheme() == null) {
			// reset pathUri in case it is null or its scheme is null
			pathUriWithRelative = Paths.get(this.path, fixedRelativePath)
					.toUri();
		}

		if (pathUriWithoutRelative == null
				|| pathUriWithoutRelative.getScheme() == null) {
			pathUriWithoutRelative = Paths.get(this.path).toUri();
		}

		final String autoEncodedUrl = SVNEncodingUtil
				.autoURIEncode(pathUriWithRelative.toString());
		final SVNURL tmpUrl = SVNURL.parseURIEncoded(autoEncodedUrl);

		if (pathUriWithRelative.getScheme().equals("file")) {
			// file://***
			FSRepositoryFactory.setup();
		} else if (pathUriWithRelative.getScheme().startsWith("http")) {
			// http://*** or https://***
			DAVRepositoryFactory.setup();
		} else if (pathUriWithRelative.getScheme().startsWith("svn")) {
			// svn://*** or svn+xxx://***
			SVNRepositoryFactoryImpl.setup();
		} else {
			// otherwise is malformed
			eLogger.fatal("invalid URI {} has been specified",
					pathUriWithRelative.toString());
			throw new IllegalArgumentException(pathUriWithRelative.getScheme()
					+ " is not a valid scheme of URI for SCAnalyzer");
		}

		repository = SVNRepositoryFactory.create(tmpUrl);
		SVNURL location = repository.getLocation();
		location = location.setPath(
				pathUriWithoutRelative.getPath().toString(), true);
		repository.setLocation(location, false);

		this.url = location;
		this.relativePath = "/" + pathUriWithRelative.toString().substring(
				pathUriWithoutRelative.toString().length());

		logger.trace("the repository has been successfully initialized");
		logger.trace("path: " + this.path);
		logger.trace("relative path: " + this.relativePath);
		logger.trace("language: " + language.toString());
		logger.trace("URL: " + this.url.toString());
	}

	/**
	 * Get the path of the repository under managed
	 * 
	 * @return the path of the repository
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get the relative path under considered
	 * 
	 * @return the relative path
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * Get the URL of the repository under managed
	 * 
	 * @return the URL of the repository
	 */
	public SVNURL getUrl() {
		return url;
	}

	/**
	 * Get the instance of the repository under managed
	 * 
	 * @return the instance of the repository
	 */
	public SVNRepository getRepository() {
		return repository;
	}

	/**
	 * Get the list of all the relevant files in the specified revision under
	 * the relative path
	 * 
	 * @param revisionNum
	 *            the revision number to be targeted
	 * @return a list having all the relevant files in the specified revision
	 * @throws SVNException
	 *             If any error occurred when connecting the repository
	 */
	public synchronized List<String> getListOfRelevantFiles(
			final long revisionNum) throws SVNException {
		return getListOfRelevantFiles(revisionNum, this.relativePath);
	}

	/**
	 * Get the list of relevant files in the specified revision whose path start
	 * with the given path. The returned paths will be the path to the file from
	 * the root of the repository.
	 * 
	 * @param revisionNum
	 *            the revision number to be targeted
	 * @param targetPath
	 *            the path from which the relative files will be detected
	 * @return a list having all the relevant files in the specified revision
	 * @throws SVNException
	 *             If any error occurred when connecting the repository
	 */
	public synchronized List<String> getListOfRelevantFiles(
			final long revisionNum, final String targetPath)
			throws SVNException {
		assert targetPath != null;
		logger.trace("start getting the list of source files in revision "
				+ revisionNum);
		final SVNClientManager clientManager = SVNClientManager.newInstance();
		final SVNLogClient logClient = clientManager.getLogClient();

		final List<String> result = new ArrayList<String>();

		final SVNURL targetUrl = this.url.appendPath(targetPath, false);
		String fixedTargetPath = targetPath;
		if (!fixedTargetPath.startsWith("/")) {
			fixedTargetPath = "/" + fixedTargetPath;
		}
		if (fixedTargetPath.endsWith("/")) {
			fixedTargetPath = fixedTargetPath.substring(0,
					fixedTargetPath.lastIndexOf("/"));
		}
		final String finalFixedTargetPath = fixedTargetPath;

		logClient.doList(targetUrl, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), true, SVNDepth.INFINITY,
				SVNDirEntry.DIRENT_ALL, new ISVNDirEntryHandler() {

					@Override
					public void handleDirEntry(SVNDirEntry dirEntry)
							throws SVNException {
						final String path = dirEntry.getRelativePath();

						if (language.isRelevantFile(path)) {
							logger.trace(finalFixedTargetPath + "/" + path
									+ " has been identified as a relevant file");
							result.add(finalFixedTargetPath + "/" + path);
						}
					}

				});

		clientManager.dispose();

		logger.trace(result.size()
				+ " files have been detected as relevant files");

		return Collections.unmodifiableList(result);
	}

	/**
	 * Get all logs of the commit to the specified revision
	 * 
	 * @param revisionNum
	 *            the revision to be targeted
	 * @return a collection of entries of log between the specified revision and
	 *         the last revision
	 * @throws SVNException
	 */
	@SuppressWarnings("unchecked")
	public synchronized Collection<SVNLogEntry> getLog(final long revisionNum)
			throws SVNException {
		return this.repository.log(new String[] { this.relativePath }, null,
				revisionNum, revisionNum, true, true);
	}

}
