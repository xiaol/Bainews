package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.bumptech.glide.Glide;
import com.github.jinsedeyuzhou.VPlayPlayer;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewNewsFeedAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.AdUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.FeedDislikePopupWindow;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.channel.ChannelTabStrip;
import com.news.yazhidao.widget.tag.TagCloudLayout;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 15/10/28.
 * 主界面
 */
public class MainAty extends BaseActivity implements View.OnClickListener, NewsFeedFgt.NewsSaveDataCallBack, SharePopupWindow.ShareDismiss {

    public static final int REQUEST_CODE = 1001;
    public static final String ACTION_USER_LOGIN = "com.news.yazhidao.ACTION_USER_LOGIN";
    public static final String ACTION_USER_LOGOUT = "com.news.yazhidao.ACTION_USER_LOGOUT";
    public static final String ACTION_FOUCES = "com.news.yazhidao.ACTION_FOUCES";
    public static final String ACTION_SHOW_SHARE = "com.news.yazhidao.ACTION_SHOW_SHARE";
    public static final String KEY_INTENT_USER_URL = "key_intent_user_url";
    public static final String KEY_INTENT_CURRENT_POSITION = "key_intent_current_position";
    public static final String KEY_CURRENT_CHANNEL = "key_current_channel";
    private ArrayList<ChannelItem> mUnSelChannelItems;

    private ChannelTabStrip mChannelTabStrip;
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPagerAdapter;
    private ImageView mChannelExpand, mivShareBg;
    private ChannelItemDao mChannelItemDao;
    private Handler mHandler = new Handler();
    private UserLoginReceiver mReceiver;
    private long mLastPressedBackKeyTime;
    private ArrayList<ChannelItem> mSelChannelItems;//默认展示的频道
    private HashMap<String, ArrayList<NewsFeed>> mSaveData = new HashMap<>();
    private ImageView mUserCenter;
    private TextView mtvNewWorkBar;
    private ConnectivityManager mConnectivityManager;
    private IntentFilter mFilter;
    public VPlayPlayer vPlayPlayer;
    public NewsFeed newsFeedVideo;
    /**
     * 自定义的PopWindow
     */
    FeedDislikePopupWindow dislikePopupWindow;
    SharePopupWindow mSharePopupWindow;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    private ChannelItem mCurrentChannel;
    private int mCurrentChannelPos;
    private TelephonyManager mTelephonyManager;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private RelativeLayout mrlMain, mainContainer;
    private boolean isLogin;

    @Override
    public void result(String channelId, ArrayList<NewsFeed> results) {
        mSaveData.put(channelId, results);
    }


    private class UserLoginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USER_LOGIN.equals(action)) {
                String url = intent.getStringExtra(KEY_INTENT_USER_URL);
                Logger.e("liang", "url main=" + url);
                if (!TextUtil.isEmptyString(url)) {
                    Logger.e("jigang", "user login------1111");
                    setUserCenterIcon(Uri.parse(url));
                }
            } else if (ACTION_USER_LOGOUT.equals(action)) {
                Logger.e("jigang", "user login------2222");
                setUserCenterIcon(null);
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    /////////////网络连接
                    String name = netInfo.getTypeName();

                    if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        /////WiFi网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                        /////有线网络

                    } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        /////////3g网络

                    }
                    mtvNewWorkBar.setVisibility(View.GONE);
                } else {
                    ////////网络断开
                    mtvNewWorkBar.setVisibility(View.VISIBLE);
                }
            } else if (ACTION_FOUCES.equals(action)) {
                int current_position = intent.getIntExtra(KEY_INTENT_CURRENT_POSITION, 0);
                channelItems = mChannelItemDao.queryForSelected();
                mViewPager.setCurrentItem(current_position);
                mCurrentChannel = channelItems.get(current_position);
                Fragment item = mViewPagerAdapter.getItem(current_position);
                if (item != null) {
                    ((NewsFeedFgt) item).setNewsFeed(null);
                    ((NewsFeedFgt) item).setChannelId("1000");
                }
                mViewPagerAdapter.setmChannelItems(channelItems);
                mViewPagerAdapter.notifyDataSetChanged();
                mChannelTabStrip.setViewPager(mViewPager);
                mChannelTabStrip.scrollTo(0, 0);
                Logger.e("jigang", "--- onActivityResult");
            } else if (ACTION_SHOW_SHARE.equals(action)) {
                NewsFeed newsFeed = (NewsFeed) intent.getSerializableExtra("newsfeed");
                mSharePopupWindow = new SharePopupWindow(MainAty.this, MainAty.this);
                mSharePopupWindow.setFavoriteGone();
                mSharePopupWindow.setVideo(true);
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                String remark = newsFeed.getDescr();
                String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(newsFeed.getUrl()) + "&interface";
                mSharePopupWindow.setTitleAndNid(newsFeed.getTitle(), newsFeed.getNid(), remark);
                mSharePopupWindow.showAtLocation(mrlMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }
    }

    @Override
    public void shareDismiss() {
        mivShareBg.setVisibility(View.GONE);
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mSharePopupWindow = null;
    }
