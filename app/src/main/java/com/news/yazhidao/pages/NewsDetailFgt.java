package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailELVAdapter;
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
 * Created by fengjigang on 15/9/17.
 * 新闻详情页,主要用于SplashAty界面点击查看新闻详情时,展示其后面的若干条新闻
 */
public class NewsDetailFgt extends Fragment implements View.OnClickListener, CommentPopupWindow.IUpdateCommentCount, CommentPopupWindow.IUpdatePraiseCount, SharePopupWindow.ShareDismiss {

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId;
    private String mPlatformType;
    private String uuid;
    private String mNewsDetailUrl;
    private ImageView mivShareBg;
    private Context mContext;
    //新闻内容POJO
    private NewsDetail mNewsDetail;
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
    float startY;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.aty_news_detail_layout, container, false);
        initViews(root);
        loadData();
        return root;
    }

    private void loadData() {
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mAniNewsLoading.start();
        Bundle arguments = getArguments();
        boolean isNewApi = arguments.getBoolean(AlbumListAty.KEY_IS_NEW_API, false);
        boolean isDigger = false;
        AlbumSubItem albumSubItem;
        String newsId = null;
        if (arguments != null) {
            isDigger = arguments.getBoolean(AlbumListAty.KEY_IS_DIGGER,false);
            albumSubItem = (AlbumSubItem) arguments.getSerializable(AlbumListAty.KEY_ALBUMSUBITEM);
            newsId = albumSubItem.getInserteId();

        }
        User user = SharedPreManager.getUser(mContext);
        if (user != null) {
            mUserId = user.getUserId();
            mPlatformType = user.getPlatformType();
        }
        mNewsDetailUrl = arguments.getString("url");
        mNewsDetailELVAdapter.setNewsUrl(mNewsDetailUrl);
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

    private void initViews(View root) {
        mDetailView = root.findViewById(R.id.mDetailWrapper);
        mDetailHeaderView = new NewsDetailHeaderView2(mContext);
        mNewsDetailLoaddingWrapper = root.findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsDetailLoaddingWrapper.setOnClickListener(this);
        mNewsLoadingImg = (ImageView) root.findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mivShareBg = (ImageView) root.findViewById(R.id.share_bg_imageView);
        mDetailHeader = root.findViewById(R.id.mDetailHeader);
        mDetailLeftBack = root.findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailComment = root.findViewById(R.id.mDetailComment);
        mDetailComment.setOnClickListener(this);
        mDetailShare = root.findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);

        mDetailContentListView = (ExpandableListView) root.findViewById(R.id.mDetailContentListView);
        mDetailContentListView.addHeaderView(mDetailHeaderView);
        mDetailContentListView.setAdapter(mNewsDetailELVAdapter);
        mDetailContentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View childAt = view.getChildAt(0);
                if (childAt != null) {
                    if (firstVisibleItem != 0 || childAt.getY() > 0) {
                        return;
                    }
                    double move = (Math.abs(childAt.getY()) * 1.5 > childAt.getHeight()) ? childAt.getHeight() : Math.abs(childAt.getY()) * 1.1;
                    int alpaha = (int) (move * 1.0f / childAt.getHeight() * 255);
                    Drawable drawable = mContext.getResources().getDrawable(R.color.bg_home_login_header);
                    mDetailHeader.setBackground(drawable);
                    mDetailHeader.getBackground().setAlpha(alpaha);
                }
            }
        });
        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
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
    /**
     * 解析新闻详情POJO,转换成expandablelistview 所须POJO
     *
     * @param pNewsDetail
     * @return
     */
    private ArrayList<ArrayList> parseNewsDetail(NewsDetail pNewsDetail) {
        mNewsContentDataList.clear();
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
                        try {
                            int paragraphIndex = Integer.valueOf(point.paragraphIndex);
                            if (UploadCommentRequest.TEXT_PARAGRAPH.equals(point.type)) {
                                if (paragraphIndex < list.size()) {
                                    NewsDetailContent content = (NewsDetailContent) list.get(paragraphIndex);
                                    content.getComments().add(point);
                                }
                            }
                        } catch (NumberFormatException e) {

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
                if (!TextUtil.isListEmpty(self_opinion)) {
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
                if (!TextUtil.isListEmpty(self_opinion)) {
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
    /**
     * 展开所有的childview
     */
    private void expandedChildViews() {
        for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
            mDetailContentListView.expandGroup(i);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                ((Activity)mContext).finish();
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
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                CommentPopupWindow window = new CommentPopupWindow(mContext, points, mNewsDetailUrl, this, -1, this, this);
                window.setFocusable(true);
                //防止虚拟软键盘被弹出菜单遮住
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.mDetailShare:
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(mContext, this);
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

    @Override
    public void updateCommentCount(NewsDetail.Point point) {
        if (mNewsDetail != null) {
            if (mNewsDetail.point == null) {
                ArrayList<NewsDetail.Point> list = new ArrayList<>();
                list.add(point);
                mNewsDetail.point = list;
            } else {
                mNewsDetail.point.add(point);
            }
            mNewsContentDataList = parseNewsDetail(mNewsDetail);
        } else if (mNewsDetailAdd != null) {
            if (mNewsDetailAdd.point == null) {
                ArrayList<NewsDetail.Point> list = new ArrayList<>();
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
