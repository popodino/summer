package org.cjl.summer.summermvc.core;

/**
 * @Title: BeanFactory
 * @Package: org.cjl.summer.summermvc.core
 * @Description: mock the spring BeanFactory
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public interface BeanFactory {

    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}
