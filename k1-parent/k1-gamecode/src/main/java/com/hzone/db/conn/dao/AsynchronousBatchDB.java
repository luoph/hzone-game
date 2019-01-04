package com.hzone.db.conn.dao;

import java.io.Serializable;
import java.util.List;

/**
 * 批量异步db操作抽象父类
 * @author zehong.he
 *
 */
public abstract class AsynchronousBatchDB implements Serializable {
    private static final long serialVersionUID = 1L;
    protected boolean sourceStatus = false;

    /** 数据操作列名拼接, 用','分割 */
    public abstract String getRowNames();

    /** 获得数据表名 */
    public abstract String getTableName();

    /** 删除数据 */
    public void del() {
    }

    /**
     * 调用save方法，表示该数据需要保存
     */
    public synchronized void save() {
        if (this.sourceStatus) {
            return;
        }
        this.sourceStatus = true;
        DBManager.addAsynLogicDB(this);
    }

    /**
     * 调用unSave方法，表示该数据已经执行过操作
     */
    public synchronized void unSave() {
        this.sourceStatus = false;
    }

    /** sql更新日志级别开关 */
    public boolean isSqlLoggerDebugEnabled() {
        return false;
    }

    /** sql更新日志级别开关 */
    public boolean isSqlLoggerInfoEnabled() {
        return false;
    }

    //public abstract List<String> getRowNameList();

    /** sql 值列表 */
    public abstract List<Object> getRowParameterList();

    /** 是否数据只新增, 不更新老数据 */
    public boolean isInsertSqlOnly(){
        return false;
    }
}
