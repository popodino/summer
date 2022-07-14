package org.cjl.summer.mybatis.plugin;

import org.cjl.summer.jdkproxy.InvocationHandler;
import org.cjl.summer.jdkproxy.Proxy;
import org.cjl.summer.jdkproxy.ProxyClassLoader;
import org.cjl.summer.mybatis.annotation.Intercepts;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Title: Plugin
 * @Package: org.cjl.summer.mybatis.plugin
 * @Description: mock the mybatis Plugin
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class Plugin implements InvocationHandler {

    private final Object target;
    private final Interceptor interceptor;
    private final Map<Class<?>, Set<Method>> pluginMethodMap;

    public Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> pluginMethodMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.pluginMethodMap = pluginMethodMap;
    }

    public static Object wrap(Object obj, Interceptor interceptor) {
        Map<Class<?>, Set<Method>> pluginMethod = getPluginMethod(interceptor);
        Class clazz = obj.getClass();
        return Proxy.newProxyInstance(new ProxyClassLoader()
                , clazz.getInterfaces(), new Plugin(obj, interceptor, pluginMethod));
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (pluginMethodMap.containsKey(method.getDeclaringClass())) {
            Set<Method> methods = pluginMethodMap.get(method.getDeclaringClass());
            if (methods.contains(method)) {
                return interceptor.intercept(new Invocation(target, method, args));
            }
        }
        return method.invoke(target, args);
    }

    private static Map<Class<?>, Set<Method>> getPluginMethod(Interceptor interceptor) {
        Intercepts intercepts = interceptor.getClass().getAnnotation(Intercepts.class);
        if (intercepts == null) {
            throw new RuntimeException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
        }

        Map<Class<?>, Set<Method>> pluginMethodMap = new HashMap<>();
        Set<Method> pluginMethods = new HashSet<>();

        Class pluginClass = intercepts.type();
        Method[] methods = pluginClass.getDeclaredMethods();
        for (String s : intercepts.method()) {
            for (Method method : methods) {
                if (s.equals(method.getName())) {
                    pluginMethods.add(method);
                }
            }
        }

        pluginMethodMap.put(pluginClass, pluginMethods);
        return pluginMethodMap;
    }

}
