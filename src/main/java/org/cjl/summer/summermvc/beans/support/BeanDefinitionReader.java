package org.cjl.summer.summermvc.beans.support;

import org.cjl.summer.summermvc.annotation.Aspect;
import org.cjl.summer.summermvc.annotation.Component;
import org.cjl.summer.summermvc.annotation.RestController;
import org.cjl.summer.summermvc.annotation.Service;
import org.cjl.summer.summermvc.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Title: BeanDefinitionReader
 * @Package: org.cjl.summer.summermvc.beans.support
 * @Description: mock the spring BeanDefinitionReader
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class BeanDefinitionReader {
    private final String SCAN_PACKAGE = "scanPackage";

    private List<String> registryBeanClasses = new ArrayList<>();

    private Properties config = new Properties();

    public BeanDefinitionReader(String... configLocations){
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replaceAll("classpath:",""));
        try {
            config.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    public BeanDefinitionReader(String scanPackage){
        doScanner(scanPackage);
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classFile = new File(url.getFile());

        for (File file : classFile.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                if(!file.getName().endsWith(".class")) { continue;}
                registryBeanClasses.add(scanPackage + "." + file.getName().replace(".class",""));
            }
        }
        
    }

    public List<BeanDefinition> loadBeanDefinitions(){
        List<BeanDefinition> beanDefinitions = new ArrayList<>();

        try {
            for (String registryBeanClass : registryBeanClasses) {
                Class<?> beanClass = Class.forName(registryBeanClass);

                if (beanClass.isInterface()) { continue;}

                if(!beanClass.isAnnotationPresent(RestController.class)
                        && !beanClass.isAnnotationPresent(Component.class)
                        && !beanClass.isAnnotationPresent(Aspect.class)
                        && !beanClass.isAnnotationPresent(Service.class)){
                    continue;
                }

                beanDefinitions.add(doCreateBeanDefinition(beanClass.getName(), toLowerFirstCase(beanClass.getSimpleName())));

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                   beanDefinitions.add(doCreateBeanDefinition(beanClass.getName(), anInterface.getName()));
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return beanDefinitions;
    }

    private BeanDefinition doCreateBeanDefinition(String beanClassName, String factoryBeanName){
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    public Properties getConfig() {
        return config;
    }

    private String toLowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
