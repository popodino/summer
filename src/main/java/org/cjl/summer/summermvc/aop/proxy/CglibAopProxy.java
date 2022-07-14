package org.cjl.summer.summermvc.aop.proxy;

import org.cjl.summer.summermvc.aop.support.AdvisedSupport;

/**
 * @Title: CglibAopProxy
 * @Package: org.cjl.summer.summermvc.aop.proxy
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/12/2022
 * @Version: V1.0
 */
public class CglibAopProxy implements AopProxy {

    private AdvisedSupport advisedSupport;

    public CglibAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    @Override
    public Object getproxy() {
        return null;
    }

    @Override
    public Object getproxy(ClassLoader classLoader) {
        return null;
    }
}
