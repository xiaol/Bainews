package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailELVAdapter;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.HttpClientUtil;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.CommentPopupWindow;
import com.news.yazhidao.widget.NewsDetailHeaderView2;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.UserCommentDialog;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class NewsDetailAty2 extends BaseActivity implements View.OnClickListener, CommentPopupWindow.IUpdateCommentCount, CommentPopupWindow.IUpdatePraiseCount, SharePopupWindow.ShareDismiss {

    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";
    public static final String ACTION_REFRESH_COMMENT = "com.news.baijia.ACTION_REFRESH_COMMENT";

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId = "";
    private String mPlatformType = "";
    private String uuid;
    private String mNewsDetailUrl;
    private ImageView mivShareBg;
    //新闻内容POJO
    private NewsDetailAdd mNewsDetailAdd;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private NewsDetailELVAdapter mNewsDetailELVAdapter;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private ExpandableListView mDetailContentListView;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailComment,mDetailHeader, mNewsDetailLoaddingWrapper;
    private ImageView mDetailLeftBack,mDetailShare;
    private ImageView mNewsLoadingImg;
    private AnimationDrawable mAniNewsLoading;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
//    private ProgressBar mNewsDetailProgress;
    private RelativeLayout bgLayout;

    private boolean isDisplay = true;
    private int defaultH;//图片新闻文本描述的默认高度
    private String mSource;
    private String newsId = null;
    private String newsType = null;
    private long mDurationStart;//统计用户读此条新闻时话费的时间
    private boolean isReadOver;//是否看完了全文,此处指的是翻到最下面
    private boolean isCommentPage;//是否是评论页
    private String channelId;
    private String mImgUrl;
    private View mDetailAddComment;
    private TextView mDetailCommentNum;
    private String mNewsType;
    private View mImageWallWrapper;
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc;
    private View mDetailBottomBanner;
    private ImageView mDetailCommentPic;
    private WebView mDetailWebView;
    private ViewPager mNewsDetailViewPager;
    private String mNewsDocId;
    private RefreshPageBroReceiber mRefreshReceiber;
    private UserCommentDialog mCommentDialog;

    /**通知新闻详情页和评论fragment刷新评论*/
    public  class RefreshPageBroReceiber extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
                Logger.e("jigang","comment fgt refresh br");
            String number = mDetailCommentNum.getText().toString();
            mDetailCommentNum.setText(Integer.valueOf(number) + 1 + "");
        }
    }

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
        mImageViews = new ArrayList<>();
        mNewsDetailELVAdapter = new NewsDetailELVAdapter(this, mNewsContentDataList);
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
    }

    @Override
    protected void initializeViews() {
//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mDetailHeaderView = new NewsDetailHeaderView2(this);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(this);
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mNewsDetailProgress = (ProgressBar) findViewById(R.id.mNewsDetailProgress);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = findViewById(R.id.mDetailHeader);
        mDetailLeftBack = (ImageView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailComment =  findViewById(R.id.mDetailComment);
        mDetailCommentPic =  (ImageView)findViewById(R.id.mDetailCommentPic);
        mDetailComment.setOnClickListener(this);
        mDetailShare = (ImageView) findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);
        mDetailAddComment = findViewById(R.id.mDetailAddComment);
        mDetailAddComment.setOnClickListener(this);
        mDetailCommentNum = (TextView) findViewById(R.id.mDetailCommentNum);
        mDetailBottomBanner = findViewById(R.id.mDetailBottomBanner);
        mImageWallWrapper =  findViewById(R.id.mImageWallWrapper);
        mImageWallVPager = (ViewPager)findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView)findViewById(R.id.mImageWallDesc);
        mDetailWebView = (WebView)findViewById(R.id.mDetailWebView);
        mNewsDetailViewPager = (ViewPager)findViewById(R.id.mNewsDetailViewPager);

        mDetailContentListView = (ExpandableListView) findViewById(R.id.mDetailContentListView);
        mDetailContentListView.addHeaderView(mDetailHeaderView);
        mDetailContentListView.setAdapter(mNewsDetailELVAdapter);
        mDetailContentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE){
                    if(view.getLastVisiblePosition() == view.getCount() - 1){
                        isReadOver = true;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
    protected void onResume() {
        super.onResume();
        mDurationStart = System.currentTimeMillis();
        if(mRefreshReceiber == null){
            mRefreshReceiber = new RefreshPageBroReceiber();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
            registerReceiver(mRefreshReceiber, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        long readDuration = System.currentTimeMillis() - mDurationStart;
        Logger.e("jigang","time = "+ DateUtil.getDate()+",read duration = " + readDuration + ",readOver = " + isReadOver + ",newsid ="+newsId+",type="+newsType +",channelId =" +channelId+ ",uuid="+uuid+",userid="+mUserId+",location="+SharedPreManager.get(CommonConstant.FILE_USER_LOCATION,CommonConstant.KEY_USER_LOCATION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRefreshReceiber != null){
            unregisterReceiver(mRefreshReceiber);
            mRefreshReceiber = null;
        }
    }

    /**
     *  显示新闻详情和评论
     * @param result
     */
    private void displayDetailAndComment(final NewsDetail result){
        mNewsDetailViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (position == 1){
                    isCommentPage = true;
                    mDetailCommentPic.setImageResource(R.drawable.detail_switch_commet);
                    mDetailCommentNum.setVisibility(View.INVISIBLE);
                }else {
                    isCommentPage = false;
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(View.VISIBLE);
                }
            }
        });
        mNewsDetailViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0){
                    NewsDetailFgt detailFgt = new NewsDetailFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsDetailFgt.KEY_DETAIL_RESULT,result);
                    detailFgt.setArguments(args);
                    return detailFgt;
                }else {
                    NewsCommentFgt commentFgt = new NewsCommentFgt();
                    Bundle args = new Bundle();
                    args.putString(NewsCommentFgt.KEY_NEWS_DOCID,result.getDocid());
                    commentFgt.setArguments(args);
                    return commentFgt;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }
    @Override
    protected void loadData() {
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
//        mAniNewsLoading.start();
        mNewsLoadingImg.setVisibility(View.GONE);

        bgLayout.setVisibility(View.VISIBLE);
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
            channelId = getIntent().getStringExtra(NewsFeedFgt.KEY_CHANNEL_ID);
            mImgUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMG_URL);
            mNewsType = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_TYPE);
            mNewsDocId = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_DOCID);
