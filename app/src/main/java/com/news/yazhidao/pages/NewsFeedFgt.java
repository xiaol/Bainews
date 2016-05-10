package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.volley.FeedRequest;
import com.news.yazhidao.net.volley.UpLoadLogRequest;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.ChangeTextSizePopupWindow;
import com.umeng.analytics.AnalyticsConfig;

import java.util.ArrayList;

public class NewsFeedFgt extends Fragment implements Handler.Callback {

    public static final String KEY_NEWS_CHANNEL = "key_news_channel";
    public static final String KEY_PUSH_NEWS = "key_push_news";//表示该新闻是后台推送过来的
    public static final String KEY_NEWS_IMG_URL = "key_news_img_url";//确保新闻详情中有一张图
    public static final String KEY_NEWS_TYPE = "key_news_type";//新闻类型,是否是大图新闻
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_FEED = "key_news_feed";

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


    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    public static final int PULL_DOWN_REFRESH = 1;
    private static final int PULL_UP_REFRESH = 2;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed = new ArrayList<>();
    private Context mContext;
    private NetworkRequest mRequest;
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
    //    private Handler mHandler;
//    private Runnable mRunnable;
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


    public void setNewsFeed(ArrayList<NewsFeed> results) {
        this.mArrNewsFeed = results;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {

                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isNewVisity = isVisibleToUser;
        if (isNewVisity && isNeedAddSP) {//切换到别的页面加入他
            addSP(mArrNewsFeed);//第一次进入主页的时候会加入一次，不用担心这次加入是没有数据的

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
//        mHandler = new Handler(this);
        User user = SharedPreManager.getUser(mContext);
        if (user != null)
            mstrUserId = user.getUserId();
        else
            mstrUserId = "";
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
        mFlag = mSharedPreferences.getBoolean("isshow", false);
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                mlvNewsFeed.setRefreshing();
//            }
//        };
        mRefreshReciver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter(CommonConstant.CHANGE_TEXT_ACTION);
        mContext.registerReceiver(mRefreshReciver, intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("jigang", "requestCode = " + requestCode);
        if (requestCode == NewsFeedAdapter.REQUEST_CODE && data != null) {
            String newsId = data.getStringExtra(NewsFeedAdapter.KEY_NEWS_ID);
            Logger.e("jigang", "newsid = " + newsId);
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                for (NewsFeed item : mArrNewsFeed) {
                    if (item != null && newsId.equals(item.getUrl())) {
                        item.setRead(true);
                        mNewsFeedDao.update(item);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (bgLayout.getVisibility() == View.VISIBLE) {
                    bgLayout.setVisibility(View.GONE);
                }
            }
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

        View mSearchHeaderView = LayoutInflater.inflate(R.layout.search_header_layout, null);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        mSearchHeaderView.setLayoutParams(layoutParams);
        ListView lv = mlvNewsFeed.getRefreshableView();
        lv.addHeaderView(mSearchHeaderView);
        lv.setHeaderDividersEnabled(false);
        mSearch_layout = (RelativeLayout) mSearchHeaderView.findViewById(R.id.search_layout);
        mSearch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), TopicSearchAty.class);
                getActivity().startActivity(in);
            }
        });


        mAdapter = new NewsFeedAdapter(getActivity(), this, null);
        mAdapter.setClickShowPopWindow(mClickShowPopWindow);

        mlvNewsFeed.setAdapter(mAdapter);

        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.listview_empty_view, null));

        setUserVisibleHint(getUserVisibleHint());
        String platform = AnalyticsConfig.getChannel(getActivity());
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
        mHandler.postDelayed(mThread, 1500);
        return rootView;
    }

    NewsFeedAdapter.clickShowPopWindow mClickShowPopWindow = new NewsFeedAdapter.clickShowPopWindow() {
        @Override
        public void showPopWindow(int x, int y, NewsFeed feed) {
            mNewsFeedFgtPopWindow.showPopWindow(x, y, feed.getPubName(), mAdapter);

        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mHandler!=null){
            mHandler.removeCallbacks(mThread);
        }
        mContext.unregisterReceiver(mRefreshReciver);
        Logger.e("jigang", "newsfeedfgt onDestroyView"+mstrChannelId);
        ((ViewGroup) rootView.getParent()).removeView(rootView);
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

    private void loadNewsFeedData(String url, final int flag) {
        String requestUrl;
        String tstart = System.currentTimeMillis() + "";
        String fixedParams = "&cid=" + mstrChannelId + "&userid=" + mstrUserId + "&deviceid=" + mstrDeviceId + "&uid=" + SharedPreManager.getUUID();
        if (flag == PULL_DOWN_REFRESH) {
            if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                NewsFeed firstItem = mArrNewsFeed.get(0);
                tstart = DateUtil.dateStr2Long(firstItem.getPubTime()) + "";
            }
            requestUrl = HttpConstant.URL_FEED_PULL_DOWN + "tstart=" + tstart + fixedParams;
        } else {
            if (mFlag) {
                if (mIsFirst) {
                    ArrayList<NewsFeed> arrNewsFeed = mNewsFeedDao.queryByChannelId(mstrChannelId);
                    if (!TextUtil.isListEmpty(arrNewsFeed)) {
                        NewsFeed newsFeed = arrNewsFeed.get(0);
                        tstart = DateUtil.dateStr2Long(newsFeed.getPubTime()) + "";
                    }
                    requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tstart=" + tstart + fixedParams;
                } else {
                    if (!TextUtil.isListEmpty(mArrNewsFeed)) {
                        NewsFeed lastItem = mArrNewsFeed.get(mArrNewsFeed.size() - 1);
                        tstart = DateUtil.dateStr2Long(lastItem.getPubTime()) + "";
                    }
                    requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tstart=" + tstart + fixedParams;
                }
            } else {
                mSharedPreferences.edit().putBoolean("isshow", true).commit();
                tstart = Long.valueOf(tstart) - 1000 * 60 * 60 * 12 + "";
                requestUrl = HttpConstant.URL_FEED_LOAD_MORE + "tstart=" + tstart + fixedParams;
            }
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
                if (flag == PULL_DOWN_REFRESH && !mIsFirst && result != null && result.size() > 0) {
                    NewsFeed newsFeed = new NewsFeed();
                    newsFeed.setImgStyle("900");
                    result.add(newsFeed);
                    mDeleteIndex = result.size() - 1;
                }
                mIsFirst = false;
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
                            break;
                        case PULL_UP_REFRESH:
                            if (isNewVisity) {//首次进入加入他
                                addSP(result);
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
                            newsFeed.setChannelId("1");
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
                mlvNewsFeed.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
        Logger.e("jigang", "uuid = " + SharedPreManager.getUUID() + ",channelid =" + mstrChannelId + ",tstart =" + tstart);
    }

    public void loadData(int flag) {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }

        Logger.e("jigang", "loaddata -----" + flag);
        if (NetUtil.checkNetWork(mContext)) {
            if (!TextUtil.isEmptyString(mstrKeyWord)) {
                loadNewsFeedData("search", flag);
            } else if (!TextUtil.isEmptyString(mstrChannelId))
                loadNewsFeedData("recommend", flag);
            startTopRefresh();
        } else {
            stopRefresh();
            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
                mHomeRetry.setVisibility(View.VISIBLE);
            } else {
                mHomeRetry.setVisibility(View.GONE);
            }
            mAdapter.setNewsFeed(newsFeeds);
            mAdapter.notifyDataSetChanged();
            mlvNewsFeed.onRefreshComplete();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
            showChangeTextSizeView();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }


    NewsFeedFgtPopWindow mNewsFeedFgtPopWindow;

    public void setNewsFeedFgtPopWindow(NewsFeedFgtPopWindow mNewsFeedFgtPopWindow) {
        this.mNewsFeedFgtPopWindow = mNewsFeedFgtPopWindow;
    }

    public interface NewsFeedFgtPopWindow {
        public void showPopWindow(int x, int y, String pubName, NewsFeedAdapter mAdapter);
    }

    private void showChangeTextSizeView() {
        if (mstrChannelId.equals("1") && mFlag == false)
            if (mChangeTextSizePopWindow == null) {
                mChangeTextSizePopWindow = new ChangeTextSizePopupWindow(getActivity());
                mChangeTextSizePopWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mArrNewsFeed!=null&&bgLayout.getVisibility()==View.VISIBLE) {
            bgLayout.setVisibility(View.GONE);
            mAdapter.setNewsFeed(mArrNewsFeed);
        }
        mAdapter.notifyDataSetChanged();
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommonConstant.CHANGE_TEXT_ACTION.equals(intent.getAction())) {
                int size = intent.getIntExtra("textSize", CommonConstant.TEXT_SIZE_NORMAL);
                mSharedPreferences.edit().putInt("textSize", size).commit();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addSP(ArrayList<NewsFeed> result) {
        ArrayList<UploadLogDataEntity> uploadLogDataEntities = new ArrayList<UploadLogDataEntity>();
        for (NewsFeed bean : result) {
            UploadLogDataEntity uploadLogDataEntity = new UploadLogDataEntity();
            uploadLogDataEntity.setNid(bean.getUrl());
            uploadLogDataEntity.setTid(bean.getTitle());//需要改成typeID
            uploadLogDataEntity.setCid(bean.getChannelId());
            uploadLogDataEntities.add(uploadLogDataEntity);
        }
        int saveNum = SharedPreManager.upLoadLogSaveList(mstrUserId, CommonConstant.UPLOAD_LOG_MAIN, uploadLogDataEntities);
        Logger.d("aaa", "主页的数据====" + SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN));
        if (saveNum > 200) {
            RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
            String url = HttpConstant.URL_UPLOAD_LOG + "uid=" + mstrUserId + "&clas=1" +
                    "&data=" + TextUtil.getBase64(SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN));
            Logger.d("aaa", "url===" + url);
            UpLoadLogRequest<String> request = new UpLoadLogRequest<String>(Request.Method.GET, String.class, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SharedPreManager.upLoadLogDelter(CommonConstant.UPLOAD_LOG_MAIN);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(request);
        }
    }

}