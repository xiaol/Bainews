package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class SinaView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private RoundedImageView mHeadPortrait;
    private TextViewExtend mtvName, mtvContent;
    private ImageView mivPicture;
    private int miScreenWidth;

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
        miScreenWidth = DeviceInfoUtil.getScreenWidth();
    }

    private void findViews() {
        mHeadPortrait = (RoundedImageView) mRootView.findViewById(R.id.head_portrait_imageView);
        mHeadPortrait.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mtvName = (TextViewExtend) mRootView.findViewById(R.id.name_textView);
        mtvContent = (TextViewExtend) mRootView.findViewById(R.id.content_textView);
        mivPicture = (ImageView) mRootView.findViewById(R.id.picture_imageView);
    }

    public void setSinaData(NewsDetail.Weibo weiboData) {
        mtvName.setText(weiboData.user);
        mtvContent.setText(weiboData.title);
        if (weiboData.img != null && !weiboData.img.equals("")) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mivPicture.getLayoutParams();
            layoutParams.width = miScreenWidth * 135 / 720;
            layoutParams.height = layoutParams.width * 86 / 135;
            mivPicture.setLayoutParams(layoutParams);
            mtvContent.setMaxLines(2);
            ImageLoaderHelper.dispalyImage(mContext, weiboData.img, mivPicture);
        }
    }
}
