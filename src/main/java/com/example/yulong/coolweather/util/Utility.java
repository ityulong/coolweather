package com.example.yulong.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.yulong.coolweather.db.CoolWeatherDB;
import com.example.yulong.coolweather.model.City;
import com.example.yulong.coolweather.model.County;
import com.example.yulong.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yulong on 2015/1/22.
 */
public class Utility {
    public static boolean hanldeProvinceMessage(CoolWeatherDB db ,String msg) {
        if (!TextUtils.isEmpty(msg)) {
            String[] allProvince = msg.split(",");
            if (allProvince.length > 0) {
                for(String p:allProvince) {
                    String[] pMsg = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(pMsg[1]);
                    province.setProvinceCode(pMsg[0]);
                    db.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean hanldeCityMessage(CoolWeatherDB db ,String msg,int provinceId) {
        if (!TextUtils.isEmpty(msg)) {
            String[] allCity = msg.split(",");
            if (allCity.length > 0) {
                for(String c:allCity) {
                    String[] cMsg = c.split("\\|");
                    City city = new City();
                    city.setCityName(cMsg[1]);
                    city.setCityCode(cMsg[0]);
                    city.setProvinceId(provinceId);
                    db.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean hanldeCountyMessage(CoolWeatherDB db ,String msg,int cityId) {
        if (!TextUtils.isEmpty(msg)) {
            String[] allCounty = msg.split(",");
            if (allCounty.length > 0) {
                for(String c:allCounty) {
                    String[] cMsg = c.split("\\|");
                    County county = new County();
                    county.setCountyName(cMsg[1]);
                    county.setCountyCode(cMsg[0]);
                    county.setCityId(cityId);
                    db.saveCounty(county);
                }
                return true;
            }
        }
        return true;
    }

    public static void handleWeatherResponse(Context context,String response) {
        try {
            JSONObject object = new JSONObject(response);
            JSONObject msgObj = object.getJSONObject("weatherinfo");
            String cityName = msgObj.getString("city");
            String weatherCode = msgObj.getString("cityid");
            String temp1 = msgObj.getString("temp1");
            String temp2 = msgObj.getString("temp2");
            String weatherDesp = msgObj.getString("weather");
            String publishTime = msgObj.getString("ptime");
            SaveWeather(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static void SaveWeather(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean("city_selected", true);
        edit.putString("city_name", cityName);
        edit.putString("weather_code", weatherCode);
        edit.putString("temp1", temp1);
        edit.putString("temp2", temp2);
        edit.putString("weather_desp", weatherDesp);
        edit.putString("publish_time", publishTime);
        edit.putString("current_date", format.format(new Date()));
        edit.commit();
    }
}
