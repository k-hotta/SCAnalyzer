package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
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
		logger.trace("path: " + this.path);

		if (relativePath == null) {
			logger.trace("relative path: nothing has been specified");
			this.relativePath = "";
		} else if (relativePath.startsWith("/")) {
			logger.trace("relative path: " + relativePath);
			this.relativePath = relativePath;
		} else {
			eLogger.warn("the relative path doesn't start with \"/\"");
			eLogger.warn("the relative path will be replaced by the one having \"/\" in the head");
			logger.trace("relative path: /" + relativePath);
			this.relativePath = "/" + relativePath;
		}

		this.language = language;
		logger.trace("language: " + language.toString());

		URI pathUri = null;
		try {
			pathUri = new URI(this.path + this.relativePath);
		} catch (URISyntaxException e) {
			eLogger.warn(this.path + this.relativePath + " is not a valid URI");
			eLogger.warn("try to find " + this.path + this.relativePath
					+ " as a repository in local");
		}

		if (pathUri == null || pathUri.getScheme() == null) {
			// reset pathUri in case it is null or its scheme is null
			pathUri = Paths.get(this.path, this.relativePath).toUri();
		}

		final String autoEncodedUrl = SVNEncodingUtil.autoURIEncode(pathUri
				.toString());
		url = SVNURL.parseURIEncoded(autoEncodedUrl);

		if (pathUri.getScheme().equals("file")) {
			// file://***
			FSRepositoryFactory.setup();
		} else if (pathUri.getScheme().startsWith("http")) {
			// http://*** or https://***
			DAVRepositoryFactory.setup();
		} else if (pathUri.getScheme().startsWith("svn")) {
			// svn://*** or svn+xxx://***
			SVNRepositoryFactoryImpl.setup();
		} else {
			// otherwise is malformed
			eLogger.fatal("invalid URI {} has been specified",
					pathUri.toString());
			throw new IllegalArgumentException(pathUri.getScheme()
					+ " is not a valid scheme of URI for SCAnalyzer");
		}

		logger.trace("URL: " + url.toString());

		repository = SVNRepositoryFactory.create(url);
		logger.trace("the repository has been successfully initialized");
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
	 * Get the list of relative files in the specified revision
	 * 
	 * @param revisionNum
	 *            the revision number to be targeted
	 * @return a list having all the relative files in the specified revision
	 * @throws SVNException
	 *             If any error occurred when connecting the repository
	 */
	public synchronized List<String> getListOfRelativeFiles(
			final long revisionNum) throws SVNException {
		logger.trace("start getting the list of source files in revision"
				+ revisionNum);
		final SVNClientManager clientManager = SVNClientManager.newInstance();
		final SVNLogClient logClient = clientManager.getLogClient();

		final List<String> result = new ArrayList<String>();

		logClient.doList(url, SVNRevision.create(revisionNum),
				SVNRevision.create(revisionNum), true, SVNDepth.INFINITY,
				SVNDirEntry.DIRENT_ALL, new ISVNDirEntryHandler() {

					@Override
					public void handleDirEntry(SVNDirEntry dirEntry)
							throws SVNException {
						final String path = dirEntry.getRelativePath();

						if (language.isRelativeFile(path)) {
							logger.trace(path
									+ " has been identified as a relative file");
							result.add(path);
						}
					}

				});
		
		clientManager.dispose();
		
		logger.trace(result.size()
				+ " files have been detected as relative files");

		return Collections.unmodifiableList(result);
	}
}
