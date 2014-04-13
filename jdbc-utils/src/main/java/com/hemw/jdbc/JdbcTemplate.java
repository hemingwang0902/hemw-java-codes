package com.hemw.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hemw.jdbc.exception.DataAccessException;
import com.hemw.jdbc.exception.IncorrectResultSizeDataAccessException;
import com.hemw.jdbc.rowmapper.ColumnMapRowMapper;
import com.hemw.jdbc.rowmapper.RowMapper;
import com.hemw.jdbc.rowmapper.SingleColumnRowMapper;
import com.hemw.jdbc.utils.JdbcUtils;
import com.hemw.jdbc.utils.StatementCreatorUtils;

/**
 * JDBC辅助类 <br>
 * <b>创建日期</b>：2010-11-16
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public abstract class JdbcTemplate {
	private static final Logger log = Logger.getLogger(JdbcTemplate.class);
	
	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching. 
	 * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates. 
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql defining an array of SQL statements that will be executed.
	 * @return an array of the number of rows affected by each statement 
	 * @throws DataAccessException if there is any problem executing the batch
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #batchUpdate(String[], Connection)
	 */
	public static int[] batchUpdate(String[] sql) throws DataAccessException {
		int[] rowsAffected = null;
		Connection conn = JdbcUtils.getConn();
		JdbcUtils.beginTransaction(conn);
		try{
			rowsAffected = batchUpdate(sql, conn);
			JdbcUtils.commit(conn);
		}catch (DataAccessException e) {
			JdbcUtils.rollback(conn);
			throw e;
		}finally{
			JdbcUtils.closeConnection(conn);
		}
		return rowsAffected;
	}
	
	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching. 
	 * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates. 
	 * 此方法内部不做事务控制，不会关闭 Connection 对象
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql defining an array of SQL statements that will be executed.
	 * @param conn 数据库连接对象
	 * @return an array of the number of rows affected by each statement 
	 * @throws DataAccessException if there is any problem executing the batch
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static int[] batchUpdate(String[] sql, Connection conn) throws DataAccessException {
	    if(sql == null || sql.length == 0)
	        throw new IllegalArgumentException("SQL array must not be empty");
	    if(conn == null)
	        throw new IllegalArgumentException("conn must not be null");
		log.debug("Executing SQL batch update of " + sql.length + " statements");

		int[] rowsAffected = new int[sql.length];
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			if (JdbcUtils.supportsBatchUpdates(conn)) {
				for (int i = 0; i < sql.length; i++) {
					stmt.addBatch(sql[i]);
				}
				rowsAffected = stmt.executeBatch();
			} else {
				for (int i = 0; i < sql.length; i++) {
					if (!stmt.execute(sql[i])) {
						rowsAffected[i] = stmt.getUpdateCount();
					} else {
						throw new DataAccessException("Invalid batch SQL statement: " + sql[i]);
					}
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			JdbcUtils.closeStatement(stmt);
		}
		return rowsAffected;
	}

	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching. 
	 * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates. 
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql defining a SQL statements that will be executed.
	 * @param args 参数列表
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #batchUpdate(String, Object[][], int[])
	 */
	public static int[] batchUpdate(String sql, Object[][] args) throws DataAccessException {
		return batchUpdate(sql, args, new int[0]);
	}
	
	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching. 
	 * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates. 
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql defining a SQL statements that will be executed.
	 * @param args 参数列表
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #batchUpdate(String, Object[][], int[], Connection)
	 */
	public static int[] batchUpdate(String sql, Object[][] args, int[] columnTypes) throws DataAccessException {
		int[] rowsAffected = null;
		Connection conn = JdbcUtils.getConn();
		JdbcUtils.beginTransaction(conn);
		try{
			rowsAffected = batchUpdate(sql, args, columnTypes, conn);
			JdbcUtils.commit(conn);
		}catch (DataAccessException e) {
			JdbcUtils.rollback(conn);
			throw e;
		}finally{
			JdbcUtils.closeConnection(conn);
		}
		return rowsAffected;
	}

	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching. 
	 * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates. 
	 * 此方法内部不做事务控制，不会关闭 Connection 对象
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql defining a SQL statements that will be executed.
	 * @param args 参数列表
	 * @param conn 数据库连接对象
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #batchUpdate(String, Object[][], int[], Connection)
	 */
	public static int[] batchUpdate(String sql, Object[][] args, Connection conn) throws DataAccessException {
		return batchUpdate(sql, args, null, conn);
	}
	
	/**
	 * Issue multiple SQL updates on a single JDBC Statement using batching. 
	 * Will fall back to separate updates on a single Statement if the JDBC driver does not support batch updates. 
	 * 此方法内部不做事务控制，不会关闭 Connection 对象
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql defining a SQL statements that will be executed.
	 * @param args 参数列表
	 * @param conn 数据库连接对象
	 * @return an array of the number of rows affected by each statement
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static int[] batchUpdate(String sql, Object[][] args, int[] columnTypes, Connection conn) throws DataAccessException {
		log.debug("Executing SQL batch update [" + sql + "]");
		if(conn == null)
            throw new IllegalArgumentException("conn must not be null");

		int[] rowsAffected = new int[args.length];
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);

			if (JdbcUtils.supportsBatchUpdates(conn)) {
				for (int i = 0; i < args.length; i++) {
					if (columnTypes == null || columnTypes.length == 0)
						setStatementParameters(args[i], ps);
					else
						setStatementParameters(args[i], ps, columnTypes);
					ps.addBatch();
				}
				rowsAffected = ps.executeBatch();
			} else {
				for (int i = 0; i < args.length; i++) {
					ps.clearParameters();
					if (columnTypes == null || columnTypes.length == 0)
						setStatementParameters(args[i], ps);
					else
						setStatementParameters(args[i], ps, columnTypes);
					rowsAffected[i] = ps.executeUpdate();
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			JdbcUtils.closeStatement(ps);
		}
		return rowsAffected;
	}

	/**
	 * 执行更新SQL
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql 需要执行的SQL语句
	 * @return 执行SQL影响的记录行数
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #update(String, Object[], int[])
	 */
	public static int update(String sql) throws DataAccessException {
		return update(sql, null, new int[0]);
	}
	
	/**
	 * 执行更新SQL
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql 需要执行的SQL语句
	 * @param args 参数列表
	 * @return 执行SQL影响的记录行数
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #update(String, Object[], int[])
	 */
	public static int update(String sql, Object[] args) throws DataAccessException {
		return update(sql, args, new int[0]);
	}
	
	/**
	 * 执行更新SQL
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql 需要执行的SQL语句
	 * @param args 参数列表
	 * @param argTypes 参数的数据类型
	 * @return 执行SQL影响的记录行数
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		int rowsAffected = -1;
		Connection conn = JdbcUtils.getConn();
		try{
			rowsAffected = update(sql, args, argTypes, conn);
		}catch (DataAccessException e) {
			throw e;
		}finally{
			JdbcUtils.closeConnection(conn);
		}
		return rowsAffected;
	}

	/**
	 * 执行更新SQL
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql 需要执行的SQL语句
	 * @param conn 数据库连接对象
	 * @return 执行SQL影响的记录行数
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #update(String, Object[], int[], Connection)
	 */
	public static int update(String sql, Connection conn) throws DataAccessException {
		return update(sql, null, null, conn);
	}

	/**
	 * 执行更新SQL
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql 需要执行的SQL语句
	 * @param args 参数列表
	 * @param conn 数据库连接对象
	 * @return 执行SQL影响的记录行数
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see #update(String, Object[], int[], Connection)
	 */
	public static int update(String sql, Object[] args, Connection conn) throws DataAccessException {
		return update(sql, args, null, conn);
	}
	
	
	/**
	 * 执行更新SQL
	 * <br><b>创建日期</b>：2010-11-16
	 * @param sql 需要执行的SQL语句
	 * @param args 参数列表
	 * @param argTypes 参数的数据类型
	 * @param conn 数据库连接对象
	 * @return 执行SQL影响的记录行数
	 * @throws DataAccessException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 */
	public static int update(String sql, Object[] args, int[] argTypes, Connection conn) throws DataAccessException {
		log.debug("Executing prepared SQL update");
		if(conn == null)
            throw new IllegalArgumentException("conn must not be null");
		
		int rowsAffected = -1;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);

			if (argTypes == null || argTypes.length == 0)
				setStatementParameters(args, ps);
			else
				setStatementParameters(args, ps, argTypes);
			
			rowsAffected = ps.executeUpdate();
			log.debug("SQL update affected " + rowsAffected + " rows");
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			JdbcUtils.closeStatement(ps);
		}
		return rowsAffected;
	}
	
	public static Map<String, Object> queryForMap(String sql) throws DataAccessException {
		return queryForMap(sql, new Object[0], new int[0]);
	}

	public static Map<String, Object> queryForMap(String sql, Object[] args) throws DataAccessException {
		return queryForMap(sql, new Object[0], new int[0]);
	}

	public static Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return queryForObject(sql, args, argTypes, new ColumnMapRowMapper());
	}

	public static <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
		return queryForObject(sql, new Object[0], new int[0], rowMapper);
	}

	public static <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		return queryForObject(sql, args, new int[0], rowMapper);
	}

	public static <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) {
		List<T> results = queryForList(sql, args, argTypes, rowMapper);
		int size = (results != null ? results.size() : 0);
		if (size == 0) {
			throw new IncorrectResultSizeDataAccessException(1, 0);
		}
		if (results.size() > 1) {
			throw new IncorrectResultSizeDataAccessException(1, size);
		}
		return results.iterator().next();
	}

	public static <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
		return queryForObject(sql, new Object[0], new int[0], requiredType);
	}

	public static <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
		return queryForObject(sql, new Object[0], new int[0], requiredType);
	}

	public static <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) {
		return queryForObject(sql, args, argTypes, new SingleColumnRowMapper<T>(requiredType));
	}
	
	public static long queryForLong(String sql) throws DataAccessException {
		return queryForLong(sql, new Object[0], new int[0]);
	}

	public static long queryForLong(String sql, Object[] args) throws DataAccessException {
		return queryForLong(sql, new Object[0], new int[0]);
	}
	
	public static long queryForLong(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		Number number = (Number) queryForObject(sql, args, argTypes, Long.class);
		return (number != null ? number.longValue() : 0);
	}


	public static int queryForInt(String sql) throws DataAccessException {
		return queryForInt(sql, new Object[0], new int[0]);
	}

	public static int queryForInt(String sql, Object[] args) throws DataAccessException {
		return queryForInt(sql, new Object[0], new int[0]);
	}
	
	public static int queryForInt(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		Number number = (Number) queryForObject(sql, args, argTypes, Integer.class);
		return (number != null ? number.intValue() : 0);
	}

	public static <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
		return queryForList(sql, new Object[0], new int[0], elementType);
	}
	
	public static <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
		return queryForList(sql, new Object[0], new int[0], elementType);
	}

	public static <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
		return queryForList(sql, args, argTypes, new SingleColumnRowMapper<T>(elementType));
	}

	public static List<Map<String,Object>> queryForList(String sql) throws DataAccessException {
		return queryForList(sql, new Object[0], new int[0]);
	}

	public static List<Map<String,Object>> queryForList(String sql, Object[] args) throws DataAccessException {
		return queryForList(sql, new Object[0], new int[0]);
	}

	public static List<Map<String,Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
		return queryForList(sql, args, argTypes, new ColumnMapRowMapper());
	}

	public static <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) throws DataAccessException {
		return queryForList(sql, new Object[0], new int[0], rowMapper);
	}
	
	public static <T> List<T> queryForList(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		return queryForList(sql, args, new int[0], rowMapper);
	}
	
	public static <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
		log.debug("Executing prepared SQL query");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<T> results = new ArrayList<T>();
		try {
			conn = JdbcUtils.getConn();
			if(conn == null)
	            throw new IllegalArgumentException("conn must not be null");
			ps = conn.prepareStatement(sql);

			if (argTypes == null || argTypes.length == 0)
				setStatementParameters(args, ps);
			else
				setStatementParameters(args, ps, argTypes);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				results.add(rowMapper.mapRow(rs, 0));
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
			JdbcUtils.closeConnection(conn);
		}
		return results;
	}
	
	/**
	 * 给 java.sql.PreparedStatement 对象设置参数对象
	 * <br><b>创建日期</b>：2010-11-16
	 * @param values 参数值
	 * @param ps 预处理语句对象
	 * @throws SQLException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @throws SQLException 
	 * @see StatementCreatorUtils#javaTypeToSqlParameterType(Class)
	 */
	protected static void setStatementParameters(Object[] values, PreparedStatement ps) throws SQLException{
		if(values == null)
			return;
		
		int[] columnTypes = new int[values.length];
		for (int i = 0; i < columnTypes.length; i++) {
			columnTypes[i] = (values[i] == null ? JdbcUtils.TYPE_UNKNOWN : StatementCreatorUtils.javaTypeToSqlParameterType(values[i].getClass()));
		}
		setStatementParameters(values, ps, columnTypes);
	}
	
	/**
	 * 给 java.sql.PreparedStatement 对象设置参数对象
	 * <br><b>创建日期</b>：2010-11-16
	 * @param values 参数值
	 * @param ps 预处理语句对象
	 * @param columnTypes 参数值的数据类型
	 * @throws SQLException
	 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
	 * @see StatementCreatorUtils#javaTypeToSqlParameterType(Class)
	 */
	protected static void setStatementParameters(Object[] values, PreparedStatement ps, int[] columnTypes)
			throws SQLException {
		if(values == null)
			return;
		
		int colType;
		for (int i = 0; i < values.length; i++) {

			if ((columnTypes != null) && (i < columnTypes.length)) {
				colType = columnTypes[(i)];
			} else {
				colType = JdbcUtils.TYPE_UNKNOWN;
			}
			StatementCreatorUtils.setParameterValue(ps, (i + 1), colType, values[i]);
		}

	}
}
