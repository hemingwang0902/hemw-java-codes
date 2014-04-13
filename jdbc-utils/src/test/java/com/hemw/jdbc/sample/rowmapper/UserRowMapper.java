package com.hemw.jdbc.sample.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hemw.jdbc.rowmapper.RowMapper;
import com.hemw.jdbc.sample.beans.User;

public class UserRowMapper implements RowMapper<User> {

	public User mapRow(ResultSet rs, int rowIndex) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id")); // ID
		user.setUserName(rs.getString("username"));
		user.setBirthday(rs.getDate("birthday"));
		return user;
	}
}
