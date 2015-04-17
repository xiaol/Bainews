package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.news.yazhidao.utils.FastBlur;
import com.news.yazhidao.utils.ImageUtils;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeAty extends BaseActivity {

    private PullToRefreshListView lv_news;
    private MyAdapter list_adapter;
    private LinearLayout ll_title;
    private LinearLayout ll_no_network;
    private long mLastPressedBackKeyTime;
    private ArrayList<NewsFeed> feedList = new ArrayList<NewsFeed>();
    private ArrayList<NewsFeed.Source> sourceList = new ArrayList<NewsFeed.Source>();
    private int i = 0;
    private boolean flag = false;
    private boolean top_flag = false;
    private boolean visible_flag = true;
    private boolean adapterFlag = false;
    private boolean requestMore = false;
    private String opinion;
    private int color = new Color().parseColor("#55ffffff");
    private ViewHolder holder = null;
    private TextViewExtend tv_title;
    private int page = 1;
    private int height = 0;
    private int width = 0;
    private int mMostRecentY;
    private int currentSize = 0;
    private int contentSize = 0;

    private ImageView mRefreshLoadingImg;
    private AnimationDrawable mAnirefreshLoading;
    private ImageLoaderHelper imageLoader;
    private LinearLayout ll_souce_view;
    private ImageView iv_source;
    TextViewExtend tv_news_source;
    //将在下拉显示的新闻数据
    private ArrayList<NewsFeed> mMiddleNewsArr = new ArrayList<>();
    //将在当前显示的新闻数据
    private ArrayList<NewsFeed> mUpNewsArr = new ArrayList<>();
    //将在上拉显示的新闻数据
    private ArrayList<NewsFeed> mDownNewsArr = new ArrayList<>();
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            switch (scrollState) {
                case SCROLL_STATE_IDLE:
                    if (view.getFirstVisiblePosition() == 0 && !visible_flag) {
                        ll_title.setVisibility(View.VISIBLE);
                        visible_flag = true;

                    }

                    break;

                case SCROLL_STATE_FLING:

                    if (view != null) {
                        int firstPos = ((ListView) view).getFirstVisiblePosition();

                        if (firstPos == 0) {
                            ll_title.setVisibility(View.VISIBLE);
                            visible_flag = true;
                        } else if (firstPos == 1) {

                            View v = ((ListView) view).getChildAt(firstPos);

                            if (v != null) {
                                int top = v.getTop();

                                if (top > 200 && visible_flag) {

                                    ll_title.setVisibility(View.GONE);
                                    visible_flag = false;

                                } else {
                                    if (top < 50 && !visible_flag) {
                                        ll_title.setVisibility(View.VISIBLE);
                                        visible_flag = true;
                                    }
                                }

                            }
                        } else if (firstPos > 1) {

                            if (visible_flag) {

                                ll_title.setVisibility(View.GONE);
                                visible_flag = false;

                            }

                        }
                    }


                    break;

                case SCROLL_STATE_TOUCH_SCROLL:
                    if (view != null) {
                        int firstPos = ((ListView) view).getFirstVisiblePosition();

                        if (firstPos == 0) {
                            ll_title.setVisibility(View.VISIBLE);
                            visible_flag = true;
                        } else if (firstPos == 1) {

                            View v = ((ListView) view).getChildAt(firstPos);

                            if (v != null) {
                                int top = v.getTop();

                                if (top > 200 && visible_flag) {

                                    ll_title.setVisibility(View.GONE);
                                    visible_flag = false;

                                } else {
                                    if (top < 50 && !visible_flag) {
                                        ll_title.setVisibility(View.VISIBLE);
                                        visible_flag = true;
                                    }
                                }

                            }
                        } else if (firstPos > 1) {

                            if (visible_flag) {

                                ll_title.setVisibility(View.GONE);
                                visible_flag = false;

                            }

                        }
                    }
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (firstVisibleItem + visibleItemCount == totalItemCount && !top_flag) {
                top_flag = true;
            } else
                top_flag = false;
        }
    };

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
        list_adapter = new MyAdapter();
        lv_news.setAdapter(list_adapter);
        lv_news.setMode(PullToRefreshBase.Mode.BOTH);
