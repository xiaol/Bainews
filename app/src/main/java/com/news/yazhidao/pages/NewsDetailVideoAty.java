package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.LocationEntity;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.net.volley.UpLoadLogRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;
import com.news.yazhidao.widget.NewsDetailHeaderView2;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.SharePopupWindow.OnFavoritListener;
import com.news.yazhidao.widget.SwipeBackViewpager;
import com.news.yazhidao.widget.UserCommentDialog;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.news.yazhidao.pages.NewsFeedFgt.VALUE_NEWS_NOTIFICATION;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class NewsDetailVideoAty extends SwipeBackActivity implements View.OnClickListener, SharePopupWindow.ShareDismiss {

    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";
    public static final String ACTION_REFRESH_COMMENT = "com.news.baijia.ACTION_REFRESH_COMMENT";

    private int mScreenWidth, mScreenHeight;
    private String mUserId = "";
    private String mPlatformType = "";
    private String uuid;
    private ImageView mivShareBg;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailComment, mDetailHeader, mNewsDetailLoaddingWrapper;
    private ImageView mDetailShare;
    private TextView mDetailLeftBack, mDetailRightMore;
    private ImageView mNewsLoadingImg;
    private AnimationDrawable mAniNewsLoading;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
    //    private ProgressBar mNewsDetailProgress;
    private RelativeLayout bgLayout;

    private boolean isDisplay = true;
    private int defaultH;//图片新闻文本描述的默认高度
    private long mDurationStart;//统计用户读此条新闻时话费的时间
    private boolean isReadOver;//是否看完了全文,此处指的是翻到最下面
    public boolean isCommentPage;//是否是评论页
    private View mDetailAddComment;
    public TextView mDetailCommentNum;
    private View mImageWallWrapper;
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc, carefor_Text;
    private View mDetailBottomBanner;
    public ImageView mDetailCommentPic, mDetailFavorite, carefor_Image;
    //    private WebView mDetailWebView;
    public SwipeBackViewpager mNewsDetailViewPager;
    private RefreshPageBroReceiber mRefreshReceiber;
    private UserCommentDialog mCommentDialog;
    private NewsFeed mNewsFeed;
    private String mSource, mImageUrl;
    private String mUrl;
    private NewsDetailCommentDao newsDetailCommentDao;

    private LinearLayout careforLayout;
    boolean isFavorite, isCareFor;
    public static final int REQUEST_CODE = 1030;
    private NewsFeed mUsedNewsFeed;
    private StringBuffer path;
    private VPlayPlayer vp;

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiber extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("jigang", "comment fgt refresh br");

            NewsDetailComment newsDetailComment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
            newsDetailComment.setNewsFeed(mNewsFeed);
            newsDetailComment.setOriginal(mNewsFeed.getTitle());
            newsDetailCommentDao.add(newsDetailComment);

//            NewsDetailCommentItem newsDetailComment = (NewsDetailCommentItem) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
//            newsDetailComment.setNewsFeed(mNewsFeed);
//            newsDetailComment.setOriginal(mNewsFeed.getTitle());
//            newsDetailCommentDao.add(newsDetailComment);


//            } else {

            Logger.e("jigang", "comment fgt refresh br");
            int number = 0;
            try {
                number = Integer.valueOf(mDetailCommentNum.getText().toString());
            } catch (Exception e) {

            }
            if (mDetailCommentNum.getVisibility() == View.GONE && !isCommentPage) {
                mDetailCommentNum.setVisibility(View.VISIBLE);
            }
            mDetailCommentNum.setText(number + 1 + "");
            mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);

