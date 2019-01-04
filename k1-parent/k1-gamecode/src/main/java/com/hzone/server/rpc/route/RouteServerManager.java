package com.hzone.server.rpc.route;

import com.hzone.enums.ERPCType;
import com.hzone.manager.RPCManager;
import com.hzone.server.instance.InstanceFactory;
import com.hzone.server.rpc.RPCServer;
import com.hzone.server.rpc.RPCServerMethod;
import com.hzone.server.rpc.RPCSource;
import com.hzone.tool.zookeep.ZookeepServer;
import com.hzone.util.JsonUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 路由服务器管理
 * @author zehong.he
 *
 */
public class RouteServerManager {
    public final static Logger log = LogManager.getLogger(RouteServerManager.class);

    //节点map
    private static Map<String, RPCServer> clientMap = Maps.newConcurrentMap();
    //池map
    private static Map<String, List<RPCServer>> poolMap = Maps.newConcurrentMap();
    //主节点Map
    private static Map<String, RPCServer> masterMap = Maps.newConcurrentMap();

    private static AtomicLong index = new AtomicLong();

    private static ZookeepServer zk;

    private static BlockingQueue<RPCSource> taskQueue = Queues.newLinkedBlockingQueue();

    //
    private static Disruptor<DataEvent> dis;

    public static EventTranslatorOneArg<DataEvent, RPCSource> sourceTR;
    private static final WaitStrategy WAIT_STRATEGY;

    static {
        String waitstrategy = System.getProperty("x3.route.mq.waitstrategy", "yielding");
        switch (waitstrategy) {
            case "yielding":
                WAIT_STRATEGY = new YieldingWaitStrategy();
                break;
            case "blocking":
                WAIT_STRATEGY = new BlockingWaitStrategy();
                break;
            default:
                WAIT_STRATEGY = new YieldingWaitStrategy();
        }
        log.debug("x3.route.mq.waitstrategy={}, class {}", waitstrategy, WAIT_STRATEGY.getClass().getName());
    }

    /**
     * 开启disruptor
     */
    @SuppressWarnings("unchecked")
    public static void start() throws Exception {
        dis = new Disruptor<>(() -> new DataEvent(), 1024, (run) -> {
            Thread th = new Thread(run);
            th.setName("Disruptor Thread");
            return th;
        }, ProducerType.MULTI, WAIT_STRATEGY);

        sourceTR = (event, sequence, arg0) -> event.setSource(arg0);
        dis.handleEventsWith((a, b, c) -> RouteServerManager.write(a.getSource()));
        dis.start();
    }

    static class DataEvent {
        private RPCSource source;

        public DataEvent() {
            super();
        }

        public RPCSource getSource() {
            return source;
        }

        public void setSource(RPCSource source) {
            this.source = source;
        }

        @Override
        public String toString() {
            return "DataEvent [source=" + source + "]";
        }

    }

    public static void submit(RPCSource source) {
        if (dis == null) {
            log.error("disruptor 未启动");
            return;
        }
        dis.publishEvent(sourceTR, source);
    }

    /**
     * 节点连接成功后将自身信息添加进路由节点列表
     *
     * @param server
     */
    public static void putRPCServer(RPCServer server) {
        log.info("节点注册开始--->{}", server.toString());
        clientMap.put(server.getServerName(), server);
        resetServer();
        Watcher logicWatcher = new NodeWatcher(server);

        zk.exists(ZookeepServer.getLogicPath() + "/" + server.getServerName(), logicWatcher);
        log.debug("刷新节点信息成功");
    }

    private static class NodeWatcher implements Watcher {

        public RPCServer server;

