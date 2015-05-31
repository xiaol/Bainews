/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.news.yazhidao.pages;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.TimeFeed;
import com.news.yazhidao.listener.TimeOutAlarmUpdateListener;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.receiver.TimeoOutAlarmReceiver;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.widget.LetterSpacingTextView;
import com.news.yazhidao.widget.LoginPopupWindow;
import com.news.yazhidao.widget.ProgressWheel;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.TextViewVertical;
import com.news.yazhidao.widget.TimePopupWindow;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewsFeedFragment extends Fragment implements TimePopupWindow.IUpdateUI, TimeOutAlarmUpdateListener {

    private static final String ARG_POSITION = "position";
    private int position;

    //JazzyListView
    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    private Map<String, Integer> mEffectMap;
    private int mCurrentTransitionEffect = JazzyHelper.SLIDE_IN;

    //打开详情页时，带过去的url地址
    public static String KEY_URL = "url";
    //打开其他观点时，带到详情页的参数，标示从哪儿进入的详情页
    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String VALUE_NEWS_SOURCE = "other_view";
    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    private String mCurrentDate, mCurrentType;
    private JazzyListView lv_news;
    private MyAdapter list_adapter;
    private TextView tv_stroke1;
    private TextView tv_stroke2;
    private LinearLayout ll_no_network;
    private NewsFeedReceiver rt;
    private boolean isClick = false;

    private ArrayList<NewsFeed.Source> sourceList = new ArrayList<NewsFeed.Source>();
    private int color = new Color().parseColor("#55ffffff");
    private ViewHolder holder = null;
    private ViewHolder2 holder2 = null;
    private ViewHolder3 holder3 = null;
    private int height = 0;
    private int width = 0;
    private String[] images;
    private int contentSize = 0;
    private int contentSize2 = 0;
    private TextViewExtend tv_title;
    private LoadingLayout mylayout;
    private TimeFeed mCurrentTimeFeed;
    private long mTotalTime, mUpdateTime, mLastTime, mCurrentTime;
    private ProgressWheel mNewsFeedProgressWheel;
    private View mNewsFeedProgressWheelWrapper;
    private Context mContext;

    //listview重新布局刷新界面的时候是否需要动画
    private boolean mIsNeedAnim = true;
    //是否第一次执行隐藏banner动画
    private boolean mIsFistAnim = true;
    private boolean refresh_flag = false;
    private boolean top_flag = false;
    private boolean bottom_flag = false;
    //将在下拉显示的新闻数据
    private ArrayList<NewsFeed> mMiddleNewsArr = new ArrayList<>();
    //将在当前显示的新闻数据
    private ArrayList<NewsFeed> mUpNewsArr = new ArrayList<>();
    //将在上拉显示的新闻数据
    private ArrayList<NewsFeed> mDownNewsArr = new ArrayList<>();
    private ImageLoaderHelper imageLoader;
    private Handler mHandler = new Handler();

    public static NewsFeedFragment newInstance(int position) {
        NewsFeedFragment f = new NewsFeedFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        position = getArguments().getInt(ARG_POSITION);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        GlobalParams.maxWidth = width;
        GlobalParams.maxHeight = (int) (height * 0.27);
        GlobalParams.screenWidth = width;
        GlobalParams.screenHeight = height;
        imageLoader = new ImageLoaderHelper(mContext);
        TimeoOutAlarmReceiver.setListener(this);

        rt = new NewsFeedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("sendposition");
        mContext.registerReceiver(rt, filter);

    }

    @Override
    public void onDestroy() {
        if (rt != null) {
            mContext.unregisterReceiver(rt);
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_news, container, false);
        ViewCompat.setElevation(rootView, 50);


        View mHomeAtyLeftMenuWrapper = rootView.findViewById(R.id.mHomeAtyLeftMenuWrapper);
        View mHomeAtyRightMenuWrapper = rootView.findViewById(R.id.mHomeAtyRightMenuWrapper);
        final ImageView mHomeAtyRightMenu = (ImageView) rootView.findViewById(R.id.mHomeAtyRightMenu);
        mHomeAtyRightMenuWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginPopupWindow window1 = new LoginPopupWindow(mContext);
                window1.showAtLocation(((HomeAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });

        mHomeAtyLeftMenuWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClick) {
                    mCurrentTime = System.currentTimeMillis();
                    long updateTime = mUpdateTime - (mCurrentTime - mLastTime);
                    RelativeLayout screenRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.screen_RelativeLayout);
                    TimePopupWindow m_ppopupWindow = new TimePopupWindow((HomeAty) mContext, null, mCurrentTimeFeed, updateTime, mTotalTime, NewsFeedFragment.this);
                    m_ppopupWindow.setDateAndType(mCurrentDate, mCurrentType);
                    m_ppopupWindow.setAnimationStyle(R.style.AnimationAlpha);
                    m_ppopupWindow.showAtLocation(((HomeAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);
                }
            }
        });
        mNewsFeedProgressWheel = (ProgressWheel) rootView.findViewById(R.id.mNewsFeedProgressWheel);
        mNewsFeedProgressWheelWrapper = rootView.findViewById(R.id.mNewsFeedProgressWheelWrapper);
        mNewsFeedProgressWheel.spin();

        //添加umeng更新
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mContext != null)
                    UmengUpdateAgent.update(mContext);
            }
        }, 2000);

        tv_title = (TextViewExtend) rootView.findViewById(R.id.tv_title);
        ll_no_network = (LinearLayout) rootView.findViewById(R.id.ll_no_network);
        ll_no_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.checkNetWork(mContext)) {

                    lv_news.setVisibility(View.VISIBLE);
                    ll_no_network.setVisibility(View.GONE);

                    loadNewsData(1);
                } else {
                    lv_news.setVisibility(View.GONE);
                    ll_no_network.setVisibility(View.VISIBLE);
                }
            }
        });

        lv_news = (JazzyListView) rootView.findViewById(R.id.lv_news);
        lv_news.getRefreshableView().setDivider(null);
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (view.getLastVisiblePosition() == totalItemCount - 1) {
                    if (NetUtil.checkNetWork(mContext)) {
                        lv_news.setVisibility(View.VISIBLE);
                        ll_no_network.setVisibility(View.GONE);
                        showNextDownNews();
                    } else {
                        lv_news.setVisibility(View.GONE);
                        ll_no_network.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        mylayout = lv_news.getFooterLayout();

        list_adapter = new MyAdapter();
        lv_news.setAdapter(list_adapter);
        lv_news.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lv_news.setPullLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
        lv_news.setPullLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
        lv_news.setRefreshingLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
        lv_news.setRefreshingLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
        lv_news.setReleaseLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
        lv_news.setReleaseLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);

        lv_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (NetUtil.checkNetWork(mContext)) {
                    lv_news.setVisibility(View.VISIBLE);
                    ll_no_network.setVisibility(View.GONE);
                    showNextUpNews();
                } else {
                    lv_news.setVisibility(View.GONE);
                    ll_no_network.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            }
        });

        setupJazziness(mCurrentTransitionEffect);
        loadData();

        return rootView;
    }

    /**
     * 上拉加载时显示一条数据
     */
    private void showNextDownNews() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                lv_news.onRefreshComplete();
            }
        });

        for (int i = 0; i < 3; i++) {
            if (mDownNewsArr != null && mDownNewsArr.size() > 0) {
                NewsFeed _NewsFeed = mDownNewsArr.get(mDownNewsArr.size() - 1);
                if (mDownNewsArr.size() == 1) {
                    _NewsFeed.setBottom_flag(true);
                    if (mUpNewsArr.size() > 0) {
                        lv_news.setMode2(PullToRefreshBase.Mode.PULL_FROM_START, 1);
                    } else {
                        lv_news.setMode2(PullToRefreshBase.Mode.DISABLED, 1);
                    }

                }
                mMiddleNewsArr.add(_NewsFeed);
                mDownNewsArr.remove(mDownNewsArr.size() - 1);

                lv_news.setPullLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
                lv_news.setPullLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                lv_news.setRefreshingLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
                lv_news.setRefreshingLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                lv_news.setReleaseLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                lv_news.setReleaseLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);

            }
        }

        //更新ui
        list_adapter.notifyDataSetChanged();
    }


    /**
     * 下拉刷新时显示一条数据
     */
    private void showNextUpNews() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                lv_news.onRefreshComplete();
            }
        });
        if (mUpNewsArr != null && mUpNewsArr.size() > 0) {
            ListView listView = lv_news.getRefreshableView();
            TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -lv_news.getScrollY(), height * 0.4f + DensityUtil.dip2px(mContext, 20));
            localTranslateAnimation.setDuration(300);
            listView.setScrollY(0);
            listView.clearAnimation();
            listView.startAnimation(localTranslateAnimation);
            localTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (this) {
                                NewsFeed _NewsFeed = mUpNewsArr.get(mUpNewsArr.size() - 1);
                                if (mUpNewsArr.size() <= 1) {
                                    _NewsFeed.setTop_flag(true);
                                    lv_news.setMode(PullToRefreshBase.Mode.DISABLED);
                                }
                                mMiddleNewsArr.add(0, _NewsFeed);
                                mUpNewsArr.remove(mUpNewsArr.size() - 1);
                                GlobalParams.split_index_bottom++;
                                lv_news.setPullLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
                                lv_news.setPullLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                                lv_news.setRefreshingLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
                                lv_news.setRefreshingLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                                lv_news.setReleaseLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                                lv_news.setReleaseLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);

                                list_adapter.notifyDataSetChanged();

                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    protected void loadData() {
        if (NetUtil.checkNetWork(mContext)) {
            ll_no_network.setVisibility(View.GONE);
            lv_news.setMode(PullToRefreshBase.Mode.DISABLED);
            loadNewsData(1);
        } else {
            lv_news.setVisibility(View.GONE);
            ll_no_network.setVisibility(View.VISIBLE);
        }
        //获取当前更新的相关信息
        getUpdateParams();
    }


    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        lv_news.setTransitionEffect(mCurrentTransitionEffect);
    }

    private boolean misRefresh = false;

    private void getUpdateParams() {

        isClick = false;

        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_REFRESH_TIME, NetworkRequest.RequestMethod.GET);
        _Request.setCallback(new JsonCallback<TimeFeed>() {

            @Override
            public void success(TimeFeed result) {

                isClick = true;

                misRefresh = false;
                mCurrentTimeFeed = result;
                mTotalTime = Long.valueOf(mCurrentTimeFeed.getNext_update_freq());
                mUpdateTime = Long.valueOf(mCurrentTimeFeed.getNext_upate_time());
                if (mCurrentTimeFeed.getNext_update_type().equals("0")) {
                    mCurrentType = "1";
                    mCurrentDate = mCurrentTimeFeed.getHistory_date().get(2);
                } else {
                    mCurrentType = "0";
                    mCurrentDate = mCurrentTimeFeed.getHistory_date().get(3);
                }
                mLastTime = System.currentTimeMillis();
                alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext, TimeoOutAlarmReceiver.class);
                intent.setAction("updateUI");
                pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + Long.valueOf(mCurrentTimeFeed.getNext_upate_time()), pendingIntent);
            }

            @Override
            public void failed(MyAppException exception) {

                isClick = true;

                String isMorning = DateUtil.getMorningOrAfternoon(System.currentTimeMillis());
                if (isMorning != null && isMorning.equals("晚间")) {
                    mCurrentType = "1";
                } else {
                    mCurrentType = "0";
                }
                mTotalTime = 1000 * 60 * 60 * 12;
                mLastTime = System.currentTimeMillis();
                mUpdateTime = 24894000;
                Logger.e("tag", exception.getMessage());
            }
        }.setReturnType(new TypeToken<TimeFeed>() {
        }.getType()));
        _Request.execute();
    }

    static AlarmManager alarmManager;
    static PendingIntent pendingIntent;

    @Override
    public void refreshUI(String date, String type) {
        mCurrentDate = date;
        mCurrentType = type;
        String url = HttpConstant.URL_GET_NEWS_LIST + "?date=" + date + "&type=" + type;
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {

            public void success(ArrayList<NewsFeed> result) {
                if (result != null) {
                    //分别填充3个数据源
                    inflateDataInArrs(result);
                    list_adapter.notifyDataSetChanged();
                }
                lv_news.onRefreshComplete();
            }

            public void failed(MyAppException exception) {
                lv_news.onRefreshComplete();
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        request.execute();
        if (misRefresh) {
            //获取当前更新的相关信息
            getUpdateParams();
        }
    }

    @Override
    public void updateUI(Intent intent) {
        if (intent.getAction().equals("updateUI")) {
            misRefresh = true;
            if (alarmManager != null)
                alarmManager.cancel(pendingIntent);
            //更新数据
            if (mCurrentTimeFeed != null)
                refreshUI(mCurrentTimeFeed.getHistory_date().get(3), mCurrentTimeFeed.getNext_update_type());
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mMiddleNewsArr != null && mMiddleNewsArr.size() > 0)
                return mMiddleNewsArr.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return mMiddleNewsArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            contentSize = 0;
            contentSize2 = 0;
            if (!refresh_flag) {
                lv_news.setPullLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
                lv_news.setPullLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                lv_news.setRefreshingLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
                lv_news.setRefreshingLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                lv_news.setReleaseLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
                lv_news.setReleaseLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
            }

            final NewsFeed feed = mMiddleNewsArr.get(position);

            if ("400".equals(feed.getSpecial())) {
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = View.inflate(mContext, R.layout.ll_news_item2, null);
                    holder.rl_title_content = (RelativeLayout) convertView.findViewById(R.id.rl_title_content);
                    holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);
                    holder.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);
                    holder.ll_top_line = (LinearLayout) convertView.findViewById(R.id.ll_top_line);
                    holder.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                    holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                    holder.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                    holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                    holder.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday);
                    holder.ll_time_item = (LinearLayout) convertView.findViewById(R.id.ll_time_item);
                    convertView.setTag(holder);
                } else {
                    if (convertView.getTag() != null && ViewHolder.class == convertView.getTag().getClass()) {
                        holder = (ViewHolder) convertView.getTag();
                        holder.ll_source_content.removeAllViews();
                        holder.rl_bottom_mark.setVisibility(View.GONE);
                        holder.ll_time_item.setVisibility(View.GONE);
                    } else {
                        holder = new ViewHolder();
                        convertView = View.inflate(mContext, R.layout.ll_news_item2, null);
                        holder.rl_title_content = (RelativeLayout) convertView.findViewById(R.id.rl_title_content);
                        holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                        holder.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);

                        holder.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);
                        holder.ll_top_line = (LinearLayout) convertView.findViewById(R.id.ll_top_line);
                        holder.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                        holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                        holder.ll_time_item = (LinearLayout) convertView.findViewById(R.id.ll_time_item);
                        holder.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                        holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                        holder.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday);
                        convertView.setTag(holder);
                    }
                }

                String title = feed.getTitle();

                holder.tv_title.setText(title, TextView.BufferType.SPANNABLE);
