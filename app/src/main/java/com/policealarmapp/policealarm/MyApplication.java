package com.policealarmapp.policealarm;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 作者：senon on 2018/1/30 12:01
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }


}
