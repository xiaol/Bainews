package com.news.yazhidao.pages;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berkeley on 8/11/15.
 */
public class GuideAty extends BaseActivity {

    private ViewPager pager;
    private MyPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private GuidePage1 oneFragment;
    private GuidePage2 twoFragment;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_guide);
    }

    @Override
    protected void initializeViews() {
        pager = (ViewPager) findViewById(R.id.pager);

        fragmentList = new ArrayList<Fragment>();
        oneFragment = new GuidePage1();
        twoFragment = new GuidePage2();

        fragmentList.add(oneFragment);
        fragmentList.add(twoFragment);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
    }

    @Override
    protected void loadData() {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

}
