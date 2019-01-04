package com.hzone.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hzone.server.rpc.route.RouteServerManager;

/**
 * 节点存活定时扫描
 * @author zehong.he
 *
 */
public class RouteNodeLiveJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		RouteServerManager.checkRPCServerLive();
	}

}
