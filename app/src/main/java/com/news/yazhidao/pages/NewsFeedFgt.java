package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.github.jinsedeyuzhou.PlayStateParams;
import com.github.jinsedeyuzhou.PlayerManager;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewNewsFeedAdapter;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.database.ReleaseSourceItemDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.AdDeviceEntity;
import com.news.yazhidao.entity.AdEntity;
import com.news.yazhidao.entity.AdImpressionEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.FeedRequest;
import com.news.yazhidao.net.volley.NewsFeedRequestPost;
import com.news.yazhidao.receiver.HomeWatcher;
import com.news.yazhidao.receiver.HomeWatcher.OnHomePressedListener;
import com.news.yazhidao.utils.CrashHandler;
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
import com.news.yazhidao.widget.VideoItemContainer;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class NewsFeedFgt extends Fragment {

    public static final String KEY_NEWS_CHANNEL = "key_news_channel";
    public static final String KEY_PUSH_NEWS = "key_push_news";//表示该新闻是后台推送过来的
    public static final String KEY_NEWS_IMG_URL = "key_news_img_url";//确保新闻详情中有一张图
    public static final String KEY_NEWS_TYPE = "key_news_type";//新闻类型,是否是大图新闻
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_NEWS_IMAGE = "key_news_image";
    public static final String KEY_SHOW_COMMENT = "key_show_comment";
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
    private int mDeleteIndex;
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
    private NewsFeed newsFeed;

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
        User user = SharedPreManager.getUser(mContext);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("jigang", "requestCode = " + requestCode + ",data=" + data);
        if (requestCode == NewNewsFeedAdapter.REQUEST_CODE && data != null) {
            int newsId = data.getIntExtra(NewNewsFeedAdapter.KEY_NEWS_ID, 0);
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
        }
    }

    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
            if (mstrChannelId.equals("44"))
                vPlayer = PlayerManager.getPlayerManager().initialize(mContext);
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

        //=====================视频添加
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.BOTH);
        mlvNewsFeed.setMainFooterView(true);
        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                loadData(PULL_DOWN_REFRESH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                isListRefresh = true;
                loadData(PULL_UP_REFRESH);
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
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (vPlayer != null) {
            vPlayer.onChanged(newConfig);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(TAG, "ORIENTATION_PORTRAIT");
                mFeedFullScreen.setVisibility(View.GONE);
                mlvNewsFeed.setVisibility(View.VISIBLE);
                mFeedFullScreen.removeAllViews();
                int position = getPlayItemPosition();
                if (position != -1) {
                    VideoItemContainer playItemView = getPlayItemView(position);
                    View itemView = (View) playItemView.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
                    }
                    playItemView.removeAllViews();
                    playItemView.addView(vPlayer);
                    vPlayer.setShowContoller(true);
                } else {
                    mFeedSmallScreen.removeAllViews();
                    mFeedFullScreen.addView(vPlayer);
                    vPlayer.setShowContoller(false);
                    mFeedSmallLayout.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d(TAG, "ORIENTATION_LANSCAPES");
                VideoItemContainer frameLayout = (VideoItemContainer) vPlayer.getParent();
                if (frameLayout == null)
                    return;
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                    }
                }
                frameLayout.removeAllViews();
                mFeedFullScreen.addView(vPlayer);
                mFeedSmallLayout.setVisibility(View.GONE);
                mlvNewsFeed.setVisibility(View.GONE);
                mFeedFullScreen.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.notifyDataSetChanged();
            mlvNewsFeed.setVisibility(View.VISIBLE);
            if (mFeedFullScreen.getVisibility()==View.VISIBLE)
              mFeedFullScreen.setVisibility(View.GONE);
        }
    }

    /**
     * 视频播放控制
     */
    public void playVideoControl() {
        if (null == vPlayer)
            return;
//        mFeedFullScreen = (VideoContainer) getActivity().findViewById(R.id.feed_full_screen);
//        mFeedSmallScreen = (SmallVideoContainer) getActivity().findViewById(R.id.feed_small_screen);
//        mFeedSmallLayout = (RelativeLayout) getActivity().findViewById(R.id.feed_small_layout);
//        mFeedClose = (ImageView) getActivity().findViewById(R.id.feed_video_close);
        mAdapter.setOnPlayClickListener(new NewNewsFeedAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(RelativeLayout relativeLayout, NewsFeed feed) {
                cPostion = feed.getNid();
                newsFeed = feed;

                if (vPlayer.getStatus() == PlayStateParams.STATE_PAUSED) {
                    if (cPostion != lastPostion) {
                        vPlayer.stop();
                        vPlayer.release();
                    }
                }

                if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                    mFeedSmallLayout.setVisibility(View.GONE);
                    mFeedSmallScreen.removeAllViews();
                    vPlayer.setShowContoller(false);
                }

                if (lastPostion != -1) {
                    ViewGroup last = (ViewGroup) vPlayer.getParent();
                    if (last != null) {
                        last.removeAllViews();
                        View itemView = (View) last.getParent();
                        if (itemView != null) {
                            itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (vPlayer.getParent() != null) {
                    ((ViewGroup) vPlayer.getParent()).removeAllViews();
                }

                View view = (View) relativeLayout.getParent();
                VideoItemContainer frameLayout = (VideoItemContainer) view.findViewById(R.id.layout_item_video);
                frameLayout.removeAllViews();
                frameLayout.addView(vPlayer);
                vPlayer.setTitle(feed.getTitle());
                vPlayer.start(feed.getVideourl());
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
                Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, newsFeed);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        vPlayer.setCompletionListener(new VPlayPlayer.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                    vPlayer.removeAllViews();
                    mFeedSmallLayout.setVisibility(View.GONE);
                    vPlayer.setShowContoller(true);
                } else if (mFeedFullScreen.getVisibility() == View.VISIBLE) {
                    mFeedFullScreen.removeAllViews();
                    mFeedFullScreen.setVisibility(View.GONE);
                    vPlayer.setShowContoller(true);
                }

                VideoItemContainer frameLayout = (VideoItemContainer) vPlayer.getParent();
                vPlayer.release();
                if (frameLayout != null && frameLayout.getChildCount() > 0) {
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();

                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                    }
                }

                lastPostion = -1;


            }
        });


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

    public String getAdMessage() {

        Gson gson = new Gson();
        Random random = new Random();
        AdImpressionEntity adImpressionEntity = new AdImpressionEntity();
        adImpressionEntity.setAid(random.nextInt(2) == 0 ? "98" : "100");
        /** 单图91  三图164 */
        adImpressionEntity.setHeight((int) (DeviceInfoUtil.obtainDensity() * 164) + "");
        adImpressionEntity.setWidth(DeviceInfoUtil.getScreenWidth(mContext) + "");

        AdDeviceEntity adDeviceEntity = new AdDeviceEntity();
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        /** 设置IMEI */
        String imei = SharedPreManager.get("flag", "imei");
        adDeviceEntity.setImei(imei);
        /** 设置AndroidID */
        String androidId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        adDeviceEntity.setAnid(TextUtil.isEmptyString(androidId) ? null : DeviceInfoUtil.generateMD5(androidId));
        /** 设置设备品牌 */
        String brand = Build.BRAND;
        adDeviceEntity.setBrand(brand);
        /** 设置设备型号 */
        String platform = Build.MODEL;
        adDeviceEntity.setPlatform(platform);
        /** 设置操作系统 */
        adDeviceEntity.setOs("1");
        /** 设置操作系统版本号 */
        String version = Build.VERSION.RELEASE;
        adDeviceEntity.setOs_version(version);
        /** 设置屏幕分辨率 */
        adDeviceEntity.setDevice_size(CrashHandler.getResolution(mContext));
        /** 设置IP */
        String ip = "";
        if (DeviceInfoUtil.isWifiNetWorkState(mContext)) {
            ip = DeviceInfoUtil.getIpAddress(mContext);
        } else {
            ip = DeviceInfoUtil.getLocalIpAddress();
        }
        adDeviceEntity.setIp(ip);
        /** 设置网络环境 */
        String networkType = DeviceInfoUtil.getNetworkType(mContext);
        if (TextUtil.isEmptyString(networkType)) {
            adDeviceEntity.setNetwork("0");
        } else {
            if ("wifi".endsWith(networkType)) {
                adDeviceEntity.setNetwork("1");
            } else if ("2G".endsWith(networkType)) {
                adDeviceEntity.setNetwork("2");
            } else if ("3G".endsWith(networkType)) {
                adDeviceEntity.setNetwork("3");
            } else if ("4G".endsWith(networkType)) {
                adDeviceEntity.setNetwork("4");
            } else {
                adDeviceEntity.setNetwork("0");
            }
        }
        /** 设置经度 纬度 */
//        String locationJsonString = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION);
//        LocationEntity locationEntity = gson.fromJson(locationJsonString, LocationEntity.class);
//        adDeviceEntity.setLongitude(locationEntity.get);
        /** 设置横竖屏幕 */
        if (DeviceInfoUtil.isScreenChange(mContext)) {//横屏
            adDeviceEntity.setScreen_orientation("2");
        } else {//竖屏
            adDeviceEntity.setScreen_orientation("1");
        }


        AdEntity adEntity = new AdEntity();
        adEntity.setTs((System.currentTimeMillis() / 1000) + "");
        adEntity.setDevice(adDeviceEntity);
        adEntity.getImpression().add(adImpressionEntity);


        return gson.toJson(adEntity);

    }

    private void loadNewsFeedData(String url, final int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl;
        String tstart = System.currentTimeMillis() + "";
        String fixedParams = "&cid=" + mstrChannelId + "&uid=" + SharedPreManager.getUser(mContext).getMuid();
        ADLoadNewsFeedEntity adLoadNewsFeedEntity = new ADLoadNewsFeedEntity();
        adLoadNewsFeedEntity.setCid(TextUtil.isEmptyString(mstrChannelId) ? null : Long.parseLong(mstrChannelId));
        adLoadNewsFeedEntity.setUid(SharedPreManager.getUser(mContext).getMuid());
        adLoadNewsFeedEntity.setT(1);
        adLoadNewsFeedEntity.setV(1);
        Gson gson = new Gson();
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(getAdMessage()));

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
        if (mDeleteIndex != 0) {
            mArrNewsFeed.remove(mDeleteIndex);
            mDeleteIndex = 0;
            mAdapter.notifyDataSetChanged();
        }
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
            mDeleteIndex = result.size() - 1;
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
            if (mstrChannelId != null && "1".equals(mstrChannelId)) {
                for (NewsFeed newsFeed : result)
                    newsFeed.setChannel(1);
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

    }

    private void loadNewFeedError(VolleyError error, final int flag) {
        if (error.toString().contains("2002")) {
            if (mDeleteIndex != 0) {
                mArrNewsFeed.remove(mDeleteIndex);
                mDeleteIndex = 0;
                mAdapter.notifyDataSetChanged();
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
        } else if (error.toString().contains("4003") && mstrChannelId.equals("1")) {//说明三方登录已过期,防止开启3个loginty
            User user = SharedPreManager.getUser(getActivity());
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

        User user = SharedPreManager.getUser(mContext);
        Logger.e("jigang", "loaddata -----" + flag);
        if (null != user) {
            if (NetUtil.checkNetWork(mContext)) {
                if (!isNoteLoadDate) {
                    if (mstrChannelId != null && mstrChannelId.equals("1000")) {
                        if (!user.isVisitor()) {
                            loadFocusData(flag);
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
        String uid = String.valueOf(SharedPreManager.getUser(mContext).getMuid());
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
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
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
            User user = SharedPreManager.getUser(mContext);
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
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
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
        mlvNewsFeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

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
                    Log.e(TAG, "first" + firstVisibleItem + "onScroll:" + mlvNewsFeed.getRefreshableView().getChildAt(0) + "visibleCount:" + visibleItemCount + ",last：" + mlvNewsFeed.getRefreshableView().getLastVisiblePosition());
                    VideoShowControl(view);
                }
            }
        });
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

    public VideoItemContainer getPlayItemView(int cPosition) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        if (cPosition != -1) {
            View item = lv.getChildAt(cPosition);
            return (VideoItemContainer) item.findViewById(R.id.layout_item_video);
        }

        return null;
    }

    private void VideoShowControl(AbsListView view) {
        ListView lv = mlvNewsFeed.getRefreshableView();
        Log.e(TAG, "mlvNewsFeed: first" + lv.getFirstVisiblePosition() + ",last:" + lv.getLastVisiblePosition());
        Log.e(TAG, "AbsListView: first" + view.getFirstVisiblePosition() + ",last:" + view.getLastVisiblePosition());
        boolean isExist = false;
        int position = -1;
        for (int i = lv.getFirstVisiblePosition(); i <= lv.getLastVisiblePosition(); i++) {
            if (i == 0)
                continue;
            if (i > mArrNewsFeed.size())
                break;
            if (mArrNewsFeed.get(i - 1).getNid() == cPostion) {
                isExist = true;
                position = i - view.getFirstVisiblePosition();
                break;
            }
        }
        if (isExist) {
            View item = lv.getChildAt(position);
            Log.e(TAG, "item:" + item.toString() + "position:" + position);
            VideoItemContainer frameLayout = (VideoItemContainer) item.findViewById(R.id.layout_item_video);
            Log.e(TAG, "frameLayout:" + frameLayout.toString());

            if (vPlayer.isPlay() || vPlayer.getStatus() == PlayStateParams.STATE_PAUSED) {
                item.findViewById(R.id.rl_video_show).setVisibility(View.GONE);
            }

            if (vPlayer.getStatus() == PlayStateParams.STATE_PAUSED) {
                if (vPlayer.getParent() != null)
                    ((ViewGroup) vPlayer.getParent()).removeAllViews();
                frameLayout.removeAllViews();
                frameLayout.addView(vPlayer);
                return;
            }

            if (mFeedSmallLayout.getVisibility() == View.VISIBLE) {
                mFeedSmallLayout.setVisibility(View.GONE);
                mFeedSmallScreen.removeAllViews();
                vPlayer.setShowContoller(true);
                frameLayout.removeAllViews();
                frameLayout.addView(vPlayer);

            }


        } else {
            if (vPlayer != null && mFeedSmallLayout.getVisibility() == View.GONE && vPlayer.isPlay()) {
                VideoItemContainer frameLayout = (VideoItemContainer) vPlayer.getParent();
                if (frameLayout != null) {
                    frameLayout.removeAllViews();
                    View itemView = (View) frameLayout.getParent();
                    if (itemView != null) {
                        itemView.findViewById(R.id.rl_video_show).setVisibility(View.VISIBLE);
                    }
                }
                mFeedSmallScreen.removeAllViews();
                vPlayer.setShowContoller(false);
                mFeedSmallScreen.addView(vPlayer);
                mFeedSmallLayout.setVisibility(View.VISIBLE);
            }

        }
        isExist = false;

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
}