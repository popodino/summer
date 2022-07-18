package org.cjl.summer.summermvc.mvc;

import com.alibaba.fastjson.JSON;
import org.cjl.summer.summermvc.annotation.PathVariable;
import org.cjl.summer.summermvc.annotation.RequestParam;
import org.cjl.summer.tomcat.Request;
import org.cjl.summer.tomcat.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: HandlerAdapter
 * @Package: org.cjl.summer.summermvc.mvc
 * @Description: mock the spring HandlerAdapter
 * @Author: Jiulong_Chen
 * @Date: 7/12/2022
 * @Version: V1.0
 */
public class HandlerAdapter {

    private static final List<Class<?>> defalutDataClassList = new ArrayList<>();

    static {
        defalutDataClassList.add(String.class);
        defalutDataClassList.add(Integer.class);
        defalutDataClassList.add(int.class);
        defalutDataClassList.add(boolean.class);
        defalutDataClassList.add(long.class);
        defalutDataClassList.add(float.class);
        defalutDataClassList.add(double.class);
    }

    public boolean supports(Object handler) {
        return (handler instanceof HandlerMapping);
    }

    public Object handle(Request request, Response response, Object handler) throws Exception {
        HandlerMapping handlerMapping = (HandlerMapping) handler;

        if (!request.getMethod().equals(handlerMapping.getMethodType())) {
            throw new RuntimeException("method not allowed!");
        }

        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();

        Object[] paramValues = new Object[parameterTypes.length];

        Map<String, Integer> paramIndexMapping = new HashMap<>();

        Annotation[][] annotations = ((HandlerMapping) handler).getMethod().getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();
                    boolean require = ((RequestParam) annotation).require();
                    if (require && (request.getParameter(paramName) == null
                            || "".equals(request.getParameter(paramName)))) {
                        throw new RuntimeException("parameter " + paramName + " not found!");
                    }
                    if (!"".equals(paramName)) {
                        paramIndexMapping.put(paramName, i);
                    }
                } else if (annotation instanceof PathVariable) {
                    String paramName = ((PathVariable) annotation).value();
                    boolean require = ((PathVariable) annotation).require();
                    int index = handlerMapping.getPathVariableIndex(paramName);
                    if (require && (index < 0 || request.getPathVariable().length < (index + 1)
                            || "".equals(request.getPathVariable()[index]))) {
                        throw new RuntimeException("pathVariable " + paramName + " not found!");
                    }
                    paramValues[i] = request.getPathVariable()[index];
                }
            }
        }

        Map<String, String[]> parameterMap = request.getParameterMap();

        parameterMap.forEach((key, value) -> {
            if (paramIndexMapping.containsKey(key)) {
                int index = paramIndexMapping.get(key);
                paramValues[index] = caseRequestValue(value[0], parameterTypes[index]);
            }
        });

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (type == Request.class) {
                paramValues[i] = request;
            } else if (type == Response.class) {
                paramValues[i] = response;
            } else if (!defalutDataClassList.contains(type)) {
                paramValues[i] = JSON.parseObject(request.getRequestBody(), type);
            }
        }


        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if (null == result) {
            return null;
        }
        return result;
    }

    private Object caseRequestValue(String value, Class<?> parameterType) {
        if (String.class == parameterType) {
            return value;
        } else if (Integer.class == parameterType || int.class == parameterType) {
            return Integer.valueOf(value);
        } else if (Double.class == parameterType || double.class == parameterType) {
            return Double.valueOf(value);
        } else if (Float.class == parameterType || float.class == parameterType) {
            return Float.valueOf(value);
        } else if (Boolean.class == parameterType || boolean.class == parameterType) {
            return Boolean.valueOf(value);
        } else {
            return value;
        }
    }

}
