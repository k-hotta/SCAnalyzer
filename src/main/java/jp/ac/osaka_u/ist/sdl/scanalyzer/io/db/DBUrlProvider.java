package jp.ac.osaka_u.ist.sdl.scanalyzer.io.db;

import java.net.URI;
import java.net.URISyntaxException;

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

	public static final String getUrl(final DBMS dbms, final String path)
			throws URISyntaxException {
		final String driverStr = dbms.getDriverStr();
		final URI uri;
		try {
			uri = new URI(driverStr + path);
		} catch (URISyntaxException e) {
			eLogger.fatal("cannot parse the given path of database: "
					+ driverStr + path);
			throw e;
		}
		
		return uri.toString();
	}

}
