package com.hzone.db.conn.dao;

/**
 * 公共数据库继承
 * @author zehong.he
 *
 */
public abstract class AsynchronousBatchDataDB extends AsynchronousBatchDB {

	@Override
	public synchronized void save() {
        if (this.sourceStatus) {
            return;
        }
        this.sourceStatus = true;
        DBManager.addAsyncActiveDB(this);
    }

}
