package com.news.yazhidao.application;

import android.app.Application;
import android.content.Context;

import com.news.yazhidao.BuildConfig;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

/**
 * Created by fengjigang on 15/2/1.
 */
public class YaZhiDaoApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        mContext=this;
        ShareSDK.initSDK(this);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        super.onCreate();
    }
    public static Context getAppContext(){
        return mContext;
    }
}
