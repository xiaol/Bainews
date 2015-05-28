package com.news.yazhidao.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivityHelper;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

/**
 *  Created by feng on 3/23/15.
 */
public abstract class BaseActivity extends FragmentActivity {

    protected SwipeBackActivityHelper mHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fixed:Android 4.4 以上通知栏沉浸式，兼容xiaomi 4.1.2 和华为 4.1.2 系统等
        if(translucentStatus()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        setContentView();
        initializeViews();
        loadData();

    }

    protected abstract void setContentView();
    protected abstract void initializeViews();
    protected abstract void loadData();

    /**
     * 是否使用状态栏的沉浸式 默认使用
     * @return
     */
    protected boolean translucentStatus(){
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        JPushInterface.onPause(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = UmengShareHelper.mController.getConfig().getSsoHandler(requestCode) ;
//        if(ssoHandler != null){
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }
}
