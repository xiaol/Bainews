package com.news.yazhidao.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.github.jinsedeyuzhou.utils.MediaNetUtils;
import com.github.jinsedeyuzhou.utils.ToolsUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailVideoFgtAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.BaseFragment;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.NewsDetailADRequestPost;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.AttentionDetailDialog;
import com.news.yazhidao.widget.SmallVideoContainer;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.VideoContainer;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;


/**
 * Created by fengjigang on 16/3/31.
 * 新闻详情页
 */
public class NewsDetailVideoFgt extends BaseFragment {
    private static final String TAG = "NewsDetailVideoFgt";
    public static final String KEY_DETAIL_RESULT = "key_detail_result";
    public static final String ACTION_REFRESH_DTC = "com.news.yazhidao.ACTION_REFRESH_DTC";
    //    private LoadWebView mDetailWebView;
    private NewsDetail mResult;
    private SharedPreferences mSharedPreferences;
    private PullToRefreshListView mNewsDetailList;
    private NewsDetailVideoFgtAdapter mAdapter;
    private User user;
    private RelativeLayout bgLayout;
    private String mDocid, mTitle, mPubName, mPubTime, mCommentCount, mNewID;
    private ArrayList<NewsDetailComment> mComments = new ArrayList<>();
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_ID = "key_news_id";
    public static final String KEY_NEWS_TITLE = "key_news_title";
    public static final int REQUEST_CODE = 1030;
    private LinearLayout detail_shared_FriendCircleLayout,
            detail_shared_CareForLayout,
            mCommentLayout,
            detail_attention_addView,
            linearlayout_attention,
            mNewsDetailHeaderView;

    private TextView detail_shared_PraiseText,
            detail_shared_Text,
            detail_shared_hotComment;
    private RelativeLayout detail_shared_ShareImageLayout, detail_shared_MoreComment,
            detail_shared_CommentTitleLayout,
            detail_shared_ViewPointTitleLayout, adLayout,
            relativeLayout_attention;
    private ImageView detail_shared_AttentionImage,
            image_attention_line,
            image_attention_success;
    private int CommentType = 0;
    private LayoutInflater inflater;
    ViewGroup container;
    private RefreshPageBroReceiber mRefreshReceiber;
    private RefreshLikeBroReceiber mRefreshLike;
    private boolean isWebSuccess, isCommentSuccess, isCorrelationSuccess;
    private TextView mDetailSharedHotComment;
    boolean isNoHaveBean;
    private final int LOAD_MORE = 0;
    private final int LOAD_BOTTOM = 1;
    private static final int VIDEO_SMALL = 2;
    private static final int VIDEO_FULLSCREEN = 3;
    private static final int VIDEO_NORMAL = 5;

    private boolean isLike;
    private NewsDetailCommentDao mNewsDetailCommentDao;
    private TextView footView_tv;
    private ProgressBar footView_progressbar;
    private LinearLayout footerView_layout;
    private boolean isBottom;
    private boolean isLoadDate;
    private boolean isNetWork;
    private AttentionDetailDialog attentionDetailDialog;
    private ImageView iv_attention_icon;
    private TextViewExtend tv_attention_title;
    private Context mContext;
    private VPlayPlayer vp;
    private VideoContainer mDetailVideo;
    private VideoContainer mFullScreen;
    private SmallVideoContainer mSmallScreen;
    private RelativeLayout mSmallLayout;
    private ImageView mClose;
    private RelativeLayout mVideoShowBg;
    private int mScreenWidth;
    private ImageView mDetailBg;
    private TextView mDetailVideoTitle;
    private TextView mDetailLeftBack;
    private RelativeLayout mDetailWrapper;
    private int position;
    private RequestManager mRequestManager;
    private RelativeLayout mDetailContainer;
    //    private PowerManager.WakeLock mWakeLock;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.onEvent(getActivity(), "qidian_user_enter_detail_page");
        mContext = getActivity();
//        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
//        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        mRequestManager = Glide.with(this);
        Bundle arguments = getArguments();
        mDocid = arguments.getString(KEY_NEWS_DOCID);
        mNewID = arguments.getString(KEY_NEWS_ID);
        mTitle = arguments.getString(KEY_NEWS_TITLE);
        position = arguments.getInt("position", 0);
        Logger.e("aaa", "mTitle==" + mTitle);


        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mResult = (NewsDetail) arguments.getSerializable(KEY_DETAIL_RESULT);
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);

        if (mRefreshReceiber == null) {
            mRefreshReceiber = new RefreshPageBroReceiber();
            IntentFilter filter = new IntentFilter(NewsDetailVideoAty.ACTION_REFRESH_COMMENT);
            filter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
            mContext.registerReceiver(mRefreshReceiber, filter);
        }
        if (mRefreshLike == null) {
            mRefreshLike = new RefreshLikeBroReceiber();
            IntentFilter filter = new IntentFilter(VideoCommentFgt.ACTION_REFRESH_CTD);
            mContext.registerReceiver(mRefreshLike, filter);
        }


    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_news_detail_video_listview, null);
        this.inflater = inflater;
        this.container = container;
        user = SharedPreManager.getUser(mContext);
        mNewsDetailList = (PullToRefreshListView) rootView.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mDetailContainer = (RelativeLayout) getActivity().findViewById(R.id.rl_detail_container);
        bgLayout.setVisibility(View.GONE);
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        mNewsDetailList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isLoadDate) {
                    return;
                }
                isLoadDate = true;
                if (MAXPage > viewpointPage) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            beanList.addAll(beanPageList.get(viewpointPage));
                            viewpointPage++;
                            mAdapter.setNewsFeed(beanList);
                            mAdapter.notifyDataSetChanged();
                            mNewsDetailList.onRefreshComplete();
                            if (MAXPage <= viewpointPage) {
                                mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
                                footView_tv.setText("内容加载完毕");
                            }
                            isLoadDate = false;
                        }
                    }, 1000);
                }
            }
        });
        mNewsDetailList.setOnStateListener(new PullToRefreshBase.onStateListener() {
            @Override
            public void getState(PullToRefreshBase.State mState) {
                if (!isBottom) {
                    return;
                }
                boolean isVisisyProgressBar = false;
                switch (mState) {
                    case RESET://初始
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case PULL_TO_REFRESH://更多推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case RELEASE_TO_REFRESH://松开推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("松手获取更多文章");
                        break;
                    case REFRESHING:
                    case MANUAL_REFRESHING://推荐中
                        isVisisyProgressBar = true;
                        footView_tv.setText("正在获取更多文章...");
                        break;
                    case OVERSCROLLING:
                        // NO-OP
                        break;
                }
                if (isVisisyProgressBar) {
                    footView_progressbar.setVisibility(View.VISIBLE);
                } else {
                    footView_progressbar.setVisibility(View.GONE);
                }
                mNewsDetailList.setFooterViewInvisible();
            }
        });

        mNewsDetailList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            Logger.e("aaa", "滑动到底部");
                            isBottom = true;


                        } else {
                            isBottom = false;
                            Logger.e("aaa", "在33333isBottom ==" + isBottom);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
//                if (beanList.size() == 0) {
//
//                    return;
//                }
//                int lastPositon =  absListView.getLastVisiblePosition();
//                Logger.e("aaa", "lastPositon====" + lastPositon);
//                Message msg = new Message();
//                if(lastPositon -2 ==beanList.size()-1){
//                    if (MAXPage > viewpointPage) {
//                        if(oldLastPositon == lastPositon){
//                            return;
//                        }
//                        msg.what = LOAD_MORE;
//                        mHandler.sendMessage(msg);
//                    }else{
//                        msg.what = LOAD_BOTTOM;
//                        mHandler.sendMessage(msg);
//
//                    }
//                }
//                oldLastPositon = lastPositon;
            }
        });