//            mNewsType = "big_pic";
            mNewsDetailELVAdapter.setNewsImgUrl(mImgUrl);
//            newsId = "2ffae38a585d31376be0465de6e591ee";
//            newsId = "64bd38470cb881dc0940e1295ba39103";
//            newsId = "705715e8ee4bc0e2fdd41e27f9b08de2";
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

        Logger.e("jigang","detail url=" + HttpConstant.URL_FETCH_CONTENT + "url=" + TextUtil.getBase64(newsId));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<NewsDetail>(Request.Method.GET, new TypeToken<NewsDetail>() {
        }.getType(), HttpConstant.URL_FETCH_CONTENT + "url=" + TextUtil.getBase64(newsId), new Response.Listener<NewsDetail>(){

            @Override
            public void onResponse(NewsDetail result) {
                mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                Logger.e("jigang", "network success~~" + result);
                if (result != null) {
                    displayDetailAndComment(result);
                    //此处判断是否是图片新闻
                    if ("big_pic".equals(mNewsType)){
                        if (!TextUtil.isListEmpty(null)){
//                            mImages = result.imgWall;
                            //隐藏listview 展示imagewall fragment
                            mDetailContentListView.setVisibility(View.GONE);
                            mImageWallWrapper.setVisibility(View.VISIBLE);
                            configViewPagerViews();
                        }else {
                            mDetailContentListView.setVisibility(View.VISIBLE);
                            mImageWallWrapper.setVisibility(View.GONE);
                        }

                    }
                    mDetailHeaderView.updateView(result);
                    if(result.getCommentSize() != 0){
                        mDetailCommentNum.setVisibility(View.VISIBLE);
                        mDetailCommentNum.setText(result.getCommentSize()+"");
                    }
                }else {
                    ToastUtil.toastShort("此新闻暂时无法查看!");
                    NewsDetailAty2.this.finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("jigang", "network fail");
                mNewsLoadingImg.setVisibility(View.VISIBLE);
                bgLayout.setVisibility(View.GONE);
            }
        });
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,0));
        requestQueue.add(feedRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
                Logger.e("jigang","back aty ------");
            if (mCommentDialog != null && mCommentDialog.isVisible()){
                mCommentDialog.dismiss();
                return true;
            }
            if (isCommentPage){
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0,true);
                mDetailCommentNum.setVisibility(View.VISIBLE);
                mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(NewsFeedAdapter.KEY_NEWS_ID,newsId);
        setResult(NewsFeedAdapter.REQUEST_CODE,intent);
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (NewsFeedFgt.VALUE_NEWS_NOTIFICATION.equals(mSource)) {
            Intent main = new Intent(this, MainAty.class);
            startActivity(main);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                onBackPressed();
                break;
            case R.id.mDetailAddComment:
                mCommentDialog = new UserCommentDialog(NewsDetailAty2.this,null);
                mCommentDialog.setDocid(mNewsDocId);
                mCommentDialog.show(NewsDetailAty2.this.getSupportFragmentManager(), "UserCommentDialog");
                break;
            case R.id.mDetailComment:
                if (!isCommentPage){
                    isCommentPage = true;
                    mNewsDetailViewPager.setCurrentItem(1);
                    mDetailCommentPic.setImageResource(R.drawable.detail_switch_commet);
                    mDetailCommentNum.setVisibility(View.INVISIBLE);
                }else {
                    isCommentPage = false;
                    mNewsDetailViewPager.setCurrentItem(0);
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(View.VISIBLE);
                }
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
     * 打开新闻评论页
     */
    private void openCommentPage(){
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
                list.add(0,point);
                mNewsDetailAdd.point = list;
            } else {
                mNewsDetailAdd.point.add(0,point);
            }
            mNewsContentDataList = TextUtil.parseNewsDetail(mNewsContentDataList,mNewsDetailAdd,mImgUrl);
        }
        //更新评论显示数字
        mDetailCommentNum.setText(mNewsDetailAdd.point.size() + "");
        mDetailCommentNum.setVisibility(View.VISIBLE);
        mNewsDetailELVAdapter.notifyDataSetChanged();
        //设置Listview默认展开
        expandedChildViews();
        openCommentPage();
    }


    @Override
    public void updataPraise() {

    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
    }

    private void configViewPagerViews(){
        mDetailHeader.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailBottomBanner.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailAddComment.setBackgroundResource(R.drawable.user_add_comment_black);
        int padding = DensityUtil.dip2px(this,8);
        mDetailLeftBack.setImageResource(R.drawable.btn_detail_left_white);
        mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment_white);
        mDetailShare.setImageResource(R.drawable.btn_detail_share_white);
        mDetailView.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailAddComment.setPadding(padding,padding,padding,padding);
        for (int i = 0; i < mImages.size(); i++) {
            final SimpleDraweeView imageView = new SimpleDraweeView(this);
            ViewGroup.LayoutParams  params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            mImageViews.add(imageView);
            imageView.setImageURI(Uri.parse(mImages.get(i).get("img")));
        }
        final int margin = DensityUtil.dip2px(this,12);
        mImageWallVPager.setPadding(0, 0, 0, 0);
        mImageWallVPager.setClipToPadding(false);
        mImageWallVPager.setPageMargin(margin);
        mImageWallVPager.setAdapter(new ImagePagerAdapter(mImageViews));
        mImageWallVPager.setOffscreenPageLimit(3);
        Logger.e("jigang","sssss =" + mImageWallDesc.getLineHeight());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageWallDesc.getLayoutParams();
        params.height = (int) (mImageWallDesc.getLineHeight() * 4.5);
        mImageWallDesc.setMaxLines(4);
        mImageWallDesc.setLayoutParams(params);
