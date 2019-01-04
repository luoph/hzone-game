package com.hzone.tool.quartz.job;

import com.hzone.db.conn.dao.DBManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * 系统异步更新数据定时器
 * @author zehong.he
 *
 */
public class AsynchronousDBJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) {
        DBManager.run(false);
    }

}
