package com.example.yulong.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.yulong.coolweather.service.AutoWeatherService;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoWeatherService.class);
        context.startService(i);
    }
}
