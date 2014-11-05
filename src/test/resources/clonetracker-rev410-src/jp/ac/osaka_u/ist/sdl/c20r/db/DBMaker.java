package jp.ac.osaka_u.ist.sdl.c20r.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * データベースを構築するクラス <br>
 * IDの連番機能が SQLite 依存の実装になっている
 * 
 * @author k-hotta
 * 
 */
public class DBMaker {

	public static void main(String[] args) {
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

			// リビジョンテーブルを作成
			{
				final StringBuilder revision = new StringBuilder();
				revision.append("create table REVISION(");
				revision.append("REVISION_ID LONG PRIMARY KEY,");
				revision.append("REVISION_NUM INTEGER UNIQUE,");
				revision.append("FILES_IN_REVISION INTEGER CHECK (FILES_IN_REVISION >= 0),");
				revision.append("BLOCKS_IN_REVISION INTEGER CHECK (BLOCKS_IN_REVISION >= 0),");
				revision.append("CLONESETS_IN_REVISION INTEGER CHECK (CLONESETS_IN_REVISION >= 0)");
				revision.append(")");
				final Statement statement = connection.createStatement();
				statement.executeUpdate(revision.toString());
				statement.close();
			}

			// ファイルテーブルを作成
			{
				final StringBuilder file = new StringBuilder();
				file.append("create table FILE(");
				file.append("FILE_ID LONG PRIMARY KEY,");
				file.append("FILE_NAME TEXT NOT NULL,");
				file.append("FILE_PATH TEXT NOT NULL");
				file.append(")");
				final Statement statement = connection.createStatement();
				statement.executeUpdate(file.toString());
				statement.close();
			}

			{
				final Statement statement = connection.createStatement();
				statement
						.executeUpdate("create index FILE_ID_INDEX_FILE on FILE(FILE_ID)");
				statement.close();
			}

			// ブロックテーブルを作成
			{
				final StringBuilder block = new StringBuilder();
				block.append("create table BLOCK(");
				block.append("BLOCK_ID LONG PRIMARY KEY,");
				block.append("FILE_ID LONG,");
				block.append("PARENT_UNIT_ID LONG,");
				// block.append("CLONESET_ID LONG,");
				block.append("START_REV_ID LONG,");
				block.append("END_REV_ID LONG,");
				block.append("BLOCK_TYPE TEXT NOT NULL,");
				block.append("BLOCK_START INTEGER CHECK (BLOCK_START >= 0),");
				block.append("BLOCK_END INTEGER CHECK (BLOCK_END >= 0),");
				block.append("BLOCK_HASH INTEGER,");
				block.append("BLOCK_CRD TEXT NOT NULL,");
				block.append("BLOCK_CM INTEGER CHECK (BLOCK_CM > 0),");
				block.append("FILE_DELETE INTEGER CHECK (FILE_DELETE >= 0),");
				block.append("BLOCK_LENGTH INTEGER CHECK (BLOCK_LENGTH > 0),");
				block.append("BLOCK_CC INTEGER CHECK (BLOCK_CC >= 0),");
				block.append("BLOCK_FO INTEGER CHECK (BLOCK_FO >= 0),");
				block.append("BLOCK_DISCRIMINATOR TEXT NOT NULL,");
				block.append("BLOCK_ADDED INTEGER CHECK (BLOCK_ADDED >= 0),");
				block.append("BLOCK_DELETED INTEGER CHECK (BLOCK_DELETED >= 0),");
				block.append("ROOT_CLASS_NAME TEXT NOT NULL,");
				block.append("ROOT_METHOD_NAME TEXT NOT NULL,");
				block.append("ROOT_METHOD_PARAMS TEXT NOT NULL,");
				block.append("CRD_AFTER_ROOT_METHOD TEXT NOT NULL");
				block.append(")");
				final Statement statement = connection.createStatement();
				statement.executeUpdate(block.toString());
				statement.close();
			}

			{
				final Statement statement = connection.createStatement();
				statement
						.executeUpdate("create index BLOCK_ID_INDEX_BLOCK on BLOCK(BLOCK_ID)");
				statement
						.executeUpdate("create index FILE_ID_INDEX_BLOCK on BLOCK(FILE_ID)");
				// statement
				// .executeUpdate("create index CLONESET_ID_INDEX_BLOCK on BLOCK(CLONESET_ID)");
				statement
						.executeUpdate("create index PARENT_UNIT_ID_INDEX_BLOCK on BLOCK(PARENT_UNIT_ID)");
				statement
						.executeUpdate("create index BLOCK_HASH_INDEX on BLOCK(BLOCK_HASH)");
				statement
						.executeUpdate("create index BLOCK_CM_INDEX_BLOCK on BLOCK(BLOCK_CM)");
				statement
						.executeUpdate("create index START_REV_ID_INDEX_BLOCK on BLOCK(START_REV_ID)");
				statement
						.executeUpdate("create index END_REV_ID_INDEX_BLOCK on BLOCK(END_REV_ID)");
				statement
						.executeUpdate("create index START_REV_ID_HASH_INDEX_BLOCK on BLOCK(START_REV_ID,BLOCK_HASH)");
				statement
						.executeUpdate("create index START_END_REV_ID_INDEX_BLOCK on BLOCK(START_REV_ID,END_REV_ID)");
				statement
						.executeUpdate("create index FILE_DELETE_INDEX_BLOCK on BLOCK(FILE_DELETE)");
				statement
						.executeUpdate("create index BLOCK_ADDED_INDEX_BLOCK on BLOCK(BLOCK_ADDED)");
				statement
						.executeUpdate("create index BLOCK_DELETED_INDEX_BLOCK on BLOCK(BLOCK_DELETED)");
				statement.close();
			}

			// クローンセットテーブルを作成
			{
				final StringBuilder cloneset = new StringBuilder();
				cloneset.append("create table CLONESET(");
				cloneset.append("CLONESET_ID LONG PRIMARY KEY,");
				cloneset.append("CLONESET_ELEMENTS_COUNT INTEGER,");
				cloneset.append("CLONESET_REV_ID LONG,");
				cloneset.append("CLONESET_HASH INTEGER,");
				cloneset.append("CLONESET_ELEMENTS STRING NOT NULL");
				cloneset.append(")");
				final Statement statement = connection.createStatement();
				statement.executeUpdate(cloneset.toString());
				statement.close();
			}

			{
				final Statement statement = connection.createStatement();
				statement
						.executeUpdate("create index CLONESET_ID_INDEX_CLONESET on CLONESET(CLONESET_ID)");
				statement
						.executeUpdate("create index CLONESET_HASH_INDEX_CLONESET on CLONESET(CLONESET_HASH)");
				statement
						.executeUpdate("create index CLONESET_ELEMENTS_COUNT_INDEX_CLONESET on CLONESET(CLONESET_ELEMENTS_COUNT)");
				statement
						.executeUpdate("create index CLONESET_REV_ID_INDEX_CLONESET on CLONESET(CLONESET_REV_ID)");
				statement.close();
			}

			// クローンセットペアテーブルを作成
			{
				final StringBuilder clonesetpair = new StringBuilder();
				clonesetpair.append("create table CLONESETPAIR(");
				clonesetpair.append("CLONESETPAIR_ID LONG PRIMARY KEY,");
				clonesetpair.append("CLONESETPAIR_BEFORE_REV_ID LONG,");
				clonesetpair.append("CLONESETPAIR_AFTER_REV_ID LONG,");
				clonesetpair.append("CLONESETPAIR_BEFORE_CLONESET_ID LONG,");
				clonesetpair.append("CLONESETPAIR_AFTER_CLONESET_ID LONG,");
				clonesetpair
						.append("CLONESETPAIR_HASH_CHANGED INTEGER CHECK (CLONESETPAIR_HASH_CHANGED >= 0),");
				clonesetpair
						.append("CLONESETPAIR_ADDED_ELEMENTS INTEGER CHECK (CLONESETPAIR_ADDED_ELEMENTS >= 0),");
				clonesetpair
						.append("CLONESETPAIR_DELETED_ELEMENTS INTEGER CHECK (CLONESETPAIR_DELETED_ELEMENTS >= 0),");
				clonesetpair
						.append("CLONESETPAIR_DELETED_IN_FILEDEL INTEGER CHECK (CLONESETPAIR_DELETED_IN_FILEDEL >= 0)");
				clonesetpair.append(")");
				final Statement statement = connection.createStatement();
				statement.executeUpdate(clonesetpair.toString());
				statement.close();
			}

			{
				final Statement statement = connection.createStatement();
				statement
						.executeUpdate("create index CLONESETPAIR_ID_INDEX_CLONESETPAIR on CLONESETPAIR(CLONESETPAIR_ID)");
				statement
						.executeUpdate("create index CLONESETPAIR_BEFORE_REV_ID_INDEX_CLONESETPAIR on CLONESETPAIR(CLONESETPAIR_BEFORE_REV_ID)");
				statement
						.executeUpdate("create index CLONESETPAIR_AFTER_REV_ID_INDEX_CLONESETPAIR on CLONESETPAIR(CLONESETPAIR_AFTER_REV_ID)");
				statement
						.executeUpdate("create index CLONESETPAIR_BEFORE_CLONESET_ID_INDEX_CLONESETPAIR on CLONESETPAIR(CLONESETPAIR_BEFORE_CLONESET_ID)");
				statement
						.executeUpdate("create index CLONESETPAIR_AFTER_CLONESET_ID_INDEX_CLONESETPAIR on CLONESETPAIR(CLONESETPAIR_AFTER_CLONESET_ID)");
				statement.close();
			}

			// genealogy テーブルを作成
			{
				final StringBuilder clonegenealogy = new StringBuilder();
				clonegenealogy.append("create table CLONEGENEALOGY(");
				clonegenealogy.append("CLONEGENEALOGY_ID LONG PRIMARY KEY,");
				clonegenealogy.append("CLONEGENEALOGY_START_REV_ID LONG,");
				clonegenealogy.append("CLONEGENEALOGY_END_REV_ID LONG,");
				clonegenealogy.append("CLONEGENEALOGY_PAIRS STRING NOT NULL,");
				clonegenealogy
						.append("CLONEGENEALOGY_HASH_CHANGED INTEGER CHECK (CLONEGENEALOGY_HASH_CHANGED >= 0),");
				clonegenealogy
						.append("CLONEGENEALOGY_ADDED_ELEMENTS INTEGER CHECK (CLONEGENEALOGY_ADDED_ELEMENTS >= 0),");
				clonegenealogy
						.append("CLONEGENEALOGY_DELETED_ELEMENTS INTEGER CHECK (CLONEGENEALOGY_DELETED_ELEMENTS >= 0),");
				clonegenealogy
						.append("CLONEGENEALOGY_DELETED_ELEMENTS_IN_FILEDEL INTEGER CHECK (CLONEGENEALOGY_DELETED_ELEMENTS_IN_FILEDEL >= 0),");
				clonegenealogy
						.append("CLONEGENEALOGY_ADDREV_COUNT INTEGER CHECK (CLONEGENEALOGY_ADDREV_COUNT >= 0),");
				clonegenealogy
						.append("CLONEGENEALOGY_DELREV_COUNT INTEGER CHECK (CLONEGENEALOGY_DELREV_COUNT >= 0),");
				clonegenealogy
						.append("CLONEGENEALOGY_DELREV_FILEDEL INTEGER CHECK (CLONEGENEALOGY_DELREV_FILEDEL >= 0)");
				clonegenealogy.append(")");
				final Statement statement = connection.createStatement();
				statement.executeUpdate(clonegenealogy.toString());
				statement.close();
			}

			{
				final Statement statement = connection.createStatement();
				statement
						.executeUpdate("create index CLONEGENEALOGY_ID_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_ID)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_START_REV_ID_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_START_REV_ID)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_END_REV_ID_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_END_REV_ID)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_HASH_CHANGED_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_HASH_CHANGED)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_ADDED_ELEMENTS_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_ADDED_ELEMENTS)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_DELETED_ELEMENTS_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_DELETED_ELEMENTS)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_DELETED_ELEMENTS_IN_FILEDEL_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_DELETED_ELEMENTS_IN_FILEDEL)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_ADDREV_COUNT_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_ADDREV_COUNT)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_DELREV_COUNT_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_DELREV_COUNT)");
				statement
						.executeUpdate("create index CLONEGENEALOGY_DELREV_FILEDEL_INDEX_CLONEGENEALOGY on CLONEGENEALOGY(CLONEGENEALOGY_DELREV_FILEDEL)");
				statement.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