//        vp = PlayerManager.getPlayerManager().initialize(mContext);
        vp = mNewsDetailVideoAty.vPlayPlayer;
        mAdapter = new NewsDetailVideoFgtAdapter((Activity) mContext);
        mNewsDetailList.setAdapter(mAdapter);

        addHeadView(inflater, container);
        loadData();
        loadADData();
        //视频
        mDetailVideo = (VideoContainer) rootView.findViewById(R.id.fgt_new_detail_video);
        mVideoShowBg = (RelativeLayout) rootView.findViewById(R.id.detial_video_show);
        mVideoShowBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp != null && !vp.getAllowModible() && MediaNetUtils.getNetworkType(mContext) == 6) {
                    showWifiDialog();
                } else if (MediaNetUtils.getNetworkType(mContext) == 3 || vp.getAllowModible() && MediaNetUtils.getNetworkType(mContext) == 6) {

                    mVideoShowBg.setVisibility(View.GONE);
                    mDetailVideo.setVisibility(View.VISIBLE);
                    if (vp.getParent() != null)
                        ((ViewGroup) vp.getParent()).removeAllViews();
                    vp.setTitle(mResult.getTitle());
                    vp.play(mResult.getVideourl(), position);
                    mDetailVideo.addView(vp);
                }
            }
        });


        mDetailBg = (ImageView) rootView.findViewById(R.id.detail_image_bg);
        mFullScreen = (VideoContainer) getActivity().findViewById(R.id.detail_full_screen);
        mSmallScreen = (SmallVideoContainer) getActivity().findViewById(R.id.detail_small_screen);
        mSmallLayout = (RelativeLayout) getActivity().findViewById(R.id.detai_small_layout);
        mDetailWrapper = (RelativeLayout) getActivity().findViewById(R.id.mDetailWrapper);
        mDetailLeftBack = (TextView) getActivity().findViewById(R.id.mDetailLeftBack);


        mSmallLayout.setClickable(true);
        mSmallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NewsDetailVideoAty mActivity = (NewsDetailVideoAty) mContext;
                if (mActivity.isCommentPage) {
                    mActivity.isCommentPage = false;
                    mActivity.mNewsDetailViewPager.setCurrentItem(0);

                }
            }
        });
        mClose = (ImageView) getActivity().findViewById(R.id.detial_video_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vp != null && vp.isPlay()) {
                    vp.stop();
                    vp.release();
                    mSmallScreen.removeAllViews();
                    mSmallLayout.setVisibility(View.GONE);
                    mVideoShowBg.setVisibility(View.VISIBLE);
                }
            }
        });
        //视频新增
        int widths = mScreenWidth - DensityUtil.dip2px(mContext, 0);
        RelativeLayout.LayoutParams lpVideo = (RelativeLayout.LayoutParams) mDetailBg.getLayoutParams();
        lpVideo.width = widths;
        lpVideo.height = (int) (widths * 185 / 330.0f);
        mDetailBg.setLayoutParams(lpVideo);
        setIsShowImagesSimpleDraweeViewURI(mDetailBg, mResult.getThumbnail());


        if (vp.getParent() != null)
            ((ViewGroup) vp.getParent()).removeAllViews();
