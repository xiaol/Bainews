package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class SinaItemView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private RoundedImageView mHeadPortrait;
    private TextView mtvName,mtvContent;
    private ImageView mivPicture;


    public SinaItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.item_sina, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public SinaItemView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.item_sina, null);
        addView(mRootView);
        initVars();
        findViews();
    }



    private void initVars() {
    }

    private void findViews() {
        mHeadPortrait = (RoundedImageView) mRootView.findViewById(R.id.head_portrait_imageView);
        mHeadPortrait.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mtvName = (TextView) mRootView.findViewById(R.id.name_textView);
        mtvContent= (TextView) mRootView.findViewById(R.id.content_textView);
        mivPicture = (ImageView) mRootView.findViewById(R.id.picture_imageView);
        mHeadPortrait.setBackgroundResource(R.drawable.indicator_arrow);
        mtvName.setText("哈哈哈");
        mtvContent.setText("sdfljsakljflasjdflaskdjflkaldkfj");
        mivPicture.setBackgroundResource(R.drawable.abc_btn_check_material);
    }

    public void setData(){

    }


}
