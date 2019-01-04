package com.hzone.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 模块枚举
 * @author zehong.he
 *
 */
public enum EModuleCode {
    test(1, "test"),

    ;

    private int id;
    private String name;

    EModuleCode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //通过ID，取对应的战术枚举
    public static final Map<Integer, EModuleCode> moduleEnumMap = new HashMap<>();
    public static final Map<String, EModuleCode> names = new HashMap<>();

    static {
        for (EModuleCode et : EModuleCode.values()) {
            moduleEnumMap.put(et.getId(), et);
            names.put(et.getName().toLowerCase(), et);
        }
    }

    public static EModuleCode getEModuleCode(int id) {
        return moduleEnumMap.get(id);
    }

    public static EModuleCode convert(String name) {
        return names.get(name.toLowerCase());
    }

}
