package jp.ac.osaka_u.ist.sdl.c20r;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Extractor3 {

	public static void main(final String[] args) {

		try {

			final BufferedReader reader = new BufferedReader(new FileReader(
					args[0]));
			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					args[1]));

			final List<String> cloneset = new ArrayList<String>();

			String previous_CLONE_ID = "0";
			boolean output = false;
			while (reader.ready()) {

				final String line = reader.readLine();

				if (line.startsWith("BEFORE") || line.startsWith("CLONE_ID")
						|| line.endsWith(",")) {
					continue;
				}

				final StringTokenizer tokenizer = new StringTokenizer(line, ",");
				final String CLONE_ID = tokenizer.nextToken();
				final String PATH = tokenizer.nextToken();
				final String START_LINE = tokenizer.nextToken();
				final String END_LINE = tokenizer.nextToken();
				final String LENGTH = tokenizer.nextToken();
				final String CC = tokenizer.nextToken();
				final String FO = tokenizer.nextToken();
				final String DISAPPEAR = tokenizer.nextToken();

				if(!DISAPPEAR.equals("0")){
					System.out.println(line);
					writer.write(line);	
					writer.newLine();
				}
			}

			reader.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
