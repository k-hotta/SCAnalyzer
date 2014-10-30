package jp.ac.osaka_u.ist.sdl.scanalyzer.io.in;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.scanalyzer.data.SourceFile;
import jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token;
import jp.ac.osaka_u.ist.sdl.scanalyzer.io.Language;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerFactory;

/**
 * This class parsing the given source file and transform it to a list of
 * tokens.
 * 
 * @author k-hotta
 * 
 * @see jp.ac.osaka_u.ist.sdl.scanalyzer.data.Token
 */
public class TokenSourceFileParser implements ISourceFileParser<Token> {

	/**
	 * The logger
	 */
	private static final Logger logger = LogManager
			.getLogger(TokenSourceFileParser.class);

	/**
	 * The logger for errors
	 */
	private static final Logger eLogger = LogManager.getLogger("error");

	private final Language language;

	public TokenSourceFileParser(final Language language) {
		this.language = language;
	}

	@Override
	public Map<Integer, Token> parse(SourceFile sourceFile, String contents) {
		if (sourceFile == null) {
			eLogger.fatal("cannot parse the given souceFile with TokenSourceFileParser: sourceFile must not be null");
			throw new IllegalArgumentException("sourceFile is null");
		}

		if (sourceFile.getPath() == null) {
			eLogger.fatal("cannot parse the given souceFile with TokenSourceFileParser: the path of sourceFile must not be null");
			throw new IllegalArgumentException(
					"the path of the source file is null: it doesn't seem to have been refreshed");
		}

		if (contents == null) {
			eLogger.fatal("cannot parse the given souceFile with TokenSourceFileParser: the content of sourceFile must not be null");
			throw new IllegalArgumentException("content is null");
		}

		final Map<Integer, Token> result = new TreeMap<Integer, Token>();
		int count = 0;

		IScanner scanner = null;

		try {
			scanner = ScannerFactory.newLenientScanner(
					language.getCorrespondingELanguage(), contents,
					((Long) sourceFile.getId()).toString());
			logger.trace("start parsing " + sourceFile.getPath());

			IToken token = scanner.getNextToken();
			while (token.getType() != ETokenType.EOF) {
				final Token content = new Token(token, sourceFile, ++count);
				result.put(count, content);
				token = scanner.getNextToken();
			}

		} catch (Exception e) {
			eLogger.fatal("cannot parse souceFile" + sourceFile.getPath()
					+ " with TokenSourceFileParser");
			throw new IllegalStateException(e);
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		logger.trace(result.size() + " tokens have been detected from "
				+ sourceFile.getPath());

		return result;
	}

}
