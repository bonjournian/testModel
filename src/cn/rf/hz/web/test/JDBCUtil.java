package cn.rf.hz.web.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtil {
	private static String driver, url, username, password;
	private static Connection conn = null;
	static {
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(new File(JDBCUtil.class.getClassLoader()
					.getResource("reformer_db.properties")
					.getPath())));
			driver = prop.getProperty("driver");
			url = prop.getProperty("url");
			username = prop.getProperty("username");
			password = prop.getProperty("password");
			Class.forName(driver);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void release(ResultSet res, Statement stat, Connection conn) {
		if (res != null) {
			try {
				res.close();
				res = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stat != null) {
			try {
				stat.close();
				stat = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
