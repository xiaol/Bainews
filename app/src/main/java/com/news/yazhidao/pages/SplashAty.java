package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.StartUrl;
import com.news.yazhidao.listener.DisplayImageListener;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.image.ImageManager;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fengjigang on 15/3/30.
 */
public class SplashAty extends BaseActivity {
    private Handler mHandler = new Handler();
    private String umeng_channel;
    private RelativeLayout rl_splash;
    private ImageView iv_splash_background;
    private TextView tv_splash_news;
    private ImageView iv_app_icon;
    private Animation anim_fade_out;
    private Animation anim_fade_in;
    private StartUrl splashInfo;
    private Timer timer;
    private NetworkRequest request;

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:

                    request.cancel(true);
                    tv_splash_news.setText("今日头条，百家争鸣");

                    iv_app_icon.startAnimation(anim_fade_out);
                    rl_splash.setVisibility(View.VISIBLE);
                    rl_splash.startAnimation(anim_fade_in);

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }
    @Override
    protected void setContentView() {

        anim_fade_out = AnimationUtils.loadAnimation(SplashAty.this, R.anim.alpha_out);
        anim_fade_out.setFillAfter(true);
        anim_fade_in = AnimationUtils.loadAnimation(SplashAty.this, R.anim.alpha_in);
        anim_fade_in.setFillAfter(true);

        timer = new Timer();

        setContentView(R.layout.rl_splash);
    }

    @Override
    protected void initializeViews() {

        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        rl_splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashAty.this, HomeAty.class);
                startActivity(intent);
                overridePendingTransition(R.anim.animation_alpha_in, R.anim.slide_out);
                SplashAty.this.finish();
            }
        });
        iv_splash_background = (ImageView) findViewById(R.id.iv_splash_background);
        tv_splash_news = (TextView) findViewById(R.id.tv_splash_news);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);

        //add umeng statistic access app
//        final long _Strat = System.currentTimeMillis();
//        ImageView imageView = (ImageView) findViewById(R.id.bg_imageView);
//        umeng_channel = AnalyticsConfig.getChannel(this);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date1 = df.parse("2015-05-31");
//            Date date2 = df.parse("2015-06-02");
//            if (_Strat > date1.getTime() && _Strat < date2.getTime()) {
//
//                if("c360".equals(umeng_channel)){
//                    imageView.setBackgroundResource(R.drawable.bg_splash_c360_special);
//                }else{
//                    imageView.setBackgroundResource(R.drawable.bg_splash_festival);
//                }
//            }else{
//                if("c360".equals(umeng_channel)){
//                    imageView.setBackgroundResource(R.drawable.bg_splash_c360_normal);
//                }
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        ZipperUtil.unzip(this, new ZipperUtil.ZipCompleteListener() {
//            @Override
//            public void complate() {
//                if (System.currentTimeMillis() - _Strat < 2000) {
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            SplashAty.this.finish();
//                            startActivity(new Intent(SplashAty.this, HomeAty.class));
//                        }
//                    }, 2000);
//                } else {
//                    SplashAty.this.finish();
//                    startActivity(new Intent(SplashAty.this, HomeAty.class));
//                }
//            }
//        });

        MobclickAgent.onEvent(this, CommonConstant.US_BAINEWS_USER_ASSESS_APP);
    }

    @Override
    protected void loadData() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (NetUtil.checkNetWork(SplashAty.this)) {
                    if (splashInfo == null) {
                        Message message = new Message();
                        message.what = 1;
                        doActionHandler.sendMessage(message);
                    }
                }
            }
        }, 4000);

        String url = HttpConstant.URL_GET_START_URL;
        request = new NetworkRequest(url, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs = new ArrayList<>();
        request.setParams(pairs);
        request.setCallback(new JsonCallback<StartUrl>() {

            public void success(StartUrl result) {
                if (result != null) {

                    splashInfo = result;

                    if (result.getTitle() != null) {
                        tv_splash_news.setText(result.getTitle());
                    } else {
                        tv_splash_news.setText("今日头条，百家争鸣");
                    }

                    if (result.getImgUrl() != null) {
//                        ImageLoaderHelper.dispalyImage(SplashAty.this,result.getImgUrl(),iv_splash_background);
                        ImageManager.getInstance(SplashAty.this).DisplayImage(result.getImgUrl(), iv_splash_background, false, new DisplayImageListener() {
                            @Override
                            public void success(int width, int height) {

                            }

                            @Override
                            public void failed() {

                            }
                        });
                    } else {

                    }
                }

                iv_app_icon.startAnimation(anim_fade_out);
                rl_splash.setVisibility(View.VISIBLE);
                rl_splash.startAnimation(anim_fade_in);

            }

            public void failed(MyAppException exception) {

                iv_app_icon.startAnimation(anim_fade_out);
                rl_splash.setVisibility(View.VISIBLE);
                rl_splash.startAnimation(anim_fade_in);

            }
        }.setReturnType(new TypeToken<StartUrl>() {
        }.getType()));
        request.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onStop();
    }
}
