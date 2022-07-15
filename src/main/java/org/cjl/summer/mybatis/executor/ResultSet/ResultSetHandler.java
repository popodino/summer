package org.cjl.summer.mybatis.executor.ResultSet;

import java.sql.ResultSet;
import java.util.List;

/**
 * @Title: ResultSetHandler
 * @Package: org.cjl.summer.mybatis.executor.ResultSet
 * @Description: mock the mybatis ResultSetHandler
 * @Author: Jiulong_Chen
 * @Date: 7/15/2022
 * @Version: V1.0
 */
public interface ResultSetHandler {
    public <T> List<T> handle(ResultSet resultSet, Class resultType);
}
