package org.cjl.summer.summermvc.aop.support;

import org.cjl.summer.summermvc.aop.aspect.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Title: AdviseSupport
 * @Package: org.cjl.summer.summermvc.aop.support
 * @Description: mock the spring aop AdvisedSupport
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class AdvisedSupport {

    private Class<?> targetClass;
    private Object target;

    private AopAspect aopAspect;
    private Map<Method, List<Object>> methodCache;

    public AdvisedSupport(Class<?> targetClass, Object target, AopAspect aopAspect) {
        this.targetClass = targetClass;
        this.target = target;
        this.aopAspect = aopAspect;
        parse();
    }

    public List<Object> getInterceptorOrDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {
        List<Object> cached = methodCache.get(method);
        if (null == cached) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    private void parse() {

        try {
            methodCache = new HashMap<>();

            for (Method method : this.targetClass.getMethods()) {
                if (aopAspect.pointCutMatchMethod(method)) {
                    List<Object> advices = new LinkedList<>();

                    //Around
                    if (null != aopAspect.getAspectAround()) {
                        advices.add(new MethodAroundAdviceInterceptor(aopAspect.getAspectAround(), aopAspect.getAspectInstance()));
                    }
                    //Before
                    if (null != aopAspect.getAspectBefore()) {
                        advices.add(new MethodBeforeAdviceInterceptor(aopAspect.getAspectBefore(), aopAspect.getAspectInstance()));
                    }
                    //AfterThrowing
                    if (null != aopAspect.getAspectAfterThrowing()) {
                        advices.add(new AfterThrowingAdviceInterceptor(aopAspect.getAspectAfterThrowing(), aopAspect.getAspectInstance()));
                    }
                    //After
                    if (null != aopAspect.getAspectAfter()) {
                        advices.add(new MethodAfterAdviceInterceptor(aopAspect.getAspectAfter(), aopAspect.getAspectInstance()));
                    }

                    methodCache.put(method, advices);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