//                holder.tv_title.setFontSpacing(3);

                holder.tv_interests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        intent.putExtra(KEY_NEWS_SOURCE, VALUE_NEWS_SOURCE);
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                if (feed.getCategory() != null) {
                    holder.tv_news_category.setText(feed.getCategory());
                    holder.tv_news_category.setFontSpacing(5);
                    TextUtil.setNewsBackGroundRight(holder.tv_news_category, feed.getCategory());
//                    TextUtil.setViewCompatBackground(feed.getCategory(), mylayout);
                }

                holder.tv_interests.setText(feed.getOtherNum() + "家观点");

                holder.rl_title_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });
                //点击其他观点的点击事件
                holder.ll_source_interest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //uemng statistic click other viewpoint
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_ONCLICK_OTHER_VIEWPOINT);
                    }
                });
                if (feed != null && feed.getOtherNum() != null) {
                    if (Integer.parseInt(feed.getOtherNum()) == 0) {
                        holder.ll_source_interest.setVisibility(View.GONE);
                    } else {
                        holder.ll_source_interest.setVisibility(View.VISIBLE);
                    }
                }

                //如果是最后一条新闻显示阅读更多布局
                if (feed.isBottom_flag()) {
                    holder.rl_bottom_mark.setVisibility(View.VISIBLE);
                }

                if (feed.isTime_flag()) {
                    holder.ll_time_item.setVisibility(View.VISIBLE);

                    if (mCurrentDate == null) {
                        long time = System.currentTimeMillis();
                        Date date = new Date(time);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = format.format(date);
                        String myDate = DateUtil.getMyDate(currentDate);
                        holder.tv_date.setText(myDate);

                        //判断上午还是下午
                        String am = DateUtil.getMorningOrAfternoon(time);

                        //判断是星期几
                        String weekday = "";
                        try {
                            weekday = DateUtil.dayForWeek(currentDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder.tv_weekday.setText(weekday + "|" + am);
                    } else {


                        String myDate = DateUtil.getMyDate(mCurrentDate);
                        holder.tv_date.setText(myDate);

                        String am = "";

                        //判断上午还是下午
                        if ("0".equals(mCurrentType)) {
                            am = "早间";
                        } else {
                            am = "晚间";
                        }

                        //判断是星期几
                        String weekday = "";
                        try {
                            weekday = DateUtil.dayForWeek(mCurrentDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder.tv_weekday.setText(weekday + "|" + am);
                    }
                }

                holder.rl_bottom_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv_news.getRefreshableView().setSelection(GlobalParams.split_index_bottom + 1);
                    }
                });


                if (feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                    ImageLoaderHelper.dispalyImage(mContext, feed.getImgUrl(), holder.iv_title_img, holder.iv_title_img);
                }

                final long start = System.currentTimeMillis();

                sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();
                setContentParams(holder.ll_source_content, sourceList.size());

                //解析新闻来源观点数据
                if (sourceList != null && sourceList.size() > 0) {
                    for (int a = 0; a < sourceList.size(); a++) {


                        final NewsFeed.Source source = sourceList.get(a);

                        RelativeLayout ll_souce_view = (RelativeLayout) View.inflate(mContext, R.layout.lv_source_item2, null);
                        ll_souce_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(mContext, NewsDetailWebviewAty.class);
                                intent.putExtra(KEY_URL, source.getUrl());
                                startActivity(intent);
                                //umeng statistic onclick url below the head news
                                HashMap<String, String> _MobMap = new HashMap<>();
                                _MobMap.put("resource_site_name", source.getSourceSitename());
                                MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS, _MobMap);
                            }
                        });
                        TextView iv_source = (TextView) ll_souce_view.findViewById(R.id.iv_source);
                        iv_source.setBackgroundColor(new Color().parseColor("#4fb5ea"));
                        iv_source.setText(a + 1 + "");
                        TextViewExtend tv_news_source = (TextViewExtend) ll_souce_view.findViewById(R.id.tv_news_source);
                        ImageView iv_combine_line = (ImageView) ll_souce_view.findViewById(R.id.iv_combine_line);

                        setLineVisibility(sourceList, iv_combine_line, contentSize);

                        if (source != null) {

                            String source_name = source.getSourceSitename();

                            String finalText = "";
                            String source_title = source.getTitle();
//                            source_title = "<font size =\"7\" color =\"red\">" + source_title + "</font>";
                            String source_name_font = "";
                            source_title = "<font color =\"#000000\">" + "<big>" + source_title + "</big>" + "</font>";
                            if (source_name != null) {

                                if (source.getUser() != null && !"".equals(source.getUser())) {
                                    source_name_font = "<font color =\"#7d7d7d\">" + source.getUser() + "</font>" + ": ";
                                    finalText = source_name_font + source_title;
                                    tv_news_source.setText(Html.fromHtml(finalText));
                                } else {
                                    source_name_font = "<font color =\"#7d7d7d\">" + source_name + "</font>" + ": ";
                                    finalText = source_name_font + source_title;
                                    tv_news_source.setText(Html.fromHtml(finalText));
                                }

                            } else {
                                String anonyStr = "<font color =\"#7d7d7d\">" + "匿名报道:" + "</font>" + ": ";
                                finalText = anonyStr + source_title;
                                tv_news_source.setText(Html.fromHtml(finalText));
                            }

                        }

                        if (contentSize < 3) {
                            holder.ll_source_content.addView(ll_souce_view);
                            contentSize++;
                        }
                    }
                }
            } else if ("1".equals(feed.getSpecial())) {

                if (convertView == null) {
                    holder2 = new ViewHolder2();
                    convertView = View.inflate(mContext, R.layout.ll_news_item_top, null);
                    holder2.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                    holder2.tv_title = (TextViewVertical) convertView.findViewById(R.id.tv_title);
                    holder2.tv_news_category = (TextView) convertView.findViewById(R.id.tv_news_category);
                    holder2.fl_news_content = (FrameLayout) convertView.findViewById(R.id.fl_news_content);
                    holder2.rl_top_mark = (RelativeLayout) convertView.findViewById(R.id.rl_top_mark);
                    holder2.rl_divider_top = (RelativeLayout) convertView.findViewById(R.id.rl_divider_top);
                    holder2.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                    holder2.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday);
                    convertView.setTag(holder2);
                } else {
//                    if (ViewHolder2.class == convertView.getTag().getClass()) {
//                        holder2 = (ViewHolder2) convertView.getTag();
//                        holder2.ll_bottom_item.setVisibility(View.GONE);
//                        holder2.rl_top_mark.setVisibility(View.GONE);
//                    } else {
                    holder2 = new ViewHolder2();
                    convertView = View.inflate(mContext, R.layout.ll_news_item_top, null);
                    holder2.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                    holder2.tv_title = (TextViewVertical) convertView.findViewById(R.id.tv_title);
                    holder2.tv_news_category = (TextView) convertView.findViewById(R.id.tv_news_category);
                    holder2.fl_news_content = (FrameLayout) convertView.findViewById(R.id.fl_news_content);

                    ViewGroup.LayoutParams layoutParams = holder2.fl_news_content.getLayoutParams();
                    layoutParams.width = width;
                    layoutParams.height = (int) (height * 0.40);
                    holder2.fl_news_content.setLayoutParams(layoutParams);
                    holder2.rl_top_mark = (RelativeLayout) convertView.findViewById(R.id.rl_top_mark);
                    holder2.rl_divider_top = (RelativeLayout) convertView.findViewById(R.id.rl_divider_top);
                    holder2.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                    holder2.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday);
                    convertView.setTag(holder2);
