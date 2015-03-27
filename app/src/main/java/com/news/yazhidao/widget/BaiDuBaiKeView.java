package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class BaiDuBaiKeView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private TextViewExtend mtvTitle,mtvContent;
    private ImageView mivPicture;
    int i =3;


    public BaiDuBaiKeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_layout_baidubaike, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public BaiDuBaiKeView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_layout_baidubaike, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    private void initVars() {
        
    }

    private void findViews() {
        mtvTitle = (TextViewExtend) mRootView.findViewById(R.id.title_textView);
        mtvContent = (TextViewExtend) mRootView.findViewById(R.id.content_textView);
        mivPicture = (ImageView) mRootView.findViewById(R.id.picture_imageView);

        mtvTitle.setText("李光耀");
        mtvContent.setText("李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀李光耀");
    }

    public void setZhiHuData(){
    }

}
