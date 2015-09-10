package com.news.yazhidao.widget.imagewall;

import android.app.Activity;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
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
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.widget.TextViewExtend;

import java.util.ArrayList;
import java.util.List;

public class WallActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<View> mViews;
    private ImageView mivBack;
    private TextViewExtend mtvTitle, mtvPagerNum, mtvContent;
    private int[] mImages;
    private String mTotalSize;
    private String[] aaaa;
    private int mScreenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walllayout);
        initVars();
        findViews();
        setListener();
    }

    private void initVars() {
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        aaaa = new String[]{"aaaaa", "dffffffffff", "dfdfdfdf", "2erwerwerwerwerwerwerwer"};
        mImages = new int[]{R.drawable.aaa, R.drawable.aaa,
                R.drawable.aaa, R.drawable.aaa};
        mViews = new ArrayList<View>();
        mTotalSize = String.valueOf(mImages.length);
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
        for (int i = 0; i < mImages.length; i++) {
            final SimpleDraweeView imageView = new SimpleDraweeView(this);
            mViews.add(imageView);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse("http://c.hiphotos.baidu.com/image/pic/item/e850352ac65c103851f8a024b6119313b17e8955.jpg"))
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
//                            ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
//                            layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
//                            layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
//                            imageView.setLayoutParams(layoutParams);
                        }
                    })
                    .build();
            imageView.setController(controller);
        }
        mPagerAdapter = new WallPagerAdapter(mViews);
        mViewPager.setAdapter(mPagerAdapter);
        mtvTitle.setText("sdfsdfsdf");
        mtvPagerNum.setText("sdfsdfsdf");
        mtvPagerNum.setText("1/" + mTotalSize);
        mtvContent.setText("sdfsdfsdf");
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    // 监听viewpage
    @Override
    public void onPageSelected(int arg0) {
        mtvTitle.setText(aaaa[arg0]);
        mtvPagerNum.setText(arg0 + 1 + "/" + mTotalSize);
        mtvContent.setText(aaaa[arg0] + "fasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsdddddddfasdfasdfsddddddd");
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
