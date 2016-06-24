package com.news.yazhidao.pages;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.javascript.VideoJavaScriptBridge;
import com.news.yazhidao.utils.Logger;

public class PlayVideoAty extends BaseActivity {
    private WebView mPlayVideoWebView;
    private String mVideoUrl;

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
        mPlayVideoWebView = (WebView) findViewById(R.id.mPlayVideoWebView);
//        mPlayVideoWebView.setBackgroundColor(getResources().getColor(R.color.transparent));

        mPlayVideoWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mPlayVideoWebView.getSettings().setJavaScriptEnabled(true);
//        mPlayVideoWebView.getSettings().setDatabaseEnabled(true);
        mPlayVideoWebView.getSettings().setDomStorageEnabled(true);
//        mPlayVideoWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        mPlayVideoWebView.getSettings().setLoadsImagesAutomatically(false);
//        mPlayVideoWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        Logger.e("jigang","aty url =" + mVideoUrl);
        mPlayVideoWebView.setWebChromeClient(new WebChromeClient());
        mPlayVideoWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
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
    protected void loadData() {
        mPlayVideoWebView.loadUrl(mVideoUrl);
//        mPlayVideoWebView.loadDataWithBaseURL(null,"<!DOCTYPE html><html><head lang=\"en\"><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"“width=device-width,\" initial-scale=\"1.0,\" user-scalable=\"yes,target-densitydpi=device-dpi\"><title></title></head><body><p style=\"font-size:18px\">ssssssss</p><p><img src=\"#\" width=\"200\" height=\"200\" style=\"background-color:aqua\"></p></body></html>","text/html","utf-8",null);

    }
}
