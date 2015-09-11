package com.news.yazhidao.widget.imagewall;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.widget.TextViewExtend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WallActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static final String KEY_IMAGE_WALL_DATA = "key_image_wall_data";
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<View> mViews;
    private ImageView mivBack;
    private TextViewExtend mtvTitle, mtvPagerNum, mtvContent;
    private String mTotalSize;
    private int mScreenWidth;
    private ArrayList<HashMap<String,String>> mImageWalls;


    @Override
    protected boolean isNeedAnimation() {
        return true;
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.walllayout);
    }

    @Override
    protected void initializeViews() {
        initVars();
        findViews();
        setListener();
    }

    @Override
    protected void loadData() {

    }

    private void initVars() {
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        Intent intent = getIntent();
        mImageWalls = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(KEY_IMAGE_WALL_DATA);
        mViews = new ArrayList<View>();
        mTotalSize = String.valueOf(mImageWalls.size());
    }

    // 初始化视图
    private void findViews() {
        // 实例化视图控件
        mViewPager = (ViewPager) findViewById(R.id.wall_viewPager);
        mtvTitle = (TextViewExtend) findViewById(R.id.title_textView);
        mtvPagerNum = (TextViewExtend) findViewById(R.id.pager_num_textView);
        mtvContent = (TextViewExtend) findViewById(R.id.content_textView);
        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mtvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        for (int i = 0; i < mImageWalls.size(); i++) {
            final SimpleDraweeView imageView = new SimpleDraweeView(this);
            mViews.add(imageView);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(mImageWalls.get(i).get("img")))
                    .setTapToRetryEnabled(true)
                    .setOldController(imageView.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(
                                String id,
                                @Nullable ImageInfo imageInfo,
                                @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
                            lp.width = mScreenWidth;
                            lp.height = (int) (mScreenWidth * imageInfo.getHeight() / (float) imageInfo.getWidth());
                            mViewPager.setLayoutParams(lp);
                        }
                    })
                    .build();
            imageView.setController(controller);
        }
        mPagerAdapter = new WallPagerAdapter(mViews);
        mViewPager.setAdapter(mPagerAdapter);
        HashMap<String, String> imageFirst = mImageWalls.get(0);
        mtvTitle.setText("");
        mtvPagerNum.setText("1/" + mTotalSize);
        mtvContent.setText(imageFirst.get("note"));
    }

    private void setListener() {
        mViewPager.setOnPageChangeListener(this);
        mivBack.setOnClickListener(this);
    }

    //按钮的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_imageView:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    // 监听viewpage
    @Override
    public void onPageSelected(int pageIndex) {
        mtvTitle.setText("");
        mtvPagerNum.setText(pageIndex + 1 + "/" + mTotalSize);
        mtvContent.setText(mImageWalls.get(pageIndex).get("note"));
    }

    public class WallPagerAdapter extends PagerAdapter {
        private List<View> views = new ArrayList<View>();

        public WallPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }

    }
}
