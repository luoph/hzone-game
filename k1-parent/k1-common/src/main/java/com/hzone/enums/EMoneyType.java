package com.hzone.enums;

import java.util.HashMap;
import java.util.Map;

public enum EMoneyType {
    // 这里的name对应道具配置config里的配置
    /** 球卷 */
    Money(0, "money"),
    /** 绑定球券 */
    Bind_Money(1, "bdmoney"),
    /** 经费 */
    Gold(2, "gold"),
    /** 经验 */
    Exp(3, "exp"),
    /** 建设费 */
    Build_Money(4, "jsf"),
    ;
    private final int type;
    private final String name;

    EMoneyType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public static final Map<String, EMoneyType> nameCaches = new HashMap<>();

    public String getConfigName() {
        return name;
    }

    static {
        for (EMoneyType t : values()) {
            String configName = t.getConfigName().toLowerCase();
            if (nameCaches.containsKey(configName)) {
                throw new Error("duplicate action cfg name :" + configName);
            }
            nameCaches.put(configName, t);
        }
    }

    public static EMoneyType convertByName(String cfgName) {
        return nameCaches.get(cfgName);
    }

    public static EMoneyType getMoneyByName(String name) {
        for (EMoneyType t : EMoneyType.values()) {
            if (t.name.equals(name.toLowerCase())) {
                return t;
            }
        }
        return null;
    }

}
