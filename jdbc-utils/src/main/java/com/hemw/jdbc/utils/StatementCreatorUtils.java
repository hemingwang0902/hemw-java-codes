package com.hemw.jdbc.utils;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Utility methods for PreparedStatementSetter/Creator and CallableStatementCreator
 * implementations, providing sophisticated parameter management (including support
 * for LOB values).
 *
 * <p>Used by PreparedStatementCreatorFactory and CallableStatementCreatorFactory,
 * but also available for direct use in custom setter/creator implementations.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 1.1
 * @see PreparedStatementSetter
 * @see PreparedStatementCreator
 * @see CallableStatementCreator
 * @see PreparedStatementCreatorFactory
 * @see CallableStatementCreatorFactory
 * @see SqlParameter
 * @see SqlTypeValue
 * @see org.springframework.jdbc.core.support.SqlLobValue
 */
public abstract class StatementCreatorUtils {
	private static final Logger log = Logger.getLogger(StatementCreatorUtils.class);

	private static Map<Class<?>, Integer> javaTypeToSqlTypeMap = new HashMap<Class<?>, Integer>(32);

	static {
		/* JDBC 3.0 only - not compatible with e.g. MySQL at present
		javaTypeToSqlTypeMap.put(boolean.class, new Integer(Types.BOOLEAN));
		javaTypeToSqlTypeMap.put(Boolean.class, new Integer(Types.BOOLEAN));
		*/
		javaTypeToSqlTypeMap.put(byte.class, new Integer(Types.TINYINT));
		javaTypeToSqlTypeMap.put(Byte.class, new Integer(Types.TINYINT));
		javaTypeToSqlTypeMap.put(short.class, new Integer(Types.SMALLINT));
		javaTypeToSqlTypeMap.put(Short.class, new Integer(Types.SMALLINT));
		javaTypeToSqlTypeMap.put(int.class, new Integer(Types.INTEGER));
		javaTypeToSqlTypeMap.put(Integer.class, new Integer(Types.INTEGER));
		javaTypeToSqlTypeMap.put(long.class, new Integer(Types.BIGINT));
		javaTypeToSqlTypeMap.put(Long.class, new Integer(Types.BIGINT));
		javaTypeToSqlTypeMap.put(BigInteger.class, new Integer(Types.BIGINT));
		javaTypeToSqlTypeMap.put(float.class, new Integer(Types.FLOAT));
		javaTypeToSqlTypeMap.put(Float.class, new Integer(Types.FLOAT));
		javaTypeToSqlTypeMap.put(double.class, new Integer(Types.DOUBLE));
		javaTypeToSqlTypeMap.put(Double.class, new Integer(Types.DOUBLE));
		javaTypeToSqlTypeMap.put(BigDecimal.class, new Integer(Types.DECIMAL));
		javaTypeToSqlTypeMap.put(java.sql.Date.class, new Integer(Types.DATE));
		javaTypeToSqlTypeMap.put(java.sql.Time.class, new Integer(Types.TIME));
		javaTypeToSqlTypeMap.put(java.sql.Timestamp.class, new Integer(Types.TIMESTAMP));
		javaTypeToSqlTypeMap.put(Blob.class, new Integer(Types.BLOB));
		javaTypeToSqlTypeMap.put(Clob.class, new Integer(Types.CLOB));
	}


	/**
	 * Derive a default SQL type from the given Java type.
	 * @param javaType the Java type to translate
	 * @return the corresponding SQL type, or <code>null</code> if none found
	 */
	public static int javaTypeToSqlParameterType(Class<?> javaType) {
		Integer sqlType = (Integer) javaTypeToSqlTypeMap.get(javaType);
		if (sqlType != null) {
			return sqlType.intValue();
		}
		if (Number.class.isAssignableFrom(javaType)) {
			return Types.NUMERIC;
		}
		if (isStringValue(javaType)) {
			return Types.VARCHAR;
		}
		if (isDateValue(javaType) || Calendar.class.isAssignableFrom(javaType)) {
			return Types.TIMESTAMP;
		}
		return JdbcUtils.TYPE_UNKNOWN;
	}