//                    }
                }

                ViewGroup.LayoutParams layoutParams = holder2.iv_title_img.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = (int) (height * 0.40);
                holder2.iv_title_img.setLayoutParams(layoutParams);

                String title = feed.getTitle();

                holder2.tv_title.setText(title);
                int textsize = DensityUtil.dip2px(mContext, 18);
                holder2.tv_title.setTextSize(textsize);
                holder2.tv_title.setTextColor(new Color().parseColor("#ffffff"));
                holder2.tv_title.setLineWidth(DensityUtil.dip2px(mContext, 22));
                holder2.tv_title.setShadowLayer(4f, 1, 2, new Color().parseColor("#000000"));
                holder2.tv_news_category.setText(feed.getCategory());
                TextUtil.setViewCompatBackground(feed.getCategory(), mylayout);

                TextUtil.setTextBackGround(holder2.tv_news_category, feed.getCategory());

                if (feed.isTime_flag()) {
                    holder2.rl_divider_top.setVisibility(View.GONE);
                    if (mCurrentDate == null) {
                        long time = System.currentTimeMillis();
                        Date date = new Date(time);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String currentDate = format.format(date);
                        String myDate = DateUtil.getMyDate(currentDate);
                        holder2.tv_date.setText(myDate);

                        //判断上午还是下午
                        String am = DateUtil.getMorningOrAfternoon(time);

                        //判断是星期几
                        String weekday = "";
                        try {
                            weekday = DateUtil.dayForWeek(currentDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder2.tv_weekday.setText(weekday + "|" + am);
                    } else {


                        String myDate = DateUtil.getMyDate(mCurrentDate);
                        holder2.tv_date.setText(myDate);

                        String am = "";

                        //判断上午还是下午
                        if ("0".equals(mCurrentType)) {
                            am = "早间";
                        } else {
                            am = "晚间";
                        }

                        //判断是星期几
                        String weekday = "";
                        try {
                            weekday = DateUtil.dayForWeek(mCurrentDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        holder2.tv_weekday.setText(weekday + "|" + am);
                    }
                }

                if (feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                    ImageLoaderHelper.dispalyImage(mContext, feed.getImgUrl(), holder2.iv_title_img, holder2.tv_title);
//                    ImageLoaderHelper.dispalyImage(HomeAty.this,feed.getImgUrl(),holder2.iv_title_img);
                } else {
                    holder2.tv_title.setBackgroundColor(color);
                }

                if (feed.isTop_flag()) {
                    holder2.rl_top_mark.setVisibility(View.VISIBLE);
                }
                holder2.rl_top_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv_news.getRefreshableView().setSelection(mMiddleNewsArr.size());
                    }
                });

                holder2.iv_title_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });
            } else if ("9".equals(feed.getSpecial())) {

                images = feed.getImgUrl_ex();


                holder3 = new ViewHolder3();

                if (images.length == 2) {

                    convertView = View.inflate(mContext, R.layout.ll_news_card2, null);
                    holder3.ll_image_list = (LinearLayout) convertView.findViewById(R.id.ll_image_list);
                    holder3.image_card1 = (ImageView) convertView.findViewById(R.id.image_card1);
                    holder3.image_card2 = (ImageView) convertView.findViewById(R.id.image_card2);
                    holder3.tv_title = (LetterSpacingTextView) convertView.findViewById(R.id.tv_title);
                    holder3.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);
                    holder3.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                    holder3.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                    holder3.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                    holder3.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);
                    holder3.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                    holder3.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday);
                    holder3.ll_time_item = (LinearLayout) convertView.findViewById(R.id.ll_time_item);
                } else {
                    convertView = View.inflate(mContext, R.layout.ll_news_card, null);

                    holder3.ll_image_list = (LinearLayout) convertView.findViewById(R.id.ll_image_list);
                    holder3.image_card1 = (ImageView) convertView.findViewById(R.id.image_card1);
                    holder3.image_card2 = (ImageView) convertView.findViewById(R.id.image_card2);
                    holder3.image_card3 = (ImageView) convertView.findViewById(R.id.image_card3);
                    holder3.tv_title = (LetterSpacingTextView) convertView.findViewById(R.id.tv_title);
                    holder3.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);
                    holder3.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                    holder3.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                    holder3.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                    holder3.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);
                    holder3.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
                    holder3.tv_weekday = (TextView) convertView.findViewById(R.id.tv_weekday);
                    holder3.ll_time_item = (LinearLayout) convertView.findViewById(R.id.ll_time_item);
                }

                convertView.setTag(holder3);

                String title = feed.getTitle();

                holder3.tv_title.setText(title);
                holder3.tv_title.setFontSpacing(1);
                holder3.tv_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra("url", feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                holder3.tv_interests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra("url", feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                if (feed.getCategory() != null) {
                    holder3.tv_news_category.setText(feed.getCategory());
                    holder3.tv_news_category.setFontSpacing(5);
                    TextUtil.setNewsBackGroundRight(holder3.tv_news_category, feed.getCategory());
                    TextUtil.setViewCompatBackground(feed.getCategory(), mylayout);
                }

                holder3.tv_interests.setText(feed.getOtherNum() + "家观点");

                holder3.tv_interests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        intent.putExtra(KEY_NEWS_SOURCE, VALUE_NEWS_SOURCE);
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                holder3.ll_image_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, NewsDetailAty.class);
                        intent.putExtra("url", feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                //点击其他观点的点击事件
                holder.ll_source_interest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //uemng statistic click other viewpoint
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_ONCLICK_OTHER_VIEWPOINT);
                    }
                });

                if (feed != null && feed.getOtherNum() != null) {
                    if (Integer.parseInt(feed.getOtherNum()) == 0) {
                        holder3.ll_source_interest.setVisibility(View.GONE);
                    } else {
                        holder3.ll_source_interest.setVisibility(View.VISIBLE);
                    }
                }

                //如果是最后一条新闻显示阅读更多布局
                if (feed.isBottom_flag()) {
                    holder3.rl_bottom_mark.setVisibility(View.VISIBLE);
                }

                if (feed.isTime_flag()) {
                    holder3.ll_time_item.setVisibility(View.VISIBLE);
                }
                holder3.rl_bottom_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv_news.getRefreshableView().setSelection(GlobalParams.split_index_bottom + 1);
                    }
                });

                if (images.length == 2) {
                    if (holder3.image_card1 != null) {
                        ImageLoaderHelper.dispalyImage(mContext, images[0], holder3.image_card1);
                    }
                    if (holder3.image_card2 != null) {
                        ImageLoaderHelper.dispalyImage(mContext, images[1], holder3.image_card2);
                    }
                } else {

                    if (holder3.image_card1 != null) {
                        ImageLoaderHelper.dispalyImage(mContext, images[0], holder3.image_card1);
                    }
                    if (holder3.image_card2 != null) {
                        ImageLoaderHelper.dispalyImage(mContext, images[1], holder3.image_card2);
                    }

                    if (holder3.image_card3 != null) {
                        ImageLoaderHelper.dispalyImage(mContext, images[2], holder3.image_card3);
                    }
                }

                final long start = System.currentTimeMillis();

                sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();

                //解析新闻来源观点数据
                if (sourceList != null && sourceList.size() > 0) {

                    for (int a = 0; a < sourceList.size(); a++) {


                        final NewsFeed.Source source = sourceList.get(a);

                        LinearLayout ll_souce_view = (LinearLayout) View.inflate(mContext, R.layout.lv_source_item, null);
                        ll_souce_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(mContext, NewsDetailWebviewAty.class);
                                intent.putExtra("url", source.getUrl());
                                startActivity(intent);
                                //umeng statistic onclick url below the head news
                                HashMap<String, String> _MobMap = new HashMap<>();
                                _MobMap.put("resource_site_name", source.getSourceSitename());
                                MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS, _MobMap);
                            }
                        });
                        ImageView iv_source = (ImageView) ll_souce_view.findViewById(R.id.iv_source);
                        TextViewExtend tv_news_source = (TextViewExtend) ll_souce_view.findViewById(R.id.tv_news_source);

                        if (source != null) {

                            String source_name = source.getSourceSitename();

                            String finalText = "";
                            String source_title = source.getTitle();
//                            source_title = "<font size =\"7\" color =\"red\">" + source_title + "</font>";
                            String source_name_font = "";
                            source_title = "<font color =\"#000000\">" + "<big>" + source_title + "</big>" + "</font>";
                            if (source_name != null) {

                                if (source.getUser() != null && !"".equals(source.getUser())) {

                                    source_name_font = "<font color =\"#7d7d7d\">" + source.getUser() + "</font>" + ": ";
                                    finalText = source_name_font + source_title;
                                    tv_news_source.setText(Html.fromHtml(finalText));
                                } else {
                                    source_name_font = "<font color =\"#7d7d7d\">" + source_name + "</font>" + ": ";
                                    finalText = source_name_font + source_title;
                                    tv_news_source.setText(Html.fromHtml(finalText));
                                }

                                TextUtil.setResourceSiteIcon(iv_source, source_name);

                            } else {
                                String anonyStr = "<font color =\"#7d7d7d\">" + "匿名报道:" + "</font>" + ": ";
                                finalText = anonyStr + source_title;
                                tv_news_source.setText(Html.fromHtml(finalText));
                            }

                        }

                        if (contentSize2 < 6) {
                            holder3.ll_source_content.addView(ll_souce_view);
                            contentSize2++;
                        }
                    }
                }

            }

            //下拉时给显示的item添加动画
            if (position == 0 && mIsNeedAnim) {
                convertView.clearAnimation();
                convertView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.aty_list_item_in));
                return convertView;
            }
            //上拉时给显示的item添加动画
