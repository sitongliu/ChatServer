package com.lst.dao;

import com.lst.common.MyConnection;
import com.lst.entity.LoginBean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDao {
	MyConnection myConn = new MyConnection();
	
	/**
	 * 添加一条记录
	 * @param Login
	 * @return
	 * @throws SQLException 
	 */
	public int insert( LoginBean login ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String strSql = " INSERT INTO Logins " +
							" VALUES ( ?, ? ) ";
			conn = myConn.getConnection();
			ps = conn.prepareStatement(strSql);
			ps.setString(1, login.getLoginName());
			ps.setString(2, login.getPassword());
			
			return ps.executeUpdate();
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeStatementAndConnection(ps, conn);
		}
	}
	
	/**
	 * 使用JDBC事务编程进行批量添加，都添加或都不添加
	 * @param Logins
	 * @return
	 */
	public int insert(List<LoginBean> logins) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		int count = 0;
		
		try {
			conn = myConn.getConnection();
			conn.setAutoCommit(false);
			
			for (LoginBean login: logins) {
				String strSql = " INSERT INTO Logins " +
								" VALUES ( ?, ? ) ";

				ps = conn.prepareStatement(strSql);
				ps.setString(1, login.getLoginName());
				ps.setString(2, login.getPassword());
					
				count += ps.executeUpdate();
			}
			conn.commit();
			return count;
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeStatementAndConnection(ps, conn);
		}
	}
	
	/**
	 * 未使用JDBC事务编程进行批量添加，尽力添加
	 * @param Logins
	 * @return
	 */
	public int insert2(List<LoginBean> logins) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		int count = 0;
		
		try {
			for (LoginBean login: logins) {
				String strSql = " INSERT INTO Logins " +
								" VALUES ( ?, ? ) ";
				conn = myConn.getConnection();

				try {
					ps = conn.prepareStatement(strSql);
					ps.setString(1, login.getLoginName());
					ps.setString(2, login.getPassword());
					
					count += ps.executeUpdate();
				} catch (SQLException ex) { }
			}
			return count;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeStatementAndConnection(ps, conn);
		}
	}
	
	/**
	 * 按主键删除
	 */
	public int delete( String loginName ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String strSql = " DELETE FROM Logins " +
							" WHERE LoginName = ? ";
			conn = myConn.getConnection();
			ps = conn.prepareStatement(strSql);
			ps.setString(1, loginName);
			
			return ps.executeUpdate();
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeStatementAndConnection(ps, conn);
		}
	}

	/**
	 * 批量删除，按主键列表删除
	 * @param LoginNumbers
	 * @return
	 */
	public int delete( String [] loginNames ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String strLoginNames = "' '";
			for (int i = 0; loginNames != null && i < loginNames.length; i++)
				strLoginNames += ", '" + loginNames + "'";
			
			String strSql = " DELETE FROM Logins " +
							" WHERE LoginName IN ( " + strLoginNames + " ) ";
			conn = myConn.getConnection();
			ps = conn.prepareStatement(strSql);
			
			return ps.executeUpdate();
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeStatementAndConnection(ps, conn);
		}
	}

	/**
	 * 修改，可修改主键
	 * @param Login 修改后的对象信息
	 * @param LoginNumber 原主键值
	 * @return
	 */
	public int update( LoginBean login, String loginName ) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			String strSql = " UPDATE Logins " +
							" SET LoginName = ?, Password = ? " +
							" WHERE LoginName = ? ";
			conn = myConn.getConnection();
			ps = conn.prepareStatement(strSql);
			ps.setString(1, login.getLoginName());
			ps.setString(2, login.getPassword());
			ps.setString(3, loginName);
			
			return ps.executeUpdate();
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeStatementAndConnection(ps, conn);
		}
	}
	
	/**
	 * 主键获取一条数据
	 * @return
	 */
	public LoginBean select(String loginName) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String strSql = " SELECT * " +
							" FROM Logins " +
							" WHERE LoginName = ? ";
			LoginBean login = null;
			conn = myConn.getConnection();
			ps = conn.prepareStatement(strSql);
			ps.setString(1, loginName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				login = new LoginBean();
				login.setLoginName(rs.getString(1));
				login.setPassword(rs.getString(2));
			}
			return login;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeResultSetAndStatementAndConnection(rs, ps, conn);
		}
	}
	
	/**
	 * 获取所有数据
	 * @return
	 * @throws SQLException 
	 */
	public List<LoginBean> selectAll() throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<LoginBean> logins = new ArrayList<LoginBean>();
		
		try {
			String strSql = " SELECT * " +
							" FROM Logins ";
			conn = myConn.getConnection();
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();
			while (rs.next()) {
				LoginBean 
				login = new LoginBean();
				login.setLoginName(rs.getString(1));
				login.setPassword(rs.getString(2));
				
				logins.add(login);	// 
			}
			return logins;
		} catch (SQLException e) {
			// e.printStackTrace();
			throw e;
		} finally {
			myConn.closeResultSetAndStatementAndConnection(rs, ps, conn);
		}
	}
	
}







