package jp.ac.osaka_u.ist.sdl.c20r.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

	private static DBConnection SINGLETON = null;

	private Connection connection;

	private DBConnection(final String dbPath) {
		try {
			Class.forName("org.sqlite.JDBC");
			this.connection = DriverManager.getConnection("jdbc:sqlite:"
					+ dbPath);
			this.connection.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * インスタンスを生成
	 * 
	 * @param dbPath
	 */
	public static void createInstance(final String dbPath) {
		if (SINGLETON == null) {
			SINGLETON = new DBConnection(dbPath);
		}
	}

	/**
	 * インスタンスを取得
	 * 
	 * @return
	 */
	public static DBConnection getInstance() {
		return SINGLETON;
	}

	public void close() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Statement createStatement() {
		Statement result = null;
		try {
			result = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public PreparedStatement createPreparedStatement(String queue) {
		PreparedStatement result = null;
		try {
			result = connection.prepareStatement(queue);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void commit() {
		try {
			connection.commit();
		} catch (SQLException e1) {
			try {
				connection.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
	}

	public void setAutoCommit(boolean autoCommit) {
		try {
			connection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
