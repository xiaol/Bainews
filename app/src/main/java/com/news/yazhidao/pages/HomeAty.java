package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.widget.LetterSpacingTextView;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.TextViewVertical;
import com.news.yazhidao.widget.TimePopupWindow;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class HomeAty extends BaseActivity {

    //打开详情页时，带过去的url地址
    public static String KEY_URL="url";
    //打开其他观点时，带到详情页的参数，标示从哪儿进入的详情页
    public static String KEY_NEWS_SOURCE="key_news_source";
    public static String VALUE_NEWS_SOURCE="other_view";

    private PullToRefreshListView lv_news;
    private MyAdapter list_adapter;
    private LinearLayout ll_title;
    private LinearLayout ll_no_network;
    private long mLastPressedBackKeyTime;
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
    private ImageLoaderHelper imageLoader;

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
    private Handler mHandler = new Handler();
    @Override
    protected void setContentView() {

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        GlobalParams.maxWidth = width;
        GlobalParams.maxHeight = (int) (height * 0.27);
        GlobalParams.screenWidth = width;
        GlobalParams.screenHeight = height;
        GlobalParams.context = HomeAty.this;

        setContentView(R.layout.activity_news);

        imageLoader = new ImageLoaderHelper(HomeAty.this);
    }

    @Override
    protected void initializeViews() {
        ImageView ivTimeBg = (ImageView) findViewById(R.id.iv_time);
        ivTimeBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap blurBitmap = takeScreenShot(HomeAty.this);
                TimePopupWindow m_ppopupWindow = new TimePopupWindow(HomeAty.this, blurBitmap);
                m_ppopupWindow.setAnimationStyle(R.style.AnimationAlpha);
                m_ppopupWindow.showAtLocation(HomeAty.this.getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });
        //添加umeng更新
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UmengUpdateAgent.update(HomeAty.this);
            }
        }, 2000);

        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        tv_title = (TextViewExtend) findViewById(R.id.tv_title);
        ll_no_network = (LinearLayout) findViewById(R.id.ll_no_network);
        ll_no_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.checkNetWork(HomeAty.this)) {

                    lv_news.setVisibility(View.VISIBLE);
                    ll_no_network.setVisibility(View.GONE);

                    loadNewsData(1);
                } else {
                    lv_news.setVisibility(View.GONE);
                    ll_no_network.setVisibility(View.VISIBLE);
                }
            }
        });

        lv_news = (PullToRefreshListView) findViewById(R.id.lv_news);
        lv_news.getRefreshableView().setDivider(null);
        list_adapter = new MyAdapter();
        lv_news.setAdapter(list_adapter);
        lv_news.setMode(PullToRefreshBase.Mode.BOTH);
        lv_news.setPullLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
        lv_news.setPullLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
        lv_news.setRefreshingLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
        lv_news.setRefreshingLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
        lv_news.setReleaseLabel("还有" + mDownNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_END);
        lv_news.setReleaseLabel("还有" + mUpNewsArr.size() + "条新鲜新闻...", PullToRefreshBase.Mode.PULL_FROM_START);
        lv_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (NetUtil.checkNetWork(HomeAty.this)) {
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

                if (NetUtil.checkNetWork(HomeAty.this)) {
                    lv_news.setVisibility(View.VISIBLE);
                    ll_no_network.setVisibility(View.GONE);
                    showNextDownNews();
                } else {
                    lv_news.setVisibility(View.GONE);
                    ll_no_network.setVisibility(View.VISIBLE);
                }

            }
        });
        final int _HeaderHeight = DensityUtil.dip2px(this, 55);
        //设置listview 上拉时的监听器
        lv_news.setmPullToRefreshSlidingUpListener(new PullToRefreshBase.PullToRefreshSlidingUpListener() {
            int _Start = 0;

            @Override
            public void slidingUp(int slideDistance) {
                //更具滑动的距离来设置listview的高度
                final RelativeLayout.LayoutParams _Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                if ((_Start + slideDistance) <= _HeaderHeight) {
                    mIsNeedAnim = false;
                    if (mIsFistAnim) {
                        mIsFistAnim = false;
                        Animation _AnimForListView = new Animation() {

                            @Override
                            protected void applyTransformation(float interpolatedTime, Transformation t) {
                                ObjectAnimator.ofFloat(ll_title, "translationY", -_Start, -_HeaderHeight * interpolatedTime).start();
                                _Start += _HeaderHeight * interpolatedTime;
                                _Params.topMargin = (int) (_HeaderHeight - 15 - _HeaderHeight * interpolatedTime);
                                lv_news.setLayoutParams(_Params);
                            }
                        };
                        _AnimForListView.setDuration(500); // in ms
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //隐藏banner后，下拉或者上来都要有动画效果
                                mIsNeedAnim = true;
                            }
                        }, 1000);
                        lv_news.startAnimation(_AnimForListView);
                    }
                }
            }
        });
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

        if (mDownNewsArr != null && mDownNewsArr.size() > 0) {
            NewsFeed _NewsFeed = mDownNewsArr.get(mDownNewsArr.size() - 1);
            if (mDownNewsArr.size() == 1) {
                _NewsFeed.setBottom_flag(true);
                if(mUpNewsArr.size() > 0) {
                    lv_news.setMode2(PullToRefreshBase.Mode.PULL_FROM_START, 1);
                }else{
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


            list_adapter.notifyDataSetChanged();
        }
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
            TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, -lv_news.getScrollY(), height * 0.4f + DensityUtil.dip2px(HomeAty.this, 20));
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
                                    if (mDownNewsArr.size() > 0) {
                                        lv_news.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                                    } else {
                                        lv_news.setMode(PullToRefreshBase.Mode.DISABLED);
                                    }
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

    @Override
    protected void loadData() {
        if (NetUtil.checkNetWork(HomeAty.this)) {
            ll_no_network.setVisibility(View.GONE);
            loadNewsData(1);
        } else {
            lv_news.setVisibility(View.GONE);
            ll_no_network.setVisibility(View.VISIBLE);
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
            if(!refresh_flag) {
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
                    convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item, null);
                    holder.fl_title_content = (FrameLayout) convertView.findViewById(R.id.fl_title_content);
                    holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                    holder.tv_title = (LetterSpacingTextView) convertView.findViewById(R.id.tv_title);
                    holder.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);
                    ViewGroup.LayoutParams params = holder.fl_title_content.getLayoutParams();
                    params.height = (int) (height * 0.27);

                    holder.fl_title_content.setLayoutParams(params);
                    holder.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);
                    holder.ll_top_line = (LinearLayout) convertView.findViewById(R.id.ll_top_line);
                    holder.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                    holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                    holder.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                    convertView.setTag(holder);
                } else {
                    if (ViewHolder.class == convertView.getTag().getClass()) {
                        holder = (ViewHolder) convertView.getTag();
                        holder.ll_source_content.removeAllViews();
                        holder.rl_bottom_mark.setVisibility(View.GONE);
                    } else {
                        holder = new ViewHolder();
                        convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item, null);
                        holder.fl_title_content = (FrameLayout) convertView.findViewById(R.id.fl_title_content);
                        holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                        holder.tv_title = (LetterSpacingTextView) convertView.findViewById(R.id.tv_title);
                        holder.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);
                        ViewGroup.LayoutParams params = holder.fl_title_content.getLayoutParams();
                        params.height = (int) (height * 0.27);

                        holder.fl_title_content.setLayoutParams(params);
                        holder.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);
                        holder.ll_top_line = (LinearLayout) convertView.findViewById(R.id.ll_top_line);
                        holder.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                        holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                        holder.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                        convertView.setTag(holder);
                    }
                }

                String title = feed.getTitle();

                ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) holder.iv_title_img.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = (int) (height * 0.27);
                holder.iv_title_img.setLayoutParams(layoutParams);
