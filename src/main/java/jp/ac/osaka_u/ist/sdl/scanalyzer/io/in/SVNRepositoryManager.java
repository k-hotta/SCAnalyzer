package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.util.SVNEncodingUtil;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

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
	private String path;

	/**
	 * The relative path to the SVN project <br>
	 * e.g. /trunk
	 */
	private String relativePath;

	/**
	 * The url of the repository under managed
	 */
	private SVNURL url;

	/**
	 * The instance of repository under managed
	 */
	private SVNRepository repository;

	public SVNRepositoryManager(final String path, final String relativePath)
			throws URISyntaxException, SVNException {
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

	public String getPath() {
		return path;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public SVNURL getUrl() {
		return url;
	}

	public SVNRepository getRepository() {
		return repository;
	}

}
