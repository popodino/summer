package org.cjl.summer;

import org.cjl.summer.mybatis.annotation.Mapper;
import org.cjl.summer.mybatis.annotation.ResultType;
import org.cjl.summer.mybatis.annotation.Select;

import java.util.List;

/**
 * @Title: TestMapper
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/14/2022
 * @Version: V1.0
 */
@Mapper
public interface TestMapper {

    @Select(query = "select name from t_city where id = ?")
    //@ResultType(type = int.class)
    public String getName(String id);

    @Select(query = "select id,name,pinyin from t_city where id = ?")
    @ResultType(type = City.class)
    public City getCityById(String id);
}
