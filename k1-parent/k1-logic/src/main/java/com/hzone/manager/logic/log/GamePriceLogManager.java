package com.hzone.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.hzone.enums.log.ELogVersion;
import com.hzone.server.GameSource;

/**
 * 自有球员身价变动
 * @author zehong.he
 *
 */
public class GamePriceLogManager {
	
	private static final Logger log = LogManager.getLogger("GamePriceLogManager");
	
	private static void initContext(){
		ThreadContext.put("price","1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GamePriceLogTag", ELogVersion.身价变动.getLogSyslog());
		ThreadContext.put("GamePriceLogVersion", ""+ELogVersion.身价变动.getLogVersion());
	}
	
	public static void Log(long teamId,int pid, int playerId, int oldPrice, int newPrice, ModuleLog module){
		if(!ThreadContext.containsKey("price")){
			initContext();
		}
		log.trace("{} {} {} {} {} {} {} {} ",teamId,pid,playerId,oldPrice,newPrice,module.getId(),module.getDetail(),System.currentTimeMillis());
	}
}
