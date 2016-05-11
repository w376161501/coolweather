package com.android.wang.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.wang.coolweather.service.AutoUpdateService;

/**
 * Created by Administrator on 2016/5/11.
 */
public class AutoUpdateReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context,Intent intent)
    {
       Intent i=new Intent(context,AutoUpdateService.class);
        context.startService(i);

    }
}
