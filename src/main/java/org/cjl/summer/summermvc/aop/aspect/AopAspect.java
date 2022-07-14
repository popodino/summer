package org.cjl.summer.summermvc.aop.aspect;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Title: AopConfig
 * @Package: org.cjl.summer.summermvc.aop.config
 * @Description: AopConfig information
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class AopAspect {
    private String pointCut;
    private Class aspectClass;
    private Object aspectInstance;
    private Method aspectBefore;
    private Method aspectAfter;
    private Method aspectAfterReturning;
    private Method aspectAround;
    private Method aspectAfterThrowing;
    private String afterThrowingName;

    private Pattern pointCutClassPattern;
    private Pattern pointCutMethodPattern;

    public boolean pointCutMatchClass(Class<?> clazz){
        return pointCutClassPattern.matcher(clazz.toString()).matches();
    }

    public boolean pointCutMatchMethod(Method method){
        String methodString = method.toString();
        if(methodString.contains("throws")){
            methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
        }
        return pointCutMethodPattern.matcher(methodString).matches();
    }

    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
        pointCutMethodPattern = Pattern.compile(pointCut);
        String pointCutForClass = pointCut.substring(0,pointCut.lastIndexOf(")") - 3);
        pointCutClassPattern =Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") +1));

    }

    public Class getAspectClass() {
        return aspectClass;
    }

    public void setAspectClass(Class aspectClass) {
        this.aspectClass = aspectClass;
    }

    public Object getAspectInstance() {
        return aspectInstance;
    }

    public void setAspectInstance(Object aspectInstance) {
        this.aspectInstance = aspectInstance;
    }

    public Method getAspectBefore() {
        return aspectBefore;
    }

    public void setAspectBefore(Method aspectBefore) {
        this.aspectBefore = aspectBefore;
    }

    public Method getAspectAfter() {
        return aspectAfter;
    }

    public void setAspectAfter(Method aspectAfter) {
        this.aspectAfter = aspectAfter;
    }

    public Method getAspectAfterReturning() {
        return aspectAfterReturning;
    }

    public void setAspectAfterReturning(Method aspectAfterReturning) {
        this.aspectAfterReturning = aspectAfterReturning;
    }

    public Method getAspectAround() {
        return aspectAround;
    }

    public void setAspectAround(Method aspectAround) {
        this.aspectAround = aspectAround;
    }

    public Method getAspectAfterThrowing() {
        return aspectAfterThrowing;
    }

    public void setAspectAfterThrowing(Method aspectAfterThrowing) {
        this.aspectAfterThrowing = aspectAfterThrowing;
    }

    public String getAfterThrowingName() {
        return afterThrowingName;
    }

    public void setAfterThrowingName(String afterThrowingName) {
        this.afterThrowingName = afterThrowingName;
    }
}

