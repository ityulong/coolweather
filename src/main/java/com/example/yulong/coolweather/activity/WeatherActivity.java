package com.example.yulong.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yulong.coolweather.R;
import com.example.yulong.coolweather.service.AutoWeatherService;
import com.example.yulong.coolweather.util.HttpCallBackListener;
import com.example.yulong.coolweather.util.HttpUtil;
import com.example.yulong.coolweather.util.Utility;

/**
 * Created by yulong on 2015/1/25.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private RelativeLayout weatherInfoLayout;
    private TextView cityNameTv;
    private TextView publishTv;
    private TextView weatherDespTv;
    private TextView temp1Tv;
    private TextView temp2Tv;
    private Button selectBtn;
    private Button refreshBtn;
    private ImageView weatherIconIv; // Added ImageView for weather icon
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (RelativeLayout) findViewById(R.id.msg_layout);
        cityNameTv = (TextView) findViewById(R.id.county_tv);
        publishTv = (TextView) findViewById(R.id.publish_time);
        weatherDespTv = (TextView) findViewById(R.id.weather_msg);
        temp1Tv = (TextView) findViewById(R.id.temp_start);
        temp2Tv = (TextView) findViewById(R.id.temp_end);
        selectBtn = (Button) findViewById(R.id.select_weather);
        refreshBtn = (Button) findViewById(R.id.refresh_weather);
        weatherIconIv = (ImageView) findViewById(R.id.weather_icon_iv); // Initialize ImageView
        selectBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);

        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishTv.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameTv.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }
    }

    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromService(address,"countyCode");
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromService(address,"weatherCode");
    }
    private void queryFromService(final String address,final String type) {
        HttpUtil.sendHttpRquest(address,new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    Log.i("onFinish", "countyCode execute!");
                    String[] array = response.split("\\|");
                    if (array.length == 2) {
                        String weatherCode = array[1];
                        queryWeatherInfo(weatherCode);
                    }
                } else if ("weatherCode".equals(type)) {
                    Log.i("onFinish", "weatherCode execute!");
                    Log.i("address", address);
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTv.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameTv.setText(preferences.getString("city_name",""));
        temp2Tv.setText(preferences.getString("temp2",""));
        temp1Tv.setText(preferences.getString("temp1",""));
        weatherDespTv.setText(preferences.getString("weather_desp",""));

        String publishTime = preferences.getString("publish_time", "");
        String currentDate = preferences.getString("current_date", "");
        String lastUpdatedPrefix = getString(R.string.last_updated_prefix);
        String lastUpdatedText = lastUpdatedPrefix + currentDate + " " + publishTime;
        publishTv.setText(lastUpdatedText);

        // Set weather icon based on description
        String weatherDesp = preferences.getString("weather_desp", "");
        if (weatherDesp.contains("晴")) { // Sunny
            weatherIconIv.setImageResource(R.drawable.ic_weather_sunny);
        } else if (weatherDesp.contains("多云") || weatherDesp.contains("阴")) { // Cloudy or Overcast
            weatherIconIv.setImageResource(R.drawable.ic_weather_cloudy);
        } else if (weatherDesp.contains("雨")) { // Rain
            weatherIconIv.setImageResource(R.drawable.ic_weather_rainy);
        } else if (weatherDesp.contains("雪")) { // Snow
            weatherIconIv.setImageResource(R.drawable.ic_weather_snowy);
        } else if (weatherDesp.contains("雾")) { // Fog
            weatherIconIv.setImageResource(R.drawable.ic_weather_foggy);
        } else {
            weatherIconIv.setImageResource(R.drawable.ic_weather_unknown);
        }

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameTv.setVisibility(View.VISIBLE);
        Intent i = new Intent(this, AutoWeatherService.class);
        startService(i);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.select_weather:
                Intent intent = new Intent(this, ChoseAreActivity.class);
                intent.putExtra("from_weatherActivity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishTv.setText("同步中...");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String countyCode = preferences.getString("weather_code", "");
                if (!TextUtils.isEmpty(countyCode)) {
                    queryWeatherInfo(countyCode);
                }
                break;
            default:
                break;
        }
    }
}
