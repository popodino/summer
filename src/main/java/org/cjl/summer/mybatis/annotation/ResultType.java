package org.cjl.summer.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @Title: MappingScan
 * @Package: org.cjl.summer.mybatis.annotation
 * @Description: mock the mybatis select annotation
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultType {
    Class<?> type();
}
