package com.lst.business;

import com.lst.dao.LoginDao;
import com.lst.entity.LoginBean;

import java.sql.SQLException;
import java.util.List;

public class LoginManager {
	private LoginDao dao = new LoginDao();

	public boolean add(LoginBean login) {
		try {
			// 数据清理
			login.setLoginName(login.getLoginName().trim());

			// 数据验证

			return dao.insert(login) > 0;
		} catch (SQLException e) {
			if (e.getMessage().indexOf("PK_LOGINS") >= 0)
				throw new RuntimeException("登录名已存在，不能添加！");
			else
				throw new RuntimeException("添加时出错，原因：" + e.getMessage());
		}
	}

	public boolean remove(String loginName) {
		try {
			return dao.delete(loginName) > 0;
		} catch (SQLException e) {
			throw new RuntimeException("删除时出错，原因：" + e.getMessage());
		}
	}

	public boolean modify(LoginBean login, String loginName) {
		try {
			// 数据清理
			login.setLoginName(login.getLoginName().trim());

			// 数据验证

			return dao.update(login, loginName) > 0;
		} catch (SQLException e) {
			if (e.getMessage().indexOf("PK_LOGINS") >= 0)
				throw new RuntimeException("登录名已存在，不能修改！");
			else
				throw new RuntimeException("修改时出错，原因：" + e.getMessage());
		}
	}

	public LoginBean search(String loginName) {
		try {
			return dao.select(loginName);
		} catch (SQLException e) {
			throw new RuntimeException("查找时出错，原因：" + e.getMessage());
		}
	}

	public List<LoginBean> search() {
		try {
			return dao.selectAll();
		} catch (SQLException e) {
			throw new RuntimeException("查找时出错，原因：" + e.getMessage());
		}
	}

	public LoginBean isLogin(String loginName, String password) {
		if (loginName.compareTo("tom") == 0
				|| loginName.compareTo("jerry") == 0
				|| loginName.compareTo("guomy") == 0)
			return new LoginBean();
		return null;
//		LoginBean login = null;
//		login = search(loginName);
//		if (login != null && login.getPassword().compareTo(password) == 0) {
//			return login;
//		}
//		return null;
	}

}
