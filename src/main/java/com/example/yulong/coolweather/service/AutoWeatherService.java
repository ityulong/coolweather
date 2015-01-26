package com.example.yulong.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.yulong.coolweather.receiver.MyReceiver;
import com.example.yulong.coolweather.util.HttpCallBackListener;
import com.example.yulong.coolweather.util.HttpUtil;
import com.example.yulong.coolweather.util.Utility;

public class AutoWeatherService extends Service {
    public AutoWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int MyHours = 1000 * 60 * 60 * 8;
        long TriggerTime = SystemClock.elapsedRealtime() + MyHours;
        Intent i = new Intent(this, MyReceiver.class);
        PendingIntent pend = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, TriggerTime, pend);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = preferences.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" +  weatherCode + ".html";
        HttpUtil.sendHttpRquest(address,new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoWeatherService.this, response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
