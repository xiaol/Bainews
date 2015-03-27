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
public class SinaView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private RoundedImageView mHeadPortrait;
    private TextViewExtend mtvName,mtvContent;
    private ImageView mivPicture;


    public SinaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_layout_sina, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public SinaView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_layout_sina, null);
        addView(mRootView);
        initVars();
        findViews();
    }



    private void initVars() {
    }

    private void findViews() {
        mHeadPortrait = (RoundedImageView) mRootView.findViewById(R.id.head_portrait_imageView);
        mHeadPortrait.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mtvName = (TextViewExtend) mRootView.findViewById(R.id.name_textView);
        mtvContent= (TextViewExtend) mRootView.findViewById(R.id.content_textView);
        mivPicture = (ImageView) mRootView.findViewById(R.id.picture_imageView);
        mtvName.setText("哈哈哈");
        mtvContent.setText("哈市的开发将开始多了几分；拉萨京东方；流口水的减肥；了开始就");
    }

    public void setData(){

    }


}
