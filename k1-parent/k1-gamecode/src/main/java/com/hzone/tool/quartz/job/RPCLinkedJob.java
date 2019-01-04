package com.hzone.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hzone.manager.RPCManager;

/**
 * RPC异步调用链，超时处理
 * @author zehong.he
 *
 */
public class RPCLinkedJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		RPCManager.clearTimeOut();
	}

}
