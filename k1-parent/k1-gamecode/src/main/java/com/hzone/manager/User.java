package com.hzone.manager;

import org.apache.mina.core.session.IoSession;

public class User {
    private long teamId;//
    private IoSession session;
    private long loginTime = 0;//登录时间
    private int rate = 100;//防沉谜
    private int lev;

    public User(long teamId, IoSession session) {
        super();
        this.teamId = teamId;
        this.session = session;
    }

    public long getTeamId() {
        return teamId;
    }

    public void login() {
        loginTime = System.currentTimeMillis();
    }

    public long getLogin() {
        return loginTime;
    }
    //在线时长

    public int getLoginTime() {
        if (loginTime == 0) {
            return 0;
        }
        return Math.round((System.currentTimeMillis() - loginTime) / (1000 * 60.0f));
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
        this.rate = 100;
    }

    public int getLev() {
        return lev;
    }

    public void setLev(int lev) {
        this.lev = lev;
    }

    @Override
    public String toString() {
        return "User [teamId=" + teamId + "]";
    }

}
