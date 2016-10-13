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

public class SpecialNewsHeaderView extends RelativeLayout {

    private TextView mSpecialDetail;
    private ImageView mSpecialView;
    private Context mContext;

    //新闻标题,新闻时间,新闻描述
    public SpecialNewsHeaderView(Context context) {
        this(context, null);
        mContext = context;
    }

    public SpecialNewsHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
        mContext = context;
    }

    public SpecialNewsHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View root = View.inflate(context, R.layout.aty_special_news_hearder_view, this);
        mSpecialDetail = (TextView) root.findViewById(R.id.mSpecialDetail);
        mSpecialView = (ImageView) root.findViewById(R.id.mSpecialView);
    }

    public void setHeaderViewData(Uri uri, String detail) {
        Glide.with(mContext).load(uri).centerCrop().placeholder(R.drawable.bg_load_default_small).into(mSpecialView);
        mSpecialDetail.setText(detail);
    }
}
