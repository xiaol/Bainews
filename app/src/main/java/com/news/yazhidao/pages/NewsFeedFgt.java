package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewNewsFeedAdapter;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.database.ReleaseSourceItemDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.FeedRequest;
import com.news.yazhidao.net.volley.NewsFeedRequestPost;
import com.news.yazhidao.receiver.HomeWatcher;
import com.news.yazhidao.receiver.HomeWatcher.OnHomePressedListener;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;
import com.news.yazhidao.widget.ChangeTextSizePopupWindow;
import com.news.yazhidao.widget.SmallVideoContainer;
import com.news.yazhidao.widget.VideoContainer;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.news.yazhidao.utils.manager.SharedPreManager.getUser;

public class NewsFeedFgt extends Fragment implements NativeAD.NativeAdListener {

    public static final String KEY_NEWS_CHANNEL = "key_news_channel";
    public static final String KEY_PUSH_NEWS = "key_push_news";//表示该新闻是后台推送过来的
    public static final String KEY_NEWS_IMG_URL = "key_news_img_url";//确保新闻详情中有一张图
    public static final String KEY_NEWS_TYPE = "key_news_type";//新闻类型,是否是大图新闻
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_NEWS_VIDEO = "key_news_video";
    public static final String KEY_NEWS_IMAGE = "key_news_image";
    public static final String KEY_SHOW_COMMENT = "key_show_comment";
    public static final String CURRENT_POSITION = "position";
    private static final String TAG = "NewsFeedFgt";

    /**
     * 当前fragment 所对应的新闻频道
     */
    public static String KEY_CHANNEL_ID = "key_channel_id";
    public static String KEY_WORD = "key_word";
    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static String KEY_COLLECTION = "key_collection";

    public static String KEY_TITLE = "key_title";
    public static String KEY_PUBNAME = "key_pubname";
    public static String KEY_PUBTIME = "key_pubtime";
    public static String KEY_COMMENTCOUNT = "key_commentcount";

    public static final int REQUEST_CODE = 1060;


    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    public static final int PULL_DOWN_REFRESH = 1;
    private static final int PULL_UP_REFRESH = 2;
    private NewNewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<>();
    private Context mContext;
    private PullToRefreshListView mlvNewsFeed;
    private View rootView;
    private String mstrDeviceId, mstrUserId, mstrChannelId, mstrKeyWord;
    private NewsFeedDao mNewsFeedDao;
    private ChangeTextSizePopupWindow mChangeTextSizePopWindow;
    private boolean mFlag;
    private SharedPreferences mSharedPreferences;
    private RefreshReceiver mRefreshReceiver;
    /**
     * 热词页面加载更多
     */
    private int mSearchPage = 1;
    private boolean mIsFirst = true;
    /**
     * 当前的fragment 是否已经加载过数据
     */
//    private boolean isLoadedData = false;
    private NewsSaveDataCallBack mNewsSaveCallBack;
    private View mHomeRelative;
    private View mHomeRetry;
    private RelativeLayout bgLayout, mSearch_layout;
    private boolean isListRefresh = false;
    private boolean isNewVisity = false;//当前页面是否显示
    private boolean isNeedAddSP = true;
    private Handler mHandler;
    private Runnable mThread;
    private boolean isClickHome;
    private TextView footView_tv, mRefreshTitleBar;
    private ProgressBar footView_progressbar;
    private boolean isBottom;

    private int thisVisibleItemCount, thisTotalItemCount;//判断footerView 不滑动

    //视频播放控制
    private VPlayPlayer vPlayer;
    private VideoContainer mFeedFullScreen;
    private SmallVideoContainer mFeedSmallScreen;
    private RelativeLayout mFeedSmallLayout;
    private ImageView mFeedClose;
    public int cPostion = -1;
    private int lastPostion = -1;
    public NewsFeed newsVideoFeed;
    private LinearLayout mChannelLayout;
    private ViewPager mViewPager;
    private RelativeLayout mContainer;
    private int currentPosition;
    private boolean isADRefresh = false;
    private NativeAD mNativeAD;
    private String mAppId, mNativePosID;
    private List<NativeADDataRef> mADs;
    public static final int AD_COUNT = 1;                        // 本示例中加载1条广告
    public static final int AD_POSITION = 1;                     // 插在ListView数据集的第2个位置
    private boolean isPullDown = false;


//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 100:
//                    if (vPlayer.isPlay()) {
//                        Configuration config = (Configuration) msg.obj;
//                        onConfigurationChanged(config);
//                    }
//                    break;
//            }
//        }
//    };

    public interface NewsSaveDataCallBack {
        void result(String channelId, ArrayList<NewsFeed> results);
    }

