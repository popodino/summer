package org.cjl.summer.tomcat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @Title: Request
 * @Package: org.cjl.summer.tomcat
 * @Description: mock the request from ServletAPIs
 * @Author: Jiulong_Chen
 * @Date: 7/11/2022
 * @Version: V1.0
 */
public class Request {

    private String method;
    private String uri;
    private String contextPath;

    private Map<String, List<String>> parameters = new HashMap<>();
    private Map<String, String[]> parameterMap = new HashMap<>();

    public Request(InputStream in) {

        try {
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;

            if ((len = in.read(buff)) > 0) {
                content = new String(buff, 0, len);
            }

            String line = content.split("\\n")[0];
            String[] header = line.split(" ");
            this.method = header[0];

            this.uri = header[1];

            if (uri.contains("?")) {
                this.contextPath = uri.substring(uri.indexOf("?"), uri.length());
                String paramStr = uri.substring(uri.indexOf("?") + 1, uri.length());
                if (paramStr.contains("&")) {
                    String[] params = paramStr.split("&");
                    for (String param : params) {
                        appendParameter(param);
                    }
                } else {
                    appendParameter(paramStr);
                }

                parameters.forEach((key, value) -> {
                    parameterMap.put(key, value.toArray(new String[0]));
                });

            } else {
                this.contextPath = "";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getMethod() {
        return this.method;
    }

    public String getUri() {
        return this.uri;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public String getParameter(String name) {
        String[] params = parameterMap.get(name);
        if (null == params) {
            return null;
        } else {
            return params[0];
        }
    }

    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }

    private void appendParameter(String str) {
        if (str.contains("=") && !str.startsWith("=")) {
            String[] param = str.split("=");
            if (parameters.containsKey(param[0])) {
                parameters.get(param[0]).add(param[1]);
            } else {
                List<String> paramValues = new ArrayList<>();
                paramValues.add(param[1]);
                parameters.put(param[0], paramValues);
            }

        }
    }
}
