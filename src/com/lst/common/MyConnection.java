package com.lst.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyConnection {
	//private static final String URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
	private static final String URL = "jdbc:mysql://127.0.0.1:3306/users?useUnicode= true &characterEncoding = utf-8";
	// 静态块，在加载类时运行一次（相当于类的构造方法）。
	static {
		try {
			/*Class.forName("oracle.jdbc.OracleDriver");*/
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			System.out.println("驱动包未导入或者驱动类名错误！");
		}
	}

	public  Connection getConnection() throws SQLException {
		try {
			return DriverManager.getConnection(URL, "root", "root");
		} catch (SQLException e) {
			// No suitable driver found for jdbc:oracle:thin:@127.0.0.1:1521:orcl 建立连接时驱动类未加载到JVM
			if (e.getMessage().indexOf("No suitable driver found for") >= 0)
				System.out.println("建立连接时驱动类未加载到JVM");
				// Io 异常: The Network Adapter could not establish the connection 服务器未启动或服务未启动
			else if (e.getMessage().indexOf("Io 异常: The Network Adapter could not establish the connection") >= 0)
				System.out.println("服务器未启动或服务未启动");
				// Listener refused the connection with the following error: ORA-12505... 数据库名称错误或其他原因
			else if (e.getMessage().indexOf("Listener refused the connection with the following error:") >= 0)
				System.out.println("数据库名称错误或其他原因");
				// ORA-01017: invalid username/password; logon denied 用户名或密码错误
			else if (e.getMessage().indexOf("ORA-01017") >= 0)
				System.out.println("用户名或密码错误");
			else
				e.printStackTrace();

			throw e;
		}
	}

	public void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void closeStatement(Statement st) {
		try {
			if (st != null)
				st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void closeResultSet(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeStatementAndConnection(Statement st, Connection conn) {
		closeStatement(st);
		closeConnection(conn);
	}

	public void closeResultSetAndStatementAndConnection(ResultSet rs,
														Statement st, Connection conn) {
		closeResultSet(rs);
		closeStatement(st);
		closeConnection(conn);

	}

}
