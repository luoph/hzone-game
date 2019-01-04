package com.hzone.db.conn.dao;

import java.sql.Connection;

import com.hzone.db.conn.annotation.Resource;

/**
 * NBA数据
 * @author zehong.he
 *
 */
public class NBAConnectionDAO extends BaseDAO {
	@Resource(value = ResourceType.DB_nba)
	public Database database;
	
	
	@Override
	protected Connection getRealConnection() {
		return database.getConnection();
	}

}
