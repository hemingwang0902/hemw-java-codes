package com.hemw.jdbc.sample.dao.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hemw.jdbc.JdbcTemplate;
import com.hemw.jdbc.sample.beans.User;
import com.hemw.jdbc.sample.dao.UserDao;
import com.hemw.jdbc.sample.rowmapper.UserRowMapper;
import com.hemw.jdbc.utils.JdbcUtils;

public class UserDaoImpl implements UserDao {
	static final Logger log = Logger.getLogger(UserDaoImpl.class);
	
	public int insert(User user) {
		String sql = "insert into user(username, birthday) values(?, ?)";
		return JdbcTemplate.update(sql, new Object[] { user.getUserName(), user.getBirthday() });
	}

	public void insertOrUpdateById(List<User> users) {
		String sql = "update user u set u.username=?, u.birthday=? where u.id=?";
		Object[][] params = new Object[users.size()][];
		for (int i = 0; i < params.length; i++) {
			User user = users.get(i);
			params[i] = new Object[] { user.getUserName(), user.getBirthday(),
					user.getId() };
		}

		Connection conn = JdbcUtils.getConn();
		JdbcUtils.beginTransaction(conn); // 开始事务
		try {
			int[] results = JdbcTemplate.batchUpdate(sql, params, conn); // 更新

			List<User> insertList = new ArrayList<User>();
			for (int i = 0; i < results.length; i++) {
				if (results[i] == 0) { // 等于 0 表示对应的记录没有被更新，即表中没有对应的记录
					insertList.add(users.get(i));
				}
			}

			sql = "insert into user(username, birthday) values(?, ?)";
			params = new Object[insertList.size()][];
			for (int i = 0; i < params.length; i++) {
				User user = insertList.get(i);
				params[i] = new Object[] { user.getUserName(),
						user.getBirthday() };
			}
			JdbcTemplate.batchUpdate(sql, params, conn); // 将搜索历史表中没有的分词插入到表中

			JdbcUtils.commit(conn); // 提交事务
		} catch (Exception e) {
			JdbcUtils.rollback(conn); // 回滚事务
		}
	}

	public User selectById(String id) {
		String sql = "select u.id, u.username, u.birthday from user u where u.id=?";
		;
		Object[] params = new Object[] { id };
		List<User> result = JdbcTemplate.queryForList(sql, params, new UserRowMapper());
		if (result.size() == 0)
			return null;
		return result.get(0);
	}

}