//        vp.setTitle(mResult.getTitle());
//        vp.start(mResult.getVideourl());


        if (MediaNetUtils.getNetworkType(mContext) == 3) {
            mVideoShowBg.setVisibility(View.GONE);
            vp.setTitle(mResult.getTitle());
            vp.play(mResult.getVideourl(), position);
            mDetailVideo.addView(vp);
        }

        vp.setCompletionListener(new VPlayPlayer.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (mSmallLayout.getVisibility() == View.VISIBLE) {
                    mSmallScreen.removeAllViews();
                    mSmallLayout.setVisibility(View.GONE);
                } else if (mFullScreen.getVisibility() == View.VISIBLE) {
                    mFullScreen.removeAllViews();
                    mFullScreen.setVisibility(View.GONE);
                } else if (mDetailVideo.getVisibility() == View.VISIBLE) {
                    mDetailVideo.removeAllViews();
                    mDetailVideo.setVisibility(View.GONE);
                }
                vp.stop();
                vp.release();

                mVideoShowBg.setVisibility(View.VISIBLE);

            }
        });
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                return vp.onKeyDown(keyCode, event);
            }
        });


        return rootView;
    }


    public void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getResources().getString(com.github.jinsedeyuzhou.R.string.tips_not_wifi));
        builder.setPositiveButton(mContext.getResources().getString(com.github.jinsedeyuzhou.R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (vp != null)
                    vp.setAllowModible(true);
                mVideoShowBg.setVisibility(View.GONE);
                mDetailVideo.setVisibility(View.VISIBLE);
                if (vp.getParent() != null)
                    ((ViewGroup) vp.getParent()).removeAllViews();
                vp.setTitle(mResult.getTitle());
                vp.play(mResult.getVideourl(), 0);
                mDetailVideo.addView(vp);

//                startPlayLogic();
//                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(com.github.jinsedeyuzhou.R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_MORE:
                    beanList.addAll(beanPageList.get(viewpointPage));
                    viewpointPage++;
                    mAdapter.setNewsFeed(beanList);
                    mAdapter.notifyDataSetChanged();
                    mNewsDetailList.onRefreshComplete();
                    break;
                case LOAD_BOTTOM:
                    if (isNoHaveBean) {
                        return;
                    }
                    isNoHaveBean = true;
                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                    ListView lv = mNewsDetailList.getRefreshableView();
                    LinearLayout mNewsDetailFootView = (LinearLayout) inflater.inflate(R.layout.detail_footview_layout, container, false);
                    mNewsDetailFootView.setLayoutParams(layoutParams);
                    lv.addFooterView(mNewsDetailFootView);
                    break;

                case VIDEO_SMALL:
                    if (vp == null)
                        return;
                    int currentItem = (int) msg.obj;
                    if (currentItem == 0) {
                        if (vp.isPlay() || (vp.isPlay() || vp.getStatus() == PlayStateParams.STATE_PREPARE)) {
                            if (vp.getParent() != null)
                                ((ViewGroup) vp.getParent()).removeAllViews();
                            mDetailVideo.addView(vp);
                            vp.setShowContoller(true);
                            mSmallScreen.removeAllViews();
                            mSmallLayout.setVisibility(View.GONE);

                        }
//                        else if (vp.getStatus()== PlayStateParams.STATE_PAUSED)
//                        {
//                            mSmallLayout.setVisibility(View.GONE);
//                        }

                        else {
                            if (vp.getParent() != null)
                                ((ViewGroup) vp.getParent()).removeAllViews();
                            vp.stop();
                            vp.release();
                            mVideoShowBg.setVisibility(View.VISIBLE);
                        }

                    } else if (currentItem == 1 && (vp.isPlay() || vp.getStatus() == PlayStateParams.STATE_PREPARE)) {
                        if (vp.getParent() != null)
                            ((ViewGroup) vp.getParent()).removeAllViews();
                        mSmallScreen.addView(vp);
                        vp.setShowContoller(false);
                        mSmallLayout.setVisibility(View.VISIBLE);
                        mDetailVideo.removeAllViews();
                    }
                    break;
                case VIDEO_FULLSCREEN:
//                    if (vp.isPlay()) {
//                        Configuration config = (Configuration) msg.obj;
//                        onConfigurationChanged(config);
//
////                        NewsDetailVideoAty mActivity = (NewsDetailVideoAty) mContext;
//
//                    }
                    break;
                case VIDEO_NORMAL:
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "onConfigurationChanged");
        if (vp != null) {
            vp.onChanged(newConfig);
            if (vp.getParent() != null)
                ((ViewGroup) vp.getParent()).removeAllViews();
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                mDetailContainer.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.GONE);
                mFullScreen.removeAllViews();
                mDetailVideo.addView(vp);
                mDetailVideo.setVisibility(View.VISIBLE);

            } else {
                mDetailContainer.setVisibility(View.GONE);
                mDetailVideo.removeAllViews();
                mDetailVideo.setVisibility(View.GONE);
                mFullScreen.addView(vp);
                mFullScreen.setVisibility(View.VISIBLE);


            }
        } else
            mDetailContainer.setVisibility(View.VISIBLE);

    }

    public void setIsShowImagesSimpleDraweeViewURI(ImageView draweeView, String strImg) {

        if (!TextUtil.isEmptyString(strImg)) {
            if (SharedPreManager.getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)) {
                draweeView.setImageResource(R.drawable.bg_load_default_small);
//                imageView.setImageURI(Uri.parse("res://com.news.yazhidao/" + R.drawable.bg_load_default_small));
//                Glide.with(mContext).load(R.drawable.bg_load_default_small).into(imageView);
            } else {
                Uri uri = Uri.parse(strImg);
                mRequestManager.load(uri).placeholder(R.drawable.bg_load_default_small).into(draweeView);
            }
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mRefreshReceiber != null) {
            mContext.unregisterReceiver(mRefreshReceiber);
        }
        if (mRefreshLike != null) {
            mContext.unregisterReceiver(mRefreshLike);
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop" + mDetailLeftBack.isShown() + ",visible" + mDetailLeftBack.getVisibility());

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause" + mDetailLeftBack.isShown() + ",visible" + mDetailLeftBack.getVisibility());
        if (vp != null) {
            vp.onPause();
            ToolsUtils.muteAudioFocus(mContext, true);
        }
//        if (mWakeLock != null)
//            mWakeLock.release();

//        mDetailWebView.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (vp != null) {
//            vp.onResume();
            ToolsUtils.muteAudioFocus(mContext, false);
        }
//        if (mWakeLock != null)
//            mWakeLock.acquire();

//        mDetailWebView.onResume();
    }


    boolean isAttention;

    public void addHeadView(LayoutInflater inflater, ViewGroup container) {
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        ListView lv = mNewsDetailList.getRefreshableView();
        //第1部分的WebView
        mNewsDetailHeaderView = (LinearLayout) inflater.inflate(R.layout.fgt_news_detail, container, false);
        mNewsDetailHeaderView.setLayoutParams(layoutParams);
        lv.addHeaderView(mNewsDetailHeaderView);
        //第2部分的CommentTitle
        final View mCommentTitleView = inflater.inflate(R.layout.detail_shared_layout, container, false);
        mCommentTitleView.setLayoutParams(layoutParams);
//        mNewsDetailHeaderView.addView(mCommentTitleView);
        detail_shared_FriendCircleLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_FriendCircleLayout);
        detail_shared_CareForLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_PraiseLayout);
        detail_attention_addView = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_attention_addView);
        mDetailSharedHotComment = (TextView) mCommentTitleView.findViewById(R.id.detail_shared_hotComment);
        mDetailVideoTitle = (TextView) mCommentTitleView.findViewById(R.id.detail_video_title);
        mDetailVideoTitle.setText(mResult.getTitle());
        adLayout = (RelativeLayout) mCommentTitleView.findViewById(R.id.adLayout);
        detail_shared_PraiseText = (TextView) mCommentTitleView.findViewById(R.id.detail_shared_PraiseText);
        detail_shared_AttentionImage = (ImageView) mCommentTitleView.findViewById(R.id.detail_shared_AttentionImage);
        if (mResult.getConflag() == 1) {
            isLike = true;
            detail_shared_AttentionImage.setImageResource(R.drawable.bg_attention);
        } else {
            isLike = false;
            detail_shared_AttentionImage.setImageResource(R.drawable.bg_normal_attention);
        }
        detail_shared_FriendCircleLayout.setVisibility(View.GONE);
        mCommentTitleView.findViewById(R.id.detail_shared_Text).setVisibility(View.GONE);
        mCommentLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_Layout);
        detail_shared_CommentTitleLayout = (RelativeLayout) mCommentTitleView.findViewById(R.id.detail_shared_TitleLayout);


        //添加关注源
        //源的关注
        RelativeLayout attention_item = (RelativeLayout) inflater.inflate(R.layout.detail_attention_item, container, false);

        linearlayout_attention = (LinearLayout) attention_item.findViewById(R.id.linearlayout_attention);
        image_attention_line = (ImageView) attention_item.findViewById(R.id.image_attention_line);
        image_attention_success = (ImageView) attention_item.findViewById(R.id.image_attention_success);
        relativeLayout_attention = (RelativeLayout) attention_item.findViewById(R.id.relativeLayout_attention);
        iv_attention_icon = (ImageView) attention_item.findViewById(R.id.iv_attention_icon);
        tv_attention_title = (TextViewExtend) attention_item.findViewById(R.id.tv_attention_title);
        detail_attention_addView.addView(attention_item);

        String icon = mResult.getPurl();
        String name = mResult.getPname();
        if (!TextUtil.isEmptyString(icon)) {
            mRequestManager.load(Uri.parse(icon)).placeholder(R.drawable.detail_attention_placeholder).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(iv_attention_icon);
        } else {
            mRequestManager.load("").placeholder(R.drawable.detail_attention_placeholder).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(iv_attention_icon);
        }
        if (!TextUtil.isEmptyString(name)) {
            tv_attention_title.setText(name);
        }


        //        isAttention = true;
        isAttention = mResult.getConpubflag() == 1;
        if (isAttention) {
            image_attention_success.setVisibility(View.VISIBLE);
            image_attention_line.setVisibility(View.GONE);
            linearlayout_attention.setVisibility(View.GONE);

        } else {
            image_attention_success.setVisibility(View.GONE);
            image_attention_line.setVisibility(View.VISIBLE);
            linearlayout_attention.setVisibility(View.VISIBLE);
        }
        relativeLayout_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChannelItemDao channelItemDao = new ChannelItemDao(mContext);
                channelItemDao.setFocusOnline();
                Intent in = new Intent(mContext, AttentionActivity.class);
                in.putExtra(AttentionActivity.KEY_ATTENTION_HEADIMAGE, mResult.getPurl());
                in.putExtra(AttentionActivity.KEY_ATTENTION_TITLE, mResult.getPname());
                in.putExtra(AttentionActivity.KEY_ATTENTION_CONPUBFLAG, mResult.getConpubflag());
                startActivityForResult(in, 1234);
            }
        });
        linearlayout_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null && user.isVisitor()) {
                    Intent loginAty = new Intent(mContext, LoginAty.class);
                    startActivityForResult(loginAty, 1030);
                } else {
                    addordeleteAttention(true);
                }


            }
        });

