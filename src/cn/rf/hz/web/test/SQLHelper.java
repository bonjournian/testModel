package cn.rf.hz.web.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLHelper {

	public static void select(String sql) {
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtil.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				System.out.println(rs.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.release(rs, st, conn);
		}
	}

}
