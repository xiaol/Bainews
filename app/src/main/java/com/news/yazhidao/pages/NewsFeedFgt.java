package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.database.ReleaseSourceItemDao;
import com.news.yazhidao.entity.ADLoadNewsFeedEntity;
import com.news.yazhidao.entity.AdDeviceEntity;
import com.news.yazhidao.entity.AdEntity;
import com.news.yazhidao.entity.AdImpressionEntity;
import com.news.yazhidao.entity.LocationEntity;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.protobuffer.PushResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NewsFeedFgt extends Fragment{

    public static final String KEY_NEWS_CHANNEL = "key_news_channel";
    public static final String KEY_PUSH_NEWS = "key_push_news";//表示该新闻是后台推送过来的
    public static final String KEY_NEWS_IMG_URL = "key_news_img_url";//确保新闻详情中有一张图
    public static final String KEY_NEWS_TYPE = "key_news_type";//新闻类型,是否是大图新闻
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public static final String KEY_NEWS_IMAGE = "key_news_image";

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
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<>();
    private Context mContext;
    private PullToRefreshListView mlvNewsFeed;
    private View rootView;
    private String mstrDeviceId, mstrUserId, mstrChannelId, mstrKeyWord;
    private NewsFeedDao mNewsFeedDao;
    private ChangeTextSizePopupWindow mChangeTextSizePopWindow;
    private boolean mFlag;
    private SharedPreferences mSharedPreferences;
    private RefreshReceiver mRefreshReciver;
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
        mlvNewsFeed.setRefreshing();
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
        mRefreshReciver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter(CommonConstant.CHANGE_TEXT_ACTION);
        mContext.registerReceiver(mRefreshReciver, intentFilter);
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
                    if (item != null && newsId == item.getNid()) {
                        item.setRead(true);
                        mNewsFeedDao.update(item);
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

        mAdapter = new NewsFeedAdapter(mContext, this, null);
        mAdapter.setClickShowPopWindow(mClickShowPopWindow);
        if (mstrChannelId != null && mstrChannelId.equals("1000")) {
            ReleaseSourceItemDao releaseSourceItemDao = new ReleaseSourceItemDao(mContext);
            String[] colorArr = mContext.getResources().getStringArray(R.array.bg_focus_colors);
            mAdapter.setReleaseSourceItems(releaseSourceItemDao, colorArr);
        }
        mlvNewsFeed.setAdapter(mAdapter);
        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.listview_empty_view, null));

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

    NewsFeedAdapter.clickShowPopWindow mClickShowPopWindow = new NewsFeedAdapter.clickShowPopWindow() {
        @Override
        public void showPopWindow(int x, int y, NewsFeed feed) {
            String pName = feed.getPname();
            mNewsFeedFgtPopWindow.showPopWindow(x, y, pName != null ? pName : "未知来源", mAdapter);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHandler != null) {
            mHandler.removeCallbacks(mThread);
        }
        mContext.unregisterReceiver(mRefreshReciver);
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

    public String getAdMessage(){

        Gson gson = new Gson();

        Activity mActivity = getActivity();
        AdImpressionEntity adImpressionEntity = new AdImpressionEntity();
        adImpressionEntity.setAid("100");
        /** 单图91  三图164 */
        adImpressionEntity.setHeight((int) (DeviceInfoUtil.obtainDensity() * 164) + "");
        adImpressionEntity.setWidth(DeviceInfoUtil.getScreenWidth(mActivity)+"");

        AdDeviceEntity adDeviceEntity = new AdDeviceEntity();
        TelephonyManager tm = (TelephonyManager) mActivity.getSystemService(mActivity.TELEPHONY_SERVICE);
        /** 设置IMEI */
        adDeviceEntity.setImei(TextUtil.isEmptyString(tm.getDeviceId()) ? null : DeviceInfoUtil.generateMD5(tm.getDeviceId()));
        /** 设置AndroidID */
        String androidId = Settings.Secure.getString(mActivity.getContentResolver(),Settings.Secure.ANDROID_ID);
        adDeviceEntity.setAnid(TextUtil.isEmptyString(androidId)?null:DeviceInfoUtil.generateMD5(androidId));
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
        adDeviceEntity.setDevice_size(CrashHandler.getResolution(mActivity));
        /** 设置IP */
        String ip = "";
        if(DeviceInfoUtil.isWifiNetWorkState(mActivity)){
            ip = DeviceInfoUtil.getIpAddress(mActivity);
        }else{
            ip = DeviceInfoUtil.getLocalIpAddress();
        }
        adDeviceEntity.setIp(ip);
        /** 设置网络环境 */
        String networkType = DeviceInfoUtil.getNetworkType(mActivity);
        if (TextUtil.isEmptyString(networkType)) {
            adDeviceEntity.setNetwork("0");
        }else{
            if ("wifi".endsWith(networkType)) {
                adDeviceEntity.setNetwork("1");
            }else if("2G".endsWith(networkType)){
                adDeviceEntity.setNetwork("2");
            }else if("3G".endsWith(networkType)){
                adDeviceEntity.setNetwork("3");
            }else if("4G".endsWith(networkType)){
                adDeviceEntity.setNetwork("4");
            }else{
                adDeviceEntity.setNetwork("0");
            }
        }
        /** 设置经度 纬度 */
//        String locationJsonString = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION);
//        LocationEntity locationEntity = gson.fromJson(locationJsonString, LocationEntity.class);
//        adDeviceEntity.setLongitude(locationEntity.get);
        /** 设置横竖屏幕 */
        if(DeviceInfoUtil.isScreenChange(mActivity)){//横屏
            adDeviceEntity.setScreen_orientation("2");
        }else{//竖屏
            adDeviceEntity.setScreen_orientation("1");
        }



        AdEntity adEntity = new AdEntity();
        adEntity.setTs((System.currentTimeMillis()/1000)+"");
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
        adLoadNewsFeedEntity.setCid(TextUtil.isEmptyString(mstrChannelId)?null:Long.parseLong(mstrChannelId));
        adLoadNewsFeedEntity.setUid(SharedPreManager.getUser(mContext).getMuid());
        Gson gson = new Gson();
        Logger.e("ccc", "getAdMessage==" + getAdMessage());
        adLoadNewsFeedEntity.setB(TextUtil.getBase64(getAdMessage()));

        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
                tstart = DateUtil.dateStr2Long(firstItem.getPtime()) + "";
            } else {
                tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
            }

//            requestUrl = HttpConstant.URL_FEED_PULL_DOWN + "tcr=" + tstart + fixedParams;
            adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart)?null:Long.parseLong(tstart));
            /** 梁帅：判断是否是奇点频道 */
            requestUrl = "1".equals(mstrChannelId)?HttpConstant.URL_FEED_AD_PULL_DOWN: HttpConstant.URL_FEED_PULL_DOWN + "tcr=" + tstart + fixedParams;

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
                    adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart)?null:Long.parseLong(tstart));
                    requestUrl = "1".equals(mstrChannelId)?HttpConstant.URL_FEED_AD_LOAD_MORE: HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                } else {
                    if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                        NewsFeed lastItem = mArrNewsFeed.get(mArrNewsFeed.size() - 1);
                        tstart = DateUtil.dateStr2Long(lastItem.getPtime()) + "";
                    }
