package com.news.yazhidao.pages;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.LetterSpacingTextView;
import com.news.yazhidao.widget.NewsDetailHeaderView;
import com.news.yazhidao.widget.NewsListView;
import com.news.yazhidao.widget.ShowAllListview;
import com.news.yazhidao.widget.SpeechView;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ariesy on 8/20/15.
 */
public class NewsDetailAty2 extends SwipeBackActivity {

    private static final String TAG = "NewsDetailAty";
    private ImageView mivBack;
    private ExpandableListView eplv_newsdetail;
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
    private ArrayList<View> groupList = new ArrayList<>();
    private ArrayList<View> childList = new ArrayList<>();

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.aty_detail2);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void initializeViews() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mNewsDetailProgressWheelWrapper = findViewById(R.id.mNewsDetailProgressWheelWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();

//        mlvRelate = (NewsListView) findViewById(R.id.news_detail_listView);
//        mlvRelate.addHeaderView(headerView);
//        ImageView imageView = new ImageView(this);
//        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50);
//        imageView.setLayoutParams(layoutParams);
//        imageView.setBackgroundResource(R.color.bg_gray);
//        mlvRelate.addFooterView(imageView);
//        mlvRelate.setAdapter(mNewsDetailAdapter);

        eplv_newsdetail = (ExpandableListView) findViewById(R.id.eplv_newsdetail);
        ll_no_network = (LinearLayout) findViewById(R.id.ll_no_network);
        btn_reload = (Button) findViewById(R.id.btn_reload);
        setListener();
    }

    @Override
    protected void loadData() {

        if (NetUtil.checkNetWork(NewsDetailAty2.this)) {
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

        User user = SharedPreManager.getUser(NewsDetailAty2.this);
        if (user != null) {
            userId = user.getUserId();
            platformType = user.getPlatformType();
        }

        GlobalParams.news_detail_url = getIntent().getStringExtra("url");
        uuid = DeviceInfoUtil.getUUID();

        String url = HttpConstant.URL_GET_NEWS_DETAIL + GlobalParams.news_detail_url + "&userId=" + userId + "&platformType=" + platformType;

        if (!isnew) {
            NetworkRequest _Request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
            _Request.setCallback(new JsonCallback<NewsDetail>() {

                @Override
                public void success(final NewsDetail result) {
                    if (result != null) {

                        View headerGroupView = View.inflate(NewsDetailAty2.this,R.layout.item_group_header,null);
                        ImageView mNewsDetailHeaderImg = (ImageView) headerGroupView.findViewById(R.id.mNewsDetailHeaderImg);//新闻头图

                        LetterSpacingTextView mNewsDetailHeaderTitle = (LetterSpacingTextView) headerGroupView.findViewById(R.id.mNewsDetailHeaderTitle);//新闻标题
                        TextView mNewsDetailHeaderTime = (TextView) headerGroupView.findViewById(R.id.mNewsDetailHeaderTime);//新闻时间
                        TextView mNewsDetailHeaderTemperature = (TextView) headerGroupView.findViewById(R.id.mNewsDetailHeaderTemperature);//新闻所属的温度
                        RelativeLayout rl_speech_view = (RelativeLayout) headerGroupView.findViewById(R.id.rl_speech_view);
                        ImageView iv_user_icon_article_comment = (ImageView) headerGroupView.findViewById(R.id.iv_user_icon_article_comment);
                        SpeechView sv_article_comment = (SpeechView) headerGroupView.findViewById(R.id.sv_article_comment);
                        LinearLayout ll_detail_des = (LinearLayout) headerGroupView.findViewById(R.id.ll_detail_des);
                        ShowAllListview lv_newsdetail = (ShowAllListview) headerGroupView.findViewById(R.id.lv_newsdetail);
                        ImageView tv_des_icon = (ImageView) headerGroupView.findViewById(R.id.tv_des_icon);
                        LetterSpacingTextView mNewsDetailHeaderDesc = (LetterSpacingTextView) headerGroupView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
                        groupList.add(mNewsDetailHeaderImg);

                    }
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
                if (NetUtil.checkNetWork(NewsDetailAty2.this)) {
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

    private class MyExpandableAdapter extends BaseExpandableListAdapter {

        public Object getChild(int groupPosition, int childPosition) {
            return childList.get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return childList.size();
        }

        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            return null;
        }

        // group method stub
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        public int getGroupCount() {
            return groupList.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            return null;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