//            holder.iv_title_img.setBackgroundResource(R.color.red);

                holder.tv_title.setText(title);
                holder.tv_title.setFontSpacing(3);
                holder.tv_title.setShadowLayer(6f, 1, 2, new Color().parseColor("#000000"));

                holder.tv_interests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeAty.this, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        intent.putExtra(KEY_NEWS_SOURCE,VALUE_NEWS_SOURCE);
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                if (feed.getCategory() != null) {
                    holder.tv_news_category.setText(feed.getCategory());
                    holder.tv_news_category.setFontSpacing(5);
                    TextUtil.setNewsBackGround(holder.tv_news_category, feed.getCategory());
                    TextUtil.setTopLineBackground(feed.getCategory(), holder.ll_top_line);
                    TextUtil.setNewsBackGround(holder.tv_news_category, feed.getCategory());
                    TextUtil.setTopLineBackground(feed.getCategory(), holder.ll_top_line);
                }

                holder.tv_interests.setText(feed.getOtherNum() + "家观点");

                holder.fl_title_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeAty.this, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });
                //点击其他观点的点击事件
                holder.ll_source_interest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //uemng statistic click other viewpoint
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_ONCLICK_OTHER_VIEWPOINT);
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
                holder.rl_bottom_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv_news.getRefreshableView().setSelection(GlobalParams.split_index_bottom + 1);
                    }
                });


                if (feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                    ImageLoaderHelper.dispalyImage(HomeAty.this, feed.getImgUrl(), holder.iv_title_img, holder.tv_title);
                }

                final long start = System.currentTimeMillis();

                sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();

                //解析新闻来源观点数据
                if (sourceList != null && sourceList.size() > 0) {

                    for (int a = 0; a < sourceList.size(); a++) {


                        final NewsFeed.Source source = sourceList.get(a);

                        LinearLayout ll_souce_view = (LinearLayout) View.inflate(getApplicationContext(), R.layout.lv_source_item, null);
                        ll_souce_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(HomeAty.this, NewsDetailWebviewAty.class);
                                intent.putExtra(KEY_URL, source.getUrl());
                                startActivity(intent);
                                //umeng statistic onclick url below the head news
                                HashMap<String, String> _MobMap = new HashMap<>();
                                _MobMap.put("resource_site_name", source.getSourceSitename());
                                MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS, _MobMap);
                            }
                        });
                        ImageView iv_source = (ImageView) ll_souce_view.findViewById(R.id.iv_source);
                        TextViewExtend tv_news_source = (TextViewExtend) ll_souce_view.findViewById(R.id.tv_news_source);

                        if (source != null) {

                            String source_name = source.getSourceSitename();

                            if (source_name != null) {
                                if (source.getUser() != null && !"".equals(source.getUser())) {
//                                    String str ="<font size=7>" + source.getUser() + "</font> : " + source.getTitle();

                                    tv_news_source.setText(source.getUser() + ":" + source.getTitle());
                                } else {
                                    tv_news_source.setText(source_name + ": " + source.getTitle());
                                }

                                TextUtil.setResourceSiteIcon(iv_source, source_name);

                            } else {

                                tv_news_source.setText("匿名报道:");
                            }

                        }

                        if (contentSize < 3) {
                            holder.ll_source_content.addView(ll_souce_view);
                            contentSize++;
                        }
                    }
                }
            } else if("1".equals(feed.getSpecial())){

                if (convertView == null) {
                    holder2 = new ViewHolder2();
                    convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item_top, null);
                    holder2.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                    holder2.tv_title = (TextViewVertical) convertView.findViewById(R.id.tv_title);
                    holder2.tv_news_category = (TextView) convertView.findViewById(R.id.tv_news_category);
                    holder2.fl_news_content = (FrameLayout) convertView.findViewById(R.id.fl_news_content);
                    holder2.rl_top_mark = (RelativeLayout) convertView.findViewById(R.id.rl_top_mark);
                    holder2.ll_bottom_item = (LinearLayout) convertView.findViewById(R.id.ll_bottom_item);
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
                    convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item_top, null);
                    holder2.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                    holder2.tv_title = (TextViewVertical) convertView.findViewById(R.id.tv_title);
                    holder2.tv_news_category = (TextView) convertView.findViewById(R.id.tv_news_category);
                    holder2.fl_news_content = (FrameLayout) convertView.findViewById(R.id.fl_news_content);

                    ViewGroup.LayoutParams layoutParams = holder2.fl_news_content.getLayoutParams();
                    layoutParams.width = width;
                    layoutParams.height = (int) (height * 0.40);
                    holder2.fl_news_content.setLayoutParams(layoutParams);
                    holder2.rl_top_mark = (RelativeLayout) convertView.findViewById(R.id.rl_top_mark);
                    holder2.ll_bottom_item = (LinearLayout) convertView.findViewById(R.id.ll_bottom_item);
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
                int textsize = DensityUtil.dip2px(HomeAty.this,18);
                holder2.tv_title.setTextSize(textsize);
                holder2.tv_title.setTextColor(new Color().parseColor("#ffffff"));
                holder2.tv_title.setLineWidth(40);
                holder2.tv_title.setShadowLayer(4f, 1, 2, new Color().parseColor("#000000"));
                holder2.tv_news_category.setText(feed.getCategory());

                TextUtil.setTextBackGround(holder2.tv_news_category, feed.getCategory());

                if (feed.isTime_flag()) {
                    holder2.ll_bottom_item.setVisibility(View.VISIBLE);
                    holder2.rl_divider_top.setVisibility(View.GONE);

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
                        weekday = DateUtil.dayForWeek(currentDate,format);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    holder2.tv_weekday.setText(weekday + "|" + am);
                }

                if (feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                    ImageLoaderHelper.dispalyImage(HomeAty.this, feed.getImgUrl(), holder2.iv_title_img, holder2.tv_title);
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
                        Intent intent = new Intent(HomeAty.this, NewsDetailAty.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });
            }else if("9".equals(feed.getSpecial())){

                images = feed.getImgUrl_ex();

                if (convertView == null) {

                    holder3 = new ViewHolder3();

                    if(images.length == 2){

                        convertView = View.inflate(getApplicationContext(), R.layout.ll_news_card2, null);
                    }else{
                        convertView = View.inflate(getApplicationContext(), R.layout.ll_news_card, null);
                    }

                    holder3.ll_image_list = (LinearLayout) convertView.findViewById(R.id.ll_image_list);
                    holder3.image_card1 = (ImageView) convertView.findViewById(R.id.image_card1);
                    holder3.image_card2 = (ImageView) convertView.findViewById(R.id.image_card2);
                    holder3.image_card3 = (ImageView) convertView.findViewById(R.id.image_card3);
                    holder3.tv_title = (LetterSpacingTextView) convertView.findViewById(R.id.tv_title);
                    holder3.tv_news_category = (LetterSpacingTextView) convertView.findViewById(R.id.tv_news_category);
                    holder3.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                    holder3.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                    holder3.tv_interests = (LetterSpacingTextView) convertView.findViewById(R.id.tv_interests);
                    holder3.rl_bottom_mark = (RelativeLayout) convertView.findViewById(R.id.rl_bottom_mark);

                    convertView.setTag(holder3);
                } else {
                    if (ViewHolder3.class == convertView.getTag().getClass()) {
                        holder3 = (ViewHolder3) convertView.getTag();
                        holder3.ll_source_content.removeAllViews();
                        holder3.rl_bottom_mark.setVisibility(View.GONE);
                    } else {
                        holder3 = new ViewHolder3();

                        if(images.length == 2){

                            convertView = View.inflate(getApplicationContext(), R.layout.ll_news_card2, null);
                        }else{
                            convertView = View.inflate(getApplicationContext(), R.layout.ll_news_card, null);
                        }

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

                        convertView.setTag(holder3);
                    }
                }

                String title = feed.getTitle();

                holder3.tv_title.setText(title);
                holder3.tv_title.setFontSpacing(1);
                holder3.tv_title.setShadowLayer(2f, 1, 2, new Color().parseColor("#000000"));

                holder3.tv_interests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeAty.this, NewsDetailAty.class);
                        intent.putExtra("url", feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                if (feed.getCategory() != null) {
                    holder3.tv_news_category.setText(feed.getCategory());
                    holder3.tv_news_category.setFontSpacing(5);
                    TextUtil.setNewsBackGround(holder3.tv_news_category, feed.getCategory());
                }

                holder3.tv_interests.setText(feed.getOtherNum() + "家观点");
                holder3.ll_image_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeAty.this, NewsDetailAty.class);
                        intent.putExtra("url", feed.getSourceUrl());
                        startActivity(intent);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });

                //点击其他观点的点击事件
                holder.ll_source_interest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //uemng statistic click other viewpoint
                        MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_ONCLICK_OTHER_VIEWPOINT);
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
                holder3.rl_bottom_mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv_news.getRefreshableView().setSelection(GlobalParams.split_index_bottom + 1);
                    }
                });

                if(images.length == 2){
                    ImageLoaderHelper.dispalyImage(HomeAty.this,images[0],holder3.image_card1);
                    ImageLoaderHelper.dispalyImage(HomeAty.this,images[1],holder3.image_card2);
                }else{
                    ImageLoaderHelper.dispalyImage(HomeAty.this,images[0],holder3.image_card1);
                    ImageLoaderHelper.dispalyImage(HomeAty.this,images[1],holder3.image_card2);
                    ImageLoaderHelper.dispalyImage(HomeAty.this,images[2],holder3.image_card3);
                }

                final long start = System.currentTimeMillis();

                sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();

                //解析新闻来源观点数据
                if (sourceList != null && sourceList.size() > 0) {

                    for (int a = 0; a < sourceList.size(); a++) {


                        final NewsFeed.Source source = sourceList.get(a);

                        LinearLayout ll_souce_view = (LinearLayout) View.inflate(getApplicationContext(), R.layout.lv_source_item, null);
                        ll_souce_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(HomeAty.this, NewsDetailWebviewAty.class);
                                intent.putExtra("url", source.getUrl());
                                startActivity(intent);
                                //umeng statistic onclick url below the head news
                                HashMap<String, String> _MobMap = new HashMap<>();
                                _MobMap.put("resource_site_name", source.getSourceSitename());
                                MobclickAgent.onEvent(HomeAty.this, CommonConstant.US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS, _MobMap);
                            }
                        });
                        ImageView iv_source = (ImageView) ll_souce_view.findViewById(R.id.iv_source);
                        TextViewExtend tv_news_source = (TextViewExtend) ll_souce_view.findViewById(R.id.tv_news_source);

                        if (source != null) {

                            String source_name = source.getSourceSitename();

                            if (source_name != null) {
                                if (source.getUser() != null && !"".equals(source.getUser())) {
//                                    String str ="<font size=7>" + source.getUser() + "</font> : " + source.getTitle();

                                    tv_news_source.setText(source.getUser() + ":" + source.getTitle());
                                } else {
                                    tv_news_source.setText(source_name + ": " + source.getTitle());
                                }

                                TextUtil.setResourceSiteIcon(iv_source, source_name);

                            } else {

                                tv_news_source.setText("匿名报道:");
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
                convertView.startAnimation(AnimationUtils.loadAnimation(HomeAty.this, R.anim.aty_list_item_in));
                return convertView;
            }
            //上拉时给显示的item添加动画
            if (position == mMiddleNewsArr.size() - 1 && mIsNeedAnim) {
//                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                convertView.measure(View.MeasureSpec.makeMeasureSpec(lv_news.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                int height = convertView.getHeight() == 0 ? convertView.getMeasuredHeight() : convertView.getHeight();
                ViewPropertyAnimator animator = convertView.animate()
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                convertView.setTranslationY(height / 2);
                animator.translationYBy(-height / 2);
                animator.start();
            }
            return convertView;
        }

    }
    @Override
    protected void onResume() {

        if (NetUtil.checkNetWork(HomeAty.this)) {
            lv_news.setVisibility(View.VISIBLE);
            ll_no_network.setVisibility(View.GONE);
        } else {
            lv_news.setVisibility(View.GONE);
            ll_no_network.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }


    class ViewHolder {

        ImageView iv_title_img;
        LetterSpacingTextView tv_title;
        LetterSpacingTextView tv_news_category;
        LinearLayout ll_source_content;
        LinearLayout ll_source_interest;
        FrameLayout fl_title_content;
        TextViewExtend tv_interests;
        RelativeLayout rl_bottom_mark;
        LinearLayout ll_top_line;

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
        RelativeLayout rl_bottom_mark;

    }

    private void loadNewsData(final int timenews) {

        String url = HttpConstant.URL_GET_NEWS_LIST + "?timenews=" + timenews;
        final long start = System.currentTimeMillis();
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {

            public void success(ArrayList<NewsFeed> result) {
                long delta = System.currentTimeMillis() - start;
                Logger.i("ariesy", delta + "");
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
    }

    private void inflateDataInArrs(ArrayList<NewsFeed> result) {
        int _SplitStartIndex = 0;
        if (result != null && result.size() > 1) {
            for (int i = 0; i < result.size(); i++) {
                if (!"1".equals(result.get(i).getSpecial())) {
                    _SplitStartIndex = i;
                    break;
                }
            }

            GlobalParams.split_index_top = _SplitStartIndex;

            if (_SplitStartIndex > 1) {
                NewsFeed feed = result.get(_SplitStartIndex - 1);
                feed.setTime_flag(true);
            } else if (_SplitStartIndex == 1) {
                NewsFeed feed = result.get(_SplitStartIndex - 1);
                feed.setTime_flag(true);
                feed.setTop_flag(true);
                lv_news.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            }
            if(_SplitStartIndex > 0) {
                mUpNewsArr = new ArrayList<>(result.subList(0, _SplitStartIndex - 1));
                mMiddleNewsArr = new ArrayList<>(result.subList(_SplitStartIndex - 1, _SplitStartIndex + 1));
                mDownNewsArr = new ArrayList<>(result.subList(_SplitStartIndex + 1, result.size()));
//                mDownNewsArr = new ArrayList<>(result.subList(_SplitStartIndex + 1, _SplitStartIndex + 6));
            }else{
                mMiddleNewsArr = new ArrayList<>(result.subList(0, _SplitStartIndex + 2));
                mDownNewsArr = new ArrayList<>(result.subList(_SplitStartIndex + 2, result.size()));
                lv_news.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            }
        }
    }

    private Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        // Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();
        return b;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        long pressedBackKeyTime = System.currentTimeMillis();
        if ((pressedBackKeyTime - mLastPressedBackKeyTime) < 2000) {
            finish();
        } else {
            ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
            //ToastUtil.toastLong(R.string.press_back_again_exit);
        }
        mLastPressedBackKeyTime = pressedBackKeyTime;


        return true;
    }


}
