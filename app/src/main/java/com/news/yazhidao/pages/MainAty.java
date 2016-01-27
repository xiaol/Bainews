package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.LoginPopupWindow;
import com.news.yazhidao.widget.channel.ChannelTabStrip;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/10/28.
 * 主界面
 */
public class MainAty extends BaseActivity implements View.OnClickListener, NewsFeedFgt.NewsSaveDataCallBack {

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
    private long mLastPressedBackKeyTime;
    private ArrayList<ChannelItem> mSelChannelItems;//默认展示的频道
    private HashMap<String, ArrayList<NewsFeed>> mSaveData = new HashMap<>();
    //baidu Map
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    @Override
    public void result(String channelId, ArrayList<NewsFeed> results) {
        mSaveData.put(channelId, results);
    }


    private class UserLoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USER_LOGIN.equals(intent.getAction())) {
                String url = intent.getStringExtra(KEY_INTENT_USER_URL);
                if (!TextUtil.isEmptyString(url)) {
//                    mMainUserLogin.setImageURI(Uri.parse(url));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }

    @Override
    protected void initializeViews() {
        mChannelItemDao = new ChannelItemDao(this);
        mSelChannelItems = new ArrayList<>();
        mChannelTabStrip = (ChannelTabStrip) findViewById(R.id.mChannelTabStrip);
        mTopSearch = (ImageView) findViewById(R.id.mTopSearch);
        mTopSearch.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mChannelExpand = (ImageView) findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
        mMainUserLogin = (SimpleDraweeView) findViewById(R.id.mMainUserLogin);
        mMainUserLogin.setOnClickListener(this);

        /**更新右下角用户登录图标*/
        User user = SharedPreManager.getUser(this);
        if (user != null) {
            if (!TextUtil.isEmptyString(user.getUserIcon())) {
//                mMainUserLogin.setImageURI(Uri.parse(user.getUserIcon()));
            }
        }
        /**注册用户登录广播*/
        mReceiver = new UserLoginReceiver();
        IntentFilter filter = new IntentFilter(ACTION_USER_LOGIN);
        registerReceiver(mReceiver, filter);
        //baidu Map
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initLocation();
        mLocationClient.start();
    }

    /**
     * 开始顶部 progress 刷新动画
     */
    public void startTopRefresh() {
    }

    /**
     * 停止顶部 progress 刷新动画
     */
    public void stopTopRefresh() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
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
        switch (v.getId()) {
            case R.id.mTopSearch:
                Intent topicSearch = new Intent(MainAty.this, TopicSearchAty.class);
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
//                        mMainUserLogin.setImageURI(null);
                    }
                });
                window1.showAtLocation(getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_CODE) {
            ArrayList<ChannelItem> channelItems = (ArrayList<ChannelItem>) data.getSerializableExtra(ChannelOperateAty.KEY_USER_SELECT);
            int currPosition = mViewPager.getCurrentItem();
            ChannelItem item1 = mSelChannelItems.get(currPosition);
            int index = -1;
            for (int i = 0; i < channelItems.size(); i++) {
                ChannelItem item = channelItems.get(i);
                if (item1.getId().equals(item.getId())) {
                    index = i;
                }
            }
            if (index == -1) {
                Logger.e("jigang", "index = " + index);
                index = currPosition > channelItems.size() - 1 ? channelItems.size() - 1 : currPosition;
            }
            mViewPager.setCurrentItem(index);
            Fragment item = mViewPagerAdapter.getItem(index);
            if (item != null) {
                ((NewsFeedFgt) item).setNewsFeed(mSaveData.get(item1.getId()));
            }
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setViewPager(mViewPager);

        }
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

    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mSelChannelItems = mChannelItemDao.queryForSelected();
        }

        public void setmChannelItems(ArrayList<ChannelItem> pChannelItems) {
            mSelChannelItems = pChannelItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mSelChannelItems.get(position).getName();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        @Override
        public int getCount() {
            return mSelChannelItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt feedFgt = NewsFeedFgt.newInstance(channelId);
            feedFgt.setNewsSaveDataCallBack(MainAty.this);
            return feedFgt;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt fgt = (NewsFeedFgt) super.instantiateItem(container, position);
            ArrayList<NewsFeed> newsFeeds = mSaveData.get(channelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
                fgt.refreshData();
            } else {
                fgt.setNewsFeed(newsFeeds);
            }
            return fgt;
        }
    }
}
