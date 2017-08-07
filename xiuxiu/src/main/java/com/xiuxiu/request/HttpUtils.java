package com.xiuxiu.request;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hzdykj on 2017/7/4.
 * 工具类：通过URLHttpConnection获取服务器上的XML流
 */

public class HttpUtils {
    public HttpUtils() {
    }

    //方法：返回的InputStream对象就是服务器返回的XML流。
    public static InputStream getXML(String path) {
        try {
            URL url = new URL(path);
            if (url != null) {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                int requesetCode = connection.getResponseCode();
                if (requesetCode == 200) {
                    connection.connect();
                    //如果执行成功，返回HTTP响应流
                    return connection.getInputStream();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