//            if (position > mMiddleNewsArr.size() - 2 && mIsNeedAnim) {
//                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                convertView.measure(View.MeasureSpec.makeMeasureSpec(lv_news.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                int height = convertView.getHeight() == 0 ? convertView.getMeasuredHeight() : convertView.getHeight();
//                ViewPropertyAnimator animator = convertView.animate()
//                        .setDuration(800)
//                        .setInterpolator(new AccelerateDecelerateInterpolator());
//                convertView.setTranslationY(height / 4);
//                animator.translationYBy(-height / 4);
//                animator.start();
//
//            }
            return convertView;
        }

    }

    private void setContentParams(LinearLayout ll_source_content, int i) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.rl_title_content);
        params.setMargins(30, 24, 20, 20);

        switch (i) {
            case 0:
                params.height = DensityUtil.dip2px(mContext, 0);
                break;

            case 1:
                params.height = DensityUtil.dip2px(mContext, 45);
                break;

            case 2:
                params.height = DensityUtil.dip2px(mContext, 95);
                break;

            case 3:
                params.height = DensityUtil.dip2px(mContext, 140);
                break;

            default:
                params.height = DensityUtil.dip2px(mContext, 140);
                break;

        }

        ll_source_content.setLayoutParams(params);

    }

    private void setLineVisibility(ArrayList<NewsFeed.Source> sourceList, ImageView iv_combine_line, int a) {

        switch (sourceList.size()) {
            case 1:
                iv_combine_line.setVisibility(View.GONE);
                break;

            case 2:
                if (a == 1) {
                    iv_combine_line.setVisibility(View.GONE);
                }
                break;

            case 3:
                if (a >= 2) {
                    iv_combine_line.setVisibility(View.GONE);
                }
                break;

            default:
                if (a >= 2) {
                    iv_combine_line.setVisibility(View.GONE);
                }
                break;

        }

    }

    class ViewHolder {

        ImageView iv_title_img;
        TextView tv_title;
        LetterSpacingTextView tv_news_category;
        LinearLayout ll_source_content;
        LinearLayout ll_source_interest;
        RelativeLayout rl_title_content;
        TextViewExtend tv_interests;
        RelativeLayout rl_bottom_mark;
        LinearLayout ll_top_line;
        LinearLayout ll_time_item;
        TextView tv_date;
        TextView tv_weekday;

    }

    class ViewHolder2 {

        ImageView iv_title_img;
        TextViewVertical tv_title;
        TextView tv_news_category;
        TextView tv_date;
        TextView tv_weekday;
        FrameLayout fl_news_content;
        LinearLayout ll_bottom_item;
        RelativeLayout rl_top_mark;
        RelativeLayout rl_divider_top;
    }

    class ViewHolder3 {
        LinearLayout ll_top_line;
        LinearLayout ll_image_list;
        ImageView image_card1;
        ImageView image_card2;
        ImageView image_card3;
        LetterSpacingTextView tv_title;
        LetterSpacingTextView tv_news_category;
        LinearLayout ll_source_content;
        LinearLayout ll_source_interest;
        TextViewExtend tv_interests;
        LinearLayout ll_time_item;
        TextView tv_date;
        TextView tv_weekday;
        RelativeLayout rl_bottom_mark;

    }

    private void loadNewsData(final int timenews) {

        String url = HttpConstant.URL_GET_NEWS_LIST + "?timenews=" + timenews;
//        String url = "http://121.40.38.56/news/baijia/fetchHom" + "?timenews=" + timenews;
        final long start = System.currentTimeMillis();
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {

            public void success(ArrayList<NewsFeed> result) {

                isClick = true;

                long delta = System.currentTimeMillis() - start;
                Logger.i("ariesy", delta + "");
                if (result != null) {
                    //分别填充3个数据源
                    inflateDataInArrs(result);
                    list_adapter.notifyDataSetChanged();
                }
                lv_news.onRefreshComplete();
                mNewsFeedProgressWheelWrapper.setVisibility(View.GONE);
                mNewsFeedProgressWheel.stopSpinning();
                mNewsFeedProgressWheel.setVisibility(View.GONE);
            }

            public void failed(MyAppException exception) {

                isClick = true;

                lv_news.onRefreshComplete();
                mNewsFeedProgressWheelWrapper.setVisibility(View.GONE);
                mNewsFeedProgressWheel.stopSpinning();
                mNewsFeedProgressWheel.setVisibility(View.GONE);
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        request.execute();
    }

    private void inflateDataInArrs(ArrayList<NewsFeed> result) {

        lv_news.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        int _SplitStartIndex = 0;
        if (result != null && result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                if (!"1".equals(result.get(i).getSpecial())) {
                    _SplitStartIndex = i;
                    break;
                }
            }

            GlobalParams.split_index_top = _SplitStartIndex;

            if (_SplitStartIndex >= 1) {
                NewsFeed feed = result.get(_SplitStartIndex);
                feed.setTime_flag(true);

                mUpNewsArr = new ArrayList<>(result.subList(0, _SplitStartIndex - 1));
                mMiddleNewsArr = new ArrayList<>(result.subList(_SplitStartIndex - 1, _SplitStartIndex + 1));
                mDownNewsArr = new ArrayList<>(result.subList(_SplitStartIndex + 1, result.size()));

                if (mUpNewsArr.size() == 0) {
                    lv_news.setMode(PullToRefreshBase.Mode.DISABLED);
                } else {
                    lv_news.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            } else if (_SplitStartIndex == 0) {

                NewsFeed feed = result.get(_SplitStartIndex);
                feed.setTime_flag(true);
                feed.setTop_flag(true);

                mMiddleNewsArr = new ArrayList<>(result.subList(0, _SplitStartIndex + 2));
                mDownNewsArr = new ArrayList<>(result.subList(_SplitStartIndex + 2, result.size()));
                lv_news.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }
    }

    //获取当前点击分类的新闻
    private void loadNewsFeedData(final int position) {

        String url = HttpConstant.URL_GET_NEWS_LIST + "?channelId=" + position + "&page=1&limit=50";
        final long start = System.currentTimeMillis();
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {

            public void success(ArrayList<NewsFeed> result) {


                long delta = System.currentTimeMillis() - start;
                Logger.i("ariesy", delta + "");
                if (result != null) {
                    //分别填充3个数据源
                    mMiddleNewsArr = result;
                    mDownNewsArr.clear();
                    mUpNewsArr.clear();
                    lv_news.setMode(PullToRefreshBase.Mode.DISABLED);
                    list_adapter.notifyDataSetChanged();
                }

                lv_news.onRefreshComplete();
                mNewsFeedProgressWheelWrapper.setVisibility(View.GONE);
                mNewsFeedProgressWheel.stopSpinning();
                mNewsFeedProgressWheel.setVisibility(View.GONE);
            }

            public void failed(MyAppException exception) {
                lv_news.onRefreshComplete();
                mNewsFeedProgressWheelWrapper.setVisibility(View.GONE);
                mNewsFeedProgressWheel.stopSpinning();
                mNewsFeedProgressWheel.setVisibility(View.GONE);
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        request.execute();
    }

    private class NewsFeedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("sendposition".equals(intent.getAction())) {
                if (GlobalParams.currentCatePos == 0) {
                    loadNewsData(1);
                } else {
                    loadNewsFeedData(GlobalParams.currentCatePos - 1);
                }

                mNewsFeedProgressWheel.setVisibility(View.VISIBLE);
                mNewsFeedProgressWheelWrapper.setVisibility(View.VISIBLE);
                mNewsFeedProgressWheel.spin();
            }
        }
    }


}