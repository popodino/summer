package org.cjl.summer.summermvc.context;

import org.cjl.summer.mybatis.session.Configuration;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private String[] configLocations;
    private String scanPackage;

    private String mapperScanPackage;
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    private List<AopAspect> aspectList = new ArrayList<>();
    private BeanDefinitionReader reader;

    public ApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApplicationContext(String scanPackage,String mapperScanPackage) {
        this.scanPackage = scanPackage;
        this.mapperScanPackage = mapperScanPackage;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() throws Exception {
        if(null == scanPackage || "".equals(scanPackage)){
            reader = new BeanDefinitionReader(configLocations);
        }else {
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
    public Object getBean(String beanName) throws Exception {
        if (this.factoryBeanInstanceCache.containsKey(beanName)){
            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        BeanWrapper beanWrapper = instantiateBean(beanName, beanDefinition);
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        populateBean(beanWrapper.getWrappedInstance(),beanWrapper.getWrappedClass());
        return beanWrapper.getWrappedInstance();
    }

    private void populateBean(Object instance, Class<?> beanClass) throws ClassNotFoundException {
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)){
                Autowired autowired = field.getAnnotation(Autowired.class);
                String autowiredBeanName = autowired.value().trim();
                if("".equals(autowiredBeanName)){
                    autowiredBeanName = field.getType().getName();
                }
                field.setAccessible(true);

                try {
                    if(this.factoryBeanInstanceCache.containsKey(autowiredBeanName)){
                        field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
                    }
                } catch (Exception e) {
                   e.printStackTrace();
                }

            }
        }

    }

    private BeanWrapper instantiateBean(String beanName, BeanDefinition beanDefinition) {
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
                        populateBean(instance,clazz);
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
    public Object getBean(Class<?> beanClass) throws Exception {
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

    public Map<String, BeanDefinition> getBeanDefinitionMap(){
        return beanDefinitionMap;
    }

    private void doRegistryMybatisMapper() {
        if(mapperScanPackage != null && !"".equals(mapperScanPackage)){
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactory(mapperScanPackage);
            DefaultSqlSession sqlSession = sqlSessionFactory.openSqlSession();
            factoryBeanInstanceCache.putAll(sqlSession.getConfiguration().registryAllMapper(sqlSession));
        }
    }
}
