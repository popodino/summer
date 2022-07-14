package org.cjl.summer.summermvc.aop.intercept;

/**
 * @Title: MethodInterceptor
 * @Package: org.cjl.summer.summermvc.aop.intercept
 * @Description: mock the spring aop MethodInterceptor
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public interface MethodInterceptor {

    Object invoke(MethodInvocation invocation) throws Exception;
}
