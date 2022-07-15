package org.cjl.summer.mybatis.session;

import org.cjl.summer.mybatis.annotation.Intercepts;
import org.cjl.summer.mybatis.annotation.Mapper;
import org.cjl.summer.mybatis.annotation.Select;
import org.cjl.summer.mybatis.binding.MapperRegister;
import org.cjl.summer.mybatis.executor.CachingExecutor;
import org.cjl.summer.mybatis.executor.Executor;
import org.cjl.summer.mybatis.executor.resultset.ResultSetHandler;
import org.cjl.summer.mybatis.executor.resultset.SimpleResultSetHandler;
import org.cjl.summer.mybatis.executor.SimpleExecutor;
import org.cjl.summer.mybatis.executor.parameter.ParameterHandler;
import org.cjl.summer.mybatis.executor.parameter.SimpleParameterHandler;
import org.cjl.summer.mybatis.executor.statement.SimpleStatementHandler;
import org.cjl.summer.mybatis.executor.statement.StatementHandler;
import org.cjl.summer.mybatis.executor.statement.cache.StatementCache;
import org.cjl.summer.mybatis.plugin.Interceptor;
import org.cjl.summer.mybatis.plugin.InterceptorChain;
import org.cjl.summer.summermvc.beans.BeanWrapper;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: Configuration
 * @Package: org.cjl.summer.mybatis.session
 * @Description: mock the mybatis Configuration
 * @Author: Jiulong_Chen
 * @Date: 7/13/2022
 * @Version: V1.0
 */
public class Configuration {

    public static final ResourceBundle PROPERTIES;
    private final static MapperRegister mapperRegister = new MapperRegister();
    private static final Map<String, String> mappedStatements = new HashMap<>();
    private InterceptorChain interceptorChain = new InterceptorChain();
    private List<Class<?>> mapperList = new ArrayList<>();

    static {
        PROPERTIES = ResourceBundle.getBundle("application");
    }

    public Configuration(String mapperScan) {
        scanPackage(mapperScan);
        parsingClass();
        initPlugin();
    }

    public Executor newExecutor() {
        Executor executor = new SimpleExecutor(this);
        if (PROPERTIES.containsKey("cache.enabled") && "true".equals(PROPERTIES.getString("cache.enabled"))) {
            executor = new CachingExecutor(executor);
        }
        return (Executor) interceptorChain.pluginAll(executor);
    }

    public ParameterHandler newParameterHandler() {
        ParameterHandler parameterHandler = new SimpleParameterHandler();
        return (ParameterHandler) interceptorChain.pluginAll(parameterHandler);

    }

    public StatementHandler newStatementHandler() {
        StatementHandler statementHandler = new SimpleStatementHandler(this);
        return (StatementHandler) interceptorChain.pluginAll(statementHandler);
    }

    public ResultSetHandler newResultHandler() {
        ResultSetHandler resultSetHandler = new SimpleResultSetHandler();
        return (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
    }

    private void initPlugin() {
        if (!PROPERTIES.containsKey("plugin.path")) {
            return;
        }

        String pluginPath = PROPERTIES.getString("plugin.path");
        for (String plugin : pluginPath.split(",")) {
            try {
                Class clazz = Class.forName(plugin);
                if (clazz.isAnnotationPresent(Intercepts.class)) {
                    Interceptor interceptor = (Interceptor) clazz.getConstructor().newInstance();
                    interceptorChain.addInterceptor(interceptor);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }

    public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {
        return mapperRegister.getMapper(clazz, sqlSession);
    }

    public String getStatement(String statementName) {
        return mappedStatements.get(statementName);
    }

    private void parsingClass() {
        for (Class<?> clazz : mapperList) {
            mapperRegister.addMapper(clazz);

            for (Method method : clazz.getDeclaredMethods()) {
                String statementName = clazz.getName() + "." + method.getName();
                if (!mappedStatements.containsKey(statementName) && method.isAnnotationPresent(Select.class)) {
                    String statement = method.getAnnotation(Select.class).query();
                    mappedStatements.put(statementName, statement);
                }
            }

        }

        StatementCache.getInstance().registryStatements(mappedStatements);
    }

    private void scanPackage(String mapperScan) {
        String classPath = this.getClass().getResource("/").getPath();
        mapperScan = mapperScan.replace(".", File.separator);
        doPath(new File(classPath + mapperScan));
    }

    private void doPath(File file) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                doPath(listFile);
            }
        } else {
            if (file.getName().endsWith(".class")) {
                try {
                    String className = file.getPath().replace("\\", ".");
                    className = className.substring(className.lastIndexOf("classes.") + 8, className.length())
                            .replace(".class", "");

                    Class clazz = Class.forName(className);
                    if (clazz.isInterface() && clazz.isAnnotationPresent(Mapper.class)) {
                        mapperList.add(clazz);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Map<String, BeanWrapper> registryAllMapper(DefaultSqlSession sqlSession) {
        Map<String, BeanWrapper> mapperMap = new ConcurrentHashMap<>();
        for (Class<?> mapper : mapperList) {
            BeanWrapper beanWrapper = new BeanWrapper(mapperRegister.getMapper(mapper, sqlSession));
            mapperMap.put(mapper.getName(), beanWrapper);
        }
        return mapperMap;
    }
}