//        detail_shared_FriendCircleLayout.getParent().requestDisallowInterceptTouchEvent(true);
//        detail_shared_FriendCircleLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Logger.e("aaa", "点击朋友圈");
//                ShareSdkHelper.ShareToPlatformByNewsDetail(mContext, WechatMoments.NAME, mTitle, mNewID, "1");
//                MobclickAgent.onEvent(mContext, "qidian_detail_middle_share_weixin");
//
//            }
//        });


        detail_shared_CareForLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetUtil.checkNetWork(mContext)) {
                    ToastUtil.toastShort("无法连接到网络，请稍后再试");
                    return;
                }
                MobclickAgent.onEvent(mContext, "qidian_detail_middle_like");

                if (user != null && user.isVisitor()) {
                    Intent loginAty = new Intent(mContext, LoginAty.class);
                    startActivityForResult(loginAty, REQUEST_CODE);
                } else {
                    Logger.e("aaa", "点击关心");
                    setCareForType();
                }

            }
        });
        //第3部分的CommentContent(这个内容是动态的获取数据后添加)

        //第4部分的viewPointContent
        final View mViewPointLayout = inflater.inflate(R.layout.detail_shared_layout, container, false);
        mViewPointLayout.setLayoutParams(layoutParams);
        //延时加载热点评论和相关观点
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNewsDetailHeaderView.addView(mCommentTitleView);
                mNewsDetailHeaderView.addView(mViewPointLayout);
            }
        }, 1500);
        mViewPointLayout.findViewById(R.id.detail_video_title).setVisibility(View.GONE);
        detail_shared_ShareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
        detail_shared_Text = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_Text);
        detail_shared_MoreComment = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_MoreComment);
        detail_shared_hotComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_hotComment);
        detail_shared_ViewPointTitleLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_TitleLayout);
        detail_shared_ShareImageLayout.setVisibility(View.GONE);
        detail_shared_Text.setVisibility(View.GONE);
        detail_shared_MoreComment.setVisibility(View.VISIBLE);
        detail_shared_MoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailVideoAty mActivity = (NewsDetailVideoAty) mContext;
                if (!mActivity.isCommentPage) {
                    mActivity.isCommentPage = true;

                    mActivity.mNewsDetailViewPager.setCurrentItem(1);
                    mActivity.mDetailCommentPic.setImageResource(R.drawable.btn_detail_switch_comment);
                    mActivity.mDetailCommentNum.setVisibility(View.GONE);
                }
            }
        });

        detail_shared_hotComment.setText("相关视频");

        //footView
        final LinearLayout footerView = (LinearLayout) inflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);
        footerView_layout = (LinearLayout) footerView.findViewById(R.id.footerView_layout);
        footerView_layout.setVisibility(View.GONE);

    }

    /**
     * 梁帅：关心方法
     */
    public void setCareForType() {
        Logger.e("aaa", "1111111111111111111111");
        if (!NetUtil.checkNetWork(mContext)) {
            ToastUtil.toastShort("无法连接到网络，请稍后再试");
            return;
        }
        if (isNetWork) {
            return;
        }
        isNetWork = true;

        JSONObject json = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        Logger.e("aaa", "type====" + (isLike ? Request.Method.DELETE : Request.Method.POST));
        int userID = SharedPreManager.getUser(mContext).getMuid();
        Logger.e("aaa", "url===" + HttpConstant.URL_ADDORDELETE_CAREFOR + mNewID + (userID != 0 ? "&uid=" + userID : ""));


        DetailOperateRequest detailOperateRequest = new DetailOperateRequest((isLike ? Request.Method.DELETE : Request.Method.POST),
                HttpConstant.URL_ADDORDELETE_CAREFOR + "nid=" + mNewID + (userID != 0 ? "&uid=" + userID : ""),

                json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (isLike) {
                    isLike = false;
//                    mShowCareforLayout.show(isLike);
                    detail_shared_AttentionImage.setImageResource(R.drawable.bg_normal_attention);
                    ToastUtil.toastLong("取消关心");
                } else {
                    isLike = true;
//                    mShowCareforLayout.show(isLike);
                    detail_shared_AttentionImage.setImageResource(R.drawable.bg_attention);
                    ToastUtil.toastLong("将推荐更多此类文章");
                }
                isNetWork = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.toastLong("关心失败");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        detailOperateRequest.setRequestHeader(header);
        detailOperateRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(detailOperateRequest);
    }

    private void loadData() {
        Logger.e("jigang", "fetch comments url=" + HttpConstant.URL_FETCH_HOTCOMMENTS + "did=" + TextUtil.getBase64(mDocid) + "&p=" + (1) + "&c=" + (20));
        final RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        NewsDetailRequest<ArrayList<NewsDetailComment>> feedRequest = null;
        NewsDetailRequest<ArrayList<RelatedItemEntity>> related = null;
        int userID = SharedPreManager.getUser(mContext).getMuid();
        feedRequest = new NewsDetailRequest<ArrayList<NewsDetailComment>>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
        }.getType(), HttpConstant.URL_FETCH_HOTCOMMENTS + "did=" + TextUtil.getBase64(mDocid) +
                (userID != 0 ? "&uid=" + userID : "") +
                "&p=" + (1) + "&c=" + (20)
                , new Response.Listener<ArrayList<NewsDetailComment>>() {

            @Override
            public void onResponse(ArrayList<NewsDetailComment> result) {
                isCommentSuccess = true;
                isBgLayoutSuccess();
                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "network success, comment" + result);

                if (!TextUtil.isListEmpty(result)) {
                    mComments = result;
                    for (int i = 0; i < mComments.size(); i++) {
                        if (i > 2) {
                            mComments.remove(i);
                        }
                    }
//                        mAdapter.setCommentList(mComments);
//                        mAdapter.notifyDataSetChanged();
                    Logger.d("aaa", "评论加载完毕！！！！！！");
                    //同步服务器上的评论数据到本地数据库
                    //  addCommentInfoToSql(mComments);
                    mDetailSharedHotComment.setText("热门评论");//
                    addCommentContent(result);
                } else {
                    detail_shared_CommentTitleLayout.setVisibility(View.GONE);
                    detail_shared_MoreComment.setVisibility(View.GONE);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isCommentSuccess = true;
                isBgLayoutSuccess();
                mNewsDetailList.onRefreshComplete();
                detail_shared_CommentTitleLayout.setVisibility(View.GONE);
                detail_shared_MoreComment.setVisibility(View.GONE);
                Logger.e("jigang", "URL_FETCH_HOTCOMMENTS  network fail");

            }
        });
        Logger.e("jigang", "URL_NEWS_RELATED=" + HttpConstant.URL_NEWS_RELATED + "nid=" + mNewID);
        related = new NewsDetailRequest<ArrayList<RelatedItemEntity>>(Request.Method.GET,
                new TypeToken<ArrayList<RelatedItemEntity>>() {
                }.getType(),
//                '改回去'+ mNewID
                HttpConstant.URL_NEWS_RELATED + "nid=" + mNewID,
                new Response.Listener<ArrayList<RelatedItemEntity>>() {
                    @Override
                    public void onResponse(ArrayList<RelatedItemEntity> relatedItemEntities) {

                        Logger.e("jigang", "URL_NEWS_RELATED  network success~~" + relatedItemEntities.toString());
                        isCorrelationSuccess = true;
                        isBgLayoutSuccess();
//                            ArrayList<RelatedItemEntity> relatedItemEntities = response.getSearchItems();
//                            Logger.e("jigang", "network success RelatedEntity~~" + response);

                        if (!TextUtil.isListEmpty(relatedItemEntities)) {
                            setBeanPageList(relatedItemEntities);
                        } else {
                            setNoRelatedDate();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isCorrelationSuccess = true;
                        isBgLayoutSuccess();
                        setNoRelatedDate();
                        Logger.e("jigang", "URL_NEWS_RELATED  network error~~");
                    }
                });


        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        related.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));

