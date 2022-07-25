package org.cjl.summer.summermvc.context;

import org.cjl.summer.mybatis.session.DefaultSqlSession;
import org.cjl.summer.mybatis.session.SqlSessionFactory;
import org.cjl.summer.summermvc.annotation.*;
import org.cjl.summer.summermvc.aop.aspect.AopAspect;
import org.cjl.summer.summermvc.aop.proxy.AopProxy;
import org.cjl.summer.summermvc.aop.proxy.CglibAopProxy;
import org.cjl.summer.summermvc.aop.proxy.JdkAopProxy;
import org.cjl.summer.summermvc.aop.support.AdvisedSupport;
import org.cjl.summer.summermvc.beans.BeanWrapper;
import org.cjl.summer.summermvc.beans.config.BeanDefinition;
import org.cjl.summer.summermvc.beans.support.BeanDefinitionReader;
import org.cjl.summer.summermvc.core.BeanFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: ApplicationContext
 * @Package: org.cjl.summer.summermvc.context
 * @Description: mock the spring ApplicationContext
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class ApplicationContext implements BeanFactory {
    private final static String DEFAULT_CONFIG_LOCATION = "application.properties";
    private String[] configLocations;
    private String scanPackage;
    private String mapperScanPackage;
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();
    private final Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();
    private final List<AopAspect> aspectList = new ArrayList<>();

    private final Properties config = new Properties();

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_LOCATION)) {
            config.load(in);
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApplicationContext(String scanPackage, String mapperScanPackage) {
        this.scanPackage = scanPackage;
        this.mapperScanPackage = mapperScanPackage;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_LOCATION)) {
            config.load(in);
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() throws Exception {
        BeanDefinitionReader reader;
        if (null == scanPackage || "".equals(scanPackage)) {
            reader = new BeanDefinitionReader(configLocations);
        } else {
            reader = new BeanDefinitionReader(scanPackage);
        }

        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        doRegistryBeanDefinition(beanDefinitions);

        doRegistryMybatisMapper();

        doAutowired();

    }

    private void doAutowired() {
        beanDefinitionMap.forEach((key, value) -> {
            if (!value.isLazyInit()) {
                try {
                    getBean(key);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void doRegistryBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("This class '" + beanDefinition.getFactoryBeanName() + "' is exists!!");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            doRegistryAopAspect(beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) {
        if (this.factoryBeanInstanceCache.containsKey(beanName)) {
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        BeanWrapper beanWrapper = instantiateBean(beanDefinition);
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        populateBean(beanWrapper.getWrappedInstance(), beanWrapper.getWrappedClass());
        return beanWrapper.getWrappedInstance();
    }

    private void populateBean(Object instance, Class<?> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String autowiredBeanName = autowired.value().trim();
                if ("".equals(autowiredBeanName)) {
                    autowiredBeanName = field.getType().getName();
                }
                try {
                    if (this.factoryBeanInstanceCache.containsKey(autowiredBeanName)) {
                        field.setAccessible(true);
                        field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (field.isAnnotationPresent(Value.class)) {
                Value value = field.getAnnotation(Value.class);
                if ("".equals(value.value())) {
                    continue;
                }

                StringBuilder propertiesName = new StringBuilder();
                if (beanClass.isAnnotationPresent(ConfigurationProperties.class)) {
                    ConfigurationProperties configurationProperties = beanClass.getAnnotation(ConfigurationProperties.class);
                    propertiesName.append(configurationProperties.prefix()).append(".");
                }
                propertiesName.append(value.value());
                String propertiesValue = config.getProperty(propertiesName.toString());

                if (propertiesValue == null) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    setFieldValue(field, instance, propertiesValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

        }

    }

    private BeanWrapper instantiateBean(BeanDefinition beanDefinition) {
        String beanClassName = beanDefinition.getBeanClassName();
        BeanWrapper beanWrapper = null;

        try {
            if (factoryBeanObjectCache.containsKey(beanClassName)) {
                beanWrapper = new BeanWrapper(factoryBeanObjectCache.get(beanClassName));
            } else {
                Class<?> clazz = Class.forName(beanClassName);
                Object instance = clazz.getConstructor().newInstance();
                for (AopAspect aopAspect : aspectList) {
                    if (aopAspect.pointCutMatchClass(clazz)) {
                        populateBean(instance, clazz);
                        AdvisedSupport advisedSupport = new AdvisedSupport(clazz, instance, aopAspect);
                        instance = createProxy(advisedSupport).getproxy();
                    }
                }
                this.factoryBeanObjectCache.put(beanClassName, instance);
                beanWrapper = new BeanWrapper(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return beanWrapper;
    }

    private AopProxy createProxy(AdvisedSupport advisedSupport) {
        if (advisedSupport.getTargetClass().getInterfaces().length > 0) {
            return new JdkAopProxy(advisedSupport);
        }
        return new CglibAopProxy(advisedSupport);
    }


    @Override
    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    private void doRegistryAopAspect(BeanDefinition beanDefinition) throws Exception {
        Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
        if (clazz.isAnnotationPresent(Aspect.class)) {
            AopAspect aopAspect = new AopAspect();
            aopAspect.setAspectClass(clazz);
            aopAspect.setAspectInstance(clazz.getConstructor().newInstance());
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(PointCut.class)) {
                    aopAspect.setPointCut(method.getAnnotation(PointCut.class).value());
                } else if (method.isAnnotationPresent(Before.class)) {
                    aopAspect.setAspectBefore(method);
                } else if (method.isAnnotationPresent(After.class)) {
                    aopAspect.setAspectAfter(method);
                } else if (method.isAnnotationPresent(Around.class)) {
                    aopAspect.setAspectAround(method);
                } else if (method.isAnnotationPresent(AfterThrowing.class)) {
                    aopAspect.setAspectAfterThrowing(method);
                }
            }
            if (null == aopAspect.getPointCut() || "".equals(aopAspect.getPointCut())) {
                throw new Exception("this Aspect class '" + beanDefinition.getBeanClassName() + "' no point cut!!");
            }

            aspectList.add(aopAspect);
            factoryBeanObjectCache.put(beanDefinition.getBeanClassName(), aopAspect.getAspectInstance());
        }
    }

    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    public Properties getConfig() {
        return this.config;
    }

    private void setFieldValue(Field field, Object instance, String fieldValue) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (Integer.class == type || int.class == type) {
            field.set(instance, Integer.parseInt(fieldValue));
        } else if (Long.class == type || long.class == type) {
            field.set(instance, Long.parseLong(fieldValue));
        } else if (Boolean.class == type || boolean.class == type) {
            field.set(instance, Boolean.parseBoolean(fieldValue));
        } else if (Double.class == type || double.class == type) {
            field.set(instance, Double.parseDouble(fieldValue));
        } else {
            field.set(instance, fieldValue);
        }

    }

    private void doRegistryMybatisMapper() {
        if (mapperScanPackage != null && !"".equals(mapperScanPackage)) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactory(mapperScanPackage);
            DefaultSqlSession sqlSession = sqlSessionFactory.openSqlSession();
            factoryBeanInstanceCache.putAll(sqlSession.getConfiguration().registryAllMapper(sqlSession));
        }
    }
}
