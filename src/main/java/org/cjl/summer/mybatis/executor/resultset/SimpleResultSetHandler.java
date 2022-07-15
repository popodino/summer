package org.cjl.summer.mybatis.executor.resultset;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Title: ResultSetHandler
 * @Package: org.cjl.summer.mybatis.executor
 * @Description: mock the mybatis resultSetHandler
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class SimpleResultSetHandler implements ResultSetHandler {

    private static final List<Class<?>> defalutResultType = new ArrayList<>();

    static {
        defalutResultType.add(String.class);
        defalutResultType.add(Integer.class);
        defalutResultType.add(int.class);
        defalutResultType.add(boolean.class);
        defalutResultType.add(long.class);
        defalutResultType.add(float.class);
        defalutResultType.add(double.class);
    }

    @Override
    public <T> List<T> handle(ResultSet resultSet, Class resultType){
        List<Object> resultList = new ArrayList<>();
        try {
            while (resultSet.next()){
                Object obj = null;
                if(!defalutResultType.contains(resultType)){
                    obj = resultType.getConstructor().newInstance();
                    for (Field field : resultType.getDeclaredFields()) {
                        setValue(obj,field,resultSet);
                    }
                }else {
                    obj = getBasedDataType(resultSet,resultType);
                }

                resultList.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (List<T>) resultList;
    }

    private void setValue(Object obj, Field field, ResultSet resultSet) {
        try {
            Method method = obj.getClass().getMethod("set"+toUpperCapital(field.getName()),field.getType());
            method.invoke(obj,getResult(field, resultSet));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getResult(Field field, ResultSet resultSet) throws SQLException {
        Class type = field.getType();
        String dataName = humpToUnderline(field.getName());
        if (Integer.class == type || int.class == type) {
            return resultSet.getInt(dataName);
        } else if (String.class == type) {
            return resultSet.getString(dataName);
        } else if (Long.class == type) {
            return resultSet.getLong(dataName);
        } else if (Boolean.class == type) {
            return resultSet.getBoolean(dataName);
        } else if (Double.class == type) {
            return resultSet.getDouble(dataName);
        } else {
            return resultSet.getString(dataName);
        }
    }

    private String humpToUnderline(String name) {
        StringBuilder sb = new StringBuilder(name);
        int temp = 0;
        if(!name.contains("_")){
            for (int i = 0; i < name.length(); i++) {
                if (Character.isUpperCase(name.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    private String toUpperCapital(String str){
        String capital = str.substring(0,1);
        String tail = str.substring(1);
        return capital.toUpperCase() + tail;
    }

    private Object getBasedDataType(ResultSet resultSet,Class<?> type) throws SQLException {
        if (Integer.class == type || int.class == type) {
            return resultSet.getInt(1);
        } else if (String.class == type) {
            return resultSet.getString(1);
        } else if (Long.class == type) {
            return resultSet.getLong(1);
        } else if (Boolean.class == type) {
            return resultSet.getBoolean(1);
        } else if (Double.class == type) {
            return resultSet.getDouble(1);
        } else {
            return resultSet.getString(1);
        }
    }
}
