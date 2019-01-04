package com.hzone.script;

import com.hzone.db.conn.dao.ResourceType;
import com.hzone.enums.EVersion;
import com.hzone.server.GameSource;
import com.hzone.server.instance.InstanceFactory;
import org.joda.time.DateTime;

public class StartupContextImpl implements StartupContext {

    public StartupContextImpl() {

    }

    @Override
    public void setServerDate(String date) {
        GameSource.openTime = DateTime.parse(date);
    }

    public void addResource(ResourceType resName, Object obj) {
        InstanceFactory.get().addDataBaseResource(resName.getResName(), obj);
    }

    public void setServerName(String serverName) {
        GameSource.serverName = serverName;
    }

    @Override
    public void setPool(String pool) {
        GameSource.pool = pool;
    }

    public void setZKPath(String path) {
        GameSource.zkPath = path;
    }

    public void setShardid(int shardid) {
        GameSource.shardId = shardid;
    }

    public void setCharset(String charset) {
        GameSource.charset = EVersion.valueOf(charset);
    }

    public void setGm(boolean gm) {
        GameSource.GM = gm;
    }

    public void setAuthAccount(boolean auth) {
        GameSource.isDebug = !auth;
    }

}
