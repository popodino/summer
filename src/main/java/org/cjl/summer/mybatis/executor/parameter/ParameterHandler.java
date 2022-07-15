package org.cjl.summer.mybatis.executor.parameter;

/**
 * @Title: ParameterHandler
 * @Package: org.cjl.summer.mybatis.executor.parameter
 * @Description: mock the mybatis ParameterHandler
 * @Author: Jiulong_Chen
 * @Date: 7/15/2022
 * @Version: V1.0
 */
public interface ParameterHandler {
    public void setParameter(Object[] parameters);
}
