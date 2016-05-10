package com.android.wang.coolweather.util;

/**
 * Created by Administrator on 2016/5/10.
 */
public interface HttpCallbackListener {
    void onFinish(String t);
    void onError(Exception e);
}
