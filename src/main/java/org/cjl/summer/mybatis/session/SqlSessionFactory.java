package org.cjl.summer.mybatis.session;

/**
 * @Title: SqlSessionFactory
 * @Package: org.cjl.summer.mybatis.session
 * @Description: mock the mybatis SqlSessionFactory
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class SqlSessionFactory {
    private Configuration configuration;

    public SqlSessionFactory(String mapperScan) {
        this.configuration = new Configuration(mapperScan);
    }

    public Configuration getConfiguration(){
        return this.configuration;
    }

    public DefaultSqlSession openSqlSession(){
        return new DefaultSqlSession(configuration);
    }
}
