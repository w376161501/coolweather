package com.android.wang.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.android.wang.coolweather.util.HttpCallbackListener;
import com.android.wang.coolweather.util.HttpUtil;
import com.android.wang.coolweather.util.Utility;

/**
 * Created by Administrator on 2016/5/11.
 */
public class AutoUpdateService extends Service{
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int houe=640000;
        long triggerAtTime= SystemClock.elapsedRealtime()+houe;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    private void  UpdateWeather()
    {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode=preferences.getString("weather_code", "");
        String address="http://www.weather.com.cn/data/cityinfo"+weatherCode+".xml";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String t) {
                Utility.hanleWeatherREsponse(AutoUpdateService.this,t);
            }

            @Override
            public void onError(Exception e) {

                e.printStackTrace();
            }
        });
    }
}
