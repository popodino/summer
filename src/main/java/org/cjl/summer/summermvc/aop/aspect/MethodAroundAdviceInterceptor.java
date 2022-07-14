package org.cjl.summer.summermvc.aop.aspect;

import org.cjl.summer.summermvc.aop.intercept.MethodInterceptor;
import org.cjl.summer.summermvc.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @Title: MethodBeforeAdviceInterceptor
 * @Package: org.cjl.summer.summermvc.aop.aspect
 * @Description: mock the spring aop MethodAroundAdviceInterceptor
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class MethodAroundAdviceInterceptor extends AbstractAspectAdvice implements MethodInterceptor {

    JoinPoint joinPoint;


    public MethodAroundAdviceInterceptor(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Exception {

        this.joinPoint = invocation;
//        around();
//        Object obj =  invocation.process();
//        around();
//        return obj;
        return around();
    }

    private Object around() throws Exception{
        return super.invokeAdviceMethod(this.joinPoint,null,null);
    }
}
