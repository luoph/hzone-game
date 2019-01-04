package com.hzone.annotation;

import com.hzone.enums.ERPCType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc方法注解
 * 初始化方法属性
 * @author zehong.he
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCMethod {
	int code();
	ERPCType type();
	String pool();
}
