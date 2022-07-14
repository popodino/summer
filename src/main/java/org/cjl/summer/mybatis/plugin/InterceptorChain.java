package org.cjl.summer.mybatis.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: InterceptorChain
 * @Package: org.cjl.summer.mybatis.plugin
 * @Description: mock the mybatis interceptorChain
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class InterceptorChain {
    private final List<Interceptor> interceptors = new ArrayList<>();

    public void addInterceptor(Interceptor interceptor){
        this.interceptors.add(interceptor);
    }

    public Object pluginAll(Object target){
        for (Interceptor interceptor : interceptors) {
            target =interceptor.plugin(target);
        }
        return target;
    }
    public boolean hasPlugin(){
        return !interceptors.isEmpty();
    }
}
