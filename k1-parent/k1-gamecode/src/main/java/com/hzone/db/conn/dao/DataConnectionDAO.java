package com.hzone.db.conn.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.hzone.db.conn.annotation.Resource;



public class DataConnectionDAO extends BaseDAO{
	@Resource(value = ResourceType.DB_data)
	public Database database;
	/**
	 * Method getConnection.
	 * @return Connection
	 * @throws SQLException 
	 */
	protected Connection getRealConnection() {
		return database.getConnection();
	}
		
}
