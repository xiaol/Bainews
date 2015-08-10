package com.news.yazhidao.application;

import android.app.Application;
import android.content.Context;

import com.news.yazhidao.utils.CrashHandler;
import com.readystatesoftware.systembartint.BuildConfig;

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
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        super.onCreate();
    }
    public static Context getAppContext(){
        return mContext;
    }
}
