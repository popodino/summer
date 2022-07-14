package org.cjl.summer.summermvc.beans.config;

/**
 * @Title: BeanDefinition
 * @Package: org.cjl.summer.summermvc.beans.config
 * @Description: mock the spring BeanDefinition
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class BeanDefinition {
    private String beanClassName;
    private boolean isLazyInit = false;
    private String factoryBeanName;
    private boolean isSingleton = true;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return isLazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        isLazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }
}
