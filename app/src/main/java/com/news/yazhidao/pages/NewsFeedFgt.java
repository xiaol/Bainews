package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.volley.FeedRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.adcoco.AdcocoUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.umeng.analytics.AnalyticsConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsFeedFgt extends Fragment implements Handler.Callback {

    public static final String KEY_NEWS_CHANNEL = "key_news_channel";
    public static final String KEY_PUSH_NEWS = "key_push_news";//表示该新闻是后台推送过来的
    public static final String KEY_NEWS_IMG_URL = "key_news_img_url";//确保新闻详情中有一张图
    public static final String KEY_NEWS_TYPE = "KEY_NEWS_TYPE";//新闻类型,是否是大图新闻

    /**
     * 当前fragment 所对应的新闻频道
     */
    public static String KEY_CHANNEL_ID = "key_channel_id";
    public static String KEY_WORD = "key_word";
    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static String KEY_COLLECTION = "key_collection";
    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    public static final int PULL_DOWN_REFRESH = 1;
    private static final int PULL_UP_REFRESH = 2;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed;
    private Context mContext;
    private NetworkRequest mRequest;
    private PullToRefreshListView mlvNewsFeed;
    private View rootView;
    private String mstrDeviceId, mstrUserId, mstrChannelId, mstrKeyWord;
    private NewsFeedDao mNewsFeedDao;
    /**
     * 热词页面加载更多
     */
    private int mSearchPage = 1;
//    private Handler mHandler;
//    private Runnable mRunnable;
//    private boolean mIsFirst = true;
    /**
     * 当前的fragment 是否已经加载过数据
     */
//    private boolean isLoadedData;
    private NewsSaveDataCallBack mNewsSaveCallBack;
    private View mHomeRelative;
    private View mHomeRetry;
    private RelativeLayout bgLayout;
    private boolean  isListRefresh = false;

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
            if(bgLayout.getVisibility() == View.VISIBLE){
                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
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
//        isLoadedData = true;
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
//        mRunnable = new Runnable() {
//            @Override
//            public void run() {
//                mlvNewsFeed.setRefreshing();
//            }
//        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("jigang","requestCode = " + requestCode);
        if (requestCode == NewsFeedAdapter.REQUEST_CODE && data != null){
            String newsId = data.getStringExtra(NewsFeedAdapter.KEY_NEWS_ID);
            Logger.e("jigang","newsid = " + newsId);
            if (!TextUtil.isListEmpty(mArrNewsFeed)){
                for (NewsFeed item : mArrNewsFeed){
                    if (item != null && newsId.equals(item.getNewsId())){
                        item.setRead(true);
                        mNewsFeedDao.update(item);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if(bgLayout.getVisibility() == View.VISIBLE){
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
        mAdapter = new NewsFeedAdapter(getActivity(),this);
        mlvNewsFeed.setAdapter(mAdapter);
        mlvNewsFeed.setEmptyView(View.inflate(mContext, R.layout.listview_empty_view, null));
        setUserVisibleHint(getUserVisibleHint());
        String platform = AnalyticsConfig.getChannel(getActivity());
        if ("adcoco".equals(platform) && !TextUtil.isListEmpty(mArrNewsFeed)) {
            AdcocoUtil.setup(getActivity());
            try {
                new AdcocoUtil().insertAdcoco(mArrNewsFeed, mlvNewsFeed.getRefreshableView(), mArrNewsFeed.size(), -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //load news data

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                mlvNewsFeed.setRefreshing();
                loadData(PULL_DOWN_REFRESH);
                isListRefresh = false;

            }
        }, 800);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.e("jigang", "newsfeedfgt onDestroyView");
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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        FeedRequest<ArrayList<NewsFeed>> feedRequest = new FeedRequest<ArrayList<NewsFeed>>(Request.Method.POST, new TypeToken<ArrayList<NewsFeed>>() {
        }.getType(), "http://api.deeporiginalx.com/news/baijia/" + url, new Response.Listener<ArrayList<NewsFeed>>(){

            @Override
            public void onResponse(final ArrayList<NewsFeed> result) {

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
                            if (mArrNewsFeed != null) {
                                mArrNewsFeed.addAll(result);
                            }
                            break;
                    }
                    if (mNewsSaveCallBack != null) {
                        mNewsSaveCallBack.result(mstrChannelId, mArrNewsFeed);
                    }
                    if (mstrChannelId != null && "TJ0001".equals(mstrChannelId)) {
                        for (NewsFeed newsFeed : result)
                            newsFeed.setChannelId("TJ0001");
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsFeedDao.insert(result);
                        }
                    }).start();
                    mAdapter.setNewsFeed(mArrNewsFeed);
                    mAdapter.notifyDataSetChanged();
                    if(bgLayout.getVisibility() == View.VISIBLE){
                        bgLayout.setVisibility(View.GONE);
                    }
                } else {
                    //向服务器发送请求,已成功,但是返回结果为null,需要显示重新加载view
                    if (TextUtil.isListEmpty(mArrNewsFeed)){
                        ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                        if (TextUtil.isListEmpty(newsFeeds)){
                            mHomeRetry.setVisibility(View.VISIBLE);
                        }else {
                            mArrNewsFeed = newsFeeds;
                            mHomeRetry.setVisibility(View.GONE);
                            mAdapter.setNewsFeed(newsFeeds);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else {
                        mAdapter.setNewsFeed(mArrNewsFeed);
                        mAdapter.notifyDataSetChanged();

                    }
                    if(bgLayout.getVisibility() == View.VISIBLE){
                        bgLayout.setVisibility(View.GONE);
                    }
                }
                mlvNewsFeed.onRefreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (TextUtil.isListEmpty(mArrNewsFeed)){
                    ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                    if (TextUtil.isListEmpty(newsFeeds)){
                        mHomeRetry.setVisibility(View.VISIBLE);
                    }else {
                        mArrNewsFeed = newsFeeds;
                        mHomeRetry.setVisibility(View.GONE);
                        mAdapter.setNewsFeed(newsFeeds);
                        mAdapter.notifyDataSetChanged();
                        if(bgLayout.getVisibility() == View.VISIBLE){
                            bgLayout.setVisibility(View.GONE);
                        }
                    }
                }
                stopRefresh();
                mlvNewsFeed.onRefreshComplete();
            }
        });
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("userid", mstrUserId);
        params.put("deviceid", mstrDeviceId);
        params.put("channelid", mstrChannelId);
        params.put("keyword", mstrKeyWord + "");
        params.put("page", mSearchPage + "");
        feedRequest.setRequestParams(params);
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000,0,0));
        requestQueue.add(feedRequest);
        Logger.e("jigang","deviceid = " +  mstrDeviceId + ",channelid =" +mstrChannelId);
    }

    public void loadData(int flag) {
        if(!isListRefresh){
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
            if (TextUtil.isListEmpty(newsFeeds)){
                mHomeRetry.setVisibility(View.VISIBLE);
            }else {
                mHomeRetry.setVisibility(View.GONE);
            }

            mAdapter.setNewsFeed(newsFeeds);
            mAdapter.notifyDataSetChanged();
            mlvNewsFeed.onRefreshComplete();
            if(bgLayout.getVisibility() == View.VISIBLE){
                bgLayout.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}