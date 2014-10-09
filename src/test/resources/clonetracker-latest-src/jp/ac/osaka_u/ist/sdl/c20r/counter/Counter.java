package jp.ac.osaka_u.ist.sdl.c20r.counter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class Counter {

	private static int numberOfFile;
	private static int linesOfCode;

	public static void main(String[] args) {

		try {

			final String REVISION_FILE = args[0];
			final String REPOSITORY_PATH = args[1];
			final String WORKING_DIRECTORY = args[2];
			final String OUTPUT = args[3];

			final SVNURL url = SVNURL.parseURIDecoded("file:///"
					+ REPOSITORY_PATH);
			final SVNRepository repository = FSRepositoryFactory.create(url);
			FSRepositoryFactory.setup();
			final SVNUpdateClient updateClient = SVNClientManager.newInstance()
					.getUpdateClient();
			updateClient.setIgnoreExternals(false);

			final BufferedReader reader = new BufferedReader(new FileReader(
					REVISION_FILE));
			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					OUTPUT));
			writer.write("revision, nof, loc");
			writer.newLine();

			final Long firstRevision = Long.parseLong(reader.readLine());

			updateClient
					.doCheckout(url, new File(WORKING_DIRECTORY),
							SVNRevision.create(firstRevision),
							SVNRevision.create(firstRevision),
							SVNDepth.INFINITY, false);

			System.out.print("revision ");
			System.out.print(firstRevision.toString());
			System.out.println(" was checked out.");

			numberOfFile = 0;
			linesOfCode = 0;
			count(new File(WORKING_DIRECTORY));
			writer.write(firstRevision.toString());
			writer.write(", ");
			writer.write(Integer.toString(numberOfFile));
			writer.write(", ");
			writer.write(Integer.toString(linesOfCode));
			writer.newLine();

			while (reader.ready()) {

				final Long revision = Long.parseLong(reader.readLine());
				updateClient.doUpdate(new File(WORKING_DIRECTORY),
						SVNRevision.create(revision), SVNDepth.INFINITY, true,
						true);

				System.out.print("updated to ");
				System.out.print(revision.toString());
				System.out.println(".");

				numberOfFile = 0;
				linesOfCode = 0;
				count(new File(WORKING_DIRECTORY));
				writer.write(revision.toString());
				writer.write(", ");
				writer.write(Integer.toString(numberOfFile));
				writer.write(", ");
				writer.write(Integer.toString(linesOfCode));
				writer.newLine();
			}

			reader.close();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void count(File file) {

		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				count(child);
			}
		}

		else if (file.isFile()) {

			if (file.getName().endsWith(".java")) {
				numberOfFile++;
				try {
					final BufferedReader reader = new BufferedReader(
							new FileReader(file));
					while (reader.ready()) {
						linesOfCode++;
					}
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