//                  requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                    adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart)?null:Long.parseLong(tstart));
                    requestUrl = "1".equals(mstrChannelId)?HttpConstant.URL_FEED_AD_LOAD_MORE: HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;

                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                mFlag = true;
                tstart = Long.valueOf(tstart) - 1000 * 60 * 60 * 12 + "";
//              requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
                adLoadNewsFeedEntity.setTcr(TextUtil.isEmptyString(tstart)?null:Long.parseLong(tstart));
                requestUrl = "1".equals(mstrChannelId)?HttpConstant.URL_FEED_AD_LOAD_MORE: HttpConstant.URL_FEED_LOAD_MORE + "tcr=" + tstart + fixedParams;
            }
        }
        Logger.e("ccc", "requestUrl==" + requestUrl);

        RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
        if("1".equals(mstrChannelId)){
            Logger.e("ccc", "requestBody==" + gson.toJson(adLoadNewsFeedEntity));
            NewsFeedRequestPost<ArrayList<NewsFeed>> newsFeedRequestPost = new NewsFeedRequestPost(requestUrl, gson.toJson(adLoadNewsFeedEntity), new Response.Listener<ArrayList<NewsFeed>>() {
                @Override
                public void onResponse(final ArrayList<NewsFeed> result) {
                    loadNewFeedSuccess(result,flag);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadNewFeedError(error,flag);
                }
            });
            HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
            header.put("Content-Type", "application/json");
            newsFeedRequestPost.setRequestHeaders(header);
            newsFeedRequestPost.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(newsFeedRequestPost);
        }else {

            FeedRequest<ArrayList<NewsFeed>> feedRequest = new FeedRequest<ArrayList<NewsFeed>>(Request.Method.GET, new TypeToken<ArrayList<NewsFeed>>() {
            }.getType(), requestUrl, new Response.Listener<ArrayList<NewsFeed>>() {

                @Override
                public void onResponse(final ArrayList<NewsFeed> result) {
                    loadNewFeedSuccess(result,flag);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loadNewFeedError(error,flag);
                }
            });
//            HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
//            header.put("Content-Type", "application/json");
//            feedRequest.setRequestHeaders(header);
            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
            feedRequest.setRequestHeader(header);
            feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(feedRequest);
        }

    }

    public void loadNewFeedSuccess(final ArrayList<NewsFeed> result,int flag){
        Logger.e("aaa","==="+ result.toString());
        if (mDeleteIndex != 0) {
            mArrNewsFeed.remove(mDeleteIndex);
            mDeleteIndex = 0;
        }
        if (mIsFirst || flag == PULL_DOWN_REFRESH) {
            if (result == null || result.size() == 0) {
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
                    if (mArrNewsFeed == null)
                        mArrNewsFeed = result;
                    else
                        mArrNewsFeed.addAll(0, result);
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

    }
    private void loadNewFeedError(VolleyError error,final int flag){
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

                if (mDeleteIndex != 0) {
                    mArrNewsFeed.remove(mDeleteIndex);
                    mDeleteIndex = 0;
                }
                if (mIsFirst || flag == PULL_DOWN_REFRESH) {
                    if (result == null || result.size() == 0) {
                        mRefreshTitleBar.setText("已是最新数据");
                        mRefreshTitleBar.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mRefreshTitleBar.getVisibility() == View.VISIBLE) {
                                    mRefreshTitleBar.setVisibility(View.GONE);
                                }
                            }
                        }, 1000);
                        mlvNewsFeed.onRefreshComplete();
                        if (bgLayout.getVisibility() == View.VISIBLE) {
                            bgLayout.setVisibility(View.GONE);
                        }
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
                            Logger.e("ccc", "=================33=================" + mArrNewsFeed.size());
                            if (mArrNewsFeed == null)
                                mArrNewsFeed = result;
                            else
                                mArrNewsFeed.addAll(0, result);
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
                            Logger.e("ccc", "===========PULL_UP_REFRESH==========");
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (thisVisibleItemCount >= thisTotalItemCount) {//删除 footerView 这个方法可以显示无数据的情况
                                Logger.e("ccc", "=================111=================");
                                if (footerView.getVisibility() == View.VISIBLE) {
                                    footerView.setVisibility(View.GONE);
                                    mlvNewsFeed.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                }
                            } else {
                                Logger.e("ccc", "================222==================");
                                if (footerView.getVisibility() == View.GONE) {
                                    footerView.setVisibility(View.VISIBLE);
                                    mlvNewsFeed.setMode(PullToRefreshBase.Mode.BOTH);
                                }
                            }
                        }
                    }, 100);


                    if (mNewsSaveCallBack != null) {
                        mNewsSaveCallBack.result(mstrChannelId, mArrNewsFeed);
                    }
                    //如果频道是1,则说明此频道的数据都是来至于其他的频道,为了方便存储,所以要修改其channelId
                    if (mstrChannelId != null && "1".equals(mstrChannelId)) {
                        for (NewsFeed newsFeed : result)
                            newsFeed.setChannel(1);
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                    UserManager.registerVisitor(getActivity(), new UserManager.RegisterVisitorListener() {
                        @Override
                        public void registeSuccess() {
                            loadFocusData(flag);
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
        void showPopWindow(int x, int y, String pubName, NewsFeedAdapter mAdapter);
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
                if (mArrNewsFeed != null) {
                    mArrNewsFeed.clear();
                }
                mAdapter.notifyDataSetChanged();
                focusBg.setVisibility(View.VISIBLE);
            } else {
                focusBg.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtil.isListEmpty(mArrNewsFeed)) {
                            loadFocusData(PULL_UP_REFRESH);
                        } else {
                            HashMap<String, String> attentions4Map = SharedPreManager.getAttentions4Map();
                            Iterator<NewsFeed> iterator = mArrNewsFeed.iterator();
                            while (iterator.hasNext()) {
                                if (attentions4Map.get(iterator.next().getPname()) == null) {
                                    iterator.remove();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }, 1000);
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
        lv.addHeaderView(mSearchHeaderView);
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
            }
        });
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