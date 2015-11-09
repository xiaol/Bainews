package com.news.yazhidao.pages;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailELVAdapter;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.NewsDetailContent;
import com.news.yazhidao.entity.NewsDetailEntry;
import com.news.yazhidao.entity.NewsDetailImageWall;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.HttpClientUtil;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.request.UploadCommentRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.CommentPopupWindow;
import com.news.yazhidao.widget.NewsDetailHeaderView2;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
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
public class NewsDetailAty2 extends SwipeBackActivity implements View.OnClickListener, CommentPopupWindow.IUpdateCommentCount, CommentPopupWindow.IUpdatePraiseCount, SharePopupWindow.ShareDismiss {

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId;
    private String mPlatformType;
    private String uuid;
    private String mNewsDetailUrl;
    private ImageView mivShareBg;
    //新闻内容POJO
    private NewsDetailAdd mNewsDetailAdd;
    private ArrayList<ArrayList> mNewsContentDataList;
    private NewsDetailELVAdapter mNewsDetailELVAdapter;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private ExpandableListView mDetailContentListView;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailLeftBack, mDetailComment, mDetailShare, mDetailHeader, mNewsDetailLoaddingWrapper;
    private ImageView mNewsLoadingImg;
    private AnimationDrawable mAniNewsLoading;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
    private ProgressBar mNewsDetailProgress;

