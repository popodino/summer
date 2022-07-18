package org.cjl.summer.mybatis.session;

import org.cjl.summer.mybatis.executor.Executor;

import java.sql.SQLException;
import java.util.List;

/**
 * @Title: DefaultSqlSession
 * @Package: org.cjl.summer.mybatis.session
 * @Description: mock the mybatis DefaultSqlSession
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class DefaultSqlSession {

    private Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        this.executor = configuration.newExecutor();
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public <T> T getMapper(Class<T> clazz) {
        return configuration.getMapper(clazz, this);
    }

    public <T> T selectOne(String statement, Object[] parameters, Class resultType) throws SQLException {
        String sql = configuration.getStatement(statement);
        sql = (null == sql || "".equals(sql)) ? statement : sql;
        List<T> resultList = executor.query(sql, parameters, resultType);
        return (null != resultList && !resultList.isEmpty()) ? resultList.get(0) : null;
    }

    public <T> List<T> selectList(String statement, Object[] parameters, Class resultType) throws SQLException {
        String sql = configuration.getStatement(statement);
        sql = (null == sql || "".equals(sql)) ? statement : sql;
        return executor.query(sql, parameters, resultType);
    }

    public int update(String statement, Object[] parameters) throws SQLException {
        String sql = configuration.getStatement(statement);
        sql = (null == sql || "".equals(sql)) ? statement : sql;
        return executor.update(sql, parameters);
    }
}
