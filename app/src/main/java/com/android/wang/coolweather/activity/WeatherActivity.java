package com.android.wang.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wang.coolweather.R;
import com.android.wang.coolweather.service.AutoUpdateService;
import com.android.wang.coolweather.util.HttpCallbackListener;
import com.android.wang.coolweather.util.HttpUtil;
import com.android.wang.coolweather.util.Utility;

/**
 * Created by Administrator on 2016/5/10.
 */
public class WeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDataText;
    private Button switchCity;
    private Button refreshWeather;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        currentDataText=(TextView)findViewById(R.id.current_date);
        temp1Text=(TextView)findViewById(R.id.temp1);
        temp2Text=(TextView)findViewById(R.id.temp2);
        String countyrCode=getIntent().getStringExtra("country_code");
        switchCity=(Button)findViewById(R.id.switch_city);
        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                return;

            }
        });
        refreshWeather=(Button)findViewById(R.id.refresh_city);
        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               publishText.setText("同步中》》》");
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherCode=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode))
                {
                    queryWeatherInfo(weatherCode);
                }
            }
        });
        if(!TextUtils.isEmpty(countyrCode))
        {
            publishText.setText("同步中》》》");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyrCode);
        }
        else
        {
            showWeather();
        }
    }
    private void queryWeatherCode(String countrycode)
    {
        String  address="http://www.weather.com.cn/data/list3/city"+countrycode+".xml";
        queryFromServe(address,"weatherCode");
    }
    private void queryWeatherInfo(String weatherCode)
    {
        String  address="http://www.weather.com.cn/data/cityinfo"+weatherCode+".xml";
        queryFromServe(address,"weatherCode");
    }
    private void queryFromServe(final String  address,final String type)
    {


        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String t) {
                boolean result = false;
                if ("countryCode".equals(type)) {

                    if (!TextUtils.isEmpty(t)) {
                        String[] array = t.split("\\\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.hanleWeatherREsponse(WeatherActivity.this, t);
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

                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    private void showWeather()
    {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(preferences.getString("city_name",""));
        temp1Text.setText(preferences.getString("temp1",""));
        temp2Text.setText(preferences.getString("temp2",""));
        weatherDespText.setText(preferences.getString("weather_desp",""));
        publishText.setText("今天"+preferences.getString("publish_time","")+"发布");
        currentDataText.setText(preferences.getString("current_date", ""));

        weatherInfoLayout.setVisibility(View.INVISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent=new Intent(this, AutoUpdateService.class);
        startActivity(intent);
    }

}
