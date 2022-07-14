package org.cjl.summer.mybatis.executor;

import org.cjl.summer.mybatis.parameter.ParameterHandler;
import org.cjl.summer.mybatis.session.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Title: StatementHandler
 * @Package: org.cjl.summer.mybatis.executor
 * @Description: mock the mybatis statementHandler
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class StatementHandler {
    private ResultSetHandler resultSetHandler = new ResultSetHandler();

    public <T> List<T> query(String statement, Object[] parameters, Class resultType) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        List<T> result = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(statement);
            new ParameterHandler(preparedStatement).setParameter(parameters);
            preparedStatement.execute();

            result = resultSetHandler.handle(preparedStatement.getResultSet(),resultType);

            return result;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(null != preparedStatement){
                preparedStatement.close();
            }
            if(null != connection){
                connection.close();
            }
        }
    }

    private Connection getConnection() {
        String driver = Configuration.PROPERTIES.getString("jdbc.driver");
        String url = Configuration.PROPERTIES.getString("jdbc.url");
        String username = Configuration.PROPERTIES.getString("jdbc.username");
        String password = Configuration.PROPERTIES.getString("jdbc.password");
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
