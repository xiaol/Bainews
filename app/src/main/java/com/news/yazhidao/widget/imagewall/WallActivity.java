package com.news.yazhidao.widget.imagewall;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WallActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_IMAGE_WALL_DATA = "key_image_wall_data";
    private ViewPager mWallVPager;
    private PagerAdapter mPagerAdapter;
    private ArrayList<View> mViews;
    private ImageView mWallLeftBack;
    private TextView mWallDesc;
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
    }

    @Override
    protected void loadData() {

    }

    private void initVars() {
        Intent intent = getIntent();
        mImageWalls = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra(KEY_IMAGE_WALL_DATA);
        mViews = new ArrayList<View>();
    }

    // 初始化视图
    private void findViews() {
        // 实例化视图控件
        mWallVPager = (ViewPager) findViewById(R.id.mWallVPager);
        mWallDesc = (TextView) findViewById(R.id.mWallDesc);
        mWallLeftBack = (ImageView) findViewById(R.id.mWallLeftBack);
        mWallLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallActivity.this.finish();
            }
        });
        mWallDesc.setMovementMethod(ScrollingMovementMethod.getInstance());
        for (int i = 0; i < mImageWalls.size(); i++) {
            final SimpleDraweeView imageView = new SimpleDraweeView(this);
            ViewGroup.LayoutParams  params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            mViews.add(imageView);
            imageView.setImageURI(Uri.parse(mImageWalls.get(i).get("img")));
        }
        mWallVPager.setAdapter(new WallPagerAdapter(mViews));
        mWallVPager.setOffscreenPageLimit(3);
        mWallDesc.setText(Html.fromHtml(1 + "<small>" + "/" + mImageWalls.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImageWalls.get(0).get("note")));
        mWallVPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mWallDesc.setText(Html.fromHtml(position + 1 + "<small>" + "/" + mImageWalls.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImageWalls.get(position).get("note")));
            }
        });

    }

    //按钮的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mWallLeftBack:
                onBackPressed();
                break;
        }
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
