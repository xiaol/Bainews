package com.news.yazhidao.pages;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.javascript.VideoJavaScriptBridge;
import com.news.yazhidao.utils.Logger;

import java.lang.reflect.InvocationTargetException;

public class PlayVideoAty extends BaseActivity {
    private WebView mPlayVideoWebView;
    private String mVideoUrl;
    private TextView mPlayVideoLeftBack;
//    private JavascriptInterface javascriptInterface;


    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_play_video);
    }

    @Override
    protected void initializeViews() {
        mVideoUrl = getIntent().getStringExtra(VideoJavaScriptBridge.KEY_VIDEO_URL);
        mVideoUrl = "http://v.qq.com/iframe/player.html?vid=g03082cgop0";
        mPlayVideoWebView = (WebView) findViewById(R.id.mPlayVideoWebView);
        mPlayVideoLeftBack = (TextView) findViewById(R.id.mPlayVideoLeftBack);
        mPlayVideoLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
//
        Logger.e("jigang","aty url =" + mVideoUrl);
        initWebView();

    }
    public void initWebView(){

        WebSettings settings = mPlayVideoWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        InsideWebViewClient mInsideWebViewClient = new InsideWebViewClient();
        mPlayVideoWebView.setWebChromeClient(new WebChromeClient());
        mPlayVideoWebView.setWebViewClient(mInsideWebViewClient);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSimulateClick(mPlayVideoWebView, mPlayVideoWebView.getWidth() / 2, mPlayVideoWebView.getHeight() / 2);
            }
        }, 1000);

    }

    /**
     *  Android 模拟点击
     * @param view
     * @param x
     * @param y
     */
    private void setSimulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        Logger.e("aaa", "x===" + x);
        Logger.e("aaa", "y===" + y);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }



    @Override
    protected void onResume() {
        super.onResume();
        mPlayVideoWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayVideoWebView.onPause();
    }
    @Override
    public void onDestroy() {
        mPlayVideoWebView.destroy();
        super.onDestroy();
    }
    @Override
    public void onConfigurationChanged(Configuration config) {

        super.onConfigurationChanged(config);

        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }

    }


        @Override
    protected void loadData() {
        mPlayVideoWebView.loadUrl(mVideoUrl);
//        mPlayVideoWebView.loadDataWithBaseURL(null,"<!DOCTYPE html><html><head lang=\"en\"><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"“width=device-width,\" initial-scale=\"1.0,\" user-scalable=\"yes,target-densitydpi=device-dpi\"><title></title></head><body><p style=\"font-size:18px\">ssssssss</p><p><img src=\"#\" width=\"200\" height=\"200\" style=\"background-color:aqua\"></p></body></html>","text/html","utf-8",null);

    }
    private class InsideWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
    }

}
