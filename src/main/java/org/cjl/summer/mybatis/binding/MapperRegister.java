package org.cjl.summer.mybatis.binding;

import org.cjl.summer.mybatis.session.DefaultSqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: MapperRegister
 * @Package: org.cjl.summer.mybatis.binding
 * @Description: mock the mybatis MapperRegister
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class MapperRegister {
    private final Map<Class<?>, MapperProxyFactory> mappers = new HashMap<>();

    public <T> void addMapper(Class<T> clazz){
        mappers.put(clazz, new MapperProxyFactory<T>(clazz));
    }

    public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession){
        if(!mappers.containsKey(clazz)){
            throw new RuntimeException("Type: " + clazz + " can not find");
        }

        return (T) mappers.get(clazz).newInstance(sqlSession);
    }
}