//        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", "Basic X29pZH5jeDYyMmNvKXhuNzU2NmVuMXNzJy5yaXg0aWphZWUpaTc0M2JjbG40M2l1NDZlYXE3MXcyYV94KDBwNA");
//        feedRequest.setRequestHeader(header);
//        HashMap<String, String> header1 = new HashMap<>();
//        header1.put("Authorization", "Basic X29pZH5jeDYyMmNvKXhuNzU2NmVuMXNzJy5yaXg0aWphZWUpaTc0M2JjbG40M2l1NDZlYXE3MXcyYV94KDBwNA");
//        related.setRequestHeader(header1);

        requestQueue.add(feedRequest);
        final NewsDetailRequest<ArrayList<RelatedItemEntity>> finalRelated = related;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestQueue.add(finalRelated);
            }
        }, 1000);


    }

    private NewsDetailVideoAty mNewsDetailVideoAty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mNewsDetailVideoAty = (NewsDetailVideoAty) activity;
        mNewsDetailVideoAty.setHandler(mHandler);
    }


    public void setNoRelatedDate() {
        RelatedItemEntity entity = new RelatedItemEntity();
        entity.setUrl("-1");
        ArrayList<RelatedItemEntity> relatedItemEntities = new ArrayList<RelatedItemEntity>();
//        if(relatedItemEntities == null||relatedItemEntities.size() == 0){
//            relatedItemEntities = new ArrayList<RelatedItemEntity>();
//        }
        mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
        relatedItemEntities.add(entity);
        mAdapter.setNewsFeed(relatedItemEntities);
        mAdapter.notifyDataSetChanged();
        detail_shared_ViewPointTitleLayout.setVisibility(View.GONE);
        if (footerView_layout.getVisibility() == View.VISIBLE) {
            footerView_layout.setVisibility(View.GONE);
        }
    }

    private void addCommentInfoToSql(ArrayList<NewsDetailComment> mComments) {
        if (TextUtil.isListEmpty(mComments)) {
            int commentNum = mComments.size();
            List<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
            String[] commentIds = new String[commentNum];
            for (int i = 0; i < commentNum; i++) {
                commentIds[i] = mComments.get(i).getComment_id();
            }
            mNewsDetailCommentDao = new NewsDetailCommentDao(mContext);
            newsDetailCommentItems = mNewsDetailCommentDao.qureyByIds(commentIds);
            if (newsDetailCommentItems == null || newsDetailCommentItems.size() == 0) {
                return;
            } else {
                for (NewsDetailComment ndc : newsDetailCommentItems) {
                    mNewsDetailCommentDao.update(ndc);
                }
            }
        }
    }

    ArrayList<ArrayList<RelatedItemEntity>> beanPageList = new ArrayList<ArrayList<RelatedItemEntity>>();
    ArrayList<RelatedItemEntity> beanList = new ArrayList<RelatedItemEntity>();
    int viewpointPage = 0;
    int pageSize = 6;
    int MAXPage;

    public void setBeanPageList(ArrayList<RelatedItemEntity> relatedItemEntities) {
        Logger.e("aaa", "time:================比较前=================");
        for (int i = 0; i < relatedItemEntities.size(); i++) {
            Logger.e("aaa", "time:===" + relatedItemEntities.get(i).getPtime());
        }
        Collections.sort(relatedItemEntities);
        Logger.e("aaa", "time:================比较====后=================");
        for (int i = 0; i < relatedItemEntities.size(); i++) {
            Logger.e("aaa", "time:===" + relatedItemEntities.get(i).getPtime());
        }
        int listSice = relatedItemEntities.size();
        int page = (listSice / pageSize) + (listSice % pageSize == 0 ? 0 : 1);
        MAXPage = page;
        int mYear = 0;
        for (int i = 0; i < page; i++) {
            ArrayList<RelatedItemEntity> listBean = new ArrayList<RelatedItemEntity>();
            for (int j = 0; j < pageSize; j++) {
                int itemPosition = j + i * pageSize;
                if (itemPosition + 1 > listSice) {
                    break;
                }
                Logger.e("aaa", "page:" + itemPosition);
                Calendar calendar = DateUtil.strToCalendarLong(relatedItemEntities.get(itemPosition).getPtime());

                int thisYear = calendar.get(Calendar.YEAR);//获取年份
                if (thisYear != mYear) {
                    mYear = thisYear;
                    relatedItemEntities.get(itemPosition).setYearFrist(true);
                }

                listBean.add(relatedItemEntities.get(itemPosition));
            }
            beanPageList.add(listBean);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                beanList.addAll(beanPageList.get(viewpointPage));
                viewpointPage++;
                mAdapter.setNewsFeed(beanList);
                mAdapter.notifyDataSetChanged();
                if (MAXPage <= viewpointPage) {
                    mNewsDetailList.setMode(PullToRefreshBase.Mode.DISABLED);
                    footView_tv.setText("内容加载完毕");
                } else {
                    if (mNewsDetailList.getMode() != PullToRefreshBase.Mode.PULL_FROM_END) {
                        mNewsDetailList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }
                }
                if (footerView_layout.getVisibility() == View.GONE) {
                    footerView_layout.setVisibility(View.VISIBLE);
                }
                detail_shared_ViewPointTitleLayout.setVisibility(View.VISIBLE);
            }
        }, 500);

    }

    private void addNewsLove(NewsDetailComment comment, final int position, final CommentHolder holder, final boolean isAdd) {
//        try {
//            String name = URLEncoder.encode(user.getUserName(), "utf-8");
//            String cid = URLEncoder.encode(comment.getId(), "utf-8");
//            user.setUserName(name);
//            comment.setId(cid);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Logger.e("jigang", "love url=" + HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName());
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//
//
//
//
//        NewsLoveRequest<String> loveRequest = new NewsLoveRequest<String>(Request.Method.POST, new TypeToken<String>() {
//        }.getType(), HttpConstant.URL_ADDORDELETE_LOVE_COMMENT , new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String result) {
//                //+ "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName()
//                mNewsDetailList.onRefreshComplete();
//                Logger.e("jigang", "network success, love" + result);
//                if (!TextUtil.isEmptyString(result)) {
//                    mComments.get(position).setPraise(true);
//                    mComments.get(position).setCommend(Integer.parseInt(result));
//                    holder.ivPraise.setImageResource(R.drawable.bg_praised);
//                    holder.tvPraiseCount.setText(result);
////                    viewList.get(position).invalidate();//刷新界面
////                    mAdapter.notifyDataSetChanged();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
//                Logger.e("jigang", "network fail");
//            }
//        });
//        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(getActivity()).getAuthorToken());
//        loveRequest.setRequestHeader(header);
//        HashMap<String, String> mParams = new HashMap<>();
//        mParams.put("cid", comment.getId());
//        mParams.put("uid", user.getUserId());
//        loveRequest.setRequestParams(mParams);
//
//        loveRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//        requestQueue.add(loveRequest);

        if (isNetWork) {
            return;
        }
        isNetWork = true;
//        String uid = null;
//        try {
//            String name = URLEncoder.encode(user.getUserName(), "utf-8");
//            String cid = URLEncoder.encode(comment.getId(), "utf-8");
//            uid =  URLEncoder.encode(user.getMuid()+"", "utf-8");
//            user.setUserName(name);
//            comment.setId(cid);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        Logger.e("jigang", "love url=" + HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + comment.getId());
        JSONObject json = new JSONObject();
//        try {
//            json.put("cid", comment.getId());
//            json.put("uid",uid);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Logger.e("aaa", "json+++++++++++++++++++++++" + json.toString());

        DetailOperateRequest request = new DetailOperateRequest(isAdd ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&cid=" + comment.getId()
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");


                mNewsDetailList.onRefreshComplete();
//                Logger.e("jigang", "network success, love" + result);
//                if (!TextUtil.isEmptyString(result)) {
//                    mComments.get(position).setPraise(true);
//                    mComments.get(position).setCommend(Integer.parseInt(result));
//                    holder.ivPraise.setImageResource(R.drawable.bg_praised);
//                    holder.tvPraiseCount.setText(result);
//                }
                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "network success, love" + data);
                if (!TextUtil.isEmptyString(data)) {
                    if (isAdd) {
                        mComments.get(position).setUpflag(1);
                        holder.ivPraise.setImageResource(R.drawable.bg_praised);
                    } else {
                        mComments.get(position).setUpflag(0);
                        holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
                    }
                    int commend = Integer.parseInt(data);
                    mComments.get(position).setCommend(commend);
                    holder.tvPraiseCount.setText(commend + "");
                    Intent intent = new Intent(ACTION_REFRESH_DTC);
                    intent.putExtra(VideoCommentFgt.LIKETYPE, isAdd);
                    intent.putExtra((VideoCommentFgt.LIKEBEAN), mComments.get(position));
                    mContext.sendBroadcast(intent);

                    isNetWork = false;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "network fail");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);


    }

    ArrayList<CommentHolder> holderList = new ArrayList<CommentHolder>();
    ArrayList<View> viewList = new ArrayList<View>();
    private View mCCView;

    public void addCommentContent(final ArrayList<NewsDetailComment> result) {
        int listSice = result.size();
        if (listSice == 0) {
            CommentType = 0;
            detail_shared_CommentTitleLayout.setVisibility(View.GONE);
            detail_shared_MoreComment.setVisibility(View.GONE);
//        }else if(listSice == 1){
//            ShowCommentBar();
//            mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
//            CommentHolder holder = new CommentHolder(mCCView);
//            holderList.add(holder);
//
//        }else if(listSice == 2){
//            ShowCommentBar();
//            for(int i = 0; i<listSice ;i++){
//                mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
//                CommentHolder holder = new CommentHolder(mCCView);
//                holderList.add(holder);
//            }
        } else {
            ShowCommentBar();
            for (int i = 0; i < listSice && i < 3; i++) {
                CommentType = i + 1;
                mCCView = inflater.inflate(R.layout.adapter_list_comment1, container, false);
                View mSelectCommentDivider = mCCView.findViewById(R.id.mSelectCommentDivider);
                CommentHolder holder = new CommentHolder(mCCView);

                int position = i;
                NewsDetailComment comment = result.get(i);

                UpdateCCView(holder, comment, position);
                holderList.add(holder);
                viewList.add(mCCView);
                if (i == 2) {
                    mSelectCommentDivider.setVisibility(View.GONE);
                }
                mCommentLayout.addView(mCCView);

            }
        }
    }


    public void ShowCommentBar() {
        if (detail_shared_CommentTitleLayout.getVisibility() == View.GONE) {
            detail_shared_CommentTitleLayout.setVisibility(View.VISIBLE);
        }
        Logger.e("aaa", "mComments.size() = " + mComments.size());
        if (mComments.size() > 3) {
            if (detail_shared_MoreComment.getVisibility() == View.GONE) {
                detail_shared_MoreComment.setVisibility(View.VISIBLE);
            }
        } else {
            if (detail_shared_MoreComment.getVisibility() == View.VISIBLE) {
                detail_shared_MoreComment.setVisibility(View.GONE);
            }
        }
    }


    class CommentHolder {
        ImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvPraiseCount;
        TextViewExtend tvTime;
        ImageView ivPraise;

        public CommentHolder(View convertView) {
            tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
            ivHeadIcon = (ImageView) convertView.findViewById(R.id.iv_user_icon);
            tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
            ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
            tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
            tvTime = (TextViewExtend) convertView.findViewById(R.id.tv_time);
        }
    }

    /**
     * 通知新闻详情页和评论fragment刷新评论
     */
    public class RefreshPageBroReceiber extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                Logger.e("aaa", "详情页===文字的改变！！！");
