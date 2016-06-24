package com.news.yazhidao.javascript;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.news.yazhidao.pages.PlayVideoAty;

/**
 * Created by fengjigang on 16/6/22.
 */
public class VideoJavaScriptBridge {

    public static final String KEY_VIDEO_URL = "KEY_VIDEO_URL";
    private Context mContext;

    public VideoJavaScriptBridge(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public void openVideo(String url){
        Intent playAty = new Intent(mContext, PlayVideoAty.class);
        playAty.putExtra(KEY_VIDEO_URL,url);
        mContext.startActivity(playAty);
    }
}
