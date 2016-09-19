package com.news.yazhidao.pages;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetailForDigger;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.widget.MyListView;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class DiggerNewsDetail extends SwipeBackActivity implements View.OnClickListener {

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId;
    private String mPlatformType;
    private String uuid;
    private String mNewsDetailUrl;
    private ImageView mivShareBg;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailLeftBack, mDetailComment, mDetailShare, mDetailHeader;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
    private RelativeLayout mDiggerNewsDetail;
    private MyListView lv_digger_news_zhihu;
    private LinearLayout ll_digger_news_detail;
    private TextView tv_digger_title;
    private TextView tv_digger_album;
    private TextView mDiggerAlbum;
    private TextView tv_digger_time;
    private TextView mDetailCommentGroupTitle;
    private ViewHoder holder;
    private ArrayList<NewsDetailForDigger.ContentEntity> list = new ArrayList<>();
    private ArrayList<NewsDetailForDigger.ZhihuEntity> zhihuList = new ArrayList<>();
    private MyAdapter adapter;

    private float startY;
    private String mSource;
    private String newsId = null;
    private String newsType = null;
    private String mSerachKey;
    private String mAlbumTitle;
    private String mCreatetime;
    private NewsDetailForDigger mNewsDetailForDigger;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {

        mSerachKey = getIntent().getStringExtra(AlbumListAty.KEY_SEARCH_KEY);
        mAlbumTitle = getIntent().getStringExtra(AlbumListAty.KEY_ALBUM_TITLE);
        mCreatetime = getIntent().getStringExtra(AlbumListAty.KEY_CREATETIME);
        mNewsDetailForDigger = (NewsDetailForDigger) getIntent().getSerializableExtra(AlbumListAty.KEY_NEWSDETAIL_FOR_DIGGER);
        setContentView(R.layout.aty_digger_news_detail_layout);
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(this);
    }

    @Override
    protected void initializeViews() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mDetailHeader = findViewById(R.id.mDetailHeader);
        mDetailLeftBack = findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDiggerAlbum = (TextView) findViewById(R.id.mDiggerAlbum);
        mDiggerAlbum.setText(mAlbumTitle);

        mDiggerNewsDetail = (RelativeLayout) findViewById(R.id.mDiggerNewsDetail);
        tv_digger_title = (TextView) findViewById(R.id.tv_digger_title);
        tv_digger_title.setText(mSerachKey);
        tv_digger_album = (TextView) findViewById(R.id.tv_digger_album);
        tv_digger_album.setText(mAlbumTitle);
        tv_digger_time = (TextView) findViewById(R.id.tv_digger_time);
        if (mCreatetime != null && mCreatetime.length() > 0) {
            tv_digger_time.setText(mCreatetime.substring(0, 16));
        } else {
            tv_digger_time.setText("");
        }
        mDetailCommentGroupTitle = (TextView) findViewById(R.id.mDetailCommentGroupTitle);

        ll_digger_news_detail = (LinearLayout) findViewById(R.id.ll_digger_news_detail);
        lv_digger_news_zhihu = (MyListView) findViewById(R.id.lv_digger_news_zhihu);
        lv_digger_news_zhihu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent webviewIntent = new Intent(DiggerNewsDetail.this, NewsDetailWebviewAty.class);
                String zhihuUrl = (String) zhihuList.get(position).getTitle();
                webviewIntent.putExtra(NewsDetailWebviewAty.KEY_URL, zhihuUrl);
                startActivity(webviewIntent);
            }
        });
    }

    @Override
    protected void loadData() {
        list = (ArrayList<NewsDetailForDigger.ContentEntity>) mNewsDetailForDigger.getContent();
        //展现挖掘机挖掘出的新闻详情
        viewDiggerNewsDetail();

        zhihuList = (ArrayList<NewsDetailForDigger.ZhihuEntity>) mNewsDetailForDigger.getZhihu();
        if (zhihuList != null && zhihuList.size() > 0) {
            adapter = new MyAdapter();
            lv_digger_news_zhihu.setAdapter(adapter);
        } else {
            mDetailCommentGroupTitle.setVisibility(View.GONE);
            lv_digger_news_zhihu.setVisibility(View.GONE);
        }

    }

    private void viewDiggerNewsDetail() {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                NewsDetailForDigger.ContentEntity entity = list.get(i);

                if (entity.getText() != null) {
                    TextView tv = new TextView(this);
                    tv.setText(entity.getText());
                    tv.setTextColor(new Color().parseColor("#494949"));
                    tv.setTextSize(14);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = DensityUtil.dip2px(this, 7);
                    params.bottomMargin = DensityUtil.dip2px(this, 7);

                    ll_digger_news_detail.addView(tv, params);
                } else if (entity.getSrc() != null) {
                    ImageView img = new ImageView(this);
//                    img.getHierarchy().setActualImageFocusPoint(new PointF(.5f, 0.35f));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(mScreenHeight * 0.3));
                    params.topMargin = DensityUtil.dip2px(this, 7);
                    params.bottomMargin = DensityUtil.dip2px(this, 7);

                    img.setLayoutParams(params);
//                    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
//                            .setAutoPlayAnimations(true)
//                            .setUri(Uri.parse(entity.getSrc()))//设置uri
//                            .build();
//                    //设置Controller
//                    img.setController(draweeController);
                    img.setImageURI(Uri.parse(entity.getSrc()));

                    ll_digger_news_detail.addView(img);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                onBackPressed();
                break;
            case R.id.mNewsLoadingImg:
                loadData();
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder = new ViewHoder();
                convertView = View.inflate(DiggerNewsDetail.this, R.layout.item_news_detail_zhihu2, null);
                holder.mDetailZhiHuTitle = (TextView) convertView.findViewById(R.id.mDetailZhiHuTitle);

                convertView.setTag(holder);
            } else {
                holder = (ViewHoder) convertView.getTag();
            }

            NewsDetailForDigger.ZhihuEntity newsdetail = zhihuList.get(position);

            if (newsdetail.getUrl() != null) {
                holder.mDetailZhiHuTitle.setText(newsdetail.getUrl());
            } else {
                holder.mDetailZhiHuTitle.setText("");
            }

            return convertView;
        }
    }

    class ViewHoder {
        TextView mDetailZhiHuTitle;
    }

}
