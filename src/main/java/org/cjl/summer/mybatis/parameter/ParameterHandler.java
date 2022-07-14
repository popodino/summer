package org.cjl.summer.mybatis.parameter;

import java.sql.PreparedStatement;

/**
 * @Title: ParameterHandler
 * @Package: org.cjl.summer.mybatis.parameter
 * @Description: mock the mybatis ParameterHandler
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class ParameterHandler {
    private PreparedStatement preparedStatement;

    public ParameterHandler(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public void setParameter(Object[] parameters) {
        try {

            for (int i = 0; i < parameters.length; i++) {
                int k = i + 1;
                if (parameters[i] instanceof Integer) {
                    preparedStatement.setInt(k, (Integer) parameters[i]);
                } else if (parameters[i] instanceof Long) {
                    preparedStatement.setLong(k, (Long) parameters[i]);
                } else if (parameters[i] instanceof Double) {
                    preparedStatement.setDouble(k, (Double) parameters[i]);
                } else if (parameters[i] instanceof Boolean) {
                    preparedStatement.setBoolean(k, (Boolean) parameters[i]);
                } else {
                    preparedStatement.setString(k, String.valueOf(parameters[i]));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
