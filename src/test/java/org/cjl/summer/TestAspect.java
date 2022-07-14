package org.cjl.summer;

import org.cjl.summer.summermvc.annotation.*;
import org.cjl.summer.summermvc.aop.aspect.JoinPoint;

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

    @Before
    public Object before(JoinPoint joinPoint) throws Throwable{

        System.out.println("============before============" + joinPoint.getArguments()[0].toString());
        return joinPoint.process();
    }

    @After
    public Object after(JoinPoint joinPoint) throws Throwable{
        System.out.println("============After============" + joinPoint.getArguments()[0].toString());
        return joinPoint.process();
    }

    @Around
    public Object around(JoinPoint joinPoint) throws Throwable{
        System.out.println("============Around============"+ joinPoint.getArguments()[0].toString());
        return joinPoint.process();
    }

    @AfterThrowing
    public Object afterThrowing(JoinPoint joinPoint,Throwable e) throws Throwable{
        System.out.println("============afterThrowing============"+ joinPoint.getArguments()[0].toString()
                + "-----" + e.getMessage());
        return joinPoint.process();
    }
}
