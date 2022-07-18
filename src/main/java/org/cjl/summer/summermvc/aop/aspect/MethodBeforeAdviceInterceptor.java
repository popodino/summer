package org.cjl.summer.summermvc.aop.aspect;

import org.cjl.summer.summermvc.aop.intercept.MethodInterceptor;
import org.cjl.summer.summermvc.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @Title: MethodBeforeAdviceInterceptor
 * @Package: org.cjl.summer.summermvc.aop.aspect
 * @Description: mock the spring aop methodBeforeAdviceInterceptor
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    JoinPoint joinPoint;


    public MethodBeforeAdviceInterceptor(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Exception {
        this.joinPoint = invocation;
        if(invocation.getMethod().getReturnType() == Void.class){
            before();
            return invocation.proceed();
        }else {
            return before();
        }

    }

    private Object before() throws Exception{
        return super.invokeAdviceMethod(this.joinPoint,null,null);
    }
}
