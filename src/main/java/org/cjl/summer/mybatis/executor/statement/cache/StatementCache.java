package org.cjl.summer.mybatis.executor.statement.cache;

import org.cjl.summer.mybatis.session.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: StatementCache
 * @Package: org.cjl.summer.mybatis.executor.statementcache
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/15/2022
 * @Version: V1.0
 */
public class StatementCache {

    private volatile static StatementCache statementCache = null;
    private static Connection connection = null;
    private static final Map<String, PreparedStatement> cache = new ConcurrentHashMap<>();

    private StatementCache(){}

    public static StatementCache getInstance(){
        if (statementCache == null){
            synchronized (StatementCache.class){
                if (statementCache == null){
                    statementCache = new StatementCache();
                }
            }
        }
        return statementCache;
    }

    public void registryStatements(Map<String, String> mappedStatements){
        if(connection == null){
            connection = getConnection();
        }

        mappedStatements.forEach((statement,sql) -> {
            if(!cache.containsKey(sql)){
                try {
                    cache.put(sql,connection.prepareStatement(sql));
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static PreparedStatement getPrepareStatement(String statement){
        return cache.get(statement);
    }

    private static Connection getConnection() {
        String driver = Configuration.PROPERTIES.getString("jdbc.driver");
        String url = Configuration.PROPERTIES.getString("jdbc.url");
        String username = Configuration.PROPERTIES.getString("jdbc.username");
        String password = Configuration.PROPERTIES.getString("jdbc.password");
        Connection connection = null;
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
