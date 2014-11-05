package jp.ac.osaka_u.ist.sdl.c20r.rev_analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * ファイルの中身を読んでくるクラス
 * 
 * @author k-hotta
 * 
 */
public class FileContentReader {

	public static String readFile(final String path) {
		final StringBuilder builder = new StringBuilder();
		final String separator = System.getProperty("line.separator");
		
		try {
			final BufferedReader br = new BufferedReader(new FileReader(
					new File(path)));
			String line;
			
			while ((line = br.readLine()) != null) {
				builder.append(line + separator);
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
}
