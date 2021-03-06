package org.cjl.summer;

import com.alibaba.fastjson.JSON;
import org.cjl.summer.mybatis.session.DefaultSqlSession;
import org.cjl.summer.mybatis.session.SqlSessionFactory;

import java.util.regex.Pattern;

/**
 * @Title: TestMybatis
 * @Package: org.cjl.summer
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/14/2022
 * @Version: V1.0
 */
public class TestMybatis {

    public static void main(String[] args) {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactory("org.cjl.summer");
        DefaultSqlSession sqlSession = sqlSessionFactory.openSqlSession();
        TestMapper testMapper = sqlSession.getMapper(TestMapper.class);
        String name = testMapper.getName(110000);

        System.out.println(" --- " + name);
    }
}
