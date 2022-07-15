package org.cjl.summer.mybatis.executor.statement;

import org.cjl.summer.mybatis.executor.resultset.ResultSetHandler;
import org.cjl.summer.mybatis.executor.parameter.ParameterHandler;
import org.cjl.summer.mybatis.executor.statement.cache.StatementCache;
import org.cjl.summer.mybatis.session.Configuration;

import java.sql.*;
import java.util.List;

/**
 * @Title: StatementHandler
 * @Package: org.cjl.summer.mybatis.executor
 * @Description: mock the mybatis statementHandler
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class SimpleStatementHandler implements StatementHandler {

    private Configuration configuration;
    private  ParameterHandler parameterHandler;
    private ResultSetHandler resultSetHandler;

    public SimpleStatementHandler(Configuration configuration) {
        this.configuration = configuration;
        this.resultSetHandler = configuration.newResultHandler();
        this.parameterHandler = configuration.newParameterHandler();
    }

    public <T> List<T> query(String statement, Object[] parameters, Class resultType) throws SQLException {

        List<T> result = null;

        try {

            PreparedStatement preparedStatement = StatementCache.getPrepareStatement(statement);
            preparedStatement = parameterHandler.setParameter(preparedStatement, parameters);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            result = resultSetHandler.handle(resultSet, resultType);
            resultSet.close();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
