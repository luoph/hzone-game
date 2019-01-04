package com.hzone.server.syn;

import com.hzone.server.MessageManager;
import com.hzone.server.RPCMessageManager;
import com.hzone.server.ServerStat;
import com.hzone.server.instance.InstanceFactory;
import com.hzone.server.proto.Request;
import com.hzone.server.rpc.RPCSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理逻辑线程
 * @author zehong.he
 *
 */
public class SynInvokeThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SynInvokeThread.class);
    private final Request req;

    public SynInvokeThread(Request req) {
        this.req = req;
    }

    @Override
    public void run() {
        try {
            //执行逻辑前，初始化当前玩家数据源
            req.start();
            if (req instanceof RPCSource) {
                RPCMessageManager.initSource((RPCSource) req);
            } else {
                MessageManager.initTeam(req);
            }

            if (req.getTeamId() > 0 || InstanceFactory.get().isUnCheck(req.getMethodCode())) {//还没登录
                if (log.isDebugEnabled()) {
                    log.debug("req << tid {} code {} reqid {} len {} method {} params [{}]",
                            req.getTeamId(), req.getMethodCode(), req.getReqId(), req.getMsgLength(),
                            req.getServerMethod() == null ? req.getMethodCode() : req.getServerMethod().getName(), req.getAttrsSimpleString());
                }
                req.invoke();
            }
            //记录请求消耗时间
            ServerStat.make(req);
        } catch (Exception e) {
            log.error(String.format("SynInvokeThread code : %s msg %s", req.getMethodCode(), e.getMessage()), e);
        }
    }

}
