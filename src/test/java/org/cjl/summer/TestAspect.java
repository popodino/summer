package org.cjl.summer;

import org.cjl.summer.summermvc.annotation.*;
import org.cjl.summer.summermvc.aop.aspect.JoinPoint;

import java.util.Arrays;
import java.util.Date;

/**
 * @Title: TestAspect
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/12/2022
 * @Version: V1.0
 */
@Aspect
public class TestAspect {

    @PointCut("public .* org.cjl.summer.*Service.*(.*)")
    public void pointCut(){}

    //@Before
    public Object before(JoinPoint joinPoint) throws Throwable{

        System.out.println("[Info] [Before] actionName: " + joinPoint.getMethod().getName() + " , args: "+ Arrays.toString(joinPoint.getArguments()));
        return joinPoint.proceed();
    }

    //@After
    public Object after(JoinPoint joinPoint) throws Throwable{
        System.out.println("[Info] [After] actionName: " + joinPoint.getMethod().getName() + " , args: "+ Arrays.toString(joinPoint.getArguments()));
        return joinPoint.proceed();
    }

    @Around
    public Object around(JoinPoint joinPoint) throws Throwable{
        long startTime = System.currentTimeMillis();
//        System.out.println("[Info] [Around] actionName: " + joinPoint.getMethod().getName()
//                + " , args: "+ Arrays.toString(joinPoint.getArguments()));
        Object obj = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("[Info] [Around] actionName: " + joinPoint.getMethod().getName()
                + " , args: "+ Arrays.toString(joinPoint.getArguments())
                + " , timeSpend: " + (endTime - startTime) + "ms"
                + " , result: " + obj.toString());
        return obj;
    }

    @AfterThrowing
    public Object afterThrowing(JoinPoint joinPoint,Throwable e) throws Throwable{
        System.out.println("============afterThrowing============"+ joinPoint.getArguments()[0].toString()
                + "-----" + e.getMessage());
        return joinPoint.proceed();
    }
}
