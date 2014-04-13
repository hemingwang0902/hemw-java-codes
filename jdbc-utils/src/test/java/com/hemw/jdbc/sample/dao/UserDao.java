package com.hemw.jdbc.sample.dao;

import java.util.List;

import com.hemw.jdbc.sample.beans.User;

/**
 * 用户DAO <br>
 * <b>创建日期</b>：2011-1-7
 * 
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public interface UserDao {
	/**
	 * 新增一条用户记录 <br>
	 * <b>创建日期</b>：2011-1-7
	 * 
	 * @param user
	 * @return
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	int insert(User user);

	/**
	 * 更新或修改用户<br>
	 * <b>创建日期</b>：2011-1-13
	 * 
	 * @param user
	 *            需要更新或新增的用户列表
	 * @return
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	void insertOrUpdateById(List<User> users);

	/**
	 * 根据用户ID查询用户 <br>
	 * <b>创建日期</b>：2011-1-7
	 * 
	 * @param id
	 *            用户ID
	 * @return 如果查询到了对应记录，则返回用户对象，否则返回<code>null</code>
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	User selectById(String id);

}
