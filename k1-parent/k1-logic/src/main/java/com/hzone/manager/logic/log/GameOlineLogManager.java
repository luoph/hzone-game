package com.hzone.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.hzone.enums.log.ELogVersion;
import com.hzone.server.GameSource;

/**
 * 在线时长数据
 * @author zehong.he
 *
 */
public class GameOlineLogManager {
	
	private static final Logger log = LogManager.getLogger("GameOlineLogManager");
	
	private static void initContext(){
		ThreadContext.put("oline","1");
		ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameOlineLogTag", ELogVersion.在线时长.getLogSyslog());
		ThreadContext.put("GameOlineLogVersion", ""+ELogVersion.在线时长.getLogVersion());
	}
	
	public static void Log(long teamId,long second,int level){
		if(!ThreadContext.containsKey("oline")){
			initContext();
		}
		log.trace("{} {} {} {} ",teamId,second,level,System.currentTimeMillis());
	}
	
	
}