    private float startY;
    private String mSource;
    private String newsId = null;
    private String newsType = null;

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
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
    }

    @Override
    protected void initializeViews() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mDetailHeaderView = new NewsDetailHeaderView2(this);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(this);
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mNewsDetailProgress = (ProgressBar) findViewById(R.id.mNewsDetailProgress);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = findViewById(R.id.mDetailHeader);
        mDetailLeftBack = findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailComment.setOnClickListener(this);
        mDetailShare = findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);

        mDetailContentListView = (ExpandableListView) findViewById(R.id.mDetailContentListView);
        mDetailContentListView.addHeaderView(mDetailHeaderView);
        mDetailContentListView.setAdapter(mNewsDetailELVAdapter);
        mDetailContentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View childAt = view.getChildAt(0);
                if (childAt != null && mNewsDetailAdd != null && !TextUtil.isEmptyString(mNewsDetailAdd.imgUrl)) {
                    if (firstVisibleItem != 0 || childAt.getY() > 0) {
                        return;
                    }
                    double move = (Math.abs(childAt.getY()) * 1.5 > childAt.getHeight()) ? childAt.getHeight() : Math.abs(childAt.getY()) * 1.1;
                    int alpaha = (int) (move * 1.0f / childAt.getHeight() * 255);
                    Drawable drawable = NewsDetailAty2.this.getResources().getDrawable(R.color.bg_home_login_header);
                    mDetailHeader.setBackgroundDrawable(drawable);
                    mDetailHeader.getBackground().setAlpha(alpaha);
                }
            }
        });
        //设置Listview默认展开
        expandedChildViews();
        //去掉Listview左边箭头
        mDetailContentListView.setGroupIndicator(null);
        //取消groupview 点击事件
        mDetailContentListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    @Override
    protected void loadData() {
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
//        mAniNewsLoading.start();
        mNewsLoadingImg.setVisibility(View.GONE);
        mNewsDetailProgress.setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getBundleExtra(AlbumListAty.KEY_BUNDLE);
        boolean isDigger = false;
        AlbumSubItem albumSubItem;
        if (bundle != null) {
            isDigger = bundle.getBoolean(AlbumListAty.KEY_IS_DIGGER);
            albumSubItem = (AlbumSubItem) bundle.getSerializable(AlbumListAty.KEY_ALBUMSUBITEM);
            newsId = albumSubItem.getInserteId();

        }else {
            newsId = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
            newsType = getIntent().getStringExtra(NewsFeedFgt.KEY_COLLECTION);
//            newsId = "5565083c8aab5076b2c9caaa36dd856a";
//            newsType = "NewsItem";
            Logger.e("jigang","newsid ="+newsId+",type="+newsType);
        }
        User user = SharedPreManager.getUser(NewsDetailAty2.this);
        if (user != null) {
            mUserId = user.getUserId();
            mPlatformType = user.getPlatformType();
        }
        mNewsDetailUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_URL);
        mNewsDetailELVAdapter.setNewsUrl(mNewsDetailUrl);
        uuid = DeviceInfoUtil.getUUID();

        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_POST_NEWS_DETAIL, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs = new ArrayList<>();
        /**是否是挖掘的新闻*/
        if (isDigger) {
            pairs.add(new BasicNameValuePair("news_id", newsId));
        } else {
            pairs.add(new BasicNameValuePair("newsid", newsId));
            pairs.add(new BasicNameValuePair("devicetype", "android"));
            pairs.add(new BasicNameValuePair("collection", newsType));
        }
        pairs.add(new BasicNameValuePair("userid", mUserId));
        pairs.add(new BasicNameValuePair("deviceid", uuid));
        _Request.setParams(pairs);
        _Request.setCallback(new JsonCallback<NewsDetailAdd>() {

            @Override
            public void success(final NewsDetailAdd result) {
                mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                mNewsDetailAdd = result;
                mNewsDetailELVAdapter.setNewsDetail(result);
                if (result != null) {
//                    result.abs = "这是一个神奇的网站,这是一个神奇的网站,这是一个神奇的网站,这是一个神奇的网站";
//                    result.title = result.title + result.title +result.title;
                    mDetailHeaderView.updateView(result);
                    parseNewsDetail(result);
                    //设置Listview默认展开
                    for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
                        mDetailContentListView.expandGroup(i);
                    }
                }else {
                    ToastUtil.toastShort("此新闻暂时无法查看!");
                    NewsDetailAty2.this.finish();
                }
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.e("jigang", "network fail");
                mNewsLoadingImg.setVisibility(View.VISIBLE);
                mNewsDetailProgress.setVisibility(View.GONE);
            }
        }.setReturnType(new TypeToken<NewsDetailAdd>() {
        }.getType()));
        _Request.execute();
    }

    @Override
    public void finish() {
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (NewsFeedFgt.VALUE_NEWS_NOTIFICATION.equals(mSource)) {
            Intent intent = new Intent(this, MainAty.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                onBackPressed();
                break;
            case R.id.mDetailComment:
                ArrayList<NewsDetailAdd.Point> points;
                if (mNewsDetailAdd != null && !TextUtil.isListEmpty(mNewsDetailAdd.point)) {
                    points = mNewsDetailAdd.point;
                } else {
                    points = null;
                }
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                CommentPopupWindow window = new CommentPopupWindow(this, points, mNewsDetailUrl, this, -1, this, this);
                window.setFocusable(true);
                //防止虚拟软键盘被弹出菜单遮住
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.mDetailShare:
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(this, this);
                // FIXME: 15/11/5 有可能以后分享的时候有问题,遇到问题后改之
                String type = "1", remark = "1";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("newsid", newsId);
                hashMap.put("type", type);
                hashMap.put("collection", newsType);
                String url = HttpClientUtil.addParamsToUrl("http://deeporiginalx.com/news.html?", hashMap);
                mSharePopupWindow.setTitleAndUrl(mNewsDetailAdd.title, url,remark);
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.mNewsLoadingImg:
                loadData();
                break;
        }
    }


    /**
     * 解析新闻详情POJO,转换成expandablelistview 所须POJO
     *
     * @param pNewsDetail
     * @return
     */
    private ArrayList<ArrayList> parseNewsDetail(NewsDetailAdd pNewsDetail) {
        mNewsContentDataList.clear();
        /**计算展示内容需要多少个组,其中包括 新闻内容,多图集合,差异化观点,精选评论,新闻词条(百度百科,豆瓣),相关观点,微博热点,知乎推荐*/
        if (pNewsDetail != null) {
            /**计算新闻内容所在组*/
            if (!TextUtil.isListEmpty(pNewsDetail.content)) {
                ArrayList list = new ArrayList<>();
                ArrayList<NewsDetailAdd.Point> points = pNewsDetail.point;
                for (int i = 0; i < pNewsDetail.content.size(); i++) {
                    LinkedTreeMap<String, HashMap<String, String>> treeMap = pNewsDetail.content.get(i);
                    HashMap<String, String> hashMap = treeMap.get(i + "");
                    if (hashMap != null && hashMap.get("txt") != null) {
                        NewsDetailContent content = new NewsDetailContent();
                        content.setContent(hashMap.get("txt"));//img img_info txt
                        content.setComments(new ArrayList<NewsDetailAdd.Point>());
                        list.add(content);
                    }
                }
                if (!TextUtil.isListEmpty(points)) {
                    for (int j = 0; j < points.size(); j++) {
                        NewsDetailAdd.Point point = points.get(j);
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
                ArrayList<NewsDetailAdd.Article> self_opinion = pNewsDetail.relate_opinion.getSelf_opinion();
                if (!TextUtil.isListEmpty(self_opinion)) {
                    mNewsContentDataList.add(self_opinion);
                }
            }
            /**计算精选评论观点组数据*/
            if (!TextUtil.isListEmpty(pNewsDetail.point)) {
                ArrayList<NewsDetailAdd.Point> points = new ArrayList<>();
                for (int j = 0; j < pNewsDetail.point.size(); j++) {
                    NewsDetailAdd.Point point = pNewsDetail.point.get(j);
                    if (UploadCommentRequest.TEXT_DOC.equals(point.type) || UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                        points.add(point);
                    }
                }
                Collections.sort(points);
                /**只要3条评论*/
                if (points.size() > 3) {
                    points = new ArrayList<>(points.subList(0, 3));
                }
                points.add(new NewsDetailAdd.Point());
                mNewsContentDataList.add(points);
            }
            /**计算新闻词条组数据*/
            ArrayList<NewsDetailEntry> entryList = new ArrayList<>();
            if (!TextUtil.isListEmpty(pNewsDetail.baike)) {
                for (NewsDetailAdd.BaiDuBaiKe item : pNewsDetail.baike) {
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

    /**
     * 展开所有的childview
     */
    private void expandedChildViews() {
        for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
            mDetailContentListView.expandGroup(i);
        }
    }

    @Override
    public void updateCommentCount(NewsDetailAdd.Point point) {
        if (mNewsDetailAdd != null) {
            if (mNewsDetailAdd.point == null) {
                ArrayList<NewsDetailAdd.Point> list = new ArrayList<>();
                list.add(point);
                mNewsDetailAdd.point = list;
            } else {
                mNewsDetailAdd.point.add(point);
            }
            mNewsContentDataList = parseNewsDetail(mNewsDetailAdd);
        }

        mNewsDetailELVAdapter.notifyDataSetChanged();
        //设置Listview默认展开
        expandedChildViews();
    }


    @Override
    public void updataPraise() {

    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
    }
}
