package org.cjl.summer;

import org.cjl.summer.mybatis.annotation.Intercepts;
import org.cjl.summer.mybatis.executor.Executor;
import org.cjl.summer.mybatis.executor.ResultSet.ResultSetHandler;
import org.cjl.summer.mybatis.plugin.Interceptor;
import org.cjl.summer.mybatis.plugin.Invocation;

import java.sql.ResultSet;
import java.util.Arrays;

/**
 * @Title: TestPlugin
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/14/2022
 * @Version: V1.0
 */
@Intercepts(type = ResultSetHandler.class, method = "handle")
public class TestResultSetHandlerPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ResultSet resultSet = (ResultSet) invocation.getArgs()[0];
        Class resultType = (Class) invocation.getArgs()[1];
        System.out.println("[Info] --resultSetHandlerPlugin-- columnCount：" + resultSet.getMetaData().getColumnCount());
        System.out.println("[Info] --resultSetHandlerPlugin-- resultType：" + resultType.getName());
        return invocation.process();
    }
}
