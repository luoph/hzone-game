package com.hzone.pk;

import com.hzone.server.GameSource;
import com.hzone.server.socket.GameSocketServer;
import com.hzone.server.socket.SocketServerConfig;
import com.hzone.util.IPUtil;

import com.esotericsoftware.minlog.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 服务器启动入口
 * @author zehong.he
 *
 */
public class GamePKnodeStartup {
    private static final Logger log = LoggerFactory.getLogger(GamePKnodeStartup.class);

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
            port = 8068;
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
            public int getPort() {
                return port;
            }

            @Override
            public String getPath() {
                return GamePKnodeStartup.class.getResource("/").getPath();
            }

            @Override
            public String getIP() {
                return ip;
            }

            @Override
            public String getManagerPackgePath() {
                return "com.hzone.manager.pk";
            }

            @Override
            public String getCommonPackgePath() {
                return "com.hzone";
            }

            @Override
            public String getActiveManagerPackgePath() {
                return "com.hzone.manager.active";
            }

            @Override
            public String getAOPackgePath() {
                return "com.hzone.db.ao.pk";
            }

            @Override
            public String getDAOPackgePath() {
                return "com.hzone.db.dao.pk";
            }

            @Override
            public String getJobPackgePath() {
                return "com.hzone.job";
            }

            @Override
            public String getJSScriptPath() {
                return jsPath;
            }

            @Override
            public ClassLoader getClassLoader() {
                return GamePKnodeStartup.class.getClassLoader();
            }

        };
        try {
            GameSource.net = net == 1;
            GameSocketServer g = new GameSocketServer(config);
            if (GameSource.pool.equals("route")) {
                g.startRoute();
            } else {
                g.startNode();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
            throw e;
        }
    }

}
