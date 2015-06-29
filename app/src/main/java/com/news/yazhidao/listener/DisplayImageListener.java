package com.news.yazhidao.listener;

import android.graphics.Bitmap;

/**
 * Created by fengjigang on 15/6/4.
 * 上传用户评论接口回调
 */
public interface DisplayImageListener {
    void success(Bitmap bitmap);
    void failed();
}
