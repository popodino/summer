package org.cjl.summer.summermvc.aop.aspect;

import java.lang.reflect.Method;

/**
 * @Title: AbstractAspectAdvice
 * @Package: org.cjl.summer.summermvc.aop.aspect
 * @Description: mock the spring aop AbstractAspectAdvice
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public abstract class AbstractAspectAdvice {
    private Method method;
    private Object target;

    public AbstractAspectAdvice(Method method, Object target) {
        this.method = method;
        this.target = target;
    }

    public Object invokeAdviceMethod(JoinPoint joinPoint, Object returnvalue, Throwable throwable) throws Exception {
        Class<?>[] paramTypes = this.method.getParameterTypes();
        if (null == paramTypes || paramTypes.length == 0) {
            return this.method.invoke(target);
        } else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = throwable;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnvalue;
                }
            }
            return this.method.invoke(target, args);
        }
    }
}