//            }
        }
    }


    @Override
    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_video_detail_layout);
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(this);
        mNewsContentDataList = new ArrayList<>();
        mImageViews = new ArrayList<>();
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
    }

    @Override
    protected void initializeViews() {
        mUsedNewsFeed = (NewsFeed) getIntent().getSerializableExtra(VideoCommentFgt.KEY_NEWS_FEED);
//        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
//        mImageUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMAGE);
//        mUsedNewsFeed = getDate();
//        mImageUrl = "http://bdp-pic.deeporiginalx.com/W0JAM2ExMmYwNGQ.png";

        careforLayout = (LinearLayout) findViewById(R.id.careforLayout);
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
        mDetailLeftBack = (TextView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailRightMore = (TextView) findViewById(R.id.mDetailRightMore);
        mDetailRightMore.setOnClickListener(this);
//        mDetailRightAttention = (TextView) findViewById(R.id.mDetailRightAttention);
//        mDetailRightAttention.setOnClickListener(this);
        mDetailComment = findViewById(R.id.mDetailComment);
        mDetailCommentPic = (ImageView) findViewById(R.id.mDetailCommentPic);
        mDetailFavorite = (ImageView) findViewById(R.id.mDetailFavorite);
        mDetailFavorite.setOnClickListener(this);
        carefor_Text = (TextView) findViewById(R.id.carefor_Text);
        carefor_Image = (ImageView) findViewById(R.id.carefor_Image);

        mDetailComment.setOnClickListener(this);
        mDetailShare = (ImageView) findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);
        mDetailAddComment = findViewById(R.id.mDetailAddComment);
        mDetailAddComment.setOnClickListener(this);
        mDetailCommentNum = (TextView) findViewById(R.id.mDetailCommentNum);
        mDetailBottomBanner = findViewById(R.id.mDetailBottomBanner);
        mImageWallWrapper = findViewById(R.id.mImageWallWrapper);
        mImageWallVPager = (ViewPager) findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView) findViewById(R.id.mImageWallDesc);
        mNewsDetailViewPager = (SwipeBackViewpager) findViewById(R.id.mNewsDetailViewPager);



        //初始化新闻评论DAO
        newsDetailCommentDao = new NewsDetailCommentDao(this);
    }

    long lastTime, nowTime;

    @Override
    protected void onResume() {
        super.onResume();
        nowTime = System.currentTimeMillis();
        MobclickAgent.onPause(this);
        mDurationStart = System.currentTimeMillis();
        if (mRefreshReceiber == null) {
            mRefreshReceiber = new RefreshPageBroReceiber();
            IntentFilter filter = new IntentFilter(ACTION_REFRESH_COMMENT);
//            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            registerReceiver(mRefreshReceiber, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lastTime = System.currentTimeMillis() - nowTime + lastTime;
        long readDuration = System.currentTimeMillis() - mDurationStart;

    }


    @Override
    protected void onDestroy() {
        upLoadLog();
        super.onDestroy();
        if (mRefreshReceiber != null) {
            unregisterReceiver(mRefreshReceiber);
            mRefreshReceiber = null;
        }
    }

    /**
     * 上报日志
     *
     * @throws IOException
     */
    private void upLoadLog() {
        Log.e("aaa", "开始上传日志！");
        if (mNewsFeed == null || SharedPreManager.getUser(this) == null) {
            return;
        }
        User user = SharedPreManager.getUser(this);
        final UploadLogDataEntity uploadLogDataEntity = new UploadLogDataEntity();
        uploadLogDataEntity.setN(mNewsFeed.getNid() + "");
        uploadLogDataEntity.setC(mNewsFeed.getChannel() + "");
        uploadLogDataEntity.setT("0");
        uploadLogDataEntity.setS(lastTime / 1000 + "");
        uploadLogDataEntity.setF("0");
        final String locationJsonString = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION);
        String LogData = SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_DETAIL);//;
        LocationEntity locationEntity = null;
        Gson gson = new Gson();
        locationEntity = gson.fromJson(locationJsonString, LocationEntity.class);
        if (!TextUtil.isEmptyString(LogData)) {
            SharedPreManager.upLoadLogSave(user.getMuid() + "", CommonConstant.UPLOAD_LOG_DETAIL, locationJsonString, uploadLogDataEntity);
        }

