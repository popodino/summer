package org.cjl.summer.summermvc.aop.proxy;

/**
 * @Title: AopProxy
 * @Package: org.cjl.summer.summermvc.aop.proxy
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public interface AopProxy {

    Object getproxy();

    Object getproxy(ClassLoader classLoader);
}
