package org.rda.QueryStore.dao;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class DBConnectionBuilder {
	private static String configuration = "jdbc:mysql://127.0.0.1:3306/query_store?useEncoding=true&useUnicode=true&characterEncoding=UTF-8";
	private static String dbUser = "dbUserIdentity";
	private static String dbPassword = "passwordForUser";

	private static final DBConnectionBuilder instance = new DBConnectionBuilder();

	private DBConnectionBuilder() {

	}

	public static DBConnectionBuilder getInstance() {
		return instance;
	}

	public Connection getConnection() throws SQLException,
			ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection conn = (Connection) DriverManager.getConnection(
				configuration, dbUser, dbPassword);
		conn.setReadOnly(false);
		conn.setAutoCommit(false);
		return conn;
	}
}