        private NodeWatcher(RPCServer server) {
            this.server = server;
        }

        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == EventType.NodeDataChanged) {//数据变更，覆盖数据
                log.debug("逻辑节点，数据修改--" + event.getPath());
                RPCServer tmp = clientMap.get(server.getServerName());
                RPCServer n = JsonUtil.toObj(zk.get(event.getPath()), RPCServer.class);
                tmp.reset(n);
                resetServer();
                zk.exists(event.getPath(), this);
            } else if (event.getType() == EventType.NodeDeleted) {
                log.info("逻辑节点删除,节点名称:{}, path:{}", server.getServerName(), event.getPath());
                clientMap.remove(server.getServerName());
                resetServer();
            }
        }
    }

    /**
     * zk列表发生变动，重新刷新节点列表初始数据
     *
     * @param serverList
     */
    public static void updateRPCServer(List<RPCServer> serverList) {
        clientMap = serverList.stream().map(server -> clientMap.get(server.getServerName())).filter(server -> server != null).collect(Collectors.toMap(RPCServer::getServerName, (val) -> val));
        resetServer();
    }

    public static void checkRPCServerLive() {
        clientMap.values()
            .stream()
            .filter(rpcServer -> !rpcServer.getSession().isActive())
            .peek(node -> log.error("Node Connecton is Close ->[{}]", node.getServerName()))
            .forEach(node -> node.setOpen(0));
        //		closeList.stream().forEach(node->{
        //			String zkPath = ZookeepServer.getLogicPath()+"/"+node.getServerName();
        //			RPCServer n =  JsonUtil.toObj(zk.get(zkPath), RPCServer.class);
        //			node.reset(n);
        //			resetServer();
        //			Watcher logicWatcher = new NodeWatcher(node);
        //			zk.exists(zkPath,logicWatcher);
        //		});
    }

    /**
     * 重置服务列表
     */
    private static synchronized void resetServer() {
        Map<String, List<RPCServer>> tp = Maps.newHashMap();
        Map<String, RPCServer> mp = Maps.newHashMap();
        clientMap.values().forEach(rpc -> tp.computeIfAbsent(rpc.getPool(), (key) -> Lists.newArrayList()).add(rpc));
        clientMap.values().stream().filter(rpc -> rpc.isMaster()).forEach(rpc -> mp.put(rpc.getPool(), rpc));
        poolMap = tp;
        masterMap = mp;
    }

    /**
     * 启动路由消息线程
     */
    public static void startRoute() {
        try {
            start();
        } catch (Exception e) {
            log.error("启动报错{}", e);
            log.error(e.getMessage(), e);
        }
        //		Runnable codeJob = ()->{
        //			RPCSource source = null;
        //			while(true){
        //				try {
        //					source = taskQueue.take();
        //					RouteServerManager.write(source);
        //				} catch (InterruptedException e) {
        //					log.err("<--------消息队列出错------>");
        //				} catch(Exception ex){
        //					ex.printStackTrace();
        //					log.err("<--------路由转发消息异常------>{}",source.getMethodCode());
        //				}
        //			}
        //		};
        //		//
        //		int threadCount = Runtime.getRuntime().availableProcessors()+2;
        //		for(int i = 0 ; i < threadCount ; i ++){
        //			Executors.newSingleThreadExecutor().execute(codeJob);
        //		}
    }

    public static void setZK(ZookeepServer zk) {
        RouteServerManager.zk = zk;
    }

    /**
     * 消息派发
     */
    private static void write(RPCSource source) {
        RPCServerMethod method = null;
        ERPCType type;
        try {
            if (source.getMethodCode() == RPCManager.CallBack) {
                type = ERPCType.NONE;
            } else {
                method = InstanceFactory.get().getServerMethodByCode(source.getMethodCode());
                type = method.getType();
            }
            long idx = index.incrementAndGet();
            source.setTeamId(idx);
            boolean ret = false;
            switch (type) {
                case ALL: //池全推送
                    ret = sendAllNodeOfPool(method, source);
                    break;
                case MASTER://主推送
                    ret = sendToMaster(method, source);
                    break;
                case NONE:
                    ret = sendTarget(method, source, idx);
                    break;
                case ALLNODE://池中的所有节点
                    ret = sendAll(source) > 0;
                    break;
            }

            if (!ret) {
                log.error("无可用节点，导致请求抛出{} mc {} {}", source.toString(), source.getMethodCode(), source.getServerMethod());
            }
        } catch (Exception e) {
            log.error(String.format("远程调用异常 source %s , mc %s msg %s", source, source.getMethodCode(), e.getMessage()), e);
        }
    }

    private static boolean sendTarget(RPCServerMethod method, RPCSource source, long index) {
        if (source.getReceive() == null) {//负载推送
            //取出存活的节点
            List<RPCServer> nodes = poolMap.get(method.getPool());
            if (nodes == null) {
                return false;
            }
            if (nodes.size() == 1) {
                RPCServer rpcServer = nodes.get(0);
                if (rpcServer.isActive()) {
                    rpcServer.send(source);
                    return true;
                }
            } else {//随机选择一个节点
                List<RPCServer> list = new ArrayList<>();
                for (RPCServer rpcServer : nodes) {
                    if (rpcServer.isActive()) {
                        list.add(rpcServer);
                    }
                }
                if (list.size() > 0) {
                    RPCServer node = list.get((int) index % list.size());
                    node.send(source);
                    return true;
                }
            }
        } else {
            int num = 0;
            for (String rec : source.getReceive()) {//指定节点推送
                RPCServer rpcServer = clientMap.get(rec);
                if (rpcServer != null && rpcServer.isActive()) {
                    rpcServer.send(source);
                    num++;
                }
            }
            return num > 0;
        }
        return false;
    }

    private static int sendAll(RPCSource source) {
        int num = 0;
        for (RPCServer node : clientMap.values()) {
            if (node.isActive()) {
                node.send(source);
                num++;
            }
        }
        return num;
    }

    private static boolean sendToMaster(RPCServerMethod method, RPCSource source) {
        RPCServer node = masterMap.get(method.getPool());
        if (node != null && node.isActive()) {
            node.send(source);
            return true;
        }
        return false;
    }

    private static boolean sendAllNodeOfPool(RPCServerMethod method, RPCSource source) {
        List<RPCServer> nodes = poolMap.get(method.getPool());
        int num = 0;
        if (nodes != null && nodes.size() > 0) {//池中所有节点
            for (RPCServer node : nodes) {
                if (node.isActive()) {
                    node.send(source);
                    num++;
                }
            }
        }
        return num > 0;
    }

}
