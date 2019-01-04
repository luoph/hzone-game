package com.hzone.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.hzone.enums.EMoneyType;
import com.hzone.enums.log.ELogVersion;
import com.hzone.server.GameSource;

/**
 * 游戏货币日志
 * @author zehong.he
 *
 */
public class GameMoneyLogManager {
	
	private static final Logger log = LogManager.getLogger("GameMoneyLogManager");
	
	private static void initContext(){
		ThreadContext.put("money","1");
		ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameMoneyLogTag", ELogVersion.货币统计.getLogSyslog());
		ThreadContext.put("GameMoneyLogVersion", ELogVersion.货币统计.getLogVersion());
	}
	
	public static void Log(long teamId,EMoneyType type,int val,int cur,ModuleLog module){
		if(!ThreadContext.containsKey("money")){
			initContext();
		}
		log.trace("{} {} {} {} {} {} {} {},{} ",teamId,type.ordinal(),val,cur,module.getId(),module.getDetail(),System.currentTimeMillis()
				,ThreadContext.get("GameMoneyLogTag"),ThreadContext.get("GameMoneyLogVersion"));
		
	}
}
