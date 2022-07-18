package org.cjl.summer.summermvc.aop.intercept;

import org.cjl.summer.summermvc.aop.aspect.JoinPoint;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Title: MethodInvocation
 * @Package: org.cjl.summer.summermvc.aop.intercept
 * @Description: mock the spring aop MethodInvocation
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class MethodInvocation implements JoinPoint {

    private Object proxy;
    private Object target;
    private Class<?> targetClass;
    private Method method;
    private Object[] args;
    private List<Object> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    public MethodInvocation(Object proxy, Object target, Class<?> targetClass, Method method, Object[] args
            , List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.args = args;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    @Override
    public Object proceed() throws Exception {
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() -1){

            return this.method.invoke(this.target, this.args);
        }

        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        if(interceptorOrInterceptionAdvice instanceof MethodInterceptor){
            MethodInterceptor interceptor = (MethodInterceptor) interceptorOrInterceptionAdvice;
            return interceptor.invoke(this);
        }else {
            return proceed();
        }
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}