//        BitmapDrawable drawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.news_customer_loading1));

//        mRefreshLoadingImg = (ImageView) lv_news.findViewById(R.id.pull_to_refresh_image);
//        mRefreshLoadingImg.setImageResource(R.drawable.list_refresh_animation);
//        mAnirefreshLoading = (AnimationDrawable) mRefreshLoadingImg.getDrawable();
//
//        lv_news.setLoadingDrawable(mAnirefreshLoading);
//        mAnirefreshLoading.start();
        lv_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (NetUtil.checkNetWork(HomeAty.this)) {

                    lv_news.setVisibility(View.VISIBLE);
                    ll_no_network.setVisibility(View.GONE);

//                    loadNewsData2(1);
//                    loadNewsData(1);
                    showNextUpNews();
                    page = 1;
                    adapterFlag = false;
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

//                    loadNewsData(page);
                    showNextDownNews();
                    page++;
                } else {
                    lv_news.setVisibility(View.GONE);
                    ll_no_network.setVisibility(View.VISIBLE);
                }


            }
        });

//        lv_news.setOnScrollListener(scrollListener);
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
            NewsFeed _NewsFeed = mDownNewsArr.remove(mDownNewsArr.size() - 1);
            mMiddleNewsArr.add(_NewsFeed);
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
            NewsFeed _NewsFeed = mUpNewsArr.remove(0);
            mMiddleNewsArr.add(0, _NewsFeed);
            list_adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void loadData() {
        if (NetUtil.checkNetWork(HomeAty.this)) {
            ll_no_network.setVisibility(View.GONE);
            loadNewsData(1);
            page++;
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
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item, null);
                holder.fl_title_content = (FrameLayout) convertView.findViewById(R.id.fl_title_content);
                holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
//                holder.iv_title_img.setBackgroundDrawable(null);
                ViewGroup.LayoutParams params = holder.fl_title_content.getLayoutParams();
                params.height = (int) (height * 0.27);

                holder.fl_title_content.setLayoutParams(params);

                holder.tv_title = (TextViewExtend) convertView.findViewById(R.id.tv_title);
//                holder.tv_title.setBackgroundColor(color);
                holder.tv_interests = (TextViewExtend) convertView.findViewById(R.id.tv_interests);
                holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                holder.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
//                holder.listener = new ImageLoadingListener();
                convertView.setTag(holder);
            } else {
//                holder.tv_title.setBackgroundColor(color);
//                holder.iv_title_img.setBackgroundDrawable(null);
                holder = (ViewHolder) convertView.getTag();
                holder.ll_source_content.removeAllViews();
            }

            final NewsFeed feed = mMiddleNewsArr.get(position);

            String title = feed.getTitle();

            ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) holder.iv_title_img.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = (int) (height * 0.27);
            holder.iv_title_img.setLayoutParams(layoutParams);
