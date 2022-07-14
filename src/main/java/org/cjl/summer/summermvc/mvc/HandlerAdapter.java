package org.cjl.summer.summermvc.mvc;

import org.cjl.summer.summermvc.annotation.RequestParam;
import org.cjl.summer.tomcat.Request;
import org.cjl.summer.tomcat.Response;

import java.lang.annotation.Annotation;
import java.util.HashMap;
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
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMapping);
    }

    public Object handle(Request request, Response response, Object handler) throws Exception {
        HandlerMapping handlerMapping = (HandlerMapping) handler;

        if (!request.getMethod().equals(handlerMapping.getMethodType())) {
            throw new RuntimeException("method not allowed!");
        }

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
                }
            }
        }

        Class<?>[] parameterTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> type = parameterTypes[i];
            if (type == Request.class || type == Response.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }

        Map<String, String[]> parameterMap = request.getParameterMap();

        Object[] paramValues = new Object[parameterTypes.length];

        parameterMap.forEach((key, value) -> {
            if (paramIndexMapping.containsKey(key)) {
                int index = paramIndexMapping.get(key);
                paramValues[index] = caseRequestValue(value[0], parameterTypes[index]);
            }
        });

        if (paramIndexMapping.containsKey(Request.class.getName())) {
            int index = paramIndexMapping.get(Request.class.getName());
            paramValues[index] = request;
        }

        if (paramIndexMapping.containsKey(Response.class.getName())) {
            int index = paramIndexMapping.get(Response.class.getName());
            paramValues[index] = response;
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
        }
        if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        } else {
            return value;
        }
    }

}