    public static NewsFeedFgt newInstance(String pChannelId) {
        NewsFeedFgt newsFeedFgt = new NewsFeedFgt();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CHANNEL_ID, pChannelId);
        newsFeedFgt.setArguments(bundle);
        return newsFeedFgt;
    }

    public void setNewsSaveDataCallBack(NewsSaveDataCallBack listener) {
        this.mNewsSaveCallBack = listener;
    }

    public void setChannelId(String strChannelId) {
        mstrChannelId = strChannelId;
    }

    boolean isNoteLoadDate;

    public void setNewsFeed(ArrayList<NewsFeed> results) {
        isNoteLoadDate = true;
        this.mArrNewsFeed = results;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {

                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    public void setListRefresh(boolean listRefresh) {
        isListRefresh = listRefresh;
    }

    //    public void setIsFocus() {
//        misFocus = true;
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isNewVisity = isVisibleToUser;
        Log.v(TAG, "setUserVisibleHint");
        if (vPlayer != null && !isVisibleToUser) {
            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                vPlayer.stop();
                vPlayer.release();
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
            } else {
                VideoVisibleControl();
            }
        }
        if (isNewVisity && isNeedAddSP) {//切换到别的页面加入他
//            addSP(mArrNewsFeed);//第一次进入主页的时候会加入一次，不用担心这次加入是没有数据的

            isNeedAddSP = false;
        }
        if (mHomeRetry != null && mHomeRetry.getVisibility() == View.VISIBLE) {
            loadData(PULL_DOWN_REFRESH);
        }
//        if (rootView != null && !isVisibleToUser) {
//            mlvNewsFeed.onRefreshComplete();
//            mHandler.removeCallbacks(mRunnable);
//        }
//        if (rootView != null && isVisibleToUser && isLoadedData) {
//            isLoadedData = false;
//            mHandler.postDelayed(mRunnable, 800);
//            Logger.e("jigang", "refresh " + mstrChannelId);
//            if (mArrNewsFeed == null || mIsFirst) {
//                mArrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
//                mAdapter.notifyDataSetChanged();
//                mIsFirst = false;
//            }
//        }

    }

    public void refreshData() {
        if (mlvNewsFeed != null) {
            mlvNewsFeed.setRefreshing();
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        mNewsFeedDao = new NewsFeedDao(mContext);
        mstrDeviceId = DeviceInfoUtil.getUUID();
        User user = getUser(mContext);
        if (user != null)
            mstrUserId = user.getUserId();
        else
            mstrUserId = "";
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        mFlag = mSharedPreferences.getBoolean("isshow", false);
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommonConstant.CHANGE_TEXT_ACTION);
        intentFilter.addAction(CommonConstant.NEWS_FEED_REFRESH);
        mContext.registerReceiver(mRefreshReceiver, intentFilter);
        initNativeVideoAD();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("jigang", "requestCode = " + requestCode + ",data=" + data);
        if (requestCode == NewsFeedAdapter.REQUEST_CODE && data != null) {
            int newsId = data.getIntExtra(NewsFeedAdapter.KEY_NEWS_ID, 0);
            Logger.e("jigang", "newsid = " + newsId);
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                for (NewsFeed item : mArrNewsFeed) {
                    if (item != null && newsId == item.getNid() && !item.isRead() && item.getRtype() != 4) {
                        item.setRead(true);
                        mNewsFeedDao.update(item);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }


        } else if (requestCode == LoginAty.REQUEST_CODE && data != null) {
            loadData(PULL_DOWN_REFRESH);
        } else if (requestCode == NewsFeedFgt.REQUEST_CODE && data != null) {
            currentPosition = data.getIntExtra(NewsFeedFgt.CURRENT_POSITION, 0);
//            vPlayer.
        }
    }

    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
            if (mstrChannelId.equals("44")) ;

        }
        rootView = LayoutInflater.inflate(R.layout.activity_news, container, false);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);
        mHomeRelative = rootView.findViewById(R.id.mHomeRelative);
        mRefreshTitleBar = (TextView) rootView.findViewById(R.id.mRefreshTitleBar);
        mHomeRetry = rootView.findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlvNewsFeed.setRefreshing();
                mHomeRetry.setVisibility(View.GONE);
            }
        });
        mlvNewsFeed = (PullToRefreshListView) rootView.findViewById(R.id.news_feed_listView);
        //====================视频===========================
        mFeedFullScreen = (VideoContainer) getActivity().findViewById(R.id.feed_full_screen);
        mFeedSmallScreen = (SmallVideoContainer) getActivity().findViewById(R.id.feed_small_screen);
        mFeedSmallLayout = (RelativeLayout) getActivity().findViewById(R.id.feed_small_layout);
        mFeedClose = (ImageView) getActivity().findViewById(R.id.feed_video_close);
        mChannelLayout = (LinearLayout) getActivity().findViewById(R.id.mChannelLayout);
        mViewPager = (ViewPager) getActivity().findViewById(R.id.mViewPager);
        mContainer = (RelativeLayout) getActivity().findViewById(R.id.main_container);

        //=====================视频添加
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.BOTH);
        mlvNewsFeed.setMainFooterView(true);
        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                isPullDown = true;
                loadData(PULL_DOWN_REFRESH);
                scrollAd();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                isPullDown = false;
                loadData(PULL_UP_REFRESH);
                scrollAd();
            }
        });
        addHFView(LayoutInflater);

        mAdapter = new NewNewsFeedAdapter(mContext, this, null);
        mAdapter.setClickShowPopWindow(mClickShowPopWindow);
        if (mstrChannelId != null && mstrChannelId.equals("1000")) {
            ReleaseSourceItemDao releaseSourceItemDao = new ReleaseSourceItemDao(mContext);
            String[] colorArr = mContext.getResources().getStringArray(R.array.bg_focus_colors);
            mAdapter.setReleaseSourceItems(releaseSourceItemDao, colorArr);
        }
        mlvNewsFeed.setAdapter(mAdapter);
        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.listview_empty_view, null));

        playVideoControl();
        setUserVisibleHint(getUserVisibleHint());
        //load news data
        mHandler = new Handler();
        mThread = new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                loadData(PULL_UP_REFRESH);
                isListRefresh = false;
            }
        };
        if (mstrChannelId != null && !mstrChannelId.equals("1000")) {
            mHandler.postDelayed(mThread, 1500);
        }
//        rootView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                return vPlayer.onKeyDown(keyCode, event);
//            }
//        });
//        rootView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                return vPlayer.onKeyDown(keyCode, event);
//            }
//        });

        return rootView;
    }

    private MainAty mainAty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainAty = (MainAty) activity;
        vPlayer = mainAty.vPlayPlayer;
    }

    //=================================   视频相关方法  ===============================
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "onConfigurationChanged");
        if (vPlayer != null) {
            vPlayer.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(TAG, "ORIENTATION_PORTRAIT");
                mFeedFullScreen.removeAllViews();
                mFeedFullScreen.setVisibility(View.GONE);
                mContainer.setVisibility(View.VISIBLE);
                int position = getPlayItemPosition();
                if (position != -1 && (vPlayer.getStatus() == PlayStateParams.STATE_PAUSED || vPlayer.isPlay())) {

                    FrameLayout playItemView = getPlayItemView(position);
                    View itemView = (View) playItemView.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
                    }
                    playItemView.removeAllViews();
                    playItemView.addView(vPlayer);
                    if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                        vPlayer.showBottomControl(false);
                }
