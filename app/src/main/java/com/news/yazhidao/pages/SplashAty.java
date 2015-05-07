package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.utils.ZipperUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by fengjigang on 15/3/30.
 */
public class SplashAty extends BaseActivity {
    private Handler mHandler=new Handler();
    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_splash);
    }

    @Override
    protected void initializeViews(Bundle savedInstanceState) {
        //add umeng statistic access app
        final long _Strat=System.currentTimeMillis();
        ZipperUtil.unzip(this,new ZipperUtil.ZipCompleteListener() {
            @Override
            public void complate() {
                if(System.currentTimeMillis()-_Strat<2000){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SplashAty.this.finish();
                            startActivity(new Intent(SplashAty.this, HomeAty.class));
                        }
                    },2000);
                }else{
                SplashAty.this.finish();
                startActivity(new Intent(SplashAty.this, HomeAty.class));
                }
            }
        });
        MobclickAgent.onEvent(this, CommonConstant.US_BAINEWS_USER_ASSESS_APP);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