//
//    private Handler handler;
//
//
//    public void setHandler(Handler handler) {
//        handler = handler;
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
//        {
//            mainContainer.setVisibility(View.VISIBLE);
//        }else
//        {
//            mainContainer.setVisibility(View.VISIBLE);
//        }
//        Log.e("NewsFeedFgt","MainAty");
//        Message msg=new Message();
//        msg.obj=newConfig;
//        msg.what=100;
//        handler.sendMessage(msg);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_main);
    }

    /**
     * 长按菜单键会弹出菜单（注销无用的）
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
//    }
    @Override
    protected void initializeViews() {
        vPlayPlayer = new VPlayPlayer(this);
        AnalyticsConfig.setChannel("official");
        MobclickAgent.onEvent(this, "bainews_user_assess_app");
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        mChannelItemDao = new ChannelItemDao(this);
        mSelChannelItems = new ArrayList<>();
        mrlMain = (RelativeLayout) findViewById(R.id.main_layout);
        mChannelTabStrip = (ChannelTabStrip) findViewById(R.id.mChannelTabStrip);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        mViewPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        mainContainer = (RelativeLayout) findViewById(R.id.main_container);
        mtvNewWorkBar = (TextView) findViewById(R.id.mNetWorkBar);
        mtvNewWorkBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mUserCenter = (ImageView) findViewById(R.id.mUserCenter);
        mUserCenter.setOnClickListener(this);
        setUserCenterIcon(null);
        mViewPager.setOffscreenPageLimit(2);
        mChannelExpand = (ImageView) findViewById(R.id.mChannelExpand);
        mChannelExpand.setOnClickListener(this);
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mChannelTabStrip.setViewPager(mViewPager);
//                mUserCenter.setImageURI(Uri.parse("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0"));
        dislikePopupWindow = (FeedDislikePopupWindow) findViewById(R.id.feedDislike_popupWindow);
        dislikePopupWindow.setVisibility(View.GONE);
        dislikePopupWindow.setItemClickListerer(new TagCloudLayout.TagItemClickListener() {
            @Override
            public void itemClick(int position) throws AuthFailureError {
                switch (position) {
                    case 0://不喜欢
//                        NewsFeedFgt newsFeedFgt= (NewsFeedFgt) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
//                        newsFeedFgt.disLikeItem();
                    case 1://重复、旧闻
                    case 2://内容质量差
                    case 3://不喜欢
                        final User user = SharedPreManager.getUser(MainAty.this);
                        if (user != null) {
                            RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
                            Map<String, Integer> map = new HashMap<>();
                            map.put("nid", dislikePopupWindow.getNewsId());
                            map.put("uid", user.getMuid());
                            map.put("reason", position);
                            JSONObject jsonObject = new JSONObject(map);
                            JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.POST, HttpConstant.URL_DISSLIKE_RECORD, jsonObject,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    HashMap<String, String> header = new HashMap<>();
                                    header.put("Authorization", "Basic " + user.getAuthorToken());
                                    header.put("Content-Type", "application/json");
                                    header.put("X-Requested-With", "*");
                                    return header;
                                }
                            };
                            request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
                            requestQueue.add(request);
                        }
                        mNewsFeedAdapter.disLikeDeleteItem();
                        dislikePopupWindow.setVisibility(View.GONE);
                        ToastUtil.showReduceRecommendToast(MainAty.this);
                        break;
                }
            }
        });
        mReceiver = new UserLoginReceiver();
        mFilter = new IntentFilter();
        /**注册用户登录广播*/
        mFilter.addAction(ACTION_USER_LOGIN);
        mFilter.addAction(ACTION_USER_LOGOUT);
        mFilter.addAction(ACTION_FOUCES);
        mFilter.addAction(ACTION_SHOW_SHARE);
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        /**更新右下角用户登录图标*/
        User user = SharedPreManager.getUser(this);
        if (user != null && !user.isVisitor()) {
            if (!TextUtil.isEmptyString(user.getUserIcon())) {
                Logger.e("jigang", "user login------3333");
                setUserCenterIcon(Uri.parse(user.getUserIcon()));
            }
        }
        /**请求系统权限*/
        try {
            getDeviceImei();
        } catch (Exception e) {
            SharedPreManager.save("flag", "imei", "");
        }
        uploadChannelInformation();
    }

    /**
     * 保存设置IMEI
     */
    private void getDeviceImei() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            String deviceid = mTelephonyManager.getDeviceId();
            SharedPreManager.save("flag", "imei", deviceid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver 最好放到onResume
        registerReceiver(mReceiver, mFilter);
        User user = SharedPreManager.getUser(this);
        if (user != null && !user.isVisitor() && !SharedPreManager.getBoolean(CommonConstant.FILE_USER, "isshowsubscription", false) && SharedPreManager.getBoolean(CommonConstant.FILE_USER, "isusericonlogin", false)) {
            SharedPreManager.save(CommonConstant.FILE_USER, "isshowsubscription", true);
            Intent intent = new Intent(this, SubscriptionAty.class);
            startActivity(intent);
        }
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
        vPlayPlayer.onDestory();
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
                UmengUpdateAgent.setUpdateAutoPopup(true);
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
                mCurrentChannel = mSelChannelItems.get(mViewPager.getCurrentItem());
                mCurrentChannelPos = mViewPager.getCurrentItem();
                MobclickAgent.onEvent(this, "user_open_channel_edit_page");
                startActivityForResult(channelOperate, REQUEST_CODE);
                break;
            case R.id.mUserCenter:
                User user = SharedPreManager.getUser(this);
//                //FIXME debug
//                if (user == null){
//                    user = new User();
//                    user.setUserName("forward_one");
//                    user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
//                }
                if (user != null && !user.isVisitor()) {
                    Intent userCenterAty = new Intent(this, UserCenterAty.class);
                    startActivity(userCenterAty);
                } else {
                    SharedPreManager.save(CommonConstant.FILE_USER, "isusericonlogin", true);
                    Intent loginAty = new Intent(this, LoginAty.class);
                    startActivity(loginAty);
                }
                MobclickAgent.onEvent(this, "qidian_user_open_user_center");
                break;
        }
    }

    private void setUserCenterIcon(Uri uri) {
        Glide.with(MainAty.this).load(uri).placeholder(R.drawable.btn_user_center).transform(new CommonViewHolder.GlideCircleTransform(MainAty.this, 2, getResources().getColor(R.color.white))).into(mUserCenter);
    }

    ChannelItem item1;
    ArrayList<ChannelItem> channelItems;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_CODE) {
            channelItems = (ArrayList<ChannelItem>) data.getSerializableExtra(ChannelOperateAty.KEY_USER_SELECT);
            int currPosition = mViewPager.getCurrentItem();
            item1 = mSelChannelItems.get(currPosition);
            int index = -1;
            for (int i = 0; i < channelItems.size(); i++) {
                ChannelItem item = channelItems.get(i);
                if (item1.getId().equals(item.getId())) {
                    index = i;
                    break;
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
            Logger.e("jigang", "--- onActivityResult");
            mViewPagerAdapter.setmChannelItems(channelItems);
            mViewPagerAdapter.notifyDataSetChanged();
            mChannelTabStrip.setViewPager(mViewPager);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dislikePopupWindow.getVisibility() == View.VISIBLE) {//判断自定义的 popwindow 是否显示 如果现实按返回键关闭
                dislikePopupWindow.setVisibility(View.GONE);
                return true;
            }
            long pressedBackKeyTime = System.currentTimeMillis();
            if ((pressedBackKeyTime - mLastPressedBackKeyTime) < 2000) {
                finish();
            } else {
                if (vPlayPlayer != null) {
                    if (vPlayPlayer.onKeyDown(keyCode, event))
                        return true;
                }
                if (DeviceInfoUtil.isFlyme()) {
                    ToastUtil.toastShort(getString(R.string.press_back_again_exit));
                } else {
                    ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
                }
                mLastPressedBackKeyTime = pressedBackKeyTime;
                return true;
            }
        }else if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN||keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            if (vPlayPlayer!=null&&vPlayPlayer.isPlay()&&vPlayPlayer.handleVolumeKey(keyCode))
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            mSelChannelItems = mChannelItemDao.queryForSelected();
            mUnSelChannelItems = mChannelItemDao.queryForNormal();

            //统计用户频道订阅/非订阅 频道数
            HashMap<String, String> unSubChannel = new HashMap<>();
            unSubChannel.put("unsubscribed_channels", TextUtil.List2String(mUnSelChannelItems));
            MobclickAgent.onEventValue(MainAty.this, "user_unsubscribe_channels", unSubChannel, mUnSelChannelItems.size());

            HashMap<String, String> subChannel = new HashMap<>();
            subChannel.put("subscribed_channels", TextUtil.List2String(mSelChannelItems));
            MobclickAgent.onEventValue(MainAty.this, "user_subscribed_channels", subChannel, mSelChannelItems.size());

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
            Logger.e("jigang", "----viewpager getItemPosition " + object.getClass().getSimpleName());
            int newPos = mViewPager.getCurrentItem();
            String currentId = mCurrentChannel.getName();
            String newId = channelItems.get(newPos).getName();
            if (newPos == mCurrentChannelPos && currentId.equals(newId)) {
                return POSITION_UNCHANGED;
            } else {
                return POSITION_NONE;
            }
        }

        @Override
        public int getCount() {
            return mSelChannelItems.size();
        }

        @Override
        public Fragment getItem(int position) {
            Logger.e("jigang", "----viewpager getItem " + position);
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt feedFgt = NewsFeedFgt.newInstance(channelId);
            feedFgt.setNewsFeedFgtPopWindow(mNewsFeedFgtPopWindow);
            feedFgt.setNewsSaveDataCallBack(MainAty.this);
            return feedFgt;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Logger.e("jigang", "----viewpager instantiateItem " + position);
            String channelId = mSelChannelItems.get(position).getId();
            NewsFeedFgt fgt = (NewsFeedFgt) super.instantiateItem(container, position);
            ArrayList<NewsFeed> newsFeeds = mSaveData.get(channelId);
            if (TextUtil.isListEmpty(newsFeeds)) {
//                fgt.refreshData();
            } else {
                fgt.setNewsFeed(newsFeeds);
            }
            return fgt;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            Logger.e("jigang", "----viewpager destroyItem " + position);
        }

    }

    NewNewsFeedAdapter mNewsFeedAdapter;
    NewsFeedFgt.NewsFeedFgtPopWindow mNewsFeedFgtPopWindow = new NewsFeedFgt.NewsFeedFgtPopWindow() {
        @Override
        public void showPopWindow(int x, int y, String PubName, int newsid, NewNewsFeedAdapter mAdapter) {
            mNewsFeedAdapter = mAdapter;
            dislikePopupWindow.setSourceList("来源：" + PubName);
            dislikePopupWindow.setNewsId(newsid);
            dislikePopupWindow.showView(x, y - DeviceInfoUtil.getStatusBarHeight(MainAty.this));

        }

    };

    private void uploadChannelInformation() {
        if (SharedPreManager.getUser(this) != null) {
            try {
                final String requestUrl = HttpConstant.URL_UPLOAD_CHANNEL_INFORMATION;
                RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
                Long uid = null;
                if (SharedPreManager.getUser(MainAty.this) != null) {
                    uid = Long.valueOf(SharedPreManager.getUser(MainAty.this).getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("b", TextUtil.getBase64(AdUtil.getAdMessage(this, CommonConstant.NEWS_FEED_AD_ID)));
                jsonObject.put("province", SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                /**
                 * 1：奇点资讯， 2：黄历天气，3：纹字锁屏，4：猎鹰浏览器，5：白牌 6.纹字主题
                 */
                jsonObject.put("ctype", CommonConstant.NEWS_CTYPE);
                /**
                 * 1.ios 2.android 3.网页 4.无法识别
                 */
                jsonObject.put("ptype", CommonConstant.NEWS_PTYPE);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, requestUrl,
                        jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObj) {
                        Log.i("tag", "3333");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("tag", "4444");
                    }
                });
                requestQueue.add(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
