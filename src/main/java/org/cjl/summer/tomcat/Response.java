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
    private OutputStream out;

    public Response(OutputStream out){
        this.out = out;

    }

    public void write(String content) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/html; charset=utf-8\n")
                .append("\r\n")
                .append(content);
        out.write(sb.toString().getBytes());
    }
}
