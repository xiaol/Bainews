package com.news.yazhidao.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;

/**
 * Created by Administrator on 2016/7/14.
 */
public class AvatarTextBehavior extends CoordinatorLayout.Behavior<TextView>{

    private final static float MIN_AVATAR_PERCENTAGE_SIZE = 0.3f;
    private final static int EXTRA_FINAL_AVATAR_PADDING = 80;

    private int mStartYPosition; // 起始的Y轴位置
    private int mFinalYPosition; // 结束的Y轴位置
    private int mStartSice; // 开始的文字高度
    private int mFinalSice; // 结束的文字高度
    private int mStartXPosition; // 起始的X轴高度
    private int mFinalXPosition; // 结束的X轴高度
    private float mStartToolbarPosition; // Toolbar的起始位置

    private final Context mContext;
    private float mAvatarMaxSize;

    public AvatarTextBehavior(Context context,int FX,int FY) {
        mContext = context;
        mFinalYPosition = FY;
        mFinalXPosition = FX;
        init();
    }

    private void init() {
        bindDimensions();
    }

    private void bindDimensions() {
        mAvatarMaxSize = mContext.getResources().getDimension(R.dimen.image_width);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        // 依赖Toolbar控件
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {

        // 初始化属性
        shouldInitProperties(child, dependency);

        // 最大滑动距离: 起始位置-状态栏高度
        final int maxScrollDistance = (int) (mStartToolbarPosition - getStatusBarHeight());

        // 滑动的百分比
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        // Y轴距离
        float distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
                * (1f - expandedPercentageFactor)) + (child.getHeight() / 2);

        // X轴距离
        float distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
                * (1f - expandedPercentageFactor)) + (child.getWidth() / 2);

        // 高度减小
        float heightToSubtract = ((mStartSice - mFinalSice) * (1f - expandedPercentageFactor));
//        // 高度减小
//        float widthToSubtract = ((mStartWidth - mFinalWidth) * (1f - expandedPercentageFactor));

        // 文字位置
//        if(mFinalYPosition == (mStartYPosition - distanceYToSubtract)){
//            Logger.e("aaa","111111111111111111");
//        }

        int endY = (mStartYPosition - (mStartYPosition - mFinalYPosition + child.getHeight() / 2));
        float practicalY = mStartYPosition - distanceYToSubtract;
        if(endY>practicalY) {
            child.setY(endY);
        }else if(mStartYPosition<practicalY){
            child.setY(mStartYPosition);
        }else{
            child.setY(practicalY);
        }

        child.setX(mStartXPosition - distanceXToSubtract);

//        // 文字大小
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//        lp.width = (int) (mStartSice - heightToSubtract);
//        lp.height = (int) (mStartSice - heightToSubtract);
        child.setTextSize(mStartSice - heightToSubtract);

        return true;
    }

    /**
     * 初始化动画值
     *
     * @param child      文字控件
     * @param dependency ToolBar
     */
    private void shouldInitProperties(TextView child, View dependency) {

        // 文字控件中心
        if (mStartYPosition == 0)
            mStartYPosition = (int) (child.getY() + (child.getHeight() / 2));


//        // Toolbar中心
//        if (mFinalYPosition == 0)
//            mFinalYPosition = (dependency.getHeight() / 2);


        // 文字高度
        if (mStartSice == 0)
            mStartSice = 16;


//        // 文字宽度
//        if (mStartWidth == 0)
//            mStartWidth = child.getWidth();


        // Toolbar缩略图高度
        if (mFinalSice == 0)
            mFinalSice = 17;

        // 文字控件水平中心
        if (mStartXPosition == 0)
            mStartXPosition = (int) (child.getX() + (child.getWidth() / 2));


//        // 边缘+缩略图宽度的一半
//        if (mFinalXPosition == 0)
//            mFinalXPosition = DeviceInfoUtil.getScreenWidth() / 2+mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + (mFinalSice / 2);


        // Toolbar的起始位置
        if (mStartToolbarPosition == 0)
            mStartToolbarPosition = dependency.getY() + (dependency.getHeight() / 2);

    }

    // 获取状态栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