//            holder.iv_title_img.setBackgroundResource(R.color.red);


            holder.tv_title.setText(title);
            holder.tv_title.setShadowLayer(6f, 1, 2, new Color().parseColor("#000000"));
            holder.tv_interests.setText(feed.getOtherNum() + "家观点");

            holder.fl_title_content.setOnClickListener(new View.OnClickListener() {
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
                    holder.ll_source_interest.setVisibility(View.GONE);
                } else {
                    holder.ll_source_interest.setVisibility(View.VISIBLE);
                }
            }


            if (feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                ImageLoaderHelper.dispalyImage(HomeAty.this, feed.getImgUrl(), holder.iv_title_img, holder.tv_title);

            } else {
                holder.tv_title.setBackgroundColor(color);
                applyBlur(holder.iv_title_img, holder.tv_title);
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
//                    TextViewExtend tv_news_des = (TextViewExtend) ll_souce_view.findViewById(R.id.tv_news_des);


                    if (source != null) {

                        String source_name = source.getSourceSitename();

                        if (source_name != null) {
                            if (source.getUser() != null && !"".equals(source.getUser())) {
                                tv_news_source.setText(source.getUser() + ": " + source.getTitle());
                            } else {
                                tv_news_source.setText(source_name + ": " + source.getTitle());
                            }

                            TextUtil.setResourceSiteIcon(iv_source, source_name);

                        } else {

                            tv_news_source.setText("匿名报道:");
                        }

//                        if (source.getTitle() != null) {
//                            tv_news_des.setText(source.getTitle());
//                        } else {
//                            tv_news_des.setText("");
//                        }

                    }

                    if (contentSize < 3) {
                        holder.ll_source_content.addView(ll_souce_view);
                        contentSize++;
                    }
                }
            }

            long deata = System.currentTimeMillis() - start;
            Logger.i("aaaaaa", convertView.getHeight() + "");
            //给item添加动画
            if (position == 0) {
            TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, 100, convertView.getHeight());
            localTranslateAnimation.setDuration(300L);
            lv_news.clearAnimation();
            lv_news.startAnimation(localTranslateAnimation);
                final View finalConvertView = convertView;
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
                                lv_news.clearAnimation();
                                finalConvertView.clearAnimation();
                                finalConvertView.startAnimation(AnimationUtils.loadAnimation(HomeAty.this, R.anim.aty_list_item_in));
                            }
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

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
        TextViewExtend tv_title;
        LinearLayout ll_source_content;
        LinearLayout ll_source_interest;
        FrameLayout fl_title_content;
        TextViewExtend tv_interests;

    }

    private void loadNewsData(final int page) {

        String url = HttpConstant.URL_GET_NEWS_LIST + "?page=" + page;
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
//                    if (page == 1) {
//                        feedList.clear();
//                        //添加list
//                        for (int j = 0; j < result.size(); j++) {
//                            if (result.get(j).getSourceUrl().startsWith(""))
//                                feedList.add(result.get(j));
//                        }
//
//                        if (!adapterFlag) {
//                            list_adapter = new MyAdapter();
//                            lv_news.setAdapter(list_adapter);
//                            adapterFlag = true;
//                        } else {
//                            list_adapter.notifyDataSetChanged();
//                        }
//
//                    } else {
//                        //添加list
//                        for (int a = 0; a < result.size(); a++) {
//                            feedList.add(result.get(a));
//                        }
//                        list_adapter.notifyDataSetChanged();
//                    }
//                } else {
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
        int _SplitStartIndex;
        //TODO 将数据分成对应的3部分
        if (result != null && result.size() > 1) {
            if (result.size() % 2 == 0) {
                _SplitStartIndex = result.size() / 2 - 1;
            } else {
                _SplitStartIndex = result.size() / 2;
            }
            mDownNewsArr = new ArrayList<>(result.subList(0, _SplitStartIndex));
            mMiddleNewsArr = new ArrayList<>(result.subList(_SplitStartIndex, _SplitStartIndex + 2));
            mUpNewsArr = new ArrayList<>(result.subList(_SplitStartIndex + 2, result.size()));
            Logger.e("jigang", "------");
        }
    }


    private void applyBlur(final ImageView mImageView, final TextView mTextview) {
        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                mImageView.buildDrawingCache();

                Bitmap bmp = mImageView.getDrawingCache();
                blur(bmp, mTextview);

                if (bmp != null) {
                    bmp = null;
                }

                return true;
            }
        });
    }


    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 5;
        float radius = 1;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-2 / scaleFactor, 0);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除锯齿
        if (canvas != null && bkg != null && paint != null) {
            canvas.drawBitmap(bkg, 0, 0, paint);
            canvas.drawColor(new Color().parseColor("#66FFFFFF"));
        }

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        overlay = ImageUtils.getRoundedCornerBitmap(HomeAty.this, overlay, 1, false, false, false, true);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));

        if (overlay != null) {
            overlay = null;
        }

        Log.e("xxxx", System.currentTimeMillis() - startMs + "ms");
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
