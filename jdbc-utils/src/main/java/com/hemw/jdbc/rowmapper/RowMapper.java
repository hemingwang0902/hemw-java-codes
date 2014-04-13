package com.hemw.jdbc.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 查询结果集行映射接口
 * <br><b>创建日期</b>：2011-1-7
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public interface RowMapper<E> {

	/**
	 * 将数据库中的一行数据转换为对应的 JavaBean 对象
	 * <br><b>创建日期</b>：2010-11-15
	 * @param rs 数据库查询结果集
	 * @param rowIndex 行索引（从 0  开始）
	 * @return 转换后的 JavaBean 对象
	 * @throws SQLException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public abstract E mapRow(ResultSet rs, int rowIndex) throws SQLException;
}
