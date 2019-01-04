package com.hzone.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.hzone.enums.log.ELogVersion;
import com.hzone.server.GameSource;

/**
 * 新手引导数据
 * @author zehong.he
 *
 */
public class GameHelpStepLogManager {
	
	private static final Logger log = LogManager.getLogger("GameHelpStepLogManager");
	
	private static void initContext(){
			ThreadContext.put("helpStep","1");
			ThreadContext.put("shardId", ""+GameSource.shardId);
	        ThreadContext.put("ip", GameSource.serverName);
	        ThreadContext.put("platform", ""+GameSource.platform);
			ThreadContext.put("GameHelpStepLogTag", ELogVersion.新手引导.getLogSyslog());
			ThreadContext.put("GameHelpStepLogVersion", ""+ELogVersion.新手引导.getLogVersion());
	}
	
	public static void Log(long teamId,String helpStep){
		if(!ThreadContext.containsKey("helpStep")){
			initContext();
		}
		log.trace("{} {} {} ",teamId,helpStep,System.currentTimeMillis());
	}
}
