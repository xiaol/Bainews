package com.news.yazhidao.pages;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailELVAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.NewsDetailContent;
import com.news.yazhidao.entity.NewsDetailEntry;
import com.news.yazhidao.entity.NewsDetailImageWall;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.request.UploadCommentRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.CommentPopupWindow;
import com.news.yazhidao.widget.NewsDetailHeaderView2;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class NewsDetailAty2 extends BaseActivity implements View.OnClickListener {

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId;
    private String mPlatformType;
    private String uuid;
    private String mNewsDetailUrl;
    //新闻内容POJO
    private NewsDetail mNewsDetail;
    private NewsDetailAdd mNewsDetailAdd;
    private ArrayList<ArrayList> mNewsContentDataList;
    private NewsDetailELVAdapter mNewsDetailELVAdapter;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private ExpandableListView mDetailContentListView;
    private View mNewsDetailLoaddingWrapper;
    private ImageView mNewsLoadingImg;
    private AnimationDrawable mAniNewsLoading;
    private View mDetailView;
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
        mNewsContentDataList = new ArrayList<>();
        mNewsDetailELVAdapter = new NewsDetailELVAdapter(this, mNewsContentDataList);
    }

    @Override
    protected void initializeViews() {
//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mDetailHeaderView = new NewsDetailHeaderView2(this);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsDetailLoaddingWrapper.setOnClickListener(this);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mDetailContentListView = (ExpandableListView) findViewById(R.id.mDetailContentListView);
        mDetailContentListView.addHeaderView(mDetailHeaderView);
        mDetailContentListView.setAdapter(mNewsDetailELVAdapter);
        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
        //设置Listview默认展开
        for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
            mDetailContentListView.expandGroup(i);
        }
        //去掉Listview左边箭头
        mDetailContentListView.setGroupIndicator(null);
        mDetailContentListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mDetailHeaderView.setShareListener(this);
        mDetailHeaderView.setCommentListener(this);
        mDetailHeaderView.setLeftBackListener(this);
    }

    @Override
    protected void loadData() {
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mAniNewsLoading.start();
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
        mNewsDetailELVAdapter.setNewsUrl(mNewsDetailUrl);
//        mNewsDetailUrl = "http://sports.people.com.cn/n/2015/0910/c22176-27564278.html";
        uuid = DeviceInfoUtil.getUUID();
        String requestUrl = HttpConstant.URL_GET_NEWS_DETAIL + mNewsDetailUrl + "&userId=" + mUserId + "&platformType=" + mPlatformType;
        /**是否是新的api,除了谷歌今日焦点,其他都是新api接口*/
        if (!isNewApi) {
            NetworkRequest _Request = new NetworkRequest(requestUrl, NetworkRequest.RequestMethod.GET);
            _Request.setCallback(new JsonCallback<NewsDetail>() {

                @Override
                public void success(NewsDetail result) {
                    mAniNewsLoading.stop();
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    mNewsDetail = result;
                    mNewsDetailELVAdapter.setNewsDetail(result);
                    if (result != null) {
                        mDetailHeaderView.updateView(result);
                        parseNewsDetail(result);
                        //设置Listview默认展开
                        for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
                            mDetailContentListView.expandGroup(i);
                        }
                    }
                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", "network fail");
                    mAniNewsLoading.stop();
                    mNewsLoadingImg.setImageResource(R.drawable.ic_news_detail_reload);
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
                    mAniNewsLoading.stop();
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    mNewsDetailAdd = result;
                    mNewsDetailELVAdapter.setNewsDetail(result);
                    if (result != null) {
                        mDetailHeaderView.updateView(result);
                        parseNewsDetail(result);
                        //设置Listview默认展开
                        for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
                            mDetailContentListView.expandGroup(i);
                        }
                    }

                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", "network fail");
                    mAniNewsLoading.stop();
                    mNewsLoadingImg.setImageResource(R.drawable.ic_news_detail_reload);
                }
            }.setReturnType(new TypeToken<NewsDetailAdd>() {
            }.getType()));
            _Request.execute();
        }
    }

    float startY;
    float endY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.e("jigang","move ----"+event.getY());
                break;
            case MotionEvent.ACTION_UP:
                Logger.e("jigang","end ----"+(event.getY()- startY));
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                onBackPressed();
                break;
            case R.id.mDetailComment:
                ArrayList<NewsDetail.Point> points;
                if (mNewsDetail != null && !TextUtil.isListEmpty(mNewsDetail.point)) {
                    points = mNewsDetail.point;
                } else if (mNewsDetailAdd != null && !TextUtil.isListEmpty(mNewsDetailAdd.point)) {
                    points = mNewsDetailAdd.point;
                } else {
                    points = null;
                }
                CommentPopupWindow window = new CommentPopupWindow(this, points, mNewsDetailUrl, null, -1, 7, null);
                window.setFocusable(true);
                //防止虚拟软键盘被弹出菜单遮住
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM
                        | Gravity.CENTER, 0, 0);
                break;
            case R.id.mDetailShare:
                mSharePopupWindow = new SharePopupWindow(this);
                if (mNewsDetail != null) {
                    mSharePopupWindow.setTitleAndUrl(mNewsDetail.title, mNewsDetailUrl);
                } else {
                    mSharePopupWindow.setTitleAndUrl(mNewsDetailAdd.title, mNewsDetailUrl);
                }
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.mNewsDetailLoaddingWrapper:
                loadData();
                break;
        }
    }

    private ArrayList<ArrayList> parseNewsDetail(NewsDetail pNewsDetail) {
        /**计算展示内容需要多少个组,其中包括 新闻内容,多图集合,差异化观点,精选评论,新闻词条(百度百科,豆瓣),相关观点,微博热点,知乎推荐*/
        if (pNewsDetail != null) {
            /**计算新闻内容所在组*/
            if (!TextUtil.isEmptyString(pNewsDetail.content)) {
                ArrayList list = new ArrayList<>();
                String[] contents = pNewsDetail.content.split("\n");
                ArrayList<NewsDetail.Point> points = pNewsDetail.point;
                for (int i = 0; i < contents.length; i++) {
                    NewsDetailContent content = new NewsDetailContent();
                    content.setContent(contents[i]);
                    content.setComments(new ArrayList<NewsDetail.Point>());
                    list.add(content);
                }
                if (!TextUtil.isListEmpty(points)) {
                    for (int j = 0; j < points.size(); j++) {
                        NewsDetail.Point point = points.get(j);
                        int paragraphIndex = Integer.valueOf(point.paragraphIndex);
                        if (UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                            if (paragraphIndex < list.size()) {
                                NewsDetailContent content = (NewsDetailContent) list.get(paragraphIndex);
                                content.getComments().add(point);
                            }
                        }
                    }
                }
                if (list.size() > 0) {
                    mNewsContentDataList.add(list);
                }
            }
            /**计算图片墙所在组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.imgWall)) {
                NewsDetailImageWall imageWall = new NewsDetailImageWall();
                imageWall.setImgWall(pNewsDetail.imgWall);
                ArrayList<NewsDetailImageWall> list = new ArrayList();
                list.add(imageWall);
                if (list.size() > 0) {
                    mNewsContentDataList.add(list);
                }
            }
            /**计算差异化观点所在组数据*/
            if (pNewsDetail.relate_opinion != null) {
                ArrayList<NewsDetail.Article> self_opinion = pNewsDetail.relate_opinion.getSelf_opinion();
                if (!TextUtil.isListEmpty(self_opinion)){
                    mNewsContentDataList.add(self_opinion);
                }
            }
            /**计算精选评论观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.point)) {
                ArrayList<NewsDetail.Point> points = new ArrayList<>();
                for (int j = 0; j < pNewsDetail.point.size(); j++) {
                    NewsDetail.Point point = pNewsDetail.point.get(j);
                    if (UploadCommentRequest.TEXT_DOC.equals(point.type) || UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                        points.add(point);
                    }
                }
                Collections.sort(points);
                /**只要3条评论*/
                if (points.size() > 3) {
                    points = new ArrayList<>(points.subList(0, 3));
                }
                points.add(new NewsDetail.Point());
                mNewsContentDataList.add(points);
            }
            /**计算新闻词条组数据*/
            ArrayList<NewsDetailEntry> entryList = new ArrayList<>();
            if (!TextUtil.isListEmpty(pNewsDetail.baike)) {
                for (NewsDetail.BaiDuBaiKe item : pNewsDetail.baike) {
                    entryList.add(new NewsDetailEntry(item.title, NewsDetailEntry.EntyType.BAIDUBAIKE, item.url));
                }
            }
            if (!TextUtil.isListEmpty(pNewsDetail.douban)) {
                for (ArrayList item : pNewsDetail.douban) {
                    entryList.add(new NewsDetailEntry((String) item.get(0), NewsDetailEntry.EntyType.DOUBAN, (String) item.get(1)));
                }
            }
            if (entryList.size() != 0) {
                mNewsContentDataList.add(entryList);
            }

            /**相关观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.relate)) {
                mNewsContentDataList.add(pNewsDetail.relate);
            }
            /**微博组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.weibo)) {
                if (pNewsDetail.weibo.size() > 5) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.weibo.subList(0, 5)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.weibo);
                }
            }
            /**知乎组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.zhihu)) {
                if (pNewsDetail.zhihu.size() > 5) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.zhihu.subList(0, 5)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.zhihu);
                }
            }
        }
        return mNewsContentDataList;
    }

    private ArrayList<ArrayList> parseNewsDetail(NewsDetailAdd pNewsDetail) {
        /**计算展示内容需要多少个组,其中包括 新闻内容,多图集合,差异化观点,精选评论,新闻词条(百度百科,豆瓣),相关观点,微博热点,知乎推荐*/
        if (pNewsDetail != null) {
            /**计算新闻内容所在组*/
            if (!TextUtil.isListEmpty(pNewsDetail.content)) {
                ArrayList list = new ArrayList<>();
                ArrayList<NewsDetail.Point> points = pNewsDetail.point;
                for (int i = 0; i < pNewsDetail.content.size(); i++) {
                    LinkedTreeMap<String, HashMap<String, String>> treeMap = pNewsDetail.content.get(i);
                    HashMap<String, String> hashMap = treeMap.get(i + "");
                    if (hashMap.get("txt") != null) {
                        NewsDetailContent content = new NewsDetailContent();
                        content.setContent(hashMap.get("txt"));//img img_info txt
                        content.setComments(new ArrayList<NewsDetail.Point>());
                        list.add(content);
                    }
                }
                if (!TextUtil.isListEmpty(points)) {
                    for (int j = 0; j < points.size(); j++) {
                        NewsDetail.Point point = points.get(j);
                        int paragraphIndex = Integer.valueOf(point.paragraphIndex);
                        if (UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                            if (paragraphIndex < list.size()) {
                                NewsDetailContent content = (NewsDetailContent) list.get(paragraphIndex);
                                content.getComments().add(point);
                            }
                        }
                    }
                }
                if (list.size() > 0) {
                    mNewsContentDataList.add(list);
                }
            }
            /**计算图片墙所在组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.imgWall)) {
                NewsDetailImageWall imageWall = new NewsDetailImageWall();
                imageWall.setImgWall(pNewsDetail.imgWall);
                ArrayList<NewsDetailImageWall> list = new ArrayList();
                list.add(imageWall);
                mNewsContentDataList.add(list);
                if (!TextUtil.isListEmpty(pNewsDetail.content)) {
                    for (int i = 0; i < pNewsDetail.content.size(); i++) {
                        LinkedTreeMap<String, HashMap<String, String>> treeMap = pNewsDetail.content.get(i);
                        HashMap<String, String> hashMap = treeMap.get(i + "");
                        if (hashMap.get("img") != null) {
                            HashMap<String, String> image = new HashMap<>();//img img_info txt
                            image.put("img", hashMap.get("img"));
                            imageWall.getImgWall().add(image);
                        }
                    }
                }
            }
            /**计算差异化观点所在组数据*/
            if (pNewsDetail.relate_opinion != null) {
                ArrayList<NewsDetail.Article> self_opinion = pNewsDetail.relate_opinion.getSelf_opinion();
                if (!TextUtil.isListEmpty(self_opinion)){
                    mNewsContentDataList.add(self_opinion);
                }
            }
            /**计算精选评论观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.point)) {
                ArrayList<NewsDetail.Point> points = new ArrayList<>();
                for (int j = 0; j < pNewsDetail.point.size(); j++) {
                    NewsDetail.Point point = pNewsDetail.point.get(j);
                    if (UploadCommentRequest.TEXT_DOC.equals(point.type) || UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                        points.add(point);
                    }
                }
                Collections.sort(points);
                /**只要3条评论*/
                if (points.size() > 3) {
                    points = new ArrayList<>(points.subList(0, 3));
                }
                points.add(new NewsDetail.Point());
                mNewsContentDataList.add(points);
            }
            /**计算新闻词条组数据*/
            ArrayList<NewsDetailEntry> entryList = new ArrayList<>();
            if (!TextUtil.isListEmpty(pNewsDetail.baike)) {
                for (NewsDetail.BaiDuBaiKe item : pNewsDetail.baike) {
                    entryList.add(new NewsDetailEntry(item.title, NewsDetailEntry.EntyType.BAIDUBAIKE, item.url));
                }
            }
            if (!TextUtil.isListEmpty(pNewsDetail.douban)) {
                for (ArrayList item : pNewsDetail.douban) {
                    entryList.add(new NewsDetailEntry((String) item.get(0), NewsDetailEntry.EntyType.DOUBAN, (String) item.get(1)));
                }
            }
            if (entryList.size() != 0) {
                mNewsContentDataList.add(entryList);
            }

            /**相关观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.relate)) {
                mNewsContentDataList.add(pNewsDetail.relate);
            }
            /**微博组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.weibo)) {
                if (pNewsDetail.weibo.size() > 5) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.weibo.subList(0, 5)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.weibo);
                }
            }
            /**知乎组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.zhihu)) {
                if (pNewsDetail.zhihu.size() > 5) {
                    mNewsContentDataList.add(new ArrayList(pNewsDetail.zhihu.subList(0, 5)));
                } else {
                    mNewsContentDataList.add(pNewsDetail.zhihu);
                }
            }
        }
        return mNewsContentDataList;
    }

}
