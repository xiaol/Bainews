package com.news.yazhidao.pages;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;

import com.astuetz.PagerSlidingTabStrip;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class HomeAty extends BaseActivity {

    //PageSlidingTabStrip
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private long mLastPressedBackKeyTime;
    //Viewpager 索引默认是1
    private int mViewPagerIndex = 1;
    //棱镜界面对应的fragment
    private LengjingFgt mLengJingFgt;

    @Override
    protected void setContentView() {
        mLengJingFgt =new LengjingFgt(this);
        GlobalParams.context = HomeAty.this;
        setContentView(R.layout.activity_viewpager);
        //判断是否是别的app分享进来的
        Intent intent = getIntent();
        final String data = intent.getStringExtra(Intent.EXTRA_TEXT);
        String type = intent.getType();
        if ("text/plain".equals(type) && !TextUtils.isEmpty(data)) {
            Log.e("jigang", type + "-----data=" + data);
            //把页面设置在挖掘机
            mViewPagerIndex = 2;

            //在LengJingFgt 中打开编辑页面,延时防止crash
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLengJingFgt.openEditWindow(TextUtil.getNewsTitle(data), TextUtil.getNewsUrl(data));
                }
            }, 800);
        }
    }

    @Override
    protected void initializeViews() {

        //pagesliding
        mTintManager = new SystemBarTintManager(HomeAty.this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        GlobalParams.tabs = tabs;

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        GlobalParams.pager = pager;
        pager.setCurrentItem(mViewPagerIndex);
        changeColor(getResources().getColor(R.color.tab_blue));

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long pressedBackKeyTime = System.currentTimeMillis();
            if ((pressedBackKeyTime - mLastPressedBackKeyTime) < 2000) {
                finish();
            } else {
                ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
            }
            mLastPressedBackKeyTime = pressedBackKeyTime;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void changeColor(int newColor) {

        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        currentColor = newColor;
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        currentColor = savedInstanceState.getInt("currentColor");
//        changeColor(currentColor);
//    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"关注", "谷歌今日焦点","挖掘机"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new CategoryFgt();
            } else if (position == 1) {
                return NewsFeedFragment.newInstance(position);
            } else if (position == 2) {
                return mLengJingFgt;
            } else {
                return null;
            }

        }
    }

}