//                else {

//                    mFeedSmallScreen.removeAllViews();
//                    mFeedFullScreen.addView(vPlayer);
//                    vPlayer.setShowContoller(false);
//                    mFeedSmallLayout.setVisibility(View.VISIBLE);
//                }
            } else {
                Log.d(TAG, "ORIENTATION_LANSCAPES");
                mContainer.setVisibility(View.GONE);
                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                    }
                }
                mFeedFullScreen.addView(vPlayer);
                if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                    vPlayer.showBottomControl(false);
                mFeedFullScreen.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.notifyDataSetChanged();
            if (mContainer.getVisibility() == View.GONE)
<<<<<<< HEAD
                mContainer.setVisibility(View.VISIBLE);
=======
                      mContainer.setVisibility(View.VISIBLE);
>>>>>>> c156b64a8f84180d38e3a8924165cb148b35326e
            if (mFeedFullScreen.getVisibility() == View.VISIBLE)
                mFeedFullScreen.setVisibility(View.GONE);
        }
    }

    /**
     * 视频播放控制
     */
    public void playVideoControl() {
        if (null == vPlayer) {
            vPlayer = new VPlayPlayer(getActivity());
<<<<<<< HEAD
            mainAty.vPlayPlayer = vPlayer;
=======
            mainAty.vPlayPlayer=vPlayer;

>>>>>>> c156b64a8f84180d38e3a8924165cb148b35326e
        }
        mAdapter.setOnPlayClickListener(new NewNewsFeedAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(RelativeLayout relativeLayout, NewsFeed feed) {
                relativeLayout.setVisibility(View.GONE);
                changePlayView(feed);
                View view = (View) relativeLayout.getParent();
                ViewGroup mItemVideo = (ViewGroup) view.findViewById(R.id.layout_item_video);
                mItemVideo.removeAllViews();
                vPlayer.setTitle(feed.getTitle());
                vPlayer.start(feed.getVideourl());
                mItemVideo.addView(vPlayer);
                lastPostion = cPostion;
            }

            @Override
            public void onItemClick(RelativeLayout rlNewsContent, NewsFeed feed) {
                if (feed == null)
                    return;
                changePlayView(feed);
                Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                intent.putExtra(NewsFeedFgt.CURRENT_POSITION, vPlayer.getCurrentPosition());
                if (isAdded())
                    startActivityForResult(intent, NewNewsFeedAdapter.REQUEST_CODE);
                else
                    mainAty.startActivityForResult(intent, NewNewsFeedAdapter.REQUEST_CODE);

                lastPostion = cPostion;
            }
        });


        mFeedClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vPlayer.isPlay()) {
                    vPlayer.stop();
                    vPlayer.release();
                    cPostion = -1;
                    lastPostion = -1;
                    mFeedSmallScreen.removeAllViews();
                    mFeedSmallLayout.setVisibility(View.GONE);
                }
            }
        });

        mFeedSmallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainAty.newsFeedVideo == null)
                    return;
                Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, mainAty.newsFeedVideo);
                intent.putExtra(NewsFeedFgt.CURRENT_POSITION, vPlayer.getCurrentPosition());
                if (isAdded())
                    startActivityForResult(intent, REQUEST_CODE);
                else
                    mainAty.startActivityForResult(intent, REQUEST_CODE);

                if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                    mFeedSmallLayout.setVisibility(View.GONE);
                    mFeedSmallScreen.removeAllViews();
                    vPlayer.setShowContoller(false);
                    vPlayer.stop();
                    vPlayer.release();
                }

            }
        });


        vPlayer.setCompletionListener(new VPlayPlayer.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                    mFeedSmallScreen.removeAllViews();
                    mFeedSmallLayout.setVisibility(View.GONE);
                    vPlayer.setShowContoller(false);
                } else if (mFeedFullScreen.getVisibility() == View.VISIBLE) {
                    mFeedFullScreen.removeAllViews();
                    mFeedFullScreen.setVisibility(View.GONE);
                    vPlayer.setShowContoller(false);
                } else
                    removeViews();
                if (vPlayer!=null) {
                    vPlayer.stop();
                    vPlayer.release();
                }
                lastPostion = -1;

            }
        });


    }

    public void changePlayView(final NewsFeed feed) {
        cPostion = feed.getNid();
        mainAty.newsFeedVideo = feed;
        if (cPostion != lastPostion && lastPostion != -1) {
            vPlayer.stop();
            vPlayer.release();
            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
                vPlayer.setShowContoller(false);
            } else {
                removeViews();
            }
        }
    }

    public int getPlayItemPosition() {
        ListView lv = mlvNewsFeed.getRefreshableView();
        for (int i = lv.getFirstVisiblePosition(); i <= lv.getLastVisiblePosition(); i++) {
            if (i == 0)
                continue;
            if (i > mArrNewsFeed.size())
                return -1;
            if (mArrNewsFeed.get(i - 1).getNid() == cPostion) {
                return (i - lv.getFirstVisiblePosition());
            }
        }
        return -1;
    }

    public FrameLayout getPlayItemView(int cPosition) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (cPosition != -1) {
            View item = lv.getChildAt(cPosition);
            return (FrameLayout) item.findViewById(R.id.layout_item_video);
        }

        return null;
    }

    public void removeViews() {
        ViewGroup frameLayout = (ViewGroup) vPlayer.getParent();
        if (frameLayout != null && frameLayout.getChildCount() > 0) {
            frameLayout.removeAllViews();
            View itemView = (View) frameLayout.getParent();
            if (itemView != null) {
                itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
            }
        }
    }


    private void VideoVisibleControl() {
        try {
            if (vPlayer == null)
                return;
            if (getPlayItemPosition() == -1) {
                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                if (frameLayout != null && frameLayout.getChildCount() > 0) {
                    vPlayer.stop();
                    vPlayer.release();
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        View videoSHow = itemView.findViewById(R.id.rl_video_show);
                        if (videoSHow != null) {
                            videoSHow.setVisibility(View.VISIBLE);
                        }

                    }

                }
            }

        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }


    private void VideoShowControl() {
        ListView lv = mlvNewsFeed.getRefreshableView();
        Log.e(TAG, "mlvNewsFeed: first" + lv.getFirstVisiblePosition() + ",last:" + lv.getLastVisiblePosition());
        boolean isExist = false;
        int position = -1;
        for (int i = lv.getFirstVisiblePosition(); i <= lv.getLastVisiblePosition(); i++) {
            if (i == 0)
                continue;
            if (i > mArrNewsFeed.size())
                break;
            if (mArrNewsFeed.get(i - 1).getNid() == cPostion) {
                isExist = true;
                position = i - lv.getFirstVisiblePosition();
                break;
            }
        }
        if (isExist) {
            View item = lv.getChildAt(position);
            Log.e(TAG, "item:" + item.toString() + "position:" + position);
            FrameLayout frameLayout = (FrameLayout) item.findViewById(R.id.layout_item_video);
            Log.e(TAG, "frameLayout:" + frameLayout.toString());

            if (vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PAUSED || vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PREPARE || vPlayer.getStatus() == PlayStateParams.STATE_PREPARING || vPlayer.getStatus() == PlayStateParams.STATE_PREPARED) {
                item.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
            }

            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
                vPlayer.setShowContoller(true);
                frameLayout.removeAllViews();
                frameLayout.addView(vPlayer);
            }

        } else {
            if (vPlayer != null && mFeedSmallLayout.getVisibility() == View.GONE) {
                FrameLayout frameLayout = (FrameLayout) vPlayer.getParent();
                if (frameLayout != null) {
                    if (vPlayer.getStatus() != PlayStateParams.STATE_PAUSED)
                        frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                    }
                }

                if (vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PREPARE || vPlayer.getStatus() == PlayStateParams.STATE_PREPARING || vPlayer.getStatus() == PlayStateParams.STATE_PREPARED) {
                    mFeedSmallScreen.removeAllViews();
                    mFeedSmallScreen.addView(vPlayer);
                    vPlayer.setShowContoller(false);
                    mFeedSmallLayout.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    NewNewsFeedAdapter.clickShowPopWindow mClickShowPopWindow = new NewNewsFeedAdapter.clickShowPopWindow() {
        @Override
        public void showPopWindow(int x, int y, NewsFeed feed) {
            String pName = feed.getPname();
            mNewsFeedFgtPopWindow.showPopWindow(x, y, pName != null ? pName : "未知来源", feed.getNid(), mAdapter);
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacks(mThread);
        }

        mContext.unregisterReceiver(mRefreshReceiver);
        Logger.e("jigang", "newsfeedfgt onDestroyView" + mstrChannelId);
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * 设置搜索热词
     *
     * @param pKeyWord
     */
    public void setSearchKeyWord(String pKeyWord) {
        mAdapter.setSearchKeyWord(pKeyWord);
        this.mstrKeyWord = pKeyWord;
        mArrNewsFeed = null;
        mSearchPage = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                loadData(PULL_DOWN_REFRESH);
                isListRefresh = false;
            }
        }, 1000);
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则进行刷新动画
     */
    public void startTopRefresh() {
        if (MainAty.class.equals(mContext.getClass())) {
            ((MainAty) mContext).startTopRefresh();
        }
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则停止刷新动画
     *
     * @return
     */
    public void stopRefresh() {
        if (MainAty.class.equals(mContext.getClass())) {
            ((MainAty) mContext).stopTopRefresh();
        }
    }

    private void loadAD() {
        if (mNativeAD != null && !isADRefresh) {
            mNativeAD.loadAD(AD_COUNT);
            isADRefresh = true;
        }
    }

    private void initNativeVideoAD() {
        mAppId = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.APPID);
        mNativePosID = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.NativePosID);
        if (!TextUtil.isEmptyString(mAppId)) {
            mNativeAD = new NativeAD(YaZhiDaoApplication.getInstance().getAppContext(), mAppId, mNativePosID, this);
        }
    }


    private void loadNewsFeedData(String url, final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl;
        String tstart = System.currentTimeMillis() + "";
        String fixedParams = "&cid=" + mstrChannelId + "&uid=" + getUser(mContext).getMuid();
        ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
        adLoadNewsFeedEntity.setCid(TextUtil.isEmptyString(mstrChannelId) ? null : Long.parseLong(mstrChannelId));
        adLoadNewsFeedEntity.setUid(getUser(mContext).getMuid());
        adLoadNewsFeedEntity.setT(1);
        adLoadNewsFeedEntity.setV(1);
        Gson gson = new Gson();
        //写入feed流广告位ID
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(AdUtil.getAdMessage(mContext, CommonConstant.NEWS_FEED_AD_ID)));

        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
                for (int i = 0; i < mArrNewsFeed.size(); i++) {
                    NewsFeed newsFeed = mArrNewsFeed.get(i);
                    if (newsFeed.getRtype() != 3 && newsFeed.getRtype() != 4) {
                        adLoadNewsFeedEntity.setNid(newsFeed.getNid());
                        break;
                    }
                }
                tstart = DateUtil.dateStr2Long(firstItem.getPtime()) + "";
            } else {
                tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
            }

