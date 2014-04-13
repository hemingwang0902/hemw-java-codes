package com.hemw.jdbc.exception;

/**
 * 数据访问异常
 * <br><b>创建日期</b>：2010-11-16
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class DataAccessException extends RuntimeException {

	private static final long serialVersionUID = 4498440989018002934L;

	public DataAccessException() {
		super();
	}

	public DataAccessException(String message) {
		super(message);
	}

	public DataAccessException(Throwable cause) {
		super(cause.toString());
	}
}
