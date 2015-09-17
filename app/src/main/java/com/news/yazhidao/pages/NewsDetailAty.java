package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.image.ImageManager;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.NewsDetailHeaderView;
import com.news.yazhidao.widget.NewsListView;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewsDetailAty extends SwipeBackActivity {

    private static final String TAG = "NewsDetailAty";
    private ListNewsDetailAdapter mNewsDetailAdapter;
    private ImageView mivBack;
    private NewsDetailHeaderView headerView;
    private View mNewsDetailProgressWheelWrapper;
    private AnimationDrawable mAniNewsLoading;
    private LinearLayout ll_no_network;
    private Button btn_reload;
    private ImageView mNewsLoadingImg;
    //从哪儿进入的详情页
    private String mSource;
    private SwipeBackLayout mSwipeBackLayout;
    private String uuid;
    private String userId = "";
    private String platformType = "";
    private int position = 0;
    private NewsListView mlvRelate;

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.aty_detail);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void initializeViews() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mNewsDetailAdapter = new ListNewsDetailAdapter(this);
        headerView = new NewsDetailHeaderView(this);

        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mNewsDetailProgressWheelWrapper = findViewById(R.id.mNewsDetailProgressWheelWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
        mlvRelate = (NewsListView) findViewById(R.id.news_detail_listView);
        mlvRelate.addHeaderView(headerView);
        ImageView imageView = new ImageView(this);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50);
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundResource(R.color.bg_gray);
        mlvRelate.addFooterView(imageView);
        mlvRelate.setAdapter(mNewsDetailAdapter);
        ll_no_network = (LinearLayout) findViewById(R.id.ll_no_network);
        btn_reload = (Button) findViewById(R.id.btn_reload);
        setListener();
    }


    @Override
    protected void loadData() {

        if (NetUtil.checkNetWork(NewsDetailAty.this)) {
            ll_no_network.setVisibility(View.GONE);
            loadNewsDetail();
            mNewsDetailProgressWheelWrapper.setVisibility(View.VISIBLE);
            mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
            mAniNewsLoading.start();
        } else {
            ll_no_network.setVisibility(View.VISIBLE);
            ToastUtil.toastLong("您的网络有点不给力，请检查网络设置....");
        }

        Intent intent = new Intent();
        intent.putExtra("position", String.valueOf(position));
        intent.putExtra("isComment", "0");

        setResult(0, intent);
    }

    private void loadNewsDetail() {
        Bundle bundle = getIntent().getBundleExtra(AlbumListAty.KEY_BUNDLE);
        boolean isnew = getIntent().getBooleanExtra(AlbumListAty.KEY_IS_NEW_API, false);
        boolean isdigger = false;
        AlbumSubItem albumSubItem;
        String newsId = null;
        if (bundle != null) {
            isdigger = bundle.getBoolean(AlbumListAty.KEY_IS_DIGGER);
            albumSubItem = (AlbumSubItem) bundle.getSerializable(AlbumListAty.KEY_ALBUMSUBITEM);
            newsId = albumSubItem.getInserteId();

        }
        position = getIntent().getIntExtra("position", 0);

        User user = SharedPreManager.getUser(NewsDetailAty.this);
        if (user != null) {
            userId = user.getUserId();
            platformType = user.getPlatformType();
        }

//        GlobalParams.news_detail_url = getIntent().getStringExtra("url");
        uuid = DeviceInfoUtil.getUUID();

        String url = HttpConstant.URL_GET_NEWS_DETAIL + getIntent().getStringExtra("url") + "&userId=" + userId + "&platformType=" + platformType;
        Logger.e("jigang","-----newsurl : " + url);
        if (!isnew) {
            NetworkRequest _Request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
            _Request.setCallback(new JsonCallback<NewsDetail>() {

                @Override
                public void success(final NewsDetail result) {
                    if (result != null) {
                        headerView.setDetailData(result, getIntent().getStringExtra("url"), new NewsDetailHeaderView.HeaderVeiwPullUpListener() {
                            @Override
                            public void onclickPullUp(int height) {
//                                msgvNewsDetail.mFlingRunnable.startScroll(-height, 1000);
                            }
                        }, false, new NewsDetailHeaderView.CommentListener() {

                            @Override
                            public void comment(boolean istrue) {
                                if (istrue) {

                                    Intent intent = new Intent();
                                    intent.putExtra("position", String.valueOf(position));
                                    intent.putExtra("isComment", "1");

                                    setResult(0, intent);
                                }
                            }
                        });
                        mNewsDetailAdapter.setData(result.relate);
                        mNewsDetailAdapter.notifyDataSetChanged();

                        if (NewsFeedFgt.VALUE_NEWS_SOURCE.equals(mSource)) {
                            mlvRelate.setSelection(1);
                        }

                    } else {
                        ToastUtil.toastShort("新闻的内容为空，无法打开");
                        NewsDetailAty.this.finish();
                    }
                    GlobalParams.start_u = System.currentTimeMillis();
                    mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                    mAniNewsLoading.stop();
                }

                @Override
                public void failed(MyAppException exception) {
                    mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                    ll_no_network.setVisibility(View.VISIBLE);
                    mAniNewsLoading.stop();
                }
            }.setReturnType(new TypeToken<NewsDetail>() {
            }.getType()));
            _Request.execute();
        } else {

            if (isdigger) {
                NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL_NEW, NetworkRequest.RequestMethod.POST);
                List<NameValuePair> pairs = new ArrayList<>();
                pairs.add(new BasicNameValuePair("news_id", newsId));
                _Request.setParams(pairs);
                _Request.setCallback(new JsonCallback<NewsDetailAdd>() {

                    @Override
                    public void success(final NewsDetailAdd result) {
                        if (result != null) {

                            headerView.setDetailData(result, getIntent().getStringExtra("url"), new NewsDetailHeaderView.HeaderVeiwPullUpListener() {
                                @Override
                                public void onclickPullUp(int height) {
//                                        msgvNewsDetail.mFlingRunnable.startScroll(-height, 1000);
                                }
                            }, true, new NewsDetailHeaderView.CommentListener() {
                                @Override
                                public void comment(boolean istrue) {

                                }
                            });

                            mNewsDetailAdapter.setData(result.relate);
                            mNewsDetailAdapter.notifyDataSetChanged();

                            if (NewsFeedFgt.VALUE_NEWS_SOURCE.equals(mSource)) {
                                mlvRelate.setSelection(1);
                            }
                        } else {
                            ToastUtil.toastShort("新闻的内容为空，无法打开");
                            NewsDetailAty.this.finish();
                        }


                        mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                        mAniNewsLoading.stop();
                    }

                    @Override
                    public void failed(MyAppException exception) {

                        mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                        mAniNewsLoading.stop();
                    }
                }.setReturnType(new TypeToken<NewsDetailAdd>() {
                }.getType()));
                _Request.execute();
            } else {


                NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL_NEW, NetworkRequest.RequestMethod.POST);

                List<NameValuePair> pairs = new ArrayList<>();

                pairs.add(new BasicNameValuePair("url", GlobalParams.news_detail_url));
                _Request.setParams(pairs);

                _Request.setCallback(new JsonCallback<NewsDetailAdd>() {

                    @Override
                    public void success(final NewsDetailAdd result) {
                        if (result != null) {
                            headerView.setDetailData(result, getIntent().getStringExtra("url"), new NewsDetailHeaderView.HeaderVeiwPullUpListener() {
                                @Override
                                public void onclickPullUp(int height) {
//                                    msgvNewsDetail.mFlingRunnable.startScroll(-height, 1000);
                                }
                            }, true, new NewsDetailHeaderView.CommentListener() {
                                @Override
                                public void comment(boolean istrue) {

                                }
                            });

                            mNewsDetailAdapter.setData(result.relate);
                            mNewsDetailAdapter.notifyDataSetChanged();

                            if (NewsFeedFgt.VALUE_NEWS_SOURCE.equals(mSource)) {
                                mlvRelate.setSelection(1);
                            }

                        } else {
                            ToastUtil.toastShort("新闻的内容为空，无法打开");
                            NewsDetailAty.this.finish();
                        }

                        mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                        mAniNewsLoading.stop();

                    }

                    @Override
                    public void failed(MyAppException exception) {
                        mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                        mAniNewsLoading.stop();
                    }
                }.setReturnType(new TypeToken<NewsDetailAdd>() {
                }.getType()));
                _Request.execute();
            }
        }
    }


    private void setListener() {
        mivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mlvRelate.setOnTouchListener(new View.OnTouchListener() {
            float _StartY = 0;
            float _DeltaY = 0;
            FrameLayout.LayoutParams _MivBackLayout = (FrameLayout.LayoutParams) mivBack.getLayoutParams();
            int _MivBackTopMargin = _MivBackLayout.topMargin;
            int _MivBackSelfHeigh = mivBack.getHeight();

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _StartY = event.getY();
                        Logger.i("down", v.getScrollY() + "");
                        break;
                    case MotionEvent.ACTION_UP:
                        _DeltaY = event.getY() - _StartY;
                        if (_DeltaY < 0) {
                            //往上滑动
                            if (mivBack.getVisibility() == View.GONE) {
                                return false;
                            }
                            if (Math.abs(_DeltaY) > _MivBackTopMargin + _MivBackSelfHeigh) {
                                mivBack.setVisibility(View.GONE);
                            }

                        } else {
                            //往下滑动
                            if (mivBack.getVisibility() == View.VISIBLE) {
                                return false;
                            }
                            if (Math.abs(_DeltaY) > _MivBackTopMargin + _MivBackSelfHeigh) {
                                mivBack.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        btn_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.checkNetWork(NewsDetailAty.this)) {
                    loadNewsDetail();
                    mNewsDetailProgressWheelWrapper.setVisibility(View.VISIBLE);
                    ll_no_network.setVisibility(View.GONE);
                } else {
                    ll_no_network.setVisibility(View.VISIBLE);
                    ToastUtil.toastLong("您的网络出现问题，请检查网络设置...");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        msgvNewsDetail.startFlingRunnable(300);
        headerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                headerView.setContentViewHeight(headerView.getContentView().getHeight());
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (NewsFeedFgt.VALUE_NEWS_NOTIFICATION.equals(mSource)) {
            Intent intent = new Intent(this, HomeAty.class);
            startActivity(intent);
        }
    }

    class ListNewsDetailAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NewsDetail.Relate> mArrData;
        int screenWidth;
        int lineHeight = 32;

        ListNewsDetailAdapter(Context context) {
            mContext = context;
            screenWidth = DeviceInfoUtil.getScreenWidth() / 2 - DensityUtil.dip2px(mContext, 24);
        }


        public void setData(ArrayList<NewsDetail.Relate> pArrData) {
            mArrData = pArrData;
        }

        @Override
        public int getCount() {
            return mArrData == null ? 0 : mArrData.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_listview_news_detail, null, false);
                holder.tvTime = (TextViewExtend) convertView.findViewById(R.id.time_textView);
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.picture_imageView);
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                holder.ivLineBottom = (ImageView) convertView.findViewById(R.id.line_bottom_imageView);
                holder.rlLine = (RelativeLayout) convertView.findViewById(R.id.line_layout);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final NewsDetail.Relate _Relate = mArrData.get(position);
            String title = _Relate.title;
            final String img = _Relate.img;
            if (!TextUtils.isEmpty(title)) {
                holder.tvContent.setText(title);
                holder.tvContent.post(new Runnable() {
                    @Override
                    public void run() {
                        int i = holder.tvContent.getLineCount();
                        if (i == 1)
                            if (TextUtils.isEmpty(img))
                                lineHeight = 32;
                            else
                                lineHeight = 57;
                        else {
                            if (TextUtils.isEmpty(img))
                                lineHeight = 52;
                            else
                                lineHeight = 67;
                        }
                        RelativeLayout.LayoutParams lpLineBottom = (RelativeLayout.LayoutParams) holder.ivLineBottom.getLayoutParams();
                        lpLineBottom.height = DensityUtil.dip2px(mContext, lineHeight);
                        lpLineBottom.width = DensityUtil.dip2px(mContext, lineHeight);
                        holder.ivLineBottom.setLayoutParams(lpLineBottom);

                        RelativeLayout.LayoutParams lpLine = (RelativeLayout.LayoutParams) holder.rlLine.getLayoutParams();
                        lpLine.leftMargin = DensityUtil.dip2px(mContext, -lineHeight / 2.0f + 10);
                        lpLine.rightMargin = DensityUtil.dip2px(mContext, -lineHeight / 2.0f + 10);
                        holder.rlLine.setLayoutParams(lpLine);

                        if (position == mArrData.size() - 1) {
                            holder.ivLineBottom.setVisibility(View.INVISIBLE);
                        } else {
                            holder.ivLineBottom.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
            if (_Relate.updateTime != null)
                holder.tvTime.setText(_Relate.updateTime.substring(5, 10).replace("-", "/"));
            else {
                for (int i = position; i < mArrData.size(); i++) {
                    NewsDetail.Relate relate = mArrData.get(i);
                    if (relate.updateTime != null) {
                        holder.tvTime.setText(relate.updateTime.substring(5, 10).replace("-", "/"));
                        break;
                    }else
                        holder.tvTime.setText("01/01");
                }
            }
            if (TextUtils.isEmpty(img))
                holder.ivPicture.setVisibility(View.GONE);
            else {
                holder.ivPicture.setVisibility(View.VISIBLE);
                ImageManager.getInstance(mContext).DisplayImage(img, holder.ivPicture, false, null);
            }
            RelativeLayout.LayoutParams lpLineBottom = (RelativeLayout.LayoutParams) holder.ivLineBottom.getLayoutParams();
            lpLineBottom.height = DensityUtil.dip2px(mContext, lineHeight);
            lpLineBottom.width = DensityUtil.dip2px(mContext, lineHeight);
            holder.ivLineBottom.setLayoutParams(lpLineBottom);

            RelativeLayout.LayoutParams lpLine = (RelativeLayout.LayoutParams) holder.rlLine.getLayoutParams();
            lpLine.leftMargin = DensityUtil.dip2px(mContext, -lineHeight / 2.0f + 10);
            lpLine.rightMargin = DensityUtil.dip2px(mContext, -lineHeight / 2.0f + 10);
            holder.rlLine.setLayoutParams(lpLine);

            if (position == mArrData.size() - 1) {
                holder.ivLineBottom.setVisibility(View.INVISIBLE);
            } else {
                holder.ivLineBottom.setVisibility(View.VISIBLE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent _Intent = new Intent(NewsDetailAty.this, NewsDetailWebviewAty.class);
                    _Intent.putExtra("url", _Relate.url);
                    startActivity(_Intent);
                    // add umeng statistic
                    HashMap<String, String> _MobParam = new HashMap<>();
                    _MobParam.put("resource_site_name", _Relate.sourceSitename);
                    MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_RELATE_ITEM_CLICK, _MobParam);
                }
            });
            return convertView;
        }
    }


    class Holder {
        TextViewExtend tvTime;
        ImageView ivPicture;
        TextView tvContent;
        ImageView ivLineBottom;
        RelativeLayout rlLine;
        TextView tvSource;
    }


}
