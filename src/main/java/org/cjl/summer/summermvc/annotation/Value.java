package org.cjl.summer.summermvc.annotation;

import java.lang.annotation.*;

/**
 * @Title: Controller
 * @Package: org.cjl.summer.summermvc.annotation
 * @Description: mock the spring Value annotation
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    String value() default "";
}