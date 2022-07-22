package org.cjl.summer.jdkproxy;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: Proxy
 * @Package: org.cjl.summer.jdkproxy
 * @Description: Mock the proxy of JDKDynamicProxy
 * @Author: Jiulong_Chen
 * @Date: 7/7/2022
 * @Version: V1.0
 */
public class Proxy {
    private static final String LN = "\r\n";
    private static int index = 0;

    private static Map<Class, Class> mappings = new HashMap<Class, Class>();

    static {
        mappings.put(int.class, Integer.class);
    }

    /**
     * @Title: newProxyInstance
     * @Description: Mock the newProxyInstance of JDKDynamicProxy
     * @Param: [classLoader, interfaces, invocationHandler]
     * @Return: java.lang.Object
     * @Author: Jiulong_Chen
     * @Date: 7/7/2022 5:27 PM
     */
    public static Object newProxyInstance(ProxyClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {

        try {
            String proxyClassName = "$Proxy" + index;
            index++;
            String sourceCode = generateSourceCode(interfaces, proxyClassName);

            String filePath = Proxy.class.getClassLoader().getResource("").getPath();
            File file = new File(filePath + proxyClassName + ".java");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(sourceCode);
            fileWriter.flush();
            fileWriter.close();

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
            Iterable iterable = manager.getJavaFileObjects(file);

            JavaCompiler.CompilationTask task = compiler.getTask(null, manager, null, null, null, iterable);
            task.call();
            manager.close();
            file.delete();

            Class proxyClass = classLoader.findClass(proxyClassName);
            Constructor constructor = proxyClass.getConstructor(InvocationHandler.class);

            return constructor.newInstance(invocationHandler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Title: generateSourceCode
     * @Description: generate source code to implement the interface's method
     * @Param: [interfaces]
     * @Return: java.lang.String
     * @Author: Jiulong_Chen
     * @Date: 7/7/2022 5:30 PM
     */
    private static String generateSourceCode(Class<?>[] interfaces, String proxyClassName) {
        StringBuilder code = new StringBuilder();
        code.append("package org.cjl.summer.jdkproxy;" + LN);
        code.append("import java.lang.reflect.Method;" + LN);
        code.append("@SuppressWarnings(\"unchecked\")" + LN);
        code.append("public class " + proxyClassName + " implements ");

        for (int i = 0; i < interfaces.length; i++) {
            code.append(interfaces[i].getName());
            if (i != interfaces.length - 1) {
                code.append(",");
            }
        }

        code.append("{" + LN);

        code.append("InvocationHandler h;" + LN);
        code.append("public " + proxyClassName + "(InvocationHandler h) {" + LN);
        code.append("this.h = h;" + LN);
        code.append("}" + LN);

        for (Class<?> anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();

            for (Method method : methods) {
                Class<?>[] paramTypes = method.getParameterTypes();

                StringBuilder paramNames = new StringBuilder();
                StringBuilder paramValues = new StringBuilder();
                StringBuilder paramClasses = new StringBuilder();

                for (int i = 0; i < paramTypes.length; i++) {
                    Class clazz = paramTypes[i];
                    String type = clazz.getTypeName();
                    String paramName = toLowerFirstCase(clazz.getSimpleName()) + i;

                    paramNames.append(type + " " + paramName);
                    paramValues.append(paramName);
                    paramClasses.append(type + ".class");

                    if (i != paramTypes.length - 1) {
                        paramNames.append(",");
                        paramValues.append(",");
                        paramClasses.append(",");
                    }
                }
                code.append("@SuppressWarnings(\"unchecked\")" + LN);
                code.append("public " + method.getReturnType().getName() + " " + method.getName()
                        + "(" + paramNames.toString() + ") {" + LN);
                code.append("try{" + LN);
                code.append("Method m = " + anInterface.getName() + ".class.getMethod(\"" + method.getName() + "\"," +
                        " new Class[]{" + paramClasses.toString() + "});" + LN);
                code.append(method.getReturnType() == void.class ? "" : "return " + "(" + method.getReturnType().getName() + ")"
                        + "this.h.invoke(this,m,new Object[]{" + paramValues.toString() + "});");
                code.append("}catch(Throwable e){" + LN);
                //code.append("throw new Exception(e);" + LN);
                code.append("}");
                code.append(getReturnEmptyCode(method.getReturnType()));
                code.append("}");
            }
        }

        code.append("}");

//        System.out.println(code.toString());
//        System.out.println("=================================");
        return code.toString();
    }

    private static String toLowerFirstCase(String str) {
        str = str.replace("[]", "");
        String firstCase = str.substring(0,1).toLowerCase();
        return firstCase + str.substring(1,str.length());
    }

    private static String getReturnEmptyCode(Class<?> returnClass) {
        if (mappings.containsKey(returnClass)) {
            return "return 0;";
        } else if (returnClass == void.class) {
            return "";
        } else {
            return "return null;";
        }
    }

}
