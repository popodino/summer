package org.cjl.summer.mybatis.plugin;

import java.lang.annotation.Target;

/**
 * @Title: Interceptor
 * @Package: org.cjl.summer.mybatis.plugin
 * @Description: mock the mybatis interceptor
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public interface Interceptor {

    Object intercept(Invocation invocation) throws Throwable;

    default Object plugin(Object target){
        return Plugin.wrap(target,this);
    };
}
