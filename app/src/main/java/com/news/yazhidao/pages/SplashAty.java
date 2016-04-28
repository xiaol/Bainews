package com.news.yazhidao.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;

/**
 * Created by fengjigang on 15/3/30.
 */
public class SplashAty extends BaseActivity {

    private ImageView iv_splash_background;
    private boolean flag;
    private ImageView mSplashLine;
    private ImageView mSplashSlogan;
    private ImageView mSplashMask;
    private View mSplashContent;
    private TextView mSplashVersion;

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }

    @Override
    protected void setContentView() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_splash);
    }


    @Override
    protected void initializeViews() {
        SharedPreferences sp = getSharedPreferences("showflag", 0);
        flag = sp.getBoolean("isshow", false);
        iv_splash_background = (ImageView) findViewById(R.id.iv_splash_background);
        mSplashSlogan = (ImageView) findViewById(R.id.mSplashSlogan);
        mSplashMask = (ImageView) findViewById(R.id.mSplashMask);
        mSplashContent = findViewById(R.id.mSplashContent);
        mSplashVersion = (TextView)findViewById(R.id.mSplashVersion);
        mSplashVersion.setText(getResources().getString(R.string.app_name) + " v" + getResources().getString(R.string.app_version));
        int screenHeight = DeviceInfoUtil.getScreenHeight(this);
//        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_alpha_in);
//        mSplashSlogan.setAnimation(animation);
        mSplashLine = (ImageView) findViewById(R.id.mSplashLine);
        final Animation mask = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSplashMask.setAnimation(mask);
            }
        }, 100);
        mask.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashMask.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        Logger.e("jigang", "h=" + mSplashLine.getHeight() + ",w=" + mSplashLine.getWidth());
//        if (DeviceInfoUtil.isFlyme() || "meizu".equals(AnalyticsConfig.getChannel(this))) {
//            iv_splash_background.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    protected void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean showGuidePage = SharedPreManager.getBoolean(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE);
                if (!showGuidePage) {
                    Intent intent = new Intent(SplashAty.this, GuideLoginAty.class);
                    startActivity(intent);
                    SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE, true);
                } else {
                    Intent mainAty = new Intent(SplashAty.this, MainAty.class);
                    startActivity(mainAty);
                }
                SplashAty.this.finish();

            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long openTimes = SharedPreManager.getLong(CommonConstant.FILE_USER, CommonConstant.KEY_USER_OPEN_APP);
        if (openTimes == 0) {
            SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_OPEN_APP, 1);

        } else {
            SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_OPEN_APP, openTimes + 1);
        }
        //产生用户UUID
        SharedPreManager.saveUUID();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
