package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class ZhiHuView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private TextViewExtend mtvContent;
    private ImageView iv_arrow_zhihu;

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
        mtvContent = (TextViewExtend) mRootView.findViewById(R.id.content_textView);
        iv_arrow_zhihu = (ImageView) mRootView.findViewById(R.id.iv_arrow_zhihu);

    }

    public void setZhiHuData(NewsDetail.ZhiHu zhiHuData){
        mtvContent.setText(zhiHuData.title);
        ViewGroup.LayoutParams params = iv_arrow_zhihu.getLayoutParams();

        if(zhiHuData.title.length() > 44 && zhiHuData.title.length() < 60){
            params.height = 140;
        }else if(zhiHuData.title.length() > 75 && zhiHuData.title.length() < 90){
            params.height = 200;
        }else if(zhiHuData.title.length() > 90){
            params.height = 220;
        }

        iv_arrow_zhihu.setLayoutParams(params);
    }
}
