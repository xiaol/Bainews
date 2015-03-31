package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.Handler;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

/**
 * Created by fengjigang on 15/3/30.
 */
public class SplashAty extends BaseActivity {
    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_splash);
    }

    @Override
    protected void initializeViews() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO 开启子线程来进行字体解压缩

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashAty.this.finish();
                startActivity(new Intent(SplashAty.this, HomeAty.class));
            }
        }, 2000);
    }
}
