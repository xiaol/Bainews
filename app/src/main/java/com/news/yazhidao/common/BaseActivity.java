package com.news.yazhidao.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

/**
 *  Created by feng on 3/23/15.
 */
public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initializeViews();
        loadData();

    }

    protected abstract void setContentView();
    protected abstract void initializeViews();
    protected abstract void loadData();


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
//        UMSsoHandler ssoHandler = UmengShareHelper.mController.getConfig().getSsoHandler(requestCode) ;
//        if(ssoHandler != null){
//            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }
}
