package com.hzone.server;

import com.hzone.manager.Service;
import com.hzone.manager.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务订阅管理
 * @author zehong.he
 *
 */
public class ServiceManager {
    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
    private static Map<String, Service> SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 离线移除服务订阅
     *
     * @param teamId
     */
    public static void offline(long teamId) {
        User user = GameSource.getUser(teamId);
        List<Service> services = new ArrayList<>(SERVICE_MAP.values());
        for (Service service : services) {
            service.remove(user);
        }
    }

    public static Collection<User> getUsers(String key) {
        Service service = SERVICE_MAP.get(key);
        if (service == null) {
            return Collections.emptyList();
        }
        return service.getUsers().values();
    }

    public synchronized static void addService(String key, long teamId) {
        if (GameSource.isNPC(teamId)) {
            return;
        }
        User user = GameSource.getUser(teamId);
        Service service = SERVICE_MAP.get(key);
        if (service == null) {
            service = new Service(key);
            SERVICE_MAP.put(key, service);
        }
        service.putUser(user);
    }

    public static void removeService(String key, long teamId) {
        User user = GameSource.getUser(teamId);
        Service service = SERVICE_MAP.get(key);
        if (service != null) {
            service.remove(user);
        }
    }

    public static void clearTimeOutService() {
        Set<String> keys = SERVICE_MAP.keySet();
        Service service = null;
        for (String key : keys) {
            service = SERVICE_MAP.get(key);
            if (service.size() <= 0) {
                SERVICE_MAP.remove(key);
                log.debug("service remove key[{}]", key);
            }
        }
    }

    public static void removeService(String key) {
        SERVICE_MAP.remove(key);
    }

}