//                int size = intent.getIntExtra("textSize", CommonConstant.TEXT_SIZE_NORMAL);
//                mSharedPreferences.edit().putInt("textSize", size).commit();
//                mDetailWebView.loadDataWithBaseURL(null, TextUtil.genarateHTML(mResult, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL),
//                        SharedPreManager.getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)),
//                        "text/html", "utf-8", null);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                mDetailWebView.setLayoutParams(params);
                mAdapter.notifyDataSetChanged();
                CCViewNotifyDataSetChanged();

            } else {
//                Logger.e("jigang", "detailaty refresh br");
//                NewsDetailComment comment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
//                mComments.add(0, comment);
//                UpdateCCOneData();
            }

        }
    }

    /**
     * 点赞的广播
     */
    public class RefreshLikeBroReceiber extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {

            }
            Logger.e("aaa", "详情接收到！");
            NewsDetailComment bean = (NewsDetailComment) intent.getSerializableExtra(VideoCommentFgt.LIKEBEAN);
            boolean isAdd = intent.getBooleanExtra(VideoCommentFgt.LIKETYPE, false);
            boolean isLikeType = false;
            int size = mComments.size();
            for (int i = 0; i < mComments.size(); i++)
                if (mComments.get(i).getId().equals(bean.getId())) {
                    isLikeType = true;
                    mComments.set(i, bean);
                    CCViewNotifyDataSetChanged();
                }
            if (!isLikeType && isAdd && size < 3) {
                ShowCommentBar();
                mComments.add(bean);
                size = mComments.size();
                CommentType = size;
                mCCView = inflater.inflate(R.layout.adapter_list_comment1, container, false);
                View mSelectCommentDivider = mCCView.findViewById(R.id.mSelectCommentDivider);
                CommentHolder holder = new CommentHolder(mCCView);

//                NewsDetailComment comment = result.get(i);

                UpdateCCView(holder, bean, CommentType - 1);
                holderList.add(holder);
                viewList.add(mCCView);
                if (size == 2) {
                    mSelectCommentDivider.setVisibility(View.GONE);
                }
                mCommentLayout.addView(mCCView);

            }
        }
    }

    public void UpdateCCOneData() {
        if (CommentType == 3) {
            CCViewNotifyDataSetChanged();
            ShowCommentBar();
        } else {
            CommentType = CommentType + 1;
            mCCView = inflater.inflate(R.layout.adapter_list_comment1, container, false);
            CommentHolder holder = new CommentHolder(mCCView);
            holderList.add(holder);
            viewList.add(mCCView);
            CCViewNotifyDataSetChanged();
            mCommentLayout.addView(mCCView);
            ShowCommentBar();
        }

    }

    public void CCViewNotifyDataSetChanged() {
        for (int i = 0; i < CommentType; i++) {
            CommentHolder holder = holderList.get(i);
            NewsDetailComment newsDetailComment = mComments.get(i);
            UpdateCCView(holder, newsDetailComment, i);
        }
    }
