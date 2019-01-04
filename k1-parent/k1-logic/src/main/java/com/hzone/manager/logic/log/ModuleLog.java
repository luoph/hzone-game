package com.hzone.manager.logic.log;

import com.hzone.enums.EModuleCode;

public class ModuleLog {

    private EModuleCode code;
    /**
     * 模块明细说明
     */
    private String detail;

    private ModuleLog(EModuleCode code, String detail) {
        super();
        this.code = code;
        this.detail = detail;
    }

    /**
     * 创建模块日志
     *
     * @param code
     * @param detail
     * @return
     */
    public static ModuleLog getModuleLog(EModuleCode code, String detail) {
        return new ModuleLog(code, detail == null || detail.equals("") ? "null" : detail);
    }

    /**
     * 创建模块日志
     */
    public static ModuleLog build(EModuleCode code, String detail) {
        return new ModuleLog(code, detail == null || detail.equals("") ? "null" : detail);
    }

    public int getId() {
        return this.code.getId();
    }

    public String getDetail() {
        if (this.detail == null || this.detail.equals("")) {
            return "null";
        }
        return this.detail;
    }

    public String getDetail(int maxLen) {
        if (detail == null || detail.equals("")) {
            return code.getName();
        }
        if (detail.length() > maxLen) {
            return detail.substring(0, maxLen);
        }
        return detail;
    }

    public String getName() {
        return this.code.getName();
    }

    @Override
    public String toString() {
        return "{" +
            "\"code\":" + code +
            ", \"detail\":\"" + detail + "\"" +
            '}';
    }
}
