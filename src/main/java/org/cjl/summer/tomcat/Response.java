package org.cjl.summer.tomcat;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Title: Response
 * @Package: org.cjl.summer.tomcat
 * @Description: mock the Response from ServletAPI
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class Response {
    private final OutputStream out;

    public Response(OutputStream out){
        this.out = out;

    }

    public void write(String content) throws IOException {
        String sb = "HTTP/1.1 200 OK\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "\r\n" +
                content;
        out.write(sb.getBytes());
    }
}
