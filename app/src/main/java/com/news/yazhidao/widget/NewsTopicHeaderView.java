package com.news.yazhidao.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.news.yazhidao.R;

public class NewsTopicHeaderView extends RelativeLayout {

    private TextView mTopicDetail;
    private ImageView mTopicView;
    private Context mContext;

    //新闻标题,新闻时间,新闻描述
    public NewsTopicHeaderView(Context context) {
        this(context, null);
        mContext = context;
    }

    public NewsTopicHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        mContext = context;
    }

    public NewsTopicHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View root = View.inflate(context, R.layout.aty_news_topic_hearder_view, this);
        mTopicDetail = (TextView) root.findViewById(R.id.mTopicDetail);
        mTopicView = (ImageView) root.findViewById(R.id.mTopicView);
    }

    public void setHeaderViewData(Uri uri, String detail) {
        Glide.with(mContext).load(uri).centerCrop().placeholder(R.drawable.bg_load_default_small).into(mTopicView);
        mTopicDetail.setText(detail);
    }
}
