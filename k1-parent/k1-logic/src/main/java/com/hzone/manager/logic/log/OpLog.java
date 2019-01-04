package com.hzone.manager.logic.log;

import com.hzone.db.conn.dao.AsyncOpLogDB;
import com.hzone.db.conn.dao.BatchDataHelper;
import com.hzone.enums.EMoneyType;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** 运行操作日志 */
public class OpLog {
    /** 操作明细长度 */
    private static final int MAX_DETAIL_LEN = 16;

    /**  */
    private static class TeamLog {
        /** 球队id */
        private final long tid;
        /** 球队等级 */
        private final int lev;

        public TeamLog(long tid, int lev) {
            this.tid = tid;
            this.lev = lev;
        }
    }

    /** 操作来源 */
    public static class OpSrc {
        /** 来源模块 */
        private final int om;
        /** 来源接口(code) */
        private final int ot;

        public OpSrc(int om, int ot) {
            this.om = om;
            this.ot = ot;
        }
    }

    public static abstract class BaseLog extends AsyncOpLogDB {
        private static final long serialVersionUID = 1L;
        private List<Object> args = Collections.emptyList();

        public BaseLog setArgs(List<Object> args) {
            this.args = args;
            return this;
        }

        public List<Object> getArgs() {
            return args;
        }

        @Override
        public final List<Object> getRowParameterList() {
            return args;
        }

        public boolean isInsertSqlOnly() {
            return true;
        }
    }

    public static final class KVLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "kv_log";
        }

        @Override
        public String getRowNames() {
            return "k, v";
        }
    }

    /** 游戏键值数据日志 */
    public static void kvlog(String key, String val) {
        new KVLog()
            .setArgs(Lists.newArrayList(key, val))
            .save();
        GameKeyValLogManager.Log(key, val);//TODO temp
    }

    public static final class PropLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "prop_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, r_id, up_n, f_n, om, od";
        }
    }

    /** 道具操作日志 */
    public static void proplog(long tid, int propId, int upNum, int finalNum, ModuleLog om) {
        new PropLog()
            .setArgs(Lists.newArrayList(tid, propId, upNum, finalNum, om.getId(), om.getDetail(MAX_DETAIL_LEN)))
            .save();
        GamePropLogManager.Log(tid, propId, upNum, finalNum, om);//TODO temp
    }

    public static final class BesignLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "besign_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, b_id, p_id, price, up_n, om, od";
        }

    }

    /** 待签变动日志 */
    public static void besignlog(long tid, int bid, int playerId, int price, int upNum, ModuleLog module) {
        new BesignLog()
            .setArgs(Lists.newArrayList(tid, bid, playerId, price, upNum, module.getId(), module.getDetail(MAX_DETAIL_LEN)))
            .save();
        GameBesignLogManager.Log(tid, bid, playerId, price, upNum, module);//TODO temp
    }

    public static final class HelpstepLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "helpstep_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, t_lev, step";
        }

    }

    /** 新手引导日志 */
    public static void helpsteplog(long teamId, int teamLev, String helpStep) {
        if (helpStep != null && helpStep.length() > 256) {
            helpStep = helpStep.substring(0, 256);
        }
        new HelpstepLog()
            .setArgs(Lists.newArrayList(teamId, teamLev, helpStep))
            .save();
        GameHelpStepLogManager.Log(teamId, helpStep);//TODO temp
    }

    public static final class MoneyLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "money_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, type, up_n, f_n, om, od";
        }
    }

    /** 货币日志 */
    public static void moneylog(long teamId, EMoneyType type, int upNum, int finalNum, ModuleLog module) {
        new MoneyLog()
            .setArgs(Lists.newArrayList(teamId, type.getType(), upNum, finalNum, module.getId(), module.getDetail(MAX_DETAIL_LEN)))
            .save();
        GameMoneyLogManager.Log(teamId, type, upNum, finalNum, module);//TODO temp
    }

    public static final class OnlineLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "online_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, t_lev, sec";
        }
    }

    /** 在线时长数据日志 */
    public static void onlinelog(long teamId, long second, int level) {
        new OnlineLog()
            .setArgs(Lists.newArrayList(teamId, level, second))
            .save();
        GameOlineLogManager.Log(teamId, second, level);//TODO temp
    }

    public static final class PlayerLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "pr_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, p_id, pr_id, price, up_n, om, od";
        }
    }

    /** 球员变动日志 */
    public static void playerlog(long teamId, int pid, int playerRid, int price, int upNum, ModuleLog module) {
        new PlayerLog()
            .setArgs(Lists.newArrayList(teamId, pid, playerRid, price, upNum, module.getId(), module.getDetail(MAX_DETAIL_LEN)))
            .save();
        GamePlayerLogManager.Log(teamId, pid, playerRid, price, upNum, module);//TODO temp
    }

    public static final class PriceLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "price_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, p_id, pr_id, s_p, f_p, om, od";
        }
    }

    /** 自有球员身价变动日志 */
    public static void pricelog(long teamId, int pid, int playerRid, int srcPrice, int finalPrice, ModuleLog module) {
        new PriceLog()
            .setArgs(Lists.newArrayList(teamId, pid, playerRid, srcPrice, finalPrice, module.getId(), module.getDetail(MAX_DETAIL_LEN)))
            .save();
        GamePriceLogManager.Log(teamId, pid, playerRid, srcPrice, finalPrice, module);//TODO temp
    }

    public static final class ChatLog extends BaseLog {
        private static final long serialVersionUID = 1L;

        @Override
        public String getTableName() {
            return "chat_log";
        }

        @Override
        public String getRowNames() {
            return "t_id, t_n, t_lev, l_id, l_n, type, msg";
        }
    }

    /** 聊天日志 */
    public static void chatlog(long teamId, String teamName, int level, int leagueId, String leagueName, int type, String chatMsg) {
        new ChatLog()
            .setArgs(Lists.newArrayList(teamId, teamName, level, leagueId, leagueName, type, chatMsg))
            .save();
        GameChatLogManager.Log(teamId, teamName, level, leagueId, leagueName, type, chatMsg);//TODO temp
    }

    public static void main(String[] args) {
        BatchDataHelper.init();
        String str = BatchDataHelper.getPreSqlMap().entrySet().stream()
            .map(e -> e.getKey() + "\t" +
                e.getValue() + BatchDataHelper.findBatchDataParameterSql(e.getKey()) + BatchDataHelper.findBatchDataAfterSql(e.getKey()))
            .collect(Collectors.joining("\n"));
        System.out.println(str);
    }

}
