package com.hzone.util;

import com.google.gson.Gson;

/**
 * 
 * @author zehong.he
 *
 */
public class JsonUtil {	
	
	private static Gson g = new Gson();
	
	public static String toJson(Object t){
		return g.toJson(t);
	}
	
	public static <T> T toObj(String son,Class<? extends T> c){
		return g.fromJson(son, c);
	}
	
}
