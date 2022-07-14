package org.cjl.summer.mybatis.executor;

import java.sql.SQLException;
import java.util.List;

/**
 * @Title: Exector
 * @Package: org.cjl.summer.mybatis.executor
 * @Description: mock the mybatis executor
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public interface Executor {
    <T> List<T> query(String statement, Object[] parameter,Class resultType) throws SQLException;

    int update(String statement, Object[] parameter) throws SQLException;
}
