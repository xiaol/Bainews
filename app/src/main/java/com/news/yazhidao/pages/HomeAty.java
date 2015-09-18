package com.news.yazhidao.pages;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;


public class HomeAty extends BaseActivity {

    //PageSlidingTabStrip
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    private MyPagerAdapter adapter;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private long mLastPressedBackKeyTime;
    //Viewpager 索引默认是1
    private int mViewPagerIndex = 1;
    private List<Fragment> mFragmentList;
    private CategoryFgt mfgtCategory;
    private NewsFeedFgt mfgeNewsFeed;
    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;
    private FloatingActionMenu leftCenterMenu;
    private LinearLayout ll_darker_layer;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_viewpager);
        ShareSDK.initSDK(this);
        mFragmentList = new ArrayList<Fragment>();
        mfgtCategory = new CategoryFgt();
        mfgeNewsFeed = new NewsFeedFgt();
        mFragmentList.add(mfgtCategory);
        mFragmentList.add(mfgeNewsFeed);

        GlobalParams.context = HomeAty.this;
        ll_darker_layer = (LinearLayout) findViewById(R.id.ll_darker_layer);
        ll_darker_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftCenterMenu != null) {
                    leftCenterMenu.close(true);
                }
            }
        });
    }

    @Override
    protected void initializeViews() {
        //pagesliding
        mTintManager = new SystemBarTintManager(HomeAty.this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //tabs 需要动态设置高度(4.4要使用沉浸式)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            tabs.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 30)));
        }
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 1)
                    mfgeNewsFeed.CancelRequest();
                if (position == 2) {
                    if (leftCenterButton != null) {
                        leftCenterButton.detach(null);
                        leftCenterButton.attach(starParams, null);
                    }
                } else {
                    if (leftCenterButton != null) {
                        leftCenterMenu.close(true);
                        leftCenterButton.detach(null);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        GlobalParams.tabs = tabs;

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
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
                if (DeviceInfoUtil.isFlyme()) {
                    ToastUtil.toastShort(getString(R.string.press_back_again_exit));
                } else {
                    ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
                }
                mLastPressedBackKeyTime = pressedBackKeyTime;
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean translucentStatus() {
        return true;
    }

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }

    /**
     * 设置状态栏和tabbar颜色
     *
     * @param newColor
     */
    private void changeColor(int newColor) {
        tabs.setBackgroundColor(newColor);
        /**如果系统版本在4.4以下就使用黑色*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            tabs.setBackgroundResource(R.drawable.bg_common_header_gradient);
        } else {
            if ("dior".equals(Build.DEVICE) && "dior".equals(Build.PRODUCT)) {
                tabs.setBackgroundResource(R.drawable.bg_common_header_gradient);
            }
        }
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        SystemBarTintManager.SystemBarConfig config = mTintManager.getConfig();
        currentColor = newColor;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"关注", "谷歌今日焦点"};

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
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
    }

}
