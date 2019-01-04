package com.hzone.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hzone.server.GameSource;
import com.hzone.server.ServerStat;


/**
 * 系统消息打印
 * @author zehong.he
 *
 */
public class SystemStatJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(GameSource.statJob)
			ServerStat.print();
	}

}
