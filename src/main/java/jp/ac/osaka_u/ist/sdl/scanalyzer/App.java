package jp.ac.osaka_u.ist.sdl.scanalyzer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Logger logger = LogManager.getLogger(App.class);
        Logger eLogger = LogManager.getLogger("error");
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        eLogger.warn("warn");
        eLogger.error("error");
        eLogger.fatal("fatal");
    }
}
