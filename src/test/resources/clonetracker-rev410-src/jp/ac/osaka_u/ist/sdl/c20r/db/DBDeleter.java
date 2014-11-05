package jp.ac.osaka_u.ist.sdl.c20r.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class DBDeleter {

	public static void main(String[] args) throws Exception {
		try {
			// コマンドライン引数を処理
			final Options options = new Options();

			{
				final Option d = new Option("d", "database", true, "database");
				d.setArgName("database");
				d.setArgs(1);
				d.setRequired(true);
				options.addOption(d);
			}

			final CommandLineParser parser = new PosixParser();
			final CommandLine cmd = parser.parse(options, args);

			// データベースを作成
			final StringBuilder url = new StringBuilder();
			url.append("jdbc:sqlite:");
			url.append(cmd.getOptionValue("d"));

			Class.forName("org.sqlite.JDBC");
			
			final Connection connection = DriverManager.getConnection(url
					.toString());

			drop(connection, "REVISION");
			drop(connection, "FILE");
			drop(connection, "BLOCK");
			//drop(connection, "CLONESET");

		} catch (Exception e) {
			e.printStackTrace();
			//System.exit(1);
			throw e;
		}
	}

	private static void drop(Connection connection, String tableName)
			throws Exception {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("drop table " + tableName);
		stmt.close();
	}

}
