package com.example.yulong.coolweather.util;

/**
 * Created by yulong on 2015/1/21.
 */
public interface HttpCallBackListener {
    public void onFinish(String response);
    public void onError(Exception e);
}
