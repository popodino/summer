package org.cjl.summer.summermvc.aop.aspect;

import java.lang.reflect.Method;

/**
 * @Title: JoinPoint
 * @Package: org.cjl.summer.summermvc.aop.aspect
 * @Description: mock the spring aop joinpoint
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public interface JoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    Object process() throws Exception;
}
