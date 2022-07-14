package org.cjl.summer.summermvc.beans;

/**
 * @Title: BeanWrapper
 * @Package: org.cjl.summer.summermvc.beans
 * @Description: mock the spring BeanWrapper
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class BeanWrapper {
    private Object beanInstance;
    private Class<?> beanClass;

    public BeanWrapper(Object beanInstance) {
        this.beanInstance = beanInstance;
    }

    public Object getWrappedInstance() {
        return beanInstance;
    }

    public Class<?> getWrappedClass() {
        return this.beanInstance.getClass();
    }
}
