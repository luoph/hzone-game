package com.hzone.manager.logic.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.hzone.enums.log.ELogVersion;
import com.hzone.server.GameSource;

public class GameChatLogManager {
	
	private static final Logger log = LogManager.getLogger("GameChatLogManager");
	
	private static void initContext(){
		ThreadContext.put("chat", "1");
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
		ThreadContext.put("GameChatLogTag", ELogVersion.聊天记录.getLogSyslog());
		ThreadContext.put("GameChatLogVersion", ""+ELogVersion.聊天记录.getLogVersion());
	}
	
	/**
	 * 聊天日志
	 * @param teamId
	 * @param teamName
	 * @param level
	 * @param leagueId
	 * @param leagueName
	 * @param type 聊天类型
	 * @param chatMsg 消息内容
	 */
	public static void Log(long teamId,String teamName,int level,int leagueId, String leagueName, int type, String chatMsg){
		if(!ThreadContext.containsKey("chat")){
			initContext();
		}
		teamName = teamName.replaceAll(" ", "_");
		chatMsg = chatMsg.replaceAll(" ", "_");
		log.trace("{} {} {} {} {} {} {} {} ",teamId,teamName,level,leagueId,leagueName,type,chatMsg,System.currentTimeMillis());
	}
}