//            requestUrl = HttpConstant.URL_FEED_PULL_DOWN + "tcr=" + tstart + fixedParams;
            adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
            /** 梁帅：判断是否是奇点频道 */
            requestUrl = HttpConstant.URL_FEED_AD_PULL_DOWN;

        } else {
            if (mFlag) {
                if (mIsFirst) {
                    ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
                    if (!TextUtil.isListEmpty(arrNewsFeed)) {
                        NewsFeed newsFeed = arrNewsFeed.get(0);
                        tstart = DateUtil.dateStr2Long(newsFeed.getPtime()) + "";
                    } else {
                        tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
                    }
//                  requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                    adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
                    requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;
                } else {
                    if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                        NewsFeed lastItem = mArrNewsFeed.get(mArrNewsFeed.size() - 1);
                        tstart = DateUtil.dateStr2Long(lastItem.getPtime()) + "";
                        for (int i = mArrNewsFeed.size() - 1; i > 0; i--) {
                            NewsFeed newsFeed = mArrNewsFeed.get(i);
                            if (newsFeed.getRtype() != 3 && newsFeed.getRtype() != 4) {
                                adLoadNewsFeedEntity.setNid(newsFeed.getNid());
                                break;
                            }
                        }
                    }
//                  requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                    adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
                    requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;

                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                mFlag = true;
                tstart = Long.valueOf(tstart) - 1000 * 60 * 60 * 12 + "";
//              requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart) ? null : Long.parseLong(tstart));
                requestUrl = HttpConstant.URL_FEED_AD_LOAD_MORE;
            }
        }

        Logger.e("ccc", "requestUrl==" + requestUrl);
        RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
