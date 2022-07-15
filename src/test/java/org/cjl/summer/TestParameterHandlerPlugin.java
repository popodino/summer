package org.cjl.summer;

import org.cjl.summer.mybatis.annotation.Intercepts;
import org.cjl.summer.mybatis.executor.parameter.ParameterHandler;
import org.cjl.summer.mybatis.plugin.Interceptor;
import org.cjl.summer.mybatis.plugin.Invocation;

import java.util.Arrays;

/**
 * @Title: TestPlugin
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/14/2022
 * @Version: V1.0
 */
@Intercepts(type = ParameterHandler.class,method = "setParameter")
public class TestParameterHandlerPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] parameter = (Object[]) invocation.getArgs()[1];
        System.out.println("[Info] --parameterHandlerPlugin-- parameter(before)：" + Arrays.toString(parameter));

        Object[] newParam = new Object[]{"110107"};

        System.out.println("[Info] --parameterHandlerPlugin-- parameter(after)：" + Arrays.toString(newParam));
        invocation.getArgs()[1] = newParam;

        return invocation.process();
    }
}
