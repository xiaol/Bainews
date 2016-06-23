package com.news.yazhidao.javascript;

import android.webkit.JavascriptInterface;

import com.news.yazhidao.utils.ToastUtil;

/**
 * Created by fengjigang on 16/6/22.
 */
public class VideoJavaScriptBridge {
    @JavascriptInterface
    public void openVideo(String url){
        ToastUtil.toastShort(url);
    }
}
