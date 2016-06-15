package com.news.yazhidao.pages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.webview.LoadWebView;

public class PlayVideoActivity extends BaseActivity {
    private LoadWebView PlayVideo_WebView;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_play_video);
    }

    @Override
    protected void initializeViews() {
        PlayVideo_WebView = (LoadWebView) findViewById(R.id.PlayVideo_WebView);
        PlayVideo_WebView.setBackgroundColor(getResources().getColor(R.color.transparent));

        PlayVideo_WebView.getSettings().setJavaScriptEnabled(true);
        PlayVideo_WebView.getSettings().setDatabaseEnabled(true);
        PlayVideo_WebView.getSettings().setDomStorageEnabled(true);
        PlayVideo_WebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        PlayVideo_WebView.getSettings().setLoadsImagesAutomatically(false);
        PlayVideo_WebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        PlayVideo_WebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL)),
//                "text/html;charset=UTF-8", "utf-8", null);
        PlayVideo_WebView.setDf(new LoadWebView.PlayFinish() {
            @Override
            public void After() {
                Log.e("aaa", "22222");

//                Log.e("aaa","1111");
            }
        });
    }

    @Override
    protected void loadData() {

    }
}