//    private void setNewsTime(TextViewExtend tvTime, String updateTime) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date date = dateFormat.parse(updateTime);
//            long between = System.currentTimeMillis() - date.getTime();
//            if (between >= (24 * 3600000)) {
//                tvTime.setText(updateTime);
////                tvTime.setText("");
//            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
//                tvTime.setText(between / 3600000 + "小时前");
////                tvTime.setText("");
//            } else {
//                int time = (int) (between * 60 / 3600000);
//                if (time > 0)
//                    tvTime.setText(between * 60 / 3600000 + "分钟前");
//                else
//                    tvTime.setText(between * 60 * 60 / 3600000 + "秒前");
////                if (between / 3600000 / 60 == 0) {
////                    tvTime.setText("刚刚");
////                } else {
////                    tvTime.setText(between / 3600000 / 60 + "分钟前");
////                }
//            }
//        } catch (ParseException e) {
//            tvTime.setText(updateTime);
//            e.printStackTrace();
//        }
//
//    }

    public void UpdateCCView(final CommentHolder holder, final NewsDetailComment comment, final int position) {
        final User user = SharedPreManager.getUser(mContext);
        if (!TextUtil.isEmptyString(comment.getAvatar())) {
            Uri uri = Uri.parse(comment.getAvatar());
            mRequestManager.load(uri).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(holder.ivHeadIcon);
        } else {
            mRequestManager.load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(holder.ivHeadIcon);
        }
        holder.tvName.setText(comment.getUname());
        holder.tvPraiseCount.setText(comment.getCommend() + "");
        Logger.e("aaa", "LS=comment.getCtime()===" + comment.getCtime());
        holder.tvTime.setText("" + DateUtil.getTimes(DateUtil.dateStr2Long(comment.getCtime())));
//        setNewsTime(holder.tvTime, comment.getCtime());
        holder.tvContent.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        holder.tvContent.setText(comment.getContent());
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "点击内容");
            }
        });
        if (comment.getUpflag() == 0) {
            holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
        } else {
            holder.ivPraise.setImageResource(R.drawable.bg_praised);
        }
