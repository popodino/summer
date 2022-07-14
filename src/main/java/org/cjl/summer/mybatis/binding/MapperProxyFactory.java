package org.cjl.summer.mybatis.binding;

import org.cjl.summer.jdkproxy.Proxy;
import org.cjl.summer.jdkproxy.ProxyClassLoader;
import org.cjl.summer.mybatis.session.DefaultSqlSession;

/**
 * @Title: MapperProxyFactory
 * @Package: org.cjl.summer.mybatis.binding
 * @Description: mock the mybatis MapperProxyFactory
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class MapperProxyFactory<T> {
    private Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(DefaultSqlSession sqlSession){
        return (T) Proxy.newProxyInstance(new ProxyClassLoader()
                , new Class[]{mapperInterface},new MapperProxy(sqlSession));
    }
}
