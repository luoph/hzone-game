package com.hzone.tool.log;

import com.hzone.server.GameSource;
import org.apache.logging.log4j.ThreadContext;



/**
 * 
 * @author zehong.he
 *
 */
public class LoggerManager {
	public static void initThreadContext(){
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
	}

}
