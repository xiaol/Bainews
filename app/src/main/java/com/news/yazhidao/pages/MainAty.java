package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.LoginPopupWindow;
import com.news.yazhidao.widget.channel.ChannelTabStrip;
import com.umeng.update.UmengUpdateAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by fengjigang on 15/10/28.
 * 主界面
 */
public class MainAty extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1001;
    public static final String ACTION_USER_LOGIN = "com.news.yazhidao.ACTION_USER_LOGIN";
    public static final String KEY_INTENT_USER_URL = "key_intent_user_url";

    private ChannelTabStrip mChannelTabStrip;
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private ImageView mChannelExpand;
    private ChannelItemDao mChannelItemDao;
    private ImageView mTopSearch;
    private SimpleDraweeView mMainUserLogin;
    private Handler mHandler = new Handler();
    private UserLoginReceiver mReceiver;
    private ProgressBar mTopRefreshProgress;
    private ImageView mTopRefresh;

    private class UserLoginReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USER_LOGIN.equals(intent.getAction())) {
                String url = intent.getStringExtra(KEY_INTENT_USER_URL);
                if (!TextUtil.isEmptyString(url)) {
                    mMainUserLogin.setImageURI(Uri.parse(url));
                }
            }
        }
    }
    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_main);
    }

    @Override
    protected void initializeViews() {
        mChannelItemDao = new ChannelItemDao(this);
        mChannelTabStrip = (ChannelTabStrip) findViewById(R.id.mChannelTabStrip);
        mTopSearch = (ImageView)findViewById(R.id.mTopSearch);
        mTopSearch.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOffscreenPageLimit(1);
        mChannelExpand = (ImageView)findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
        mMainUserLogin = (SimpleDraweeView)findViewById(R.id.mMainUserLogin);
        mMainUserLogin.setOnClickListener(this);
        mTopRefreshProgress = (ProgressBar)findViewById(R.id.mTopRefreshProgress);
        mTopRefresh = (ImageView)findViewById(R.id.mTopRefresh);
        /**更新右下角用户登录图标*/
        User user = SharedPreManager.getUser(this);
        if (user != null){
            if (!TextUtil.isEmptyString(user.getUserIcon())){
                mMainUserLogin.setImageURI(Uri.parse(user.getUserIcon()));
            }
        }
        /**注册用户登录广播*/
        mReceiver = new UserLoginReceiver();
        IntentFilter filter = new IntentFilter(ACTION_USER_LOGIN);
        registerReceiver(mReceiver,filter);
    }

    /**
     * 开始顶部 progress 刷新动画
     */
    public void startTopRefresh(){
        mTopRefresh.setVisibility(View.INVISIBLE);
        mTopRefreshProgress.setVisibility(View.VISIBLE);
    }

    /**
     * 停止顶部 progress 刷新动画
     */
    public void stopTopRefresh(){
        mTopRefresh.setVisibility(View.VISIBLE);
        mTopRefreshProgress.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void loadData() {
        //添加umeng更新
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    UmengUpdateAgent.update(MainAty.this);
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mTopSearch:
                Intent topicSearch = new Intent(MainAty.this,TopicSearchAty.class);
                startActivity(topicSearch);
                break;
            case R.id.mChannelExpand:
                Intent channelOperate = new Intent(MainAty.this, ChannelOperateAty.class);
                startActivityForResult(channelOperate, REQUEST_CODE);
                break;
            case R.id.mMainUserLogin:
                LoginPopupWindow window1 = new LoginPopupWindow(this, new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        mMainUserLogin.setImageURI(null);
                    }
                });
                window1.showAtLocation(getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_CODE){
            ArrayList<ChannelItem> channelItems = (ArrayList<ChannelItem>) data.getSerializableExtra(ChannelOperateAty.KEY_USER_SELECT);
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setViewPager(mViewPager);
        }
    }

    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
        private boolean isFirstInit = true;
        private final SparseArray<WeakReference<Fragment>> mFragmentArray = new SparseArray<WeakReference<Fragment>>();

        private ArrayList<ChannelItem> mChannelItems = new ArrayList<>();

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mChannelItems = mChannelItemDao.queryForSelected();
        }
        public void setmChannelItems(ArrayList<ChannelItem> pChannelItems){
            this.mChannelItems = pChannelItems;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mChannelItems.get(position).getName();
        }

        @Override
        public int getCount() {
            return mChannelItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            WeakReference<Fragment> mWeakFgt = mFragmentArray.get(position);
            if (mWeakFgt != null && mWeakFgt.get() != null) {
                return mWeakFgt.get();
            }
            return NewsFeedFgt.newInstance(mChannelItems.get(position).getId());
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.e("jigang", "instantiateItem:" + position);
            WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
            if (mWeakFragment != null && mWeakFragment.get() != null) {
                return mWeakFragment.get();
            }
            Fragment fgt = (Fragment) super.instantiateItem(container, position);
            mFragmentArray.put(position, new WeakReference<>(fgt));
            return fgt;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            Log.e("jigang", "destroyItem:" + position);
            WeakReference<Fragment> mWeakFragment = mFragmentArray.get(position);
            if (mWeakFragment != null) {
                mWeakFragment.clear();
            }
        }
    }
}