//        Logger.e("ccc", "详情页的数据====" + SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_DETAIL));
//        if (saveNum >= 5) {
        Logger.e("aaa", "确认上传日志！");


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String userid = null, p = null, t = null, i = null;
        try {
            userid = URLEncoder.encode(user.getMuid() + "", "utf-8");
            if (locationEntity != null) {
                if (locationEntity.getProvince() != null)
                    p = URLEncoder.encode(locationEntity.getProvince() + "", "utf-8");
                if (locationEntity.getCity() != null)
                    t = URLEncoder.encode(locationEntity.getCity(), "utf-8");
                if (locationEntity.getDistrict() != null)
                    i = URLEncoder.encode(locationEntity.getDistrict(), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = HttpConstant.URL_UPLOAD_LOG + "u=" + userid + "&p=" + p +
                "&t=" + t + "&i=" + i + "&d=" + TextUtil.getBase64(TextUtil.isEmptyString(LogData) ? gson.toJson(uploadLogDataEntity) : SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_DETAIL));
        Logger.d("aaa", "url===" + url);

        UpLoadLogRequest<String> request = new UpLoadLogRequest<String>(Request.Method.GET, String.class, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreManager.upLoadLogDelter(CommonConstant.UPLOAD_LOG_DETAIL);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage().contains("GIF")) {
                    SharedPreManager.upLoadLogDelter(CommonConstant.UPLOAD_LOG_DETAIL);
                } else {
                    SharedPreManager.upLoadLogSave(mUserId, CommonConstant.UPLOAD_LOG_DETAIL, locationJsonString, uploadLogDataEntity);
                }
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Content-Type", "image/gif");
        request.setRequestHeader(header);
        requestQueue.add(request);
//        }
    }

    FragmentPagerAdapter pagerAdapter;

    /**
     * 显示新闻详情和评论
     *
     * @param result
     */
    private void displayDetailAndComment(final NewsDetail result) {
        /** 判断是否收藏 */
        isFavorite = SharedPreManager.myFavoriteisSame(mUrl);
        if (result.getColflag() == 1) {
            if (!isFavorite) {
                SharedPreManager.myFavoriteSaveList(mNewsFeed);
            }
            isFavorite = true;
            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
        } else {
            if (isFavorite) {
                SharedPreManager.myFavoritRemoveItem(mUrl);
            }
            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
            isFavorite = false;
        }
//        /** 判断是否关注 */
//        mDetailRightAttention.setText(result.getConpubflag() == 1 ? "已关注" : "去关注");

        mNewsDetailViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    isCommentPage = true;
//                    onShowFragmentListener.setOnShowFragment(false);
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mDetailCommentNum.setVisibility(View.GONE);

                    Drawable drawable= getResources().getDrawable(R.drawable.btn_left_back);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mDetailLeftBack.setCompoundDrawables(drawable,null,null,null);

                    Drawable share= getResources().getDrawable(R.drawable.btn_detail_right_more);
                    share.setBounds(0, 0, share.getMinimumWidth(), share.getMinimumHeight());
                    mDetailRightMore.setCompoundDrawables(null,null,share,null);


                } else {
                    isCommentPage = false;
//                    onShowFragmentListener.setOnShowFragment(true);
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment);
                    mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);

                    Drawable drawable1= getResources().getDrawable(R.drawable.detial_video_back);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    mDetailLeftBack.setCompoundDrawables(drawable1,null,null,null);

                    Drawable share1= getResources().getDrawable(R.drawable.detai_video_share);
                    share1.setBounds(0, 0, share1.getMinimumWidth(), share1.getMinimumHeight());
                    mDetailRightMore.setCompoundDrawables(null,null,share1,null);

                }
            }
        });
        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    NewsDetailVideoFgt detailFgt = new NewsDetailVideoFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(NewsDetailVideoFgt.KEY_DETAIL_RESULT, result);
                    args.putString(NewsDetailVideoFgt.KEY_NEWS_DOCID, result.getDocid());
                    args.putString(NewsDetailVideoFgt.KEY_NEWS_ID, mUrl);
                    args.putString(NewsDetailVideoFgt.KEY_NEWS_TITLE, mNewsFeed.getTitle());
