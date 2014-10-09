package jp.ac.osaka_u.ist.sdl.c20r.csv_analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CSVAnalyzerResultWriter {

	private final String outputFilePath;

	private final Map<Integer, String> toPrint;

	public CSVAnalyzerResultWriter(final String outputFilePath,
			final Map<Integer, String> toPrint) {
		this.outputFilePath = outputFilePath;
		this.toPrint = toPrint;
	}

	public void write() {
		try {

			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
					new File(outputFilePath))));

			pw.println("Revision Number, # of Clone Sets, # of Clone Elements, # of Disappeared Clone Sets, # of Disappeared Clone Elements, # of Disappeared Clone Sets Including File Deletions, # of Disappeared Clone Sets Not Including File Deletions, # of Disappeared Clone Sets Both Including & Not Including File Deletions, # of Moved Blocks");

			for (Map.Entry<Integer, String> entry : toPrint.entrySet()) {
				pw.println(entry.getValue());
			}

			pw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
