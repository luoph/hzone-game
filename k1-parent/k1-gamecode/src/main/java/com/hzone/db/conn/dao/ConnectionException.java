package com.hzone.db.conn.dao;
/**
 * 功能：数据库连接异常
 * @author zehong.he
 *
 */
public class ConnectionException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9092810927158094209L;

	/**
	 * Constructor for DataStoreException.
	 * 
	 * @param msg
	 *            String
	 */
	public ConnectionException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataStoreException.
	 * 
	 * @param e
	 *            Throwable
	 */
	public ConnectionException(Throwable e) {
		super(e);
	}

	/**
	 * Constructor for DataStoreException.
	 * 
	 * @param msg
	 *            String
	 * @param e
	 *            Throwable
	 */
	public ConnectionException(String msg, Throwable e) {
		super(msg, e);
	}
}
