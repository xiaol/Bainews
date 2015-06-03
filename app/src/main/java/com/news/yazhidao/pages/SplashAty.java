package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.utils.ZipperUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fengjigang on 15/3/30.
 */
public class SplashAty extends BaseActivity {
    private Handler mHandler = new Handler();
    private String umeng_channel;

    @Override
    protected void setContentView() {

//        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //获得当前窗体对象
//        Window window = SplashAty.this.getWindow();
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);

        setContentView(R.layout.aty_splash);
    }

    @Override
    protected void initializeViews() {
        //add umeng statistic access app
        final long _Strat = System.currentTimeMillis();
        ImageView imageView = (ImageView) findViewById(R.id.bg_imageView);
        umeng_channel = AnalyticsConfig.getChannel(this);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = df.parse("2015-05-31");
            Date date2 = df.parse("2015-06-02");
            if (_Strat > date1.getTime() && _Strat < date2.getTime()) {

                if("c360".equals(umeng_channel)){
                    imageView.setBackgroundResource(R.drawable.bg_splash_c360_special);
                }else{
                    imageView.setBackgroundResource(R.drawable.bg_splash_festival);
                }
            }else{
                if("c360".equals(umeng_channel)){
                    imageView.setBackgroundResource(R.drawable.bg_splash_c360_normal);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ZipperUtil.unzip(this, new ZipperUtil.ZipCompleteListener() {
            @Override
            public void complate() {
                if (System.currentTimeMillis() - _Strat < 2000) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SplashAty.this.finish();
                            startActivity(new Intent(SplashAty.this, HomeAty.class));
                        }
                    }, 2000);
                } else {
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