//        if ("1".equals(mstrChannelId)) {
        Logger.e("aaa", "gson==" + gson.toJson(adLoadNewsFeedEntity));
        Logger.e("ccc", "requestBody==" + gson.toJson(adLoadNewsFeedEntity));
        NewsFeedRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsFeedRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
            @Override
            public void onResponse(final ArrayList<NewsFeed> result) {
                loadNewFeedSuccess(result, flag);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadNewFeedError(error, flag);
            }
        });
        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        newsFeedRequestPost.setRequestHeaders(header);
        newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(newsFeedRequestPost);
//        } else {
//
//            FeedRequest<ArrayList<NewsFeed>> feedRequest = new FeedRequest<ArrayList<NewsFeed>>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
//            }.getType(), requestUrl, new Response.Listener<ArrayList<NewsFeed>>() {
//
//                @Override
//                public void onResponse(final ArrayList<NewsFeed> result) {
//                    loadNewFeedSuccess(result, flag);
//
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    loadNewFeedError(error, flag);
//                }
//            });
////            HashMap<String, String> header = new HashMap<>();
////        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
////            header.put("Content-Type", "application/json");
////            feedRequest.setRequestHeaders(header);
//            HashMap<String, String> header = new HashMap<>();
//            header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
//            feedRequest.setRequestHeader(header);
//            feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//            requestQueue.add(feedRequest);
//        }

    }

    public void loadNewFeedSuccess(final ArrayList<NewsFeed> result, int flag) {
        removePrompt();
        if (mIsFirst || flag == PULL_DOWN_REFRESH) {
            if (result == null || result.size() == 0) {
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                mRefreshTitleBar.setText("已是最新数据");
                mRefreshTitleBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
                            mRefreshTitleBar.setVisibility(View.GONE);
                        }
                    }
                }, 1000);
                stopRefresh();
                mlvNewsFeed.onRefreshComplete();
                return;
            }
            mRefreshTitleBar.setText("又发现了" + result.size() + "条新数据");
            mRefreshTitleBarAnimtation();

        }
        if (flag == PULL_DOWN_REFRESH && !mIsFirst && result != null && result.size() > 0) {
            NewsFeed newsFeed = new NewsFeed();
            newsFeed.setStyle(900);
            result.add(newsFeed);
        }

        mHomeRetry.setVisibility(View.GONE);
        stopRefresh();
        if (result != null && result.size() > 0) {
            mSearchPage++;
            switch (flag) {
                case PULL_DOWN_REFRESH:
                    if (mArrNewsFeed == null) {
                        mArrNewsFeed = result;
                    } else {
                        if (result.get(0).getRtype() == 4) {
                            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
                            while (iterator.hasNext()) {
                                NewsFeed newsFeed = iterator.next();
                                if (newsFeed.getRtype() == 4 && result.get(0).getNid() == newsFeed.getNid()) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        mArrNewsFeed.addAll(0, result);
                    }
                    mlvNewsFeed.getRefreshableView().setSelection(0);
//                            mRefreshTitleBar.setText("又发现了"+result.size()+"条新数据");
//                            mRefreshTitleBar.setVisibility(View.VISIBLE);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(mRefreshTitleBar.getVisibility() == View.VISIBLE){
//                                        mRefreshTitleBar.setVisibility(View.GONE);
//                                    }
//
//                                }
//                            }, 1000);
                    break;
                case PULL_UP_REFRESH:
                    Logger.e("aaa", "===========PULL_UP_REFRESH==========");
                    if (isNewVisity) {//首次进入加入他
//                                addSP(result);
                        isNeedAddSP = false;
                    }
                    if (mArrNewsFeed == null) {
                        mArrNewsFeed = result;

                    } else {
                        mArrNewsFeed.addAll(result);
                    }

                    break;
            }
            if (mNewsSaveCallBack != null) {
                mNewsSaveCallBack.result(mstrChannelId, mArrNewsFeed);
            }
            //如果频道是1,则说明此频道的数据都是来至于其他的频道,为了方便存储,所以要修改其channelId
            if (mstrChannelId != null && ("1".equals(mstrChannelId) || "35".equals(mstrChannelId) || "44".equals(mstrChannelId))) {
                for (NewsFeed newsFeed : result) {
                    if ("1".equals(mstrChannelId)) {
                        newsFeed.setChannel(1);
                        if (newsFeed.getStyle() == 6) {
                            newsFeed.setStyle(8);
                        }
                    } else if ("35".equals(mstrChannelId)) {
                        newsFeed.setChannel(35);
                    } else if ("44".equals(mstrChannelId)) {
                        newsFeed.setChannel(44);
                    }

//                    if (newsFeed.getStyle() == 6) {
//                        newsFeed.setStyle(8);
//                    }
                }
            }
            //如果频道是42,则说明此频道的数据都是来至于其他的频道,为了方便存储,所以要修改其channelId
            if (mstrChannelId != null && "42".equals(mstrChannelId)) {
                for (NewsFeed newsFeed : result)
                    newsFeed.setChannel(42);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mNewsFeedDao.insert(result);
                }
            }).start();
            mAdapter.setNewsFeed(mArrNewsFeed);
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
            showChangeTextSizeView();
        } else {
            //向服务器发送请求,已成功,但是返回结果为null,需要显示重新加载view
            if (TextUtil.isListEmpty(mArrNewsFeed)) {
                ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                if (TextUtil.isListEmpty(newsFeeds)) {
                    mHomeRetry.setVisibility(View.VISIBLE);
                } else {
                    mArrNewsFeed = newsFeeds;
                    mHomeRetry.setVisibility(View.GONE);
                    mAdapter.setNewsFeed(newsFeeds);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                mAdapter.setNewsFeed(mArrNewsFeed);
                mAdapter.notifyDataSetChanged();

            }
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }

        mIsFirst = false;
        mlvNewsFeed.onRefreshComplete();
        //发版时候去掉
//        bgLayout.setVisibility(View.VISIBLE);
        loadAD();

    }

    private void loadNewFeedError(VolleyError error, final int flag) {
        if (error.toString().contains("2002")) {
            removePrompt();
            mRefreshTitleBar.setText("已是最新数据");
            mRefreshTitleBar.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
                        mRefreshTitleBar.setVisibility(View.GONE);
                    }

                }
            }, 1000);
        } else if (error.toString().contains("4003") && mstrChannelId.equals("1")) {//说明三方登录已过期,防止开启3个loginty
            User user = getUser(getActivity());
            user.setUtype("2");
            SharedPreManager.saveUser(user);
//                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
//                    startActivityForResult(loginAty, REQUEST_CODE);
            UserManager.registerVisitor(getActivity(), new UserManager.RegisterVisitorListener() {
                @Override
                public void registeSuccess() {
                    mlvNewsFeed.onRefreshComplete();
                    loadNewsFeedData("", flag);
                }
            });
        }
        if (TextUtil.isListEmpty(mArrNewsFeed)) {
            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
                mHomeRetry.setVisibility(View.VISIBLE);
            } else {
                mArrNewsFeed = newsFeeds;
                mHomeRetry.setVisibility(View.GONE);
                mAdapter.setNewsFeed(newsFeeds);
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
        }
        stopRefresh();
        mlvNewsFeed.onRefreshComplete();
    }


    public void loadData(final int flag) {

        User user = getUser(mContext);
        Logger.e("jigang", "loaddata -----" + flag);
        if (null != user) {
            if (NetUtil.checkNetWork(mContext)) {
                if (!isNoteLoadDate) {
                    if (mstrChannelId != null && mstrChannelId.equals("1000")) {
                        if (!user.isVisitor()) {
                            loadFocusData(flag);
                        } else {
                            setRefreshComplete();
                            registerVisitor(flag);
                        }
                    } else if (!TextUtil.isEmptyString(mstrKeyWord)) {
                        loadNewsFeedData("search", flag);
                    } else if (!TextUtil.isEmptyString(mstrChannelId)) {
                        loadNewsFeedData("recommend", flag);
                    }
                    startTopRefresh();
                } else {
                    isNoteLoadDate = false;
                    setRefreshComplete();
                }
            } else {
                setRefreshComplete();
                ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                if (TextUtil.isListEmpty(newsFeeds)) {
                    mHomeRetry.setVisibility(View.VISIBLE);
                } else {
                    mHomeRetry.setVisibility(View.GONE);
                }
                mAdapter.setNewsFeed(newsFeeds);
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
                showChangeTextSizeView();
            }
        } else {
            setRefreshComplete();
            registerVisitor(flag);
        }
    }

    private void setRefreshComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.onRefreshComplete();
            }
        }, 500);
    }

    private void registerVisitor(final int flag) {
        //请求token
        UserManager.registerVisitor(mContext, new UserManager.RegisterVisitorListener() {
            @Override
            public void registeSuccess() {
                loadData(flag);
            }
        });
    }

    public void loadFocusData(final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl;
        String uid = String.valueOf(getUser(mContext).getMuid());
        String tstart = System.currentTimeMillis() + "";
        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
                tstart = DateUtil.dateStr2Long(firstItem.getPtime()) + "";
            } else {
                tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
            }
            requestUrl = HttpConstant.URL_FEED_FOCUS_PULL_DOWN + "uid=" + uid + "&tcr=" + tstart;
        } else {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed lastItem = mArrNewsFeed.get(mArrNewsFeed.size() - 1);
                tstart = DateUtil.dateStr2Long(lastItem.getPtime()) + "";
            }
            requestUrl = HttpConstant.URL_FEED_FOCUS_LOAD_MORE + "uid=" + uid + "&tcr=" + tstart;
        }
        RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
        FeedRequest<ArrayList<NewsFeed>> feedRequest = new FeedRequest<ArrayList<NewsFeed>>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
        }.getType(), requestUrl, new Response.Listener<ArrayList<NewsFeed>>() {

            @Override
            public void onResponse(final ArrayList<NewsFeed> result) {
                loadNewFeedSuccess(result, flag);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadNewFeedError(error, flag);
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", getUser(mContext).getAuthorToken());
//        header.put("Authorization", "9097879790");
        feedRequest.setRequestHeader(header);
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
    }

    NewsFeedFgtPopWindow mNewsFeedFgtPopWindow;

    public void setNewsFeedFgtPopWindow(NewsFeedFgtPopWindow mNewsFeedFgtPopWindow) {
        this.mNewsFeedFgtPopWindow = mNewsFeedFgtPopWindow;
    }


    public interface NewsFeedFgtPopWindow {
        void showPopWindow(int x, int y, String pubName, int newsid, NewNewsFeedAdapter mAdapter);
    }

    private void showChangeTextSizeView() {
        if (mstrChannelId.equals("1") && mFlag == false) {
            if (mChangeTextSizePopWindow == null) {
                mChangeTextSizePopWindow = new ChangeTextSizePopupWindow(getActivity());
                mChangeTextSizePopWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    }

    HomeWatcher mHomeWatcher;

    @Override
    public void onResume() {
        if (vPlayer != null) {
//            vPlayer.onResume();
//            ToolsUtils.muteAudioFocus(mContext, false);
        }

        mHomeWatcher = new HomeWatcher(this.getActivity());
        mHomeWatcher.setOnHomePressedListener(mOnHomePressedListener);
        mHomeWatcher.startWatch();
        super.onResume();
        long time = (System.currentTimeMillis() - homeTime) / 1000;
        Logger.e("aaa", "time====" + time);
        if (isNewVisity && isClickHome && time >= 60) {
//            mlvNewsFeed.setRefreshing();
//            isListRefresh = true;
//            loadData(PULL_DOWN_REFRESH);
//            isClickHome = false;
//            bgLayout.setVisibility(View.VISIBLE);
//            mlvNewsFeed.createLoadingLayoutProxy(true, false);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isListRefresh = true;
//                    loadData(PULL_DOWN_REFRESH);
//                    isClickHome = false;
//                }
//            },1500);

            mThread = new Runnable() {
                @Override
                public void run() {
                    mlvNewsFeed.setRefreshing();
                    isListRefresh = true;
                    isClickHome = false;
                }
            };
            if (mstrChannelId != null && !mstrChannelId.equals("1000")) {
                mHandler.postDelayed(mThread, 1000);
            }
        } else {
            if (mArrNewsFeed != null && bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
                mAdapter.setNewsFeed(mArrNewsFeed);
            }
            mAdapter.notifyDataSetChanged();
        }
        if (mstrChannelId != null && mstrChannelId.equals("1000")) {
            User user = getUser(mContext);
            RelativeLayout focusBg = (RelativeLayout) rootView.findViewById(R.id.focus_no_data_layout);
            if (user != null && user.isVisitor()) {
                loadFocusData(PULL_DOWN_REFRESH);
//                if (mArrNewsFeed != null) {
//                    mArrNewsFeed.clear();
//                }
//                mAdapter.notifyDataSetChanged();
                focusBg.setVisibility(View.VISIBLE);
            } else {
                focusBg.setVisibility(View.GONE);
                loadFocusData(PULL_DOWN_REFRESH);
//                focusBg.setVisibility(View.GONE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (TextUtil.isListEmpty(mArrNewsFeed)) {
//                            loadFocusData(PULL_UP_REFRESH);
//                        } else {
//                            HashMap<String, String> attentions4Map = SharedPreManager.getAttentions4Map();
//                            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
//                            while (iterator.hasNext()) {
//                                if (attentions4Map.get(iterator.next().getPname()) == null) {
//                                    iterator.remove();
//                                }
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }, 1000);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
        if (vPlayer != null) {
//            ToolsUtils.muteAudioFocus(mContext, true);
            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                vPlayer.stop();
                vPlayer.release();
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
            } else if (mFeedFullScreen.getVisibility()==View.VISIBLE)
            {
                vPlayer.onPause();
            }
            else
            {
                VideoVisibleControl();
            }
        }

    }

    long homeTime;
    OnHomePressedListener mOnHomePressedListener = new OnHomePressedListener() {
        @Override
        public void onHomePressed() {
            Logger.e("aaa", "点击home键");
            if (isClickHome) {
                return;
            }
            isClickHome = true;
            homeTime = System.currentTimeMillis();
        }


        @Override
        public void onHomeLongPressed() {
            Logger.e("aaa", "长按home键");
            if (isClickHome) {
                return;
            }
            isClickHome = true;
            homeTime = System.currentTimeMillis();
        }
    };

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                Logger.e("aaa", "文字的改变！！！");
//                int size = intent.getIntExtra("textSize", CommonConstant.TEXT_SIZE_NORMAL);
//                mSharedPreferences.edit().putInt("textSize", size).commit();
                mAdapter.notifyDataSetChanged();
            } else if (CommonConstant.NEWS_FEED_REFRESH.equals(intent.getAction())) {
                mlvNewsFeed.getRefreshableView().setSelection(0);
                refreshData();
            }
        }
    }

