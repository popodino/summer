package org.cjl.summer.summermvc.aop.proxy;

import org.cjl.summer.jdkproxy.InvocationHandler;
import org.cjl.summer.jdkproxy.Proxy;
import org.cjl.summer.jdkproxy.ProxyClassLoader;
import org.cjl.summer.summermvc.aop.intercept.MethodInvocation;
import org.cjl.summer.summermvc.aop.support.AdvisedSupport;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Title: JdkAopProxy
 * @Package: org.cjl.summer.summermvc.aop.proxy
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class JdkAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport advisedSupport;

    public JdkAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getproxy() {
        return Proxy.newProxyInstance(new ProxyClassLoader(), this.advisedSupport.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object getproxy(ClassLoader classLoader) {
        return getproxy();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorAndDynamicMethodMatcher = advisedSupport
                .getInterceptorOrDynamicInterceptionAdvice(method, advisedSupport.getTargetClass());
        MethodInvocation invocation = new MethodInvocation(proxy,advisedSupport.getTarget(),advisedSupport.getTargetClass()
                ,method,args,interceptorAndDynamicMethodMatcher);
        return invocation.proceed();
    }
}
