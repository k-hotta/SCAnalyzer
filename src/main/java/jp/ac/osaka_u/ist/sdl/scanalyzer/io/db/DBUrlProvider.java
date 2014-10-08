package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class to provide the URL of database with specified DBMS and path. <br>
 * 
 * @author k-hotta
 * 
 */
public class DBUrlProvider {

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	/**
	 * The private constructor to make it impossible to make any instance of
	 * this class.
	 */
	private DBUrlProvider() {

	}

	/**
	 * Get the URL of the database, which is created with the DBMS and path. <br>
	 * 
	 * @param dbms
	 *            the DBMS
	 * @param pathStr
	 *            the path of the database
	 * @return the string representation of the URL of the database
	 * @throws URISyntaxException
	 *             If the given path is illegal
	 */
	public static final String getUrl(final DBMS dbms, final String pathStr)
			throws URISyntaxException {
		final String driverStr = dbms.getDriverStr();
		final Path path = Paths.get(pathStr);
		final URI uri;
		try {
			uri = new URI(driverStr + path.toUri().getPath().toString());
		} catch (URISyntaxException e) {
			eLogger.fatal("cannot parse the given path of database: "
					+ driverStr + path);
			throw e;
		}

		return uri.toString();
	}

}
