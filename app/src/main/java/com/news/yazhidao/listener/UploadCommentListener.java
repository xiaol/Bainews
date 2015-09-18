package com.news.yazhidao.listener;

import com.news.yazhidao.entity.NewsDetail;

/**
 * Created by fengjigang on 15/6/4.
 * 上传用户评论接口回调
 */
public interface UploadCommentListener {
    void success(NewsDetail.Point result);
    void failed();
}