//                    detailFgt.setShowCareforLayout(mShowCareforLayout);
                    detailFgt.setArguments(args);

                    return detailFgt;
                } else {
                    VideoCommentFgt commentFgt = new VideoCommentFgt();
                    Bundle args = new Bundle();
                    args.putSerializable(VideoCommentFgt.KEY_NEWS_FEED, mNewsFeed);
                    commentFgt.setArguments(args);
//                    Drawable drawable= getResources().getDrawable(R.drawable.btn_left_back);
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    mDetailLeftBack.setCompoundDrawables(drawable,null,null,null);
//
//                    Drawable share= getResources().getDrawable(R.drawable.btn_detail_right_more);
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    mDetailLeftBack.setCompoundDrawables(null,null,share,null);
                    return commentFgt;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        mNewsDetailViewPager.setAdapter(pagerAdapter);
    }
//    public OnShowFragmentListener onShowFragmentListener;
//
//    public  interface OnShowFragmentListener
//    {
//
//        void setOnShowFragment(boolean showFragment);
//    }
//
//    public void setOnShowFragmentListner(OnShowFragmentListener onShowFragmentListner)
//    {
//        onShowFragmentListener=onShowFragmentListner;
//    }
    @Override
    protected void loadData() {
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
//        mAniNewsLoading.start();
        try {
            Logger.e("aaa", "刚刚进入============" + SharedPreManager.myFavoriteGetList().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        mNewsLoadingImg.setVisibility(View.GONE);
        mNewsDetailViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        bgLayout.setVisibility(View.VISIBLE);
        mNewsFeed = (NewsFeed) getIntent().getSerializableExtra(NewsFeedFgt.KEY_NEWS_FEED);
//        mNewsFeed = getDate();
        if (mNewsFeed != null) {
            mUrl = mNewsFeed.getNid() + "";
        } else {
            mUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
        }
//                mUrl = "6562498";
//                mUrl = "9076124";
//        mUrl = "9655396";
        path = new StringBuffer();
        path.append(HttpConstant.URL_VIDEO_CONTENT);
        path.append("nid=");
        path.append(mUrl);
        User user = SharedPreManager.getUser(NewsDetailVideoAty.this);
        if (user != null ) {
            mUserId = user.getMuid() + "";
            mPlatformType = user.getPlatformType();
            path.append("&uid=");
            path.append(mUserId);
        }
        else
        {
            UserManager.registerVisitor(this, new UserManager.RegisterVisitorListener() {
                @Override
                public void registeSuccess() {
                    User user = SharedPreManager.getUser(NewsDetailVideoAty.this);
                    mUserId = user.getMuid() + "";
                    mPlatformType = user.getPlatformType();
                    path.append("&uid=");
                    path.append(mUserId);
                }});
        }
        uuid = DeviceInfoUtil.getUUID();
//        isFavorite = SharedPreManager.myFavoriteisSame(mUrl);
//        if(isFavorite){
//        }else {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//        }

        Logger.e("jigang", "detail url=" + path.toString());
        RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
        NewsDetailRequest<NewsDetail> feedRequest = new NewsDetailRequest<NewsDetail>(Request.Method.GET, new TypeToken<NewsDetail>() {
        }.getType(), path.toString(), new Response.Listener<NewsDetail>() {

            @Override
            public void onResponse(NewsDetail result) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mNewsDetailLoaddingWrapper.getVisibility() == View.VISIBLE) {
                            mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                        }

                        if (bgLayout.getVisibility() == View.VISIBLE) {
                            bgLayout.setVisibility(View.GONE);
                        }
                    }
                }, 500);


                Logger.e("jigang", "network success~~" + result);
                if (result != null) {
                    mNewsFeed = convert2NewsFeed(result);
                    displayDetailAndComment(result);
                    mDetailHeaderView.updateView(result);
                    if (result.getComment() != 0) {
                        mDetailCommentNum.setVisibility(View.VISIBLE);
                        mDetailCommentNum.setText(TextUtil.getCommentNum(result.getComment() + ""));
                        mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    }
                } else {
                    ToastUtil.toastShort("此新闻暂时无法查看!");
                    NewsDetailVideoAty.this.finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("jigang", "network fail");
                if (mNewsDetailLoaddingWrapper.getVisibility() == View.GONE) {
                    mNewsDetailLoaddingWrapper.setVisibility(View.VISIBLE);
                }
                if (mNewsLoadingImg.getVisibility() == View.GONE) {
                    mNewsLoadingImg.setVisibility(View.VISIBLE);
                }

                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        });

        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
    }

    private NewsFeed convert2NewsFeed(NewsDetail result) {
        NewsFeed mNewsFeed = new NewsFeed();
        mNewsFeed.setDocid(result.getDocid());
        mNewsFeed.setUrl(result.getUrl());
        mNewsFeed.setTitle(result.getTitle());
        mNewsFeed.setPname(result.getPname());
        mNewsFeed.setPtime(result.getPtime());
        mNewsFeed.setComment(result.getComment());
        mNewsFeed.setChannel(result.getChannel());
        mNewsFeed.setStyle(result.getImgNum());
        mNewsFeed.setImageUrl(mImageUrl);
        mNewsFeed.setNid(result.getNid());
        mNewsFeed.setColflag(result.getColflag());
        mNewsFeed.setConflag(result.getConflag());
        mNewsFeed.setConpubflag(result.getConpubflag());


        return mNewsFeed;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCommentDialog != null && mCommentDialog.isVisible()) {
                mCommentDialog.dismiss();
                return true;
            }
            if (isCommentPage) {
                isCommentPage = false;
                mNewsDetailViewPager.setCurrentItem(0, true);
                mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        if (mNewsFeed != null) {
            Intent intent = new Intent();
            intent.putExtra(NewsFeedAdapter.KEY_NEWS_ID, mNewsFeed.getNid());
            setResult(NewsFeedAdapter.REQUEST_CODE, intent);
        }
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (VALUE_NEWS_NOTIFICATION.equals(mSource)) {
            Intent main = new Intent(this, MainAty.class);
            startActivity(main);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                if (isCommentPage) {
                    isCommentPage = false;
                    mNewsDetailViewPager.setCurrentItem(0, true);
                    mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);
                    return;
                }
                onBackPressed();
                break;
//            case R.id.mDetailRightAttention:
//                ToastUtil.toastShort("去关注");
//
//                break;
            case R.id.mDetailRightMore://更多的点击
                if (mNewsFeed != null) {
                    mivShareBg.startAnimation(mAlphaAnimationIn);
                    mivShareBg.setVisibility(View.VISIBLE);
                    mSharePopupWindow = new SharePopupWindow(this, this);
                    String remark = mNewsFeed.getDescr();
                    String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
                    mSharePopupWindow.setTitleAndUrl(mNewsFeed, remark);
                    mSharePopupWindow.setOnFavoritListener(listener);
                    mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                }
                MobclickAgent.onEvent(this, "qidian_user_detail_onclick_more");
                break;
            case R.id.mDetailAddComment:
                if (mNewsFeed != null) {
                    mCommentDialog = new UserCommentDialog();
                    mCommentDialog.setDocid(mNewsFeed.getDocid());
                    mCommentDialog.show(NewsDetailVideoAty.this.getSupportFragmentManager(), "UserCommentDialog");
                }
                MobclickAgent.onEvent(this, "qidian_user_detail_add_comment");
                break;
            case R.id.mDetailComment:
                if (!isCommentPage) {
                    isCommentPage = true;
                    mNewsDetailViewPager.setCurrentItem(1);
                    mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mDetailCommentNum.setVisibility(View.GONE);
                } else {
                    isCommentPage = false;
                    mNewsDetailViewPager.setCurrentItem(0);
                    mDetailCommentPic.setImageResource(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? R.drawable.btn_detail_no_comment : R.drawable.btn_detail_comment);
                    mDetailCommentNum.setVisibility(TextUtil.isEmptyString(mDetailCommentNum.getText().toString()) ? View.GONE : View.VISIBLE);
                }
                break;
            case R.id.mDetailShare:
                if (mNewsFeed != null) {
                    mivShareBg.startAnimation(mAlphaAnimationIn);
                    mivShareBg.setVisibility(View.VISIBLE);
                    mSharePopupWindow = new SharePopupWindow(this, this);
                    String remark = mNewsFeed.getDescr();
                    String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
                    Logger.e("aaa", "mNewsFeed===" + mNewsFeed.toString());
                    mSharePopupWindow.setOnFavoritListener(listener);
                    mSharePopupWindow.setTitleAndUrl(mNewsFeed, remark);
                    mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                }
                MobclickAgent.onEvent(this, "qidian_user_detail_share");
                break;
            case R.id.mNewsLoadingImg:
                loadData();
                break;
            case R.id.mDetailFavorite:
                User user = SharedPreManager.getUser(NewsDetailVideoAty.this);
                if (user != null && user.isVisitor()) {
                    Intent loginAty = new Intent(NewsDetailVideoAty.this, LoginAty.class);
                    startActivityForResult(loginAty, REQUEST_CODE);
                } else {
                    Logger.e("bbb", "收藏触发的点击事件！！！！！");
                    loadOperate();
                }
                MobclickAgent.onEvent(this, "qidian_user_detail_favorite");
                break;
        }
    }

    OnFavoritListener listener = new OnFavoritListener() {
        @Override
        public void FavoritListener(boolean isFavoriteType) {
            isFavorite = isFavoriteType;
            loadOperate();
        }
    };

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
        isFavorite = SharedPreManager.myFavoriteisSame(mUrl);
        if (isFavorite) {
            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
        } else {
            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
        }
    }

    private void configViewPagerViews() {
        mDetailHeader.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailBottomBanner.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailAddComment.setBackgroundResource(R.drawable.user_add_comment_black);
        int padding = DensityUtil.dip2px(this, 8);
//        mDetailLeftBack.setImageResource(R.drawable.btn_detail_left_white);
        mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment_white);
        mDetailShare.setImageResource(R.drawable.btn_detail_share_white);
        mDetailView.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailAddComment.setPadding(padding, padding, padding, padding);
        for (int i = 0; i < mImages.size(); i++) {
            final ImageView imageView = new ImageView(this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            mImageViews.add(imageView);
            imageView.setImageURI(Uri.parse(mImages.get(i).get("img")));
        }
        final int margin = DensityUtil.dip2px(this, 12);
        mImageWallVPager.setPadding(0, 0, 0, 0);
        mImageWallVPager.setClipToPadding(false);
        mImageWallVPager.setPageMargin(margin);
        mImageWallVPager.setAdapter(new ImagePagerAdapter(mImageViews));
        mImageWallVPager.setOffscreenPageLimit(3);
        Logger.e("jigang", "sssss =" + mImageWallDesc.getLineHeight());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageWallDesc.getLayoutParams();
        params.height = (int) (mImageWallDesc.getLineHeight() * 4.5);
        mImageWallDesc.setMaxLines(4);
        mImageWallDesc.setLayoutParams(params);
//        mImageWallDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
//        mImages.get(1).put("note","随着越来越多的大众直播平台推出，网络主播尤其是在中国年轻人中颇具人气。杨希月就是数十万网络主播的一员，今年刚满20岁，四川某传媒学院在校生，从事网络主播两年时间，已是某平台上金牌签约主播，每月收入约10万元。杨希月在某平台上数万主播中目前排名前100名，每天受到20万粉丝拥护，一方面也得益于她的舞蹈功底，自己从小便喜欢上了舞蹈。");
        mImageWallDesc.setText(Html.fromHtml(1 + "<small>" + "/" + mImages.size() + "</small>" + "&nbsp;&nbsp;&nbsp;" + mImages.get(0).get("note")));
        mImageWallVPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mImageWallDesc.setText(Html.fromHtml(position + 1 + "<small>" + "/" + mImages.size() + "</small>" + "&nbsp;&nbsp;&nbsp;" + mImages.get(position).get("note")));
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageWallDesc.getLayoutParams();
                params.height = (int) (mImageWallDesc.getLineHeight() * 4.5);
                mImageWallDesc.setMaxLines(4);
                mImageWallDesc.setLayoutParams(params);
                Logger.e("jigang", "change =" + mImageWallDesc.getHeight());
            }
        });
        mImageWallDesc.setOnTouchListener(new View.OnTouchListener() {
            float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (defaultH == 0) {
                    defaultH = mImageWallDesc.getHeight();
                }
                Logger.e("jigang", "default =" + defaultH);
                int lineCount = mImageWallDesc.getLineCount();
                int maxHeight = mImageWallDesc.getLineHeight() * lineCount;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Logger.e("jigang", "---down");
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = event.getRawY() - startY;
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageWallDesc.getLayoutParams();
                        int height = mImageWallDesc.getHeight();
                        Logger.e("jigang", "height=" + height + ",maxHeight=" + maxHeight);
                        if (Math.abs(deltaY) > 1 && lineCount > 4) {
                            height -= deltaY;
                            if (deltaY > 0) {
                                if (height < defaultH) {
                                    height = defaultH;
                                }
                            } else {
                                if (height > maxHeight) {
                                    height = maxHeight + DensityUtil.dip2px(NewsDetailVideoAty.this, 6 * 2 + 4);
                                }
                            }
                            params.height = height;
                            mImageWallDesc.setMaxLines(Integer.MAX_VALUE);
                            mImageWallDesc.setLayoutParams(params);
                        }
                        Logger.e("jigang", event.getRawY() + "---move " + deltaY);
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.e("jigang", "---up");
                        break;
                }
                return true;
            }
        });
        final GestureDetector tapGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isDisplay) {
                    isDisplay = false;
                    ObjectAnimator.ofFloat(mDetailHeader, "alpha", 1.0f, 0).setDuration(200).start();
                    ObjectAnimator.ofFloat(mImageWallDesc, "alpha", 1.0f, 0).setDuration(200).start();
                    ObjectAnimator.ofFloat(mDetailBottomBanner, "alpha", 1.0f, 0).setDuration(200).start();
                } else {
                    isDisplay = true;
                    ObjectAnimator.ofFloat(mDetailHeader, "alpha", 0, 1.0f).setDuration(200).start();
                    ObjectAnimator.ofFloat(mImageWallDesc, "alpha", 0, 1.0f).setDuration(200).start();
                    ObjectAnimator.ofFloat(mDetailBottomBanner, "alpha", 0, 1.0f).setDuration(200).start();
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

//    ShowCareforLayout mShowCareforLayout = new ShowCareforLayout() {
//        @Override
//        public void show(boolean type) {
//            isCareFor = type;
//            loadOperate(false);
//        }
//    };

    public void CareForAnimation() {

        //图片渐变模糊度始终
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
        //渐变时间
        alphaAnimation.setDuration(500);
        careforLayout.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (careforLayout.getVisibility() == View.GONE) {
                    careforLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AlphaAnimation alphaAnimationEnd = new AlphaAnimation(1.0f, 0f);
                        //渐变时间
                        alphaAnimationEnd.setDuration(500);
                        careforLayout.startAnimation(alphaAnimationEnd);
                        alphaAnimationEnd.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (careforLayout.getVisibility() == View.VISIBLE) {
                                    careforLayout.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 1000);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 梁帅：收藏上传接口(关心放到NewsDetailFgt)
     */
    public void loadOperate() {
        Logger.e("aaa", "222222222222222");
        if (!NetUtil.checkNetWork(NewsDetailVideoAty.this)) {
            ToastUtil.toastShort("无法连接到网络，请稍后再试");
            return;
        }


        JSONObject json = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(NewsDetailVideoAty.this);
        Logger.e("aaa", "type====" + (isFavorite ? Request.Method.DELETE : Request.Method.POST));
        Logger.e("aaa", "url===" + HttpConstant.URL_ADDORDELETE_FAVORITE + "nid=" + mUrl + "&uid=" + mUserId);


        DetailOperateRequest detailOperateRequest = new DetailOperateRequest((isFavorite ? Request.Method.DELETE : Request.Method.POST),
                HttpConstant.URL_ADDORDELETE_FAVORITE + "nid=" + mUrl + "&uid=" + mUserId,
                json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                carefor_Image.setImageResource(R.drawable.hook_image);
                if (isFavorite) {
                    isFavorite = false;
                    carefor_Text.setText("收藏已取消");
                    SharedPreManager.myFavoritRemoveItem(mUsedNewsFeed.getNid() + "");
                    mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
                } else {
                    isFavorite = true;
                    carefor_Text.setText("收藏成功");
                    Logger.e("aaa", "收藏成功数据：" + mNewsFeed.toString());
                    SharedPreManager.myFavoriteSaveList(mNewsFeed);
                    mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
                }
                CareForAnimation();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                carefor_Text.setText("收藏失败");
                CareForAnimation();
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(NewsDetailVideoAty.this).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        detailOperateRequest.setRequestHeader(header);
        detailOperateRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(detailOperateRequest);

//        NewsDetailRequest<String> request = new NewsDetailRequest<String>(Request.Method.POST, new TypeToken<String>() {
//        }.getType(), isType?HttpConstant.URL_ADDORDELETE_FAVORITE:HttpConstant.URL_ADDORDELETE_CAREFOR, new Response.Listener() {
//            @Override
//            public void onResponse(Object response) {
//                if(mDetailFavorite.getVisibility() == View.GONE){
//                    mDetailFavorite.setVisibility(View.VISIBLE);
//                }
//                if(isType){
//                    carefor_Image.setImageResource(R.drawable.hook_image);
//                    if(isFavorite){
//                        isFavorite = false;
//                        carefor_Text.setText("收藏已取消");
//                        SharedPreManager.myFavoritRemoveItem(mUsedNewsFeed.getUrl());
//                        mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//                    }else{
//                        isFavorite = true;
//                        carefor_Text.setText("收藏成功");
//                        SharedPreManager.myFavoriteSaveList(mUsedNewsFeed);
//                        mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//                    }
//                    CareForAnimation();
//                }else{
//                    if(isCareFor){
//                        carefor_Image.setImageResource(R.drawable.carefor_image);
//                        carefor_Text.setText("将推荐更多此类文章");
//                        CareForAnimation();
//                    }else{
//
//                    }
//
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if(mDetailFavorite.getVisibility() == View.VISIBLE){
//                    mDetailFavorite.setVisibility(View.GONE);
//                }
//                if(isType){
//                    carefor_Text.setText("收藏失败");
//                }else{
//                    carefor_Text.setText("关心失败");
//                }
//                CareForAnimation();
//            }
//        header.put("Authorization", SharedPreManager.getUser(NewsDetailAty2.this).getAuthorToken());
//        request.setRequestHeader(header);
//        });

//        HashMap<String, String> header = new HashMap<>();
//        HashMap<String, String> mParams = new HashMap<>();
//        mParams.put("nid", mUrl);
//        mParams.put("uid", mUserId);
//        request.setRequestParams(mParams);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Logger.e("aaa", "NewsDetailAty2 <Aty> requestCode==" + requestCode + ",resultCode==" + resultCode);
//        if(requestCode == NewsDetailFgt.REQUEST_CODE&&resultCode == LoginAty.REQUEST_CODE){
//            loadData();
//        }
//    }
}
