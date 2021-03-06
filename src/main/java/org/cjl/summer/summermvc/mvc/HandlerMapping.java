package org.cjl.summer.summermvc.mvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Title: HandlerMapping
 * @Package: org.cjl.summer.summermvc.mvc
 * @Description: mock the spring handlerMapping
 * @Author: Jiulong_Chen
 * @Date: 7/12/2022
 * @Version: V1.0
 */
public class HandlerMapping {
    private Object controller;
    private Method method;
    private String methodType;
    private Pattern pattern;

    private String[] pathVariables;

    public HandlerMapping(Object controller, Method method, String methodType, Pattern pattern, String[] pathVariables) {
        this.controller = controller;
        this.method = method;
        this.methodType = methodType;
        this.pattern = pattern;
        this.pathVariables = pathVariables;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public int getPathVariableIndex(String pathVariable) {
        for (int i = 0; i < pathVariables.length; i++) {
            if (pathVariables[i].equalsIgnoreCase("{" + pathVariable + "}")) {
                return i;
            }
        }
        return -1;
    }
}
