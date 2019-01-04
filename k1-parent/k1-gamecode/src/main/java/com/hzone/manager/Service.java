package com.hzone.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Service {
    private ConcurrentMap<Long, User> users = new ConcurrentHashMap<>();
    private String key;

    public Service(String key) {
        this.key = key;
    }

    public void putUser(User user) {
        if (user == null) {
            return;
        }
        this.users.put(user.getTeamId(), user);
    }

    public void remove(User user) {
        if (user == null) {
            return;
        }
        this.users.remove(user.getTeamId());
    }

    public int size() {
        return this.users.size();
    }

    public String getKey() {
        return this.key;
    }

    public ConcurrentMap<Long, User> getUsers() {
        return users;
    }
}
