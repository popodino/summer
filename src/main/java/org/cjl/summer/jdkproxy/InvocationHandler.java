package org.cjl.summer.jdkproxy;

import java.lang.reflect.Method;

/**
 * @Title: InvocationHandler
 * @Package: org.cjl.summer.jdkproxy
 * @Description: Mock InvocationHandler of JDKDynamicProxy
 * @Author: Jiulong_Chen
 * @Date: 7/7/2022
 * @Version: V1.0
 */
public interface InvocationHandler {

    /**
     * @Title: invoke
     * @Description: Mock Invoke of JDKDynamicProxy
     * @Param: [proxy, method, args]
     * @Return: java.lang.Object
     * @Author: Jiulong_Chen
     * @Date: 7/7/2022 4:59 PM
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}