//        String commentUserid = comment.getUid();
//        if (commentUserid != null && commentUserid.length() != 0&&user != null) {
//
//            if ((user.getMuid()+"").equals(comment.getUid())) {
//                holder.ivPraise.setVisibility(View.GONE);
//            } else {
//                holder.ivPraise.setVisibility(View.VISIBLE);
//            }
//        }

        holder.ivPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null && user.isVisitor()) {
                    Intent loginAty = new Intent(mContext, LoginAty.class);
                    startActivityForResult(loginAty, REQUEST_CODE);
                } else {
                    if (comment.getUpflag() == 0) {
                        if ((user.getMuid() + "").equals(comment.getUid())) {
                            Toast.makeText(mContext, "不能给自己点赞。", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Logger.e("aaa", "点赞");
                        addNewsLove(comment, position, holder, true);
                    } else {
                        Logger.e("aaa", "取消点赞");
                        addNewsLove(comment, position, holder, false);
                    }
                }

            }
        });
    }

    public void isBgLayoutSuccess() {
        if (isCommentSuccess && isWebSuccess && isCorrelationSuccess && bgLayout.getVisibility() == View.VISIBLE) {
            bgLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vp != null) {
            vp.onDestory();
        }
        vp = null;
        /**2016年8月31日 冯纪纲 解决webview内存泄露的问题*/
//        if (mNewsDetailHeaderView != null && mDetailWebView != null) {
//            ((ViewGroup) mDetailWebView.getParent()).removeView(mDetailWebView);
//        }
//        mDetailWebView.removeAllViews();
//        mDetailWebView.destroy();
//        mDetailWebView = null;
    }

//    public interface ShowCareforLayout {
//        void show(boolean isCareFor);
//    }
//
//    ShowCareforLayout mShowCareforLayout;
//
//    public void setShowCareforLayout(ShowCareforLayout showCareforLayout) {
//        mShowCareforLayout = showCareforLayout;
//    }

    public void addordeleteAttention(boolean isAttention) {
        if (isNetWork) {
            return;
        }
        String pname = null;

        try {
            pname = URLEncoder.encode(mResult.getPname(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (user != null && user.isVisitor()) {
            user = SharedPreManager.getUser(mContext);
        }
        isNetWork = true;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        Logger.e("jigang", "attention url=" + HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&pname=" + pname);
        JSONObject json = new JSONObject();
        Logger.e("aaa", "json+++++++++++++++++++++++" + json.toString());

        DetailOperateRequest request = new DetailOperateRequest(isAttention ? Request.Method.POST : Request.Method.DELETE,
                HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + user.getMuid() + "&pname=" + pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("aaa", "json+++++++++++++++++++++++" + data);

                //保存关注信息
                SharedPreManager.addAttention(mResult.getPname());

                if (SharedPreManager.getBoolean(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID)) {
                    ToastUtil.showAttentionSuccessToast(mContext);
                } else {
                    attentionDetailDialog = new AttentionDetailDialog(mContext, mResult.getPname());
                    attentionDetailDialog.show();
                    SharedPreManager.save(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID, true);
                }

                image_attention_success.setVisibility(View.VISIBLE);
                image_attention_line.setVisibility(View.GONE);
                linearlayout_attention.setVisibility(View.GONE);
                mResult.setConpubflag(1);
                isNetWork = false;

//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
                if (error.getMessage().indexOf("2003") != -1) {
                    ToastUtil.toastShort("用户已关注该信息！");
                    image_attention_success.setVisibility(View.VISIBLE);
                    image_attention_line.setVisibility(View.GONE);
                    linearlayout_attention.setVisibility(View.GONE);
                    mResult.setConpubflag(1);
                    return;
                }
                Logger.e("jigang", "network fail");
                ToastUtil.toastShort("关注失败！");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("aaa", "NewsDetailFgt <Fgt> requestCode==" + requestCode + ",resultCode==" + resultCode);
        user = SharedPreManager.getUser(mContext);
        if (requestCode == 1234 && resultCode == 1234) {
            isAttention = data.getBooleanExtra(AttentionActivity.KEY_ATTENTION_CONPUBFLAG, false);
            if (isAttention) {
                image_attention_success.setVisibility(View.VISIBLE);
                image_attention_line.setVisibility(View.GONE);
                linearlayout_attention.setVisibility(View.GONE);
                mResult.setConpubflag(1);

            } else {
                image_attention_success.setVisibility(View.GONE);
                image_attention_line.setVisibility(View.VISIBLE);
                linearlayout_attention.setVisibility(View.VISIBLE);
                mResult.setConpubflag(0);
            }
        } else if (requestCode == 1030 && resultCode == 1006) {
            if (mContext instanceof NewsDetailVideoAty) {
                addordeleteAttention(true);
//                mNewsDetailList.smoothScrollToPosition(pos);
            }
        }
    }

    private void loadADData() {
        if (SharedPreManager.getUser(mContext) != null) {
            String requestUrl = HttpConstant.URL_NEWS_DETAIL_AD;
            ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
            adLoadNewsFeedEntity.setUid(SharedPreManager.getUser(mContext).getMuid());
            Gson gson = new Gson();
            //加入详情页广告位id
            adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, "237")));
            RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
            Logger.e("aaa", "gson==" + gson.toJson(adLoadNewsFeedEntity));
            Logger.e("ccc", "requestBody==" + gson.toJson(adLoadNewsFeedEntity));
            NewsDetailADRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsDetailADRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
                @Override
                public void onResponse(final ArrayList<NewsFeed> result) {
                    adLayout.setVisibility(View.VISIBLE);
                    final NewsFeed newsFeed = result.get(0);
                    if (newsFeed != null) {
                        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.ll_ad_item_big, null);
                        TextViewExtend title = (TextViewExtend) layout.findViewById(R.id.title_textView);
                        title.setText(newsFeed.getTitle());
                        final ImageView imageView = (ImageView) layout.findViewById(R.id.adImage);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                        int imageWidth = mScreenWidth - DensityUtil.dip2px(mContext, 56);
                        layoutParams.width = imageWidth;
                        layoutParams.height = (int) (imageWidth * 627 / 1200.0f);
                        imageView.setLayoutParams(layoutParams);
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                mRequestManager.load(result.get(0).getImgs().get(0)).into(imageView);
                            }
                        });
                        adLayout.addView(layout);
                        adLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent AdIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                                AdIntent.putExtra("key_url", newsFeed.getPurl());
                                mContext.startActivity(AdIntent);
                            }
                        });
                        AdUtil.upLoadAd(result.get(0));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    adLayout.setVisibility(View.GONE);
                }
            });
            newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(newsFeedRequestPost);
        }
    }
}