	/**
	 * Set the value for a parameter. The method used is based on the SQL type
	 * of the parameter and we can handle complex types like arrays and LOBs.
	 * @param ps the prepared statement or callable statement
	 * @param paramIndex index of the parameter we are setting
	 * @param sqlType the SQL type of the parameter
	 * @param inValue the value to set (plain value or a SqlTypeValue)
	 * @throws SQLException if thrown by PreparedStatement methods
	 * @see SqlTypeValue
	 */
	public static void setParameterValue(
	    PreparedStatement ps, int paramIndex, int sqlType, Object inValue)
	    throws SQLException {

		setParameterValueInternal(ps, paramIndex, sqlType, null, null, inValue);
	}

	/**
	 * Set the value for a parameter. The method used is based on the SQL type
	 * of the parameter and we can handle complex types like arrays and LOBs.
	 * @param ps the prepared statement or callable statement
	 * @param paramIndex index of the parameter we are setting
	 * @param sqlType the SQL type of the parameter
	 * @param typeName the type name of the parameter
	 * (optional, only used for SQL NULL and SqlTypeValue)
	 * @param inValue the value to set (plain value or a SqlTypeValue)
	 * @throws SQLException if thrown by PreparedStatement methods
	 * @see SqlTypeValue
	 */
	public static void setParameterValue(
	    PreparedStatement ps, int paramIndex, int sqlType, String typeName, Object inValue)
	    throws SQLException {

		setParameterValueInternal(ps, paramIndex, sqlType, typeName, null, inValue);
	}

	/**
	 * Set the value for a parameter. The method used is based on the SQL type
	 * of the parameter and we can handle complex types like arrays and LOBs.
	 * @param ps the prepared statement or callable statement
	 * @param paramIndex index of the parameter we are setting
	 * @param sqlType the SQL type of the parameter
	 * @param typeName the type name of the parameter
	 * (optional, only used for SQL NULL and SqlTypeValue)
	 * @param scale the number of digits after the decimal point
	 * (for DECIMAL and NUMERIC types)
	 * @param inValue the value to set (plain value or a SqlTypeValue)
	 * @throws SQLException if thrown by PreparedStatement methods
	 * @see SqlTypeValue
	 */
	private static void setParameterValueInternal(
	    PreparedStatement ps, int paramIndex, int sqlType, String typeName, Integer scale, Object inValue)
 throws SQLException {

		String typeNameToUse = typeName;
		int sqlTypeToUse = sqlType;
		Object inValueToUse = inValue;

		log.debug("Setting SQL statement parameter value: column index " + paramIndex + ", parameter value ["
				+ inValueToUse + "], value class ["
				+ (inValueToUse != null ? inValueToUse.getClass().getName() : "null") + "], SQL type "
				+ (sqlTypeToUse == JdbcUtils.TYPE_UNKNOWN ? "unknown" : Integer.toString(sqlTypeToUse)));

		if (inValueToUse == null) {
			setNull(ps, paramIndex, sqlTypeToUse, typeNameToUse);
		} else {
			setValue(ps, paramIndex, sqlTypeToUse, typeNameToUse, scale, inValueToUse);
		}
	}

	private static void setNull(PreparedStatement ps, int paramIndex, int sqlType, String typeName) throws SQLException {
		if (sqlType == JdbcUtils.TYPE_UNKNOWN) {
			boolean useSetObject = false;
			sqlType = Types.NULL;
			try {
				DatabaseMetaData dbmd = ps.getConnection().getMetaData();
				String databaseProductName = dbmd.getDatabaseProductName();
				String jdbcDriverName = dbmd.getDriverName();
				if (databaseProductName.startsWith("Informix") ||
						jdbcDriverName.startsWith("Microsoft SQL Server")) {
					useSetObject = true;
				}
				else if (databaseProductName.startsWith("DB2") ||
						jdbcDriverName.startsWith("jConnect") ||
						jdbcDriverName.startsWith("SQLServer")||
						jdbcDriverName.startsWith("Apache Derby Embedded")) {
					sqlType = Types.VARCHAR;
				}
			}
			catch (Throwable ex) {
				log.debug("Could not check database or driver name", ex);
			}
			if (useSetObject) {
				ps.setObject(paramIndex, null);
			}
			else {
				ps.setNull(paramIndex, sqlType);
			}
		}
		else if (typeName != null) {
			ps.setNull(paramIndex, sqlType, typeName);
		}
		else {
			ps.setNull(paramIndex, sqlType);
		}
	}

