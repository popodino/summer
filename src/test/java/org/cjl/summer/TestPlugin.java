package org.cjl.summer;

import org.cjl.summer.mybatis.annotation.Intercepts;
import org.cjl.summer.mybatis.executor.CachingExecutor;
import org.cjl.summer.mybatis.executor.Executor;
import org.cjl.summer.mybatis.executor.SimpleExecutor;
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
@Intercepts(type = Executor.class,method = "query")
public class TestPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String statement = (String) invocation.getArgs()[0];
        Object[] parameter = (Object[]) invocation.getArgs()[1];
        Class pojo = (Class) invocation.getArgs()[2];

        System.out.println("[Info] SQL：[" + statement + "]");
        System.out.println("[Info] Parameters：" + Arrays.toString(parameter));
        return invocation.process();
    }
}
