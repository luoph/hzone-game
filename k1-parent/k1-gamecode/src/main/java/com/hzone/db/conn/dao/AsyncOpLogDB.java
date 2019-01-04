package com.hzone.db.conn.dao;

/**
 */
public abstract class AsyncOpLogDB extends AsynchronousBatchDB {
    private static final long serialVersionUID = 1L;

    @Override
    public synchronized void save() {
        if (this.sourceStatus) {
            return;
        }
        this.sourceStatus = true;
        DBManager.addAsynOpLogDB(this);
    }

    @Override
    public void del() {
    }
}