	private static void setValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName,
 Integer scale, Object inValue) throws SQLException {

		if (sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR
				|| (sqlType == Types.CLOB && isStringValue(inValue.getClass()))) {
			ps.setString(paramIndex, inValue.toString());
		} else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
			if (inValue instanceof BigDecimal) {
				ps.setBigDecimal(paramIndex, (BigDecimal) inValue);
			} else if (scale != null) {
				ps.setObject(paramIndex, inValue, sqlType, scale.intValue());
			} else {
				ps.setObject(paramIndex, inValue, sqlType);
			}
		} else if (sqlType == Types.DATE) {
			if (inValue instanceof java.util.Date) {
				if (inValue instanceof java.sql.Date) {
					ps.setDate(paramIndex, (java.sql.Date) inValue);
				} else {
					ps.setDate(paramIndex, new java.sql.Date(((java.util.Date) inValue).getTime()));
				}
			} else if (inValue instanceof Calendar) {
				Calendar cal = (Calendar) inValue;
				ps.setDate(paramIndex, new java.sql.Date(cal.getTime().getTime()), cal);
			} else {
				ps.setObject(paramIndex, inValue, Types.DATE);
			}
		} else if (sqlType == Types.TIME) {
			if (inValue instanceof java.util.Date) {
				if (inValue instanceof java.sql.Time) {
					ps.setTime(paramIndex, (java.sql.Time) inValue);
				} else {
					ps.setTime(paramIndex, new java.sql.Time(((java.util.Date) inValue).getTime()));
				}
			} else if (inValue instanceof Calendar) {
				Calendar cal = (Calendar) inValue;
				ps.setTime(paramIndex, new java.sql.Time(cal.getTime().getTime()), cal);
			} else {
				ps.setObject(paramIndex, inValue, Types.TIME);
			}
		} else if (sqlType == Types.TIMESTAMP) {
			if (inValue instanceof java.util.Date) {
				if (inValue instanceof java.sql.Timestamp) {
					ps.setTimestamp(paramIndex, (java.sql.Timestamp) inValue);
				} else {
					ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
				}
			} else if (inValue instanceof Calendar) {
				Calendar cal = (Calendar) inValue;
				ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
			} else {
				ps.setObject(paramIndex, inValue, Types.TIMESTAMP);
			}
		} else if (sqlType == JdbcUtils.TYPE_UNKNOWN) {
			if (isStringValue(inValue.getClass())) {
				ps.setString(paramIndex, inValue.toString());
			} else if (isDateValue(inValue.getClass())) {
				ps.setTimestamp(paramIndex, new java.sql.Timestamp(((java.util.Date) inValue).getTime()));
			} else if (inValue instanceof Calendar) {
				Calendar cal = (Calendar) inValue;
				ps.setTimestamp(paramIndex, new java.sql.Timestamp(cal.getTime().getTime()), cal);
			} else {
				// Fall back to generic setObject call without SQL type specified.
				ps.setObject(paramIndex, inValue);
			}
		} else {
			// Fall back to generic setObject call with SQL type specified.
			ps.setObject(paramIndex, inValue, sqlType);
		}
	}

	/**
	 * Check whether the given value can be treated as a String value.
	 */
	private static boolean isStringValue(Class<?> inValueType) {
		// Consider any CharSequence (including JDK 1.5's StringBuilder) as String.
		return (String.class.isAssignableFrom(inValueType) ||
				StringBuffer.class.isAssignableFrom(inValueType) ||
				StringWriter.class.isAssignableFrom(inValueType));
	}

	/**
	 * Check whether the given value is a <code>java.util.Date</code>
	 * (but not one of the JDBC-specific subclasses).
	 */
	private static boolean isDateValue(Class<?> inValueType) {
		return (java.util.Date.class.isAssignableFrom(inValueType) &&
				!(java.sql.Date.class.isAssignableFrom(inValueType) ||
						java.sql.Time.class.isAssignableFrom(inValueType) ||
						java.sql.Timestamp.class.isAssignableFrom(inValueType)));
	}

}
