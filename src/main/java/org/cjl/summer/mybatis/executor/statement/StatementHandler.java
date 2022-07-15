package org.cjl.summer.mybatis.executor.statement;

import java.sql.SQLException;
import java.util.List;

/**
 * @Title: StatementHandler
 * @Package: org.cjl.summer.mybatis.executor.statement
 * @Description: mock the mybatis StatementHandler
 * @Author: Jiulong_Chen
 * @Date: 7/15/2022
 * @Version: V1.0
 */
public interface StatementHandler {
    public <T> List<T> query(String statement, Object[] parameters, Class resultType) throws SQLException;
}
