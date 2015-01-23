package com.example.yulong.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yulong on 2015/1/21.
 */
public class HttpUtil {
    public static void sendHttpRquest(final String address, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            HttpURLConnection connection = null;
            @Override
            public void run() {
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputstream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
                    StringBuilder responsStr = new StringBuilder();
                    String line;
                    while((line = reader.readLine())!=null) {
                        responsStr.append(line);
                    }
                    if (listener != null) {
                        listener.onFinish(responsStr.toString());
                    }
                }catch(Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
                finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