//    public void addSP(ArrayList<NewsFeed> result) {
//        ArrayList<UploadLogDataEntity> uploadLogDataEntities = new ArrayList<UploadLogDataEntity>();
//        for (NewsFeed bean : result) {
//            UploadLogDataEntity uploadLogDataEntity = new UploadLogDataEntity();
//            uploadLogDataEntity.setN(bean.getNid()+"");
//            uploadLogDataEntity.setT("0");//需要改成typeID
//            uploadLogDataEntity.setC(bean.getChannel()+"");
//            uploadLogDataEntities.add(uploadLogDataEntity);
//        }
//        int saveNum = SharedPreManager.upLoadLogSaveList(mstrUserId, CommonConstant.UPLOAD_LOG_MAIN, uploadLogDataEntities);
//        Logger.e("ccc", "主页的数据====" + SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN));
//    }


    //    int lastY = 0;
//    int MAX_PULL_BOTTOM_HEIGHT = 100;
    LinearLayout footerView;

    public void addHFView(LayoutInflater LayoutInflater) {
        View mSearchHeaderView;
//        if (mstrChannelId.equals("1000")) {
//            mSearchHeaderView = LayoutInflater.inflate(R.layout.search_header_layout_focus, null);
//        } else

        mSearchHeaderView = LayoutInflater.inflate(R.layout.search_header_layout, null);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        mSearchHeaderView.setLayoutParams(layoutParams);
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (!mstrChannelId.equals("44")) {
            lv.addHeaderView(mSearchHeaderView);
        }

        lv.setHeaderDividersEnabled(false);
//        if (mstrChannelId.equals("1000")) {
//            TextView tvFocus = (TextView) mSearchHeaderView.findViewById(R.id.focus_textView);
//            TextView tvFocusNum = (TextView) mSearchHeaderView.findViewById(R.id.focus_num_textView);
//            if (bgLayout.getVisibility() == View.VISIBLE) {
//                bgLayout.setVisibility(View.GONE);
//            }
//        }
        mSearch_layout = (RelativeLayout) mSearchHeaderView.findViewById(R.id.search_layout);
        mSearch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), TopicSearchAty.class);
                getActivity().startActivity(in);
                MobclickAgent.onEvent(getActivity(), "qidian_user_enter_search_page");
            }
        });


        footerView = (LinearLayout) LayoutInflater.inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);

