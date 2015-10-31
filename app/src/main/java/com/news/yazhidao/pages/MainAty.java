package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

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
        mChannelExpand = (ImageView)findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
        mMainUserLogin = (SimpleDraweeView)findViewById(R.id.mMainUserLogin);
        mMainUserLogin.setOnClickListener(this);
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
            mViewPagerAdapter.setCatalogs(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setViewPager(mViewPager);
        }
    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<ChannelItem> catalogs = new ArrayList<>();

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            catalogs = mChannelItemDao.queryForSelected();
        }
        public void setCatalogs(ArrayList<ChannelItem> pChannelItems){
            this.catalogs = pChannelItems;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return catalogs.get(position).getName();
        }

        @Override
        public int getCount() {
            return catalogs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return NewsFeedFgt1.newInstance(position);
        }

    }
}
