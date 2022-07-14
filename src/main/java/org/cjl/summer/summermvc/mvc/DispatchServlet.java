package org.cjl.summer.summermvc.mvc;

import org.cjl.summer.mybatis.annotation.MapperScan;
import org.cjl.summer.summermvc.annotation.ComponentScan;
import org.cjl.summer.summermvc.annotation.GetMapping;
import org.cjl.summer.summermvc.annotation.PostMapping;
import org.cjl.summer.summermvc.annotation.RestController;
import org.cjl.summer.summermvc.context.ApplicationContext;
import org.cjl.summer.tomcat.Request;
import org.cjl.summer.tomcat.Response;
import org.cjl.summer.tomcat.Servlet;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Title: DispatchServlet
 * @Package: org.cjl.summer.summermvc.mvc
 * @Description: TODO(Describe in one sentence)
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class DispatchServlet extends Servlet {

    private final static String[] CONFIG_LOCATION = {"classpath:application.properties"};
    private Class<?> summerBootClass;
    private ApplicationContext applicationContext;

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    public DispatchServlet(Class<?> clazz) {
        this.summerBootClass = clazz;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) throws Exception {
        try {
            doDispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.write("500  " + e.getMessage());
        }
    }

    private void doDispatch(Request request, Response response) throws Exception {
        HandlerMapping handlerMapping = getHandlerMapping(request);
        if (null == handlerMapping) {
            response.write("404 not found");
            return;
        }

        HandlerAdapter handlerAdapter = handlerAdapters.get(handlerMapping);

        Object result = handlerAdapter.handle(request, response, handlerMapping);
        response.write(result.toString());
    }

    private HandlerMapping getHandlerMapping(Request request) {
        if (handlerMappings.isEmpty()) {
            return null;
        }

        String url = request.getUri().replace(request.getContextPath(), "").replaceAll("/+", "/");
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (handlerMapping.getPattern().matcher(url).matches()) {
                return handlerMapping;
            }
        }
        return null;
    }

    @Override
    public void doGet(Request request, Response response) throws Exception {
        doPost(request, response);
    }

    @Override
    public void init() throws Exception {
        ComponentScan componentScan = summerBootClass.getAnnotation(ComponentScan.class);
        MapperScan mapperScan = summerBootClass.getAnnotation(MapperScan.class);
        if (null == componentScan || "".equals(componentScan.value())) {
            applicationContext = new ApplicationContext(CONFIG_LOCATION);
        } else {
            applicationContext = new ApplicationContext(componentScan.value()
                    , mapperScan == null ? "" : mapperScan.basePackages());
        }

        initStrategies();
    }

    private void initStrategies() {
        initHandlerMappings();

        initHandlerAdapters();

        initHandlerExceptionResolvers();

        initViewResolvers();
    }

    private void initViewResolvers() {
    }

    private void initHandlerExceptionResolvers() {
    }

    private void initHandlerAdapters() {
        for (HandlerMapping handlerMapping : handlerMappings) {
            handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }

    }

    private void initHandlerMappings() {
        applicationContext.getBeanDefinitionMap().forEach((beanName, beanDefinition) -> {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if (clazz.isAnnotationPresent(RestController.class)) {
                    String baseURL = clazz.getAnnotation(RestController.class).value();
                    ;

                    Method[] methods = clazz.getDeclaredMethods();

                    for (Method method : methods) {
                        String methodUrl = "";
                        String methodType = "";
                        if (method.isAnnotationPresent(GetMapping.class)) {
                            methodUrl = method.getAnnotation(GetMapping.class).value().trim();
                            methodType = "GET";
                        }
                        if (method.isAnnotationPresent(PostMapping.class)) {
                            methodUrl = method.getAnnotation(PostMapping.class).value().trim();
                            methodType = "POST";
                        }
                        if ("".equals(methodUrl)) {
                            continue;
                        }

                        String regex = ("/" + baseURL + "/" + methodUrl.replaceAll("\\*", ".*"))
                                .replaceAll("/+", "/");
                        Pattern pattern = Pattern.compile(regex);
                        HandlerMapping handlerMapping = new HandlerMapping(applicationContext.getBean(beanName), method, methodType, pattern);
                        handlerMappings.add(handlerMapping);
                        System.out.println("[Info] Mapped: " + methodType + " --> " + regex + " --> " + method);

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
