package org.cjl.summer.tomcat;

import org.cjl.summer.summermvc.mvc.DispatchServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Title: Tomcat
 * @Package: org.cjl.summer.tomcat
 * @Description: mock the tomcat
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class SummerBoot {
    private static int port = 8080;

    private static DispatchServlet dispatchServlet;

    private static void init(Class<?> clazz) {
        dispatchServlet = new DispatchServlet(clazz);
    }

    public static void run(Class<?> clazz) {
        init(clazz);

        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("[Info] SummerBoot: Tomcat start at port: " + port);

            while (true) {
                Socket client = server.accept();

                process(client);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void process(Socket client) throws Exception {

        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {
            Request request = new Request(in);
            Response response = new Response(out);

            dispatchServlet.service(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }

    }
}
