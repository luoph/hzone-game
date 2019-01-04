package com.hzone.db.conn.dao;

import com.hzone.db.conn.annotation.Resource;

import java.sql.Connection;
import java.sql.SQLException;

public class OpLogConnectionDAO extends BaseDAO {
    @Resource(value = ResourceType.DB_Op_Log)
    public Database database;

    /**
     * Method getConnection.
     *
     * @return Connection
     * @throws SQLException
     */
    protected Connection getRealConnection() {
        return database.getConnection();
    }

}
