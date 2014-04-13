package com.hemw.jdbc.utils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

/**
 * JDBC 工具类
 * <br><b>创建日期</b>：2010-11-15
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public abstract class JdbcUtils {
	/**
	 * Constant that indicates an unknown (or unspecified) SQL type.
	 * @see java.sql.Types
	 */
	public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;
	
	private static final Logger log = Logger.getLogger(JdbcUtils.class);
	
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF8";
	public static final String DB_USERNAME = "root";
	public static final String DB_PASSWORD = "root"; 
	
	static {
		 try {
			Class.forName(DB_DRIVER).newInstance();
		} catch (Exception e) {
			log.error("注册数据库驱动失败！", e);
		}
	}
	
	public static Connection getConn(){
		try {
			return java.sql.DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException e) {
			log.error("获取数据库连接失败！", e);
		}
		return null;
	}
	
	/**
	 * 关闭数据库连接
	 * <br><b>创建日期</b>：2010-11-15
	 * @param con 需要关闭的数据库连接
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static void closeConnection(Connection con) {
		if (con == null)
			return;
		try {
			con.close();
		} catch (SQLException ex) {
			log.debug("Could not close JDBC Connection", ex);
		} catch (Throwable ex) {
			log.debug("Unexpected exception on closing JDBC Connection", ex);
		}
	}

	/**
	 * 关闭数据库执行语句声明
	 * <br><b>创建日期</b>：2010-11-15
	 * @param stmt 需要关闭的数据库执行语句声明
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static void closeStatement(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (SQLException ex) {
			log.fatal("Could not close JDBC Statement", ex);
		} catch (Throwable ex) {
			log.fatal("Unexpected exception on closing JDBC Statement", ex);
		}
	}

	/**
	 * 关闭数据库查询结果集
	 * <br><b>创建日期</b>：2010-11-15
	 * @param rs 需要关闭的数据库查询结果集
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs == null)
			return;
		try {
			rs.close();
		} catch (SQLException ex) {
			log.fatal("Could not close JDBC ResultSet", ex);
		} catch (Throwable ex) {
			log.fatal("Unexpected exception on closing JDBC ResultSet", ex);
		}
	}

	/**
	 * 释放数据库连接资源
	 * @param rs
	 * @param stmt
	 * @param con
	 */
	public static void release(ResultSet rs, Statement stmt, Connection con) {
		closeResultSet(rs);
		closeStatement(stmt);
		closeConnection(con);
	}
	
	/**
	 * 获取数据库查询结果集指定列的数据，转换为对应类型后返回，
	 * 如果 <code>requiredType</code> 为  <code>null</code>，则直接调用 <code>getResultSetValue(ResultSet, int)</code>
	 * <br><b>创建日期</b>：2010-11-15
	 * @param rs 数据库查询结果集
	 * @param index 列序号（从 1 开始）
	 * @param requiredType 数据类型
	 * @return 转换成对应数据类型后的数据库查询结果集指定列的数据
	 * @throws SQLException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #getResultSetValue(ResultSet, int)
	 */
	public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
		if (requiredType == null) {
			return getResultSetValue(rs, index);
		}

		Object value = null;
		boolean wasNullCheck = false;

		if (String.class.equals(requiredType)) {
			value = rs.getString(index);
		} else if ((Boolean.TYPE.equals(requiredType)) || (Boolean.class.equals(requiredType))) {
			value = new Boolean(rs.getBoolean(index));
			wasNullCheck = true;
		} else if ((Byte.TYPE.equals(requiredType)) || (Byte.class.equals(requiredType))) {
			value = new Byte(rs.getByte(index));
			wasNullCheck = true;
		} else if ((Short.TYPE.equals(requiredType)) || (Short.class.equals(requiredType))) {
			value = new Short(rs.getShort(index));
			wasNullCheck = true;
		} else if ((Integer.TYPE.equals(requiredType)) || (Integer.class.equals(requiredType))) {
			value = new Integer(rs.getInt(index));
			wasNullCheck = true;
		} else if ((Long.TYPE.equals(requiredType)) || (Long.class.equals(requiredType))) {
			value = new Long(rs.getLong(index));
			wasNullCheck = true;
		} else if ((Float.TYPE.equals(requiredType)) || (Float.class.equals(requiredType))) {
			value = new Float(rs.getFloat(index));
			wasNullCheck = true;
		} else if ((Double.TYPE.equals(requiredType)) || (Double.class.equals(requiredType))
				|| (Number.class.equals(requiredType))) {
			value = new Double(rs.getDouble(index));
			wasNullCheck = true;
		} else if (byte[].class.equals(requiredType)) {
			value = rs.getBytes(index);
		} else if (java.sql.Date.class.equals(requiredType)) {
			value = rs.getDate(index);
		} else if (Time.class.equals(requiredType)) {
			value = rs.getTime(index);
		} else if ((Timestamp.class.equals(requiredType)) || (java.util.Date.class.equals(requiredType))) {
			value = rs.getTimestamp(index);
		} else if (BigDecimal.class.equals(requiredType)) {
			value = rs.getBigDecimal(index);
		} else if (Blob.class.equals(requiredType)) {
			value = rs.getBlob(index);
		} else if (Clob.class.equals(requiredType)) {
			value = rs.getClob(index);
		} else {
			value = getResultSetValue(rs, index);
		}

		if ((wasNullCheck) && (value != null) && (rs.wasNull())) {
			value = null;
		}
		return value;
	}

	/**
	 * 获取数据库查询结果集指定列的数据，
	 * 如果指定列的数据类型为 Blob，则转换为 byte[] 后返回，
	 * 如果指定列的数据类型为 Clob，则转换为 String 后返回，
	 * 如果指定列的数据类型为 日期类型，则转换为 java.sql.Date 或 java.sql.Timestamp 后返回
	 * <br><b>创建日期</b>：2010-11-15
	 * @param 数据库查询结果集
	 * @param index 列序号（从 1 开始）
	 * @return 数据库查询结果集指定列的数据
	 * @throws SQLException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		String className = null;
		if (obj != null) {
			className = obj.getClass().getName();
		}
		if (obj instanceof Blob) {
			obj = rs.getBytes(index);
		} else if (obj instanceof Clob) {
			obj = rs.getString(index);
		} else if ((className != null)
				&& ((("oracle.sql.TIMESTAMP".equals(className)) || ("oracle.sql.TIMESTAMPTZ".equals(className))))) {
			obj = rs.getTimestamp(index);
		} else if ((className != null) && (className.startsWith("oracle.sql.DATE"))) {
			String metaDataClassName = rs.getMetaData().getColumnClassName(index);
			if (("java.sql.Timestamp".equals(metaDataClassName)) || ("oracle.sql.TIMESTAMP".equals(metaDataClassName))) {
				obj = rs.getTimestamp(index);
			} else {
				obj = rs.getDate(index);
			}
		} else if ((obj != null) && (obj instanceof java.sql.Date)
				&& ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index)))) {
			obj = rs.getTimestamp(index);
		}

		return obj;
	}

	/**
	 * 检测数据库是否支持批量更新
	 * <br><b>创建日期</b>：2010-11-15
	 * @param con 数据库连接对象
	 * @return 如果支持批量更新测返回 <code>true</code>, 否则返回 <code>false</code>
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static boolean supportsBatchUpdates(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					log.debug("JDBC driver supports batch updates");
					return true;
				}

				log.debug("JDBC driver does not support batch updates");
			}
		} catch (SQLException ex) {
			log.debug("JDBC driver 'supportsBatchUpdates' method threw exception", ex);
		} catch (AbstractMethodError err) {
			log.debug("JDBC driver does not support JDBC 2.0 'supportsBatchUpdates' method", err);
		}
		return false;
	}

	/**
	 * 获取指定列的列名
	 * <br><b>创建日期</b>：2010-11-15
	 * @param resultSetMetaData 数据库查询结果集的元数据
	 * @param columnIndex 列序号（从 1 开始）
	 * @return 指定列的列名
	 * @throws SQLException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static String lookupColumnName(ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
		String name = resultSetMetaData.getColumnLabel(columnIndex);
		if ((name == null) || (name.length() < 1)) {
			name = resultSetMetaData.getColumnName(columnIndex);
		}
		return name;
	}

	/**
	 * 将数据库表的列名转换JavaBean的属性名，把下划线去掉，并将下划线后的第一个字母转换成大写(如果第二个字符是下划线，则第一个字符也会转换为大写)
	 * <br><b>创建日期</b>：2010-11-15
	 * @param name 列名
	 * @return 将列名转换后的 JavaBean 属性名
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static String convertUnderscoreNameToPropertyName(String name) {
		StringBuffer result = new StringBuffer();
		boolean nextIsUpper = false;
		if ((name != null) && (name.length() > 0)) {
			if ((name.length() > 1) && (name.substring(1, 2).equals("_"))) {
				result.append(name.substring(0, 1).toUpperCase());
			} else {
				result.append(name.substring(0, 1).toLowerCase());
			}
			for (int i = 1; i < name.length(); ++i) {
				String s = name.substring(i, i + 1);
				if (s.equals("_")) {
					nextIsUpper = true;
				} else if (nextIsUpper) {
					result.append(s.toUpperCase());
					nextIsUpper = false;
				} else {
					result.append(s.toLowerCase());
				}
			}
		}

		return result.toString();
	}

	// 开始事务
	public static void beginTransaction(Connection conn) {
		try {
			conn.setAutoCommit(false);
		} catch (Exception e) {
			log.error("开启事务失败！", e);
			throw new RuntimeException("开启事务失败！", e);
		}
	}

	// 回滚事务
	public static void rollback(Connection conn) {
		try {
			conn.rollback();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			log.error("回滚事务失败！", e);
		}

	}

	// 提交事务
	public static void commit(Connection conn) {
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			log.error("提交事务失败！", e);
			throw new RuntimeException("提交事务失败！", e);
		}
	}

}