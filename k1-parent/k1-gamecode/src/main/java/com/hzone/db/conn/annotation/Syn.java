package com.hzone.db.conn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步操作注解，一般配置实现了SynAO接口的AO使用,目前已废弃使用
 * @author zehong.he
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface Syn {

}