//        mImageWallDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
//        mImages.get(1).put("note","随着越来越多的大众直播平台推出，网络主播尤其是在中国年轻人中颇具人气。杨希月就是数十万网络主播的一员，今年刚满20岁，四川某传媒学院在校生，从事网络主播两年时间，已是某平台上金牌签约主播，每月收入约10万元。杨希月在某平台上数万主播中目前排名前100名，每天受到20万粉丝拥护，一方面也得益于她的舞蹈功底，自己从小便喜欢上了舞蹈。");
        mImageWallDesc.setText(Html.fromHtml(1 + "<small>" + "/" + mImages.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImages.get(0).get("note")));
        mImageWallVPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){

            @Override
            public void onPageSelected(int position) {
                mImageWallDesc.setText(Html.fromHtml(position + 1 + "<small>" + "/" + mImages.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImages.get(position).get("note")));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageWallDesc.getLayoutParams();
                params.height = (int) (mImageWallDesc.getLineHeight() * 4.5);
                mImageWallDesc.setMaxLines(4);
                mImageWallDesc.setLayoutParams(params);
                Logger.e("jigang","change =" + mImageWallDesc.getHeight());
            }
        });
        mImageWallDesc.setOnTouchListener(new View.OnTouchListener() {
            float startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (defaultH == 0){
                        defaultH = mImageWallDesc.getHeight();
                }
                Logger.e("jigang","default =" + defaultH);
                int lineCount = mImageWallDesc.getLineCount();
                int maxHeight = mImageWallDesc.getLineHeight() * lineCount;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Logger.e("jigang","---down");
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getRawY() - startY;
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageWallDesc.getLayoutParams();
                        int height = mImageWallDesc.getHeight();
                        Logger.e("jigang","height="+height + ",maxHeight="+maxHeight);
                        if (Math.abs(deltaY) > 1 && lineCount > 4){
                            height -= deltaY;
                            if (deltaY > 0){
                                if (height < defaultH){
                                    height = defaultH;
                                }
                            }else {
                            if (height > maxHeight){
                                height = maxHeight + DensityUtil.dip2px(NewsDetailAty2.this,6 * 2 + 4);
                            }
                            }
                            params.height = height;
                            mImageWallDesc.setMaxLines(Integer.MAX_VALUE);
                            mImageWallDesc.setLayoutParams(params);
                        }
                        Logger.e("jigang",event.getRawY() + "---move " + deltaY);
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.e("jigang","---up");
                        break;
                }
                return true;
            }
        });
        final GestureDetector tapGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isDisplay){
                    isDisplay = false;
                    ObjectAnimator.ofFloat(mDetailHeader,"alpha",1.0f,0).setDuration(200).start();
                    ObjectAnimator.ofFloat(mImageWallDesc,"alpha",1.0f,0).setDuration(200).start();
                    ObjectAnimator.ofFloat(mDetailBottomBanner,"alpha",1.0f,0).setDuration(200).start();
                }else {
                    isDisplay = true;
                    ObjectAnimator.ofFloat(mDetailHeader,"alpha",0,1.0f).setDuration(200).start();
                    ObjectAnimator.ofFloat(mImageWallDesc,"alpha",0,1.0f).setDuration(200).start();
                    ObjectAnimator.ofFloat(mDetailBottomBanner,"alpha",0,1.0f).setDuration(200).start();
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        mImageWallVPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    class ImagePagerAdapter extends PagerAdapter {


        private List<View> views = new ArrayList<View>();

        public ImagePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    }
}
