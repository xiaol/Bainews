package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.NewsDetailHeaderView2;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.imagewall.WallActivity;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class NewsDetailAty2 extends SwipeBackActivity implements View.OnClickListener {

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId;
    private String mPlatformType;
    private String uuid;
    private String mNewsDetailUrl;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private RelativeLayout mDetailView;
    private SharePopupWindow mSharePopupWindow;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_detail_layout);
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(this);
    }

    @Override
    protected void initializeViews() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mDetailHeaderView = (NewsDetailHeaderView2) findViewById(R.id.mDetailHeaderView);
        mDetailView = (RelativeLayout) findViewById(R.id.mDetailView);
        mDetailHeaderView.setShareListener(this);
        mDetailHeaderView.setCommentListener(this);
    }

    @Override
    protected void loadData() {
        Bundle bundle = getIntent().getBundleExtra(AlbumListAty.KEY_BUNDLE);
        boolean isNewApi = getIntent().getBooleanExtra(AlbumListAty.KEY_IS_NEW_API, false);
        boolean isDigger = false;
        AlbumSubItem albumSubItem;
        String newsId = null;
        if (bundle != null) {
            isDigger = bundle.getBoolean(AlbumListAty.KEY_IS_DIGGER);
            albumSubItem = (AlbumSubItem) bundle.getSerializable(AlbumListAty.KEY_ALBUMSUBITEM);
            newsId = albumSubItem.getInserteId();

        }
//        position = getIntent().getIntExtra("position", 0);
        User user = SharedPreManager.getUser(NewsDetailAty2.this);
        if (user != null) {
            mUserId = user.getUserId();
            mPlatformType = user.getPlatformType();
        }
        mNewsDetailUrl = getIntent().getStringExtra("url");
        uuid = DeviceInfoUtil.getUUID();
        String requestUrl = HttpConstant.URL_GET_NEWS_DETAIL + mNewsDetailUrl + "&userId=" + mUserId + "&platformType=" + mPlatformType;
        /**是否是新的api,除了谷歌今日焦点,其他都是新api接口*/
        if (!isNewApi) {
            NetworkRequest _Request = new NetworkRequest(requestUrl, NetworkRequest.RequestMethod.GET);
            _Request.setCallback(new JsonCallback<NewsDetail>() {

                @Override
                public void success(NewsDetail result) {
                    if (result != null) {
                        mDetailHeaderView.updateView(result);
                    }
                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", exception.getMessage());
                }
            }.setReturnType(new TypeToken<NewsDetail>() {
            }.getType()));
            _Request.execute();
        } else {
            NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL_NEW, NetworkRequest.RequestMethod.POST);
            List<NameValuePair> pairs = new ArrayList<>();
            /**是否是挖掘的新闻*/
            if (isDigger) {
                pairs.add(new BasicNameValuePair("news_id", newsId));
            } else {
                pairs.add(new BasicNameValuePair("url", mNewsDetailUrl));
            }
            _Request.setParams(pairs);
            _Request.setCallback(new JsonCallback<NewsDetailAdd>() {

                @Override
                public void success(final NewsDetailAdd result) {
                    if (result != null) {
                        mDetailHeaderView.updateView(result);
                    } else {
                    }

                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", exception.getMessage());
                }
            }.setReturnType(new TypeToken<NewsDetailAdd>() {
            }.getType()));
            _Request.execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:

                break;
            case R.id.mDetailComment:
                Intent intent = new Intent(this, WallActivity.class);
                startActivity(intent);
                break;
            case R.id.mDetailShare:
                mSharePopupWindow = new SharePopupWindow(this);
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }

}
