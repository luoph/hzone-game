package com.hzone.cfg.base;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;

import com.google.common.collect.ImmutableList;
import com.hzone.enums.EActionType;
import com.hzone.util.StringUtil;
import com.hzone.util.excel.ColData;
import com.hzone.util.excel.RowData;

public abstract class AbstractExcelBean extends ExcelBean {

    public final static Function<String, Integer> toInt = StringUtil.toInt;
    public final static Function<String, Float> toFloat = StringUtil.toFloat;
    public final static Function<String, String> toStr = StringUtil.toStr;

    /** 分割字符串为 map[int, int]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static Map<Integer, Integer> splitToIntMap(String str) {
        return splitToMap(str, toInt, toInt);
    }

    /** 分割字符串为 map[int, int]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static Map<EActionType, Integer> splitToActionMap(String str) {
        return splitToMap(str, s -> EActionType.convertByName(s), toInt);
    }

    /** 分割字符串为 map[int, int]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static Map<EActionType, Float> splitToActionFloatMap(String str) {
        return splitToMap(str, s -> EActionType.convertByName(s), toFloat);
    }

    /** 分割字符串为 map[str, int]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static Map<String, Integer> splitToStrIntMap(String str) {
        return splitToMap(str, toStr, toInt);
    }

    /** 分割字符串为 map[K, V]. 字符串格式: k1:v1,k2:v2,k3:v3, */
    public static <K, V> Map<K, V> splitToMap(String str, Function<String, K> keyMapper, Function<String, V> vMapper) {
       return StringUtil.splitToMap(str, keyMapper, vMapper);
    }

    public static int getInt(RowData row, String colName) {
        ColData cd = row.getColData(colName);
        if (cd == null) {
            return 0;
        }
        return Integer.parseInt(cd.getValueStr());
    }

    public static int getInt(String str) {
        return Integer.parseInt(str);
    }

    public static String getStr(RowData row, String colName) {
        ColData cd = row.getColData(colName);
        if (cd == null) {
            return "";
        }
        return cd.getValueStr();
    }

    public static boolean getBoolean(RowData row, String colName) {
        int i = getInt(row, colName);
        return i != 0;
    }

    public static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
        return ImmutableList.toImmutableList();
    }
}
