package com.hzone.db.conn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.hzone.db.conn.dao.ResourceType;


/**
 * 数据源辨识注解，运用在多数据库
 * @author zehong.he
 *
 */
@Target(ElementType.FIELD)   
@Retention(RetentionPolicy.RUNTIME)   
public @interface Resource {
	public ResourceType value();   
}
