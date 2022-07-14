package org.cjl.summer.jdkproxy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


/**
 * @Title: ProxyClassLoader
 * @Package: org.cjl.summer.jdkproxy
 * @Description: find the class created by proxy
 * @Author: Jiulong_Chen
 * @Date: 7/7/2022
 * @Version: V1.0
 */
public class ProxyClassLoader extends ClassLoader {
    private File classPathFile;

    public ProxyClassLoader(){
        String classPath = ProxyClassLoader.class.getClassLoader().getResource("").getPath();
        this.classPathFile = new File(classPath);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String className = ProxyClassLoader.class.getPackage().getName() + "." + name;
        if(classPathFile != null) {
            File classFile = new File(classPathFile, name.replaceAll("\\.","/") + ".class");
            if(classFile.exists()){
                FileInputStream is;
                ByteArrayOutputStream os;
                try {
                    is = new FileInputStream(classFile);
                    os= new ByteArrayOutputStream();

                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = is.read(buff)) != -1){
                        os.write(buff,0, len);
                    }
                    return defineClass(className, os.toByteArray(),0, os.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.findClass(className);
    }
}
