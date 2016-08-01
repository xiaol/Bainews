package com.news.yazhidao.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.news.yazhidao.R;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivityHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;


/**
 *  Created by feng on 3/23/15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected SwipeBackActivityHelper mHelper;

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        super.onCreate(savedInstanceState);
        ShareSDK.initSDK(this);
        if (isNeedAnimation()){
            overridePendingTransition(R.anim.aty_right_enter, R.anim.aty_no_ani);
        }
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        PushAgent.getInstance(this).onAppStart();
        //fixed:Android 4.4 以上通知栏沉浸式，兼容xiaomi 4.1.2 和华为 4.1.2 系统等
        if(translucentStatus()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//              getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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

    /**
     * 子类activity是否在进入和退出的时候需要动画
     * @return
     */
    protected boolean isNeedAnimation(){return true;};
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void finish() {
        super.finish();
        if (isNeedAnimation()){
            overridePendingTransition(0, R.anim.aty_left_exit);
        }
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
