package com.hzone.db.conn.dao;

import com.hzone.server.instance.InstanceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 异步批量更新和保存数据到数据库
 * @author zehong.he
 *
 */
public class DBManager {
    private static final Logger log = LoggerFactory.getLogger(DBManager.class);
    /** 总执行次数 */
    private static AtomicLong totalRunCount = new AtomicLong(0);
    /** 是否可以异步更新数据. 同一时刻单个应用只能有一个线程在执行异步更新 */
    private static AtomicBoolean canRun = new AtomicBoolean(true);
    /** 空转次数 */
    private static AtomicInteger emptyLoopNum = new AtomicInteger(0);
    /** logic 异步更新数据库队列(单个逻辑服) */
    private static final AsyncQueue logicQueue = new AsyncQueue();
    /** 运营活动和策划配置 异步更新数据库队列 */
    private static final AsyncQueue activeQueue = new AsyncQueue();
    /** 运营日志 */
    private static final AsyncQueue oplogQueue = new AsyncQueue(Collections.emptySet());

    /** 同一张表最大循环次数 */
    private static final int MAX_LOOP = 10;
    /** 每次循环最多批量处理多少条数据 */
    private static final int MAX_NUM_EACH_LOOP = 1000;

    // 新的保存方法  have to test
    public static void run(boolean isStop) {
        if (!isStop && emptyLoopNum.incrementAndGet() < 10 && !canRun.getAndSet(false)) {
            return;
        }
        totalRunCount.incrementAndGet();
        emptyLoopNum.set(0);
        final boolean execDelSql = isStop || totalRunCount.get() % 10 == 0;
        batchUpdate(DBHolder.GAME_DB, execDelSql, logicQueue);
        batchUpdate(DBHolder.ACTIVE_DB, execDelSql, activeQueue);
        batchUpdate(DBHolder.OP_LOG_DB, execDelSql, oplogQueue);
        canRun.set(true);

        if (log.isDebugEnabled()) {
            log.debug("批量更新数据完毕");
        }
    }

    private static void batchUpdate(BaseDAO conn, boolean execDelSql, AsyncQueue aq) {

        for (Map.Entry<String, LinkedBlockingQueue<AsynchronousBatchDB>> e : aq.queues.entrySet()) {
            String qn = e.getKey();
            LinkedBlockingQueue<AsynchronousBatchDB> queue = e.getValue();
            if (queue.peek() == null) {
                continue;
            }

            for (int loopi = 0; loopi < MAX_LOOP; loopi++) {
                final int qs = queue.size();//performence. dependency LinkedBlockingQueue.size
                final int size = Math.min(MAX_NUM_EACH_LOOP, qs);
//                if (log.isTraceEnabled()) {
//                    log.trace("batch update qn {} loopi {} qs {} size {}", qn, loopi, qs, size);
//                }
                if (size <= 0) {
                    break;
                }
                List<AsynchronousBatchDB> objs = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    AsynchronousBatchDB obj = queue.poll();
                    if (obj == null) {
                        break;
                    }
                    objs.add(obj);
                    obj.unSave();
                }
                AsynchronousBatchDB first = objs.get(0);
                conn.batchUpdate(objs, first.getTableName(), first.getRowNames(), first.isSqlLoggerDebugEnabled(), first.isSqlLoggerInfoEnabled());
                if (log.isDebugEnabled()) {
                    log.debug("batch update qn {} loopi {} key {} num {}", qn, loopi, first.getTableName(), qs);
                }
                if (size < MAX_NUM_EACH_LOOP) {
                    break;
                }
            }
        }

        if (execDelSql) {
            aq.delSql.forEach(sql -> conn.execute(sql));
        }
    }

    /** 添加logic数据到异步队列 */
    public static void addAsynLogicDB(AsynchronousBatchDB db) {
        logicQueue.add(db);
    }

    public static void addAsynOpLogDB(AsynchronousBatchDB db) {
        log.debug("add op log table {}", db.getTableName());
        oplogQueue.add(db);
    }

    /** 添加运营活动数据到异步队列 */
    public static void addAsyncActiveDB(AsynchronousBatchDataDB db) {
        activeQueue.add(db);
    }

    public static void addLogicDelSql(String sql) {
        logicQueue.addDelSql(sql);
    }

    public static void addActiveDelSql(String sql) {
        activeQueue.addDelSql(sql);
    }

    private static final class AsyncQueue {
        /** 异步更新数据库队列. map[tableName, Queue[Values] */
        private final ConcurrentMap<String, LinkedBlockingQueue<AsynchronousBatchDB>> queues = new ConcurrentHashMap<>();
        /** 数据清理sql */
        private final Set<String> delSql;

        AsyncQueue() {
            this.delSql = new LinkedHashSet<>();
        }

        AsyncQueue(Set<String> delSql) {
            this.delSql = delSql;
        }

        void add(AsynchronousBatchDB db) {
            if (db == null) {
                return;
            }
            LinkedBlockingQueue<AsynchronousBatchDB> list = queues.computeIfAbsent(db.getTableName(), k -> new LinkedBlockingQueue<>());
            list.offer(db);
        }

        void addDelSql(String sql) {
            delSql.add(sql);
        }
    }

    private static final class DBHolder {
        static final GameConnectionDAO GAME_DB;
        static final DataConnectionDAO ACTIVE_DB;//
        static final OpLogConnectionDAO OP_LOG_DB;//

        static {
            GAME_DB = new GameConnectionDAO();
            GAME_DB.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_game.getResName());

            ACTIVE_DB = new DataConnectionDAO();
            ACTIVE_DB.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_data.getResName());

            OP_LOG_DB = new OpLogConnectionDAO();
            OP_LOG_DB.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_Op_Log.getResName());
        }
    }
}
