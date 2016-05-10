package com.android.wang.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.wang.coolweather.model.City;
import com.android.wang.coolweather.model.CoolWeatherDB;
import com.android.wang.coolweather.model.County;
import com.android.wang.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/10.
 */
public class Utility {
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String respone)
    {
        if (!TextUtils.isEmpty(respone))
        {
            String[] allProvince=respone.split(",");
            if(allProvince!=null&&allProvince.length>0)
            {
                for (String P:allProvince)
                {
                    String[] array=P.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    coolWeatherDB.saveProvice(province);
                }
                return true;
            }

        }
        return false;
    }
    public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String respone,int cityId)
    {
        if (!TextUtils.isEmpty(respone))
        {
            String[] allCounty=respone.split(",");
            if(allCounty!=null&&allCounty.length>0)
            {
                for (String P:allCounty)
                {
                    String[] array=P.split("\\|");
                    County county=new County();
                    county.setCountyName(array[1]);
                    county.setCountyCode(array[0]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }

        }
        return false;
    }
    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String respone,int provinceId)
    {
        if (!TextUtils.isEmpty(respone))
        {
            String[] allCity=respone.split(",");
            if(allCity!=null&&allCity.length>0)
            {
                for (String P:allCity)
                {
                    String[] array=P.split("\\|");
                    City city=new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }

        }
        return false;
    }
    public static void hanleWeatherREsponse(Context context,String response)
    {
        try
        {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年m月d日");
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