//        lv.setFooterDividersEnabled(false);
        mlvNewsFeed.setOnStateListener(new PullToRefreshBase.onStateListener() {
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
                mlvNewsFeed.setFooterViewInvisible();
            }
        });

//        mlvNewsFeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

        // 监听listview滚到最底部
        mlvNewsFeed.setOnScrollListener(new AbsListView.OnScrollListener() {
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
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (thisVisibleItemCount < totalItemCount) {
                    thisTotalItemCount = totalItemCount;
                    thisVisibleItemCount = visibleItemCount;
                }
                if (mstrChannelId.equals("44") && vPlayer != null) {
                    VideoShowControl();
//                    VideoVisibleControl();
                    if (newsVideoFeed != null)
                        Log.e(TAG, "VideoNewsFeed:" + newsVideoFeed.toString());
                }
            }
        });
    }

    private void removePrompt() {
        if (!TextUtil.isListEmpty(mArrNewsFeed)) {
            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
            while (iterator.hasNext()) {
                NewsFeed newsFeed = iterator.next();
                if (newsFeed.getStyle() == 900) {
                    iterator.remove();
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    public void mRefreshTitleBarAnimtation() {

        //初始化
        Animation mStartAlphaAnimation = new AlphaAnimation(0f, 1.0f);
        //设置动画时间
        mStartAlphaAnimation.setDuration(500);
        mRefreshTitleBar.startAnimation(mStartAlphaAnimation);
        mStartAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mRefreshTitleBar.getVisibility() == View.GONE) {
                    mRefreshTitleBar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation mEndAlphaAnimation = new AlphaAnimation(1.0f, 0f);
                        mEndAlphaAnimation.setDuration(500);
                        mRefreshTitleBar.startAnimation(mEndAlphaAnimation);
                        mEndAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
                                    mRefreshTitleBar.setVisibility(View.GONE);
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
     * 广告滑动接口
     */
    private void scrollAd() {
        User user = SharedPreManager.getUser(mContext);
        if (user != null) {
            int uid = user.getMuid();
            //渠道类型, 1：奇点资讯， 2：黄历天气，3：纹字锁频，4：猎鹰浏览器，5：白牌 6:纹字主题
            int ctype = CommonConstant.NEWS_CTYPE;
            //平台类型，1：IOS，2：安卓，3：网页，4：无法识别
            int ptype = CommonConstant.NEWS_PTYPE;
            //mid
            String imei = SharedPreManager.get("flag", "imei");
            String requestUrl = HttpConstant.URL_SCROLL_AD + "?uid=" + uid + "&ctype=" + ctype + "&ptype=" + ptype + "&mid=" + imei;
            RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
            StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, null);
            requestQueue.add(request);
        }
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (list.size() > 0) {
            mADs = list;
            if (mADs != null && mAdapter != null && mADs.size() > 0) {
                for (int i = 0; i < mADs.size(); i++) {
                    // 强烈建议：多个广告之间的间隔最好大一些，优先保证用户体验！
                    // 此外，如果开发者的App的使用场景不是经常被用户滚动浏览多屏的话，没有必要在调用loadAD(int count)时去加载多条，只需要在用户即将进入界面时加载1条广告即可。
//                            mAdapter.addADToPosition((AD_POSITION + i * 10) % MAX_ITEMS, mADs.get(i));
                    NativeADDataRef data = mADs.get(i);
                    if (mArrNewsFeed != null && mArrNewsFeed.size() > 2) {
                        NewsFeed newsFeedFirst = mArrNewsFeed.get(1);
                        NewsFeed newsFeed = new NewsFeed();
                        newsFeed.setTitle(data.getTitle());
                        newsFeed.setRtype(3);
                        ArrayList<String> imgs = new ArrayList<>();
                        imgs.add(data.getImgUrl());
                        newsFeed.setImgs(imgs);
                        newsFeed.setPname(data.getDesc());
                        int style = newsFeedFirst.getStyle();
                        if (style == 11 || style == 12 || style == 13 || style == 5) {
                            newsFeed.setStyle(50);
                        } else {
                            newsFeed.setStyle(51);
                        }
                        newsFeed.setDataRef(data);
                        if (isPullDown) {
                            mArrNewsFeed.add(2, newsFeed);
                        } else {
                            if (mArrNewsFeed.size() >= 14) {
                                mArrNewsFeed.add(mArrNewsFeed.size() - 13, newsFeed);
                            } else {
                                mArrNewsFeed.add(2, newsFeed);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        }
        isADRefresh = false;
    }

    @Override
    public void onNoAD(int i) {
        isADRefresh = false;
    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {
        getADButtonText(nativeADDataRef);
        isADRefresh = false;
    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, int i) {
        isADRefresh = false;
    }

    /**
     * App类广告安装、下载状态的更新（普链广告没有此状态，其值为-1） 返回的AppStatus含义如下： 0：未下载 1：已安装 2：已安装旧版本 4：下载中（可获取下载进度“0-100”）
     * 8：下载完成 16：下载失败
     */
    private String getADButtonText(NativeADDataRef adItem) {
        if (adItem == null) {
            return "……";
        }
        if (!adItem.isAPP()) {
            return "查看详情";
        }
        switch (adItem.getAPPStatus()) {
            case 0:
                return "点击下载";
            case 1:
                return "点击启动";
            case 2:
                return "点击更新";
            case 4:
                return adItem.getProgress() > 0 ? "下载中" + adItem.getProgress() + "%" : "下载中"; // 特别注意：当进度小于0时，不要使用进度来渲染界面
            case 8:
                return "下载完成";
            case 16:
                return "下载失败,点击重试";
            default:
                return "查看详情";
        }
    }
}