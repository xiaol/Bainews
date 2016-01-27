package com.news.yazhidao.pages;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.adcoco.AdcocoUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.umeng.analytics.AnalyticsConfig;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFgt extends Fragment implements Handler.Callback {

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

    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
        }
        rootView = LayoutInflater.inflate(R.layout.activity_news, container, false);
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
                loadData(PULL_DOWN_REFRESH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(PULL_UP_REFRESH);
            }
        });
        mAdapter = new NewsFeedAdapter(getActivity());
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
//        loadData(PULL_DOWN_REFRESH);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mlvNewsFeed.setRefreshing();
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
            }
        }, 800);
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
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("userid", mstrUserId));
        nameValuePairList.add(new BasicNameValuePair("deviceid", mstrDeviceId));
        nameValuePairList.add(new BasicNameValuePair("channelid", mstrChannelId));
        nameValuePairList.add(new BasicNameValuePair("keyword", mstrKeyWord));
        nameValuePairList.add(new BasicNameValuePair("page", mSearchPage + ""));
        mRequest = new NetworkRequest("http://api.deeporiginalx.com/news/baijia/" + url, NetworkRequest.RequestMethod.POST);
        mRequest.setParams(nameValuePairList);
        mRequest.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {
            public void failed(MyAppException paramAnonymousMyAppException) {
                if (TextUtil.isListEmpty(mArrNewsFeed)){
                    ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                    if (TextUtil.isListEmpty(newsFeeds)){
                        mHomeRetry.setVisibility(View.VISIBLE);
                    }else {
                        mHomeRetry.setVisibility(View.GONE);
                        mAdapter.setNewsFeed(newsFeeds);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                stopRefresh();
                mlvNewsFeed.onRefreshComplete();
//                ToastUtil.toastLong("网络不给力,请检查网络....");
            }

            public void success(ArrayList<NewsFeed> result) {
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
                    mNewsFeedDao.insert(result);
                    mAdapter.setNewsFeed(mArrNewsFeed);
                    mAdapter.notifyDataSetChanged();
                } else {
                    //向服务器发送请求,已成功,但是返回结果为null,需要显示重新加载view
                    if (TextUtil.isListEmpty(mArrNewsFeed)){
                        ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
                        if (TextUtil.isListEmpty(newsFeeds)){
                            mHomeRetry.setVisibility(View.VISIBLE);
                        }else {
                            mHomeRetry.setVisibility(View.GONE);
                            mAdapter.setNewsFeed(newsFeeds);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                mlvNewsFeed.onRefreshComplete();
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        mRequest.execute();
    }

    public void loadData(int flag) {
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
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}