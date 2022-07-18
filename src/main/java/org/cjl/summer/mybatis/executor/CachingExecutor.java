package org.cjl.summer.mybatis.executor;

import org.cjl.summer.mybatis.cache.CacheKey;
import org.cjl.summer.mybatis.session.Configuration;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: CachingExecutor
 * @Package: org.cjl.summer.mybatis.executor
 * @Description: mock the mybatis cachingExecutor
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class CachingExecutor implements Executor {

    private Executor delegate;

    private static final Map<Integer, Object> cache = new ConcurrentHashMap<>();

    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> List<T> query(String statement, Object[] parameter, Class resultType) throws SQLException {
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(statement);
        cacheKey.update(joinStr(parameter));

        if (cache.containsKey(cacheKey.getCode())){
            return (List<T>) cache.get(cacheKey.getCode());
        }else {
            List<T> result = delegate.query(statement,parameter,resultType);
            cache.put(cacheKey.getCode(),result);
            return result;
        }
    }

    @Override
    public int update(String statement, Object[] parameter) throws SQLException {
        return delegate.update(statement,parameter);
    }

    public String joinStr(Object[] objs) {
        StringBuffer sb = new StringBuffer();
        if (objs != null && objs.length > 0) {
            for (Object object : objs) {
                sb.append(object.toString() + ",");
            }
        }

        int len = sb.length();

        if (len > 0) {
            sb.deleteCharAt(len - 1);
        }
        return sb.toString();
    }
}
