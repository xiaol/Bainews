package com.news.yazhidao.pages;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.GlobalParams;
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

    @Override
    protected void setContentView() {

        //隐藏状态栏
//        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //获得当前窗体对象
//        Window window = HomeAty.this.getWindow();
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);

        GlobalParams.context = HomeAty.this;
        setContentView(R.layout.activity_viewpager);
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

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(1);
        changeColor(getResources().getColor(R.color.tab_blue));

        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(HomeAty.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });

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
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            long pressedBackKeyTime = System.currentTimeMillis();
            if ((pressedBackKeyTime - mLastPressedBackKeyTime) < 2000) {
                finish();
            } else {
                ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
                //ToastUtil.toastLong(R.string.press_back_again_exit);
            }
            mLastPressedBackKeyTime = pressedBackKeyTime;
        }

        return true;
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

        private final String[] TITLES = {"关注","今日"};

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
            return NewsFeedFragment.newInstance(position);
        }
    }

}
