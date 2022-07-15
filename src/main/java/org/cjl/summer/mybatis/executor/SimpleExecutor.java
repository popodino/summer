package org.cjl.summer.mybatis.executor;

import org.cjl.summer.mybatis.executor.statement.SimpleStatementHandler;
import org.cjl.summer.mybatis.executor.statement.StatementHandler;
import org.cjl.summer.mybatis.session.Configuration;

import java.sql.SQLException;
import java.util.List;

/**
 * @Title: SimpleExecutor
 * @Package: org.cjl.summer.mybatis.executor
 * @Description: mock the mybatis simpleExecutor
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class SimpleExecutor implements Executor {

    private Configuration configuration;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> List<T> query(String statement, Object[] parameter, Class resultType) throws SQLException {
        StatementHandler statementHandler = configuration.newStatementHandler();
        return statementHandler.query(statement,parameter,resultType);
    }

    @Override
    public int update(String statement, Object[] parameter) throws SQLException {
        return 0;
    }
}
