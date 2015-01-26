package com.example.yulong.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yulong.coolweather.R;
import com.example.yulong.coolweather.db.CoolWeatherDB;
import com.example.yulong.coolweather.model.City;
import com.example.yulong.coolweather.model.County;
import com.example.yulong.coolweather.model.Province;
import com.example.yulong.coolweather.util.HttpCallBackListener;
import com.example.yulong.coolweather.util.HttpUtil;
import com.example.yulong.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yulong on 2015/1/22.
 */
public class ChoseAreActivity extends Activity {
    //控件的初始化
    private TextView titleAre;
    private ListView areaList;
    private ArrayAdapter<String> adapter;
    private List<String> dataList;
    private CoolWeatherDB weatherDB;
    private ProgressDialog progressDialog;

    //级别类型
    public static final int PROVINCE_LEVEL = 0;
    public static final int CITY_LEVEL = 1;
    public  static final int COUNTY_LEVEL = 2;
    //当前的选择（省，市，县）
    private Province currentProvince;
    private City currentCity;
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //当前的级别
    private int currentLevel;
    //是否是从weather页面跳转过来的
    boolean from_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        from_weather = getIntent().getBooleanExtra("from_weatherActivity", false);
        if (preferences.getBoolean("city_selected", false) && !from_weather) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chose_area);
        titleAre = (TextView) findViewById(R.id.title_area);
        areaList = (ListView) findViewById(R.id.list_area);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        areaList.setAdapter(adapter);
        weatherDB = CoolWeatherDB.getInstance(this);
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == PROVINCE_LEVEL) {
                    currentProvince = provinceList.get(position);
                    queryCities();
                }
                else if (currentLevel == CITY_LEVEL) {
                    currentCity = cityList.get(position);
                    queryCounties();
                }
                else if (currentLevel == COUNTY_LEVEL) {
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChoseAreActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
        Log.i("queryProvince", "query province execute!");
    }

    private void queryProvince() {
        provinceList = weatherDB.getAllProvince();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            titleAre.setText("中国");
            areaList.setSelection(0);
            currentLevel = PROVINCE_LEVEL;
        }else{
            queryFromService(null,"province");
        }
    }
    private void queryCities() {
        cityList = weatherDB.getAllCity(currentProvince.getId());
        if(cityList.size() > 0){
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            titleAre.setText(currentProvince.getProvinceName());
            areaList.setSelection(0);
            currentLevel = CITY_LEVEL;
        }
        else{
            queryFromService(currentProvince.getProvinceCode(),"city");
        }
    }

    private void queryCounties() {
        countyList = weatherDB.getAllCounty(currentCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County cou : countyList) {
                dataList.add(cou.getCountyName());
            }
            adapter.notifyDataSetChanged();
            titleAre.setText(currentCity.getCityName());
            areaList.setSelection(0);
            currentLevel = COUNTY_LEVEL;
        }
        else{
            queryFromService(currentCity.getCityCode(),"county");
        }

    }

    private void queryFromService(final String code,final String type) {
        String address;
        if(TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        else {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRquest(address,new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.hanldeProvinceMessage(weatherDB, response);
                }else if("city".equals(type)) {
                    result = Utility.hanldeCityMessage(weatherDB, response, currentProvince.getId());
                }else if ("county".equals(type)) {
                    result = Utility.hanldeCountyMessage(weatherDB, response, currentCity.getId());
                }
                if(result){
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    switch(type){
                                        case "province":
                                            queryProvince();
                                            break;
                                        case "city":
                                            queryCities();
                                            break;
                                        case "county":
                                            queryCounties();
                                            break;
                                    }
                                }
                            }
                    );
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChoseAreActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void showProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed(){
        switch(currentLevel){
            case COUNTY_LEVEL:
                queryCities();
                break;
            case CITY_LEVEL:
                queryProvince();
                break;
            case PROVINCE_LEVEL:
                if(from_weather) {
                    Intent intent = new Intent(this, WeatherActivity.class);
                    startActivity(intent);
                }
                finish();
        }
    }

}
