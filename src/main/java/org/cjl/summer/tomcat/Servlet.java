package org.cjl.summer.tomcat;

/**
 * @Title: Servlet
 * @Package: org.cjl.summer.tomcat
 * @Description: mock the servlet api
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public abstract class Servlet {

    public void service(Request request, Response response) throws Exception {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    public void init() throws Exception {
    }

    public abstract void doPost(Request request, Response response) throws Exception;

    public abstract void doGet(Request request, Response response) throws Exception;

}
