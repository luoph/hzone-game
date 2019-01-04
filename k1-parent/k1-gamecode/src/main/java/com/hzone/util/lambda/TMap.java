package com.hzone.util.lambda;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * @author zehong.he
 *
 */
public class TMap {
	private Map<String,Object> map;

	public TMap() {
		map = Maps.newHashMap();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key){
		Object val = map.get(key);
		if(val == null) return null;
		return (T) val;
	}
	
	
	public void put(String key,Object val){
		this.map.put(key, val);
	}
	
	
	
}
