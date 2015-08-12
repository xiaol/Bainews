package com.news.yazhidao.pages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

/**
 * Created by Berkeley on 8/11/15.
 */
public class GuideAty extends BaseActivity {

    private ViewPager pager;
    private MyPagerAdapter adapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_guide);
    }

    @Override
    protected void initializeViews() {
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
    }

    @Override
    protected void loadData() {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new GuidePage1();
            } else if (position == 1) {
                return new GuidePage2();
            } else{
                return null;
            }

        }
    }

}
