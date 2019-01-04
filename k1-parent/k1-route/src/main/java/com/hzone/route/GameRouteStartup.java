package com.hzone.route;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;
import com.hzone.server.GameSource;
import com.hzone.server.socket.GameSocketServer;
import com.hzone.server.socket.SocketServerConfig;
import com.hzone.util.IPUtil;

/**
 * 服务器启动入口
 * @author zehong.he
 *
 */
public class GameRouteStartup {
    private static final Logger log = LoggerFactory.getLogger(GameRouteStartup.class);

    public static void main(String[] args) throws Throwable {
    	
        //		System.out.println(System.getProperty("java.class.path"));
        Log.set(Log.LEVEL_TRACE);
        final int port;
        final String ip;
        final String jsPath;
        final int net;
        //开启调试模式
        //		GameSource.updateDebug(false);
        if (args.length >= 3) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            jsPath = args[2];
        } else {
            ip = IPUtil.getLocalIp();
            port = 8078;
            jsPath = System.getProperty("user.dir") + File.separatorChar + "config" + File.separatorChar + "config.js";
        }
        if (args.length >= 4) {
            net = Integer.parseInt(args[3]);
        } else {
            net = 1;
        }

        SocketServerConfig config = new SocketServerConfig() {

            @Override
            public String getServicePackgePath() {
                return "";
            }

            @Override
            public String getActiveManagerPackgePath() {
                return "";
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public String getPath() {
                return GameRouteStartup.class.getResource("/").getPath();
            }

            @Override
            public String getIP() {
                return ip;
            }

            @Override
            public String getManagerPackgePath() {
                return "com.hzone.manager";
            }

            @Override
            public String getCommonPackgePath() {
                return "com.hzone";
            }

            @Override
            public String getAOPackgePath() {
                return "";
            }

            @Override
            public String getDAOPackgePath() {
                return "";
            }

            @Override
            public String getJobPackgePath() {
                return "";
            }

            @Override
            public String getJSScriptPath() {
                return jsPath;
            }

            @Override
            public ClassLoader getClassLoader() {
                return GameRouteStartup.class.getClassLoader();
            }

        };
        GameSource.net = net == 1;
        try {
            GameSocketServer g = new GameSocketServer(config);
            g.startRoute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
            throw new RuntimeException(e);
        }
    }

}
