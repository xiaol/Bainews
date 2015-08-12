package com.news.yazhidao.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
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
    private boolean flag;
    private ImageView iv_news;

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    request.cancel(true);
                    tv_splash_news.setText("今日头条，百家争鸣");
                    iv_splash_background.startAnimation(anim_fade_out);
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
        anim_fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                final ScaleAnimation animation_scale = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation_scale.setDuration(8000);//设置动画持续时间
                animation_scale.setFillAfter(true);//动画执行完后是否停留在执行完的状态

                iv_news.startAnimation(animation_scale);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        anim_fade_out.setFillAfter(true);
        anim_fade_out.setRepeatCount(1);
        anim_fade_in = AnimationUtils.loadAnimation(SplashAty.this, R.anim.alpha_in);
        anim_fade_in.setFillAfter(true);

        timer = new Timer();
        setContentView(R.layout.rl_splash);
    }

    @Override
    protected void initializeViews() {

        SharedPreferences sp = getSharedPreferences("showflag", 0);
        flag = sp.getBoolean("isshow",false);

        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        iv_splash_background = (ImageView) findViewById(R.id.iv_splash_background);
        tv_splash_news = (TextView) findViewById(R.id.tv_splash_news);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
        iv_news = (ImageView) findViewById(R.id.iv_news);
        iv_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag) {
                    Intent intent = new Intent(SplashAty.this, HomeAty.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.animation_alpha_in, R.anim.slide_out);
                    SplashAty.this.finish();
                }else{
                    Intent intent_guide = new Intent(SplashAty.this, GuideAty.class);
                    startActivity(intent_guide);
                    overridePendingTransition(R.anim.animation_alpha_in, R.anim.slide_out);
                    SplashAty.this.finish();
                }
            }
        });

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
        }, 3000);

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
                        rl_splash.setVisibility(View.VISIBLE);
                    } else {
                        rl_splash.setVisibility(View.GONE);
                    }

                    if (result.getImgUrl() != null) {
//                        ImageLoaderHelper.dispalyImage(SplashAty.this,result.getImgUrl(),iv_splash_background);
                        ImageManager.getInstance(SplashAty.this).DisplayImage(result.getImgUrl(), iv_news, false, new DisplayImageListener() {
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
                iv_splash_background.startAnimation(anim_fade_out);
            }

            public void failed(MyAppException exception) {
                iv_splash_background.startAnimation(anim_fade_out);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        anim_fade_out.cancel();


        super.onDestroy();
    }
}
