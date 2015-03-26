package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class ZhiHuView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private TextView mtvContent;

    public ZhiHuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_layout_zhihu, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public ZhiHuView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_layout_zhihu, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    private void initVars() {
    }

    private void findViews() {
        mtvContent = (TextView) mRootView.findViewById(R.id.content_textView);
        mtvContent.setText("dfasdfsadfsadfdfasdfsadfsadfd");
    }

    public void setZhiHuData(){
    }
}
