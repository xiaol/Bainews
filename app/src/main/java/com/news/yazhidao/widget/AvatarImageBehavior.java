package com.news.yazhidao.widget;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.news.yazhidao.R;


/**
 * 图片控件位置动画
 *
 * @author wangchenlong
 */
@SuppressWarnings("unused")
public class AvatarImageBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private final static float MIN_AVATAR_PERCENTAGE_SIZE = 0.3f;
    private final static int EXTRA_FINAL_AVATAR_PADDING = 80;

    private int mStartYPosition; // 起始的Y轴位置
    private int mFinalYPosition; // 结束的Y轴位置
    private int mStartHeight; // 开始的图片高度
    private int mFinalHeight; // 结束的图片高度
    private int mStartXPosition; // 起始的X轴高度
    private int mFinalXPosition; // 结束的X轴高度
    private float mStartToolbarPosition; // Toolbar的起始位置

    private final Context mContext;
    private float mAvatarMaxSize;

    public AvatarImageBehavior(Context context,int FX,int FY) {
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
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        // 依赖Toolbar控件
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {

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
        float heightToSubtract = ((mStartHeight - mFinalHeight) * (1f - expandedPercentageFactor));

//        Log.e("aaa", "getStatusBarHeight()==" + getStatusBarHeight());
//        Logger.e("aaa", "mStartYPosition - distanceYToSubtract===" + (mStartYPosition - distanceYToSubtract));
//
//        Logger.e("aaa", "endY" + (mStartYPosition - (mStartYPosition - mFinalYPosition+child.getHeight() / 2)));
//        Logger.e("aaa", "endY==" + (mStartYPosition - (mStartYPosition - mFinalYPosition)));
//        Logger.e("aaa", "child.getHeight() / 2" + (child.getHeight() / 2));

        int endY = (mStartYPosition - (mStartYPosition - mFinalYPosition +  mFinalHeight/ 2));
        float practicalY = mStartYPosition - distanceYToSubtract;

        // 图片位置Y
        if(endY>practicalY){
            child.setY(endY);
        }else if(mStartYPosition<practicalY){
            child.setY(mStartYPosition);
        }else{
            child.setY(practicalY);
        }


//        child.setY(mStartYPosition - distanceYToSubtract);
        // 图片位置X
        child.setX(mStartXPosition - distanceXToSubtract);
//        Logger.e("aaa", "mStartHeight - heightToSubtract==" + (mStartHeight - heightToSubtract));
//        Logger.e("aaa", "mFinalHeight==" + mFinalHeight);
        // 图片大小
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (mStartHeight - heightToSubtract);
        lp.height = (int) (mStartHeight - heightToSubtract);
        child.setLayoutParams(lp);

        return true;
    }

    /**
     * 初始化动画值
     *
     * @param child      图片控件
     * @param dependency ToolBar
     */
    private void shouldInitProperties(ImageView child, View dependency) {

        // 图片控件中心
        if (mStartYPosition == 0)
            mStartYPosition = (int) (child.getY() + (child.getHeight() / 2));


//        // Toolbar中心
//        if (mFinalYPosition == 0)
//            mFinalYPosition = (dependency.getHeight() / 2);


        // 图片高度
        if (mStartHeight == 0)
            mStartHeight = child.getHeight();


        // Toolbar缩略图高度
        if (mFinalHeight == 0)
            mFinalHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.image_final_width);


        // 图片控件水平中心
        if (mStartXPosition == 0)
            mStartXPosition = (int) (child.getX() + (child.getWidth() / 2));


//        // 边缘+缩略图宽度的一半
//        if (mFinalXPosition == 0)
//            mFinalXPosition = DeviceInfoUtil.getScreenWidth() / 2 - mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) -(mFinalHeight / 2);
        //



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