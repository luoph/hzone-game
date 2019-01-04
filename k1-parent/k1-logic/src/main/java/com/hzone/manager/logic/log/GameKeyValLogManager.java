package com.hzone.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.hzone.enums.log.ELogVersion;
import com.hzone.server.GameSource;

/**
 * 游戏键值数据日志
 * @author zehong.he
 *
 */
public class GameKeyValLogManager {
	
	private static final Logger log = LogManager.getLogger("GameKeyValLogManager");
	
	public final static String 在线 = "Online";
	public final static String 聊天 = "Chat";
	
	private static void initContext(){
		ThreadContext.put("keyVal","1");
		ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameKeyValLogTag", ELogVersion.键值对.getLogSyslog());
		ThreadContext.put("GameKeyValLogVersion", ELogVersion.键值对.getLogVersion());
	}
	
	public static void Log(String key,String val){
		if(!ThreadContext.containsKey("keyVal")){
			initContext();
		}
		log.trace("{} {} {} ",key,val,System.currentTimeMillis());
	}
}
