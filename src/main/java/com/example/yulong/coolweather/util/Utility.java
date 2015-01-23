package com.example.yulong.coolweather.util;

import android.text.TextUtils;

import com.example.yulong.coolweather.db.CoolWeatherDB;
import com.example.yulong.coolweather.model.City;
import com.example.yulong.coolweather.model.County;
import com.example.yulong.coolweather.model.Province;

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
}
