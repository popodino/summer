package org.cjl.summer.mybatis.binding;

import org.cjl.summer.jdkproxy.InvocationHandler;
import org.cjl.summer.mybatis.annotation.ResultType;
import org.cjl.summer.mybatis.session.DefaultSqlSession;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Title: MapperProxy
 * @Package: org.cjl.summer.mybatis.binding
 * @Description: mock the mybatis MapperProxy
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class MapperProxy implements InvocationHandler {

   private DefaultSqlSession sqlSession;
    public MapperProxy(DefaultSqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Class<?> resultType = method.getReturnType();
        if(method.isAnnotationPresent(ResultType.class)){
            resultType = method.getAnnotation(ResultType.class).type();
        }
        String statementId = method.getDeclaringClass().getName() + "."+method.getName();
        if(sqlSession.getConfiguration().hasStatement(statementId)){
            if(method.getReturnType() == List.class){
                return sqlSession.selectList(statementId, args,resultType);
            }else{
                return sqlSession.selectOne(statementId, args,resultType);
            }
        }
        return method.invoke(proxy,args);
    }
}
