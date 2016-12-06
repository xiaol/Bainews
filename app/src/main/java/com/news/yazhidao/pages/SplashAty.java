package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.AppsItemInfo;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.net.volley.FeedRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.GsonUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/3/30.
 */
public class SplashAty extends BaseActivity {

    private ImageView iv_splash_background;
    private boolean flag;
    private ImageView mSplashLine;
    private ImageView mSplashSlogan;
    private ImageView mSplashMask;
    private View mSplashContent;
    private TextView mSplashVersion;
    private String TAG = "SplashAty";
    private ChannelItemDao mChannelItemDao;
    //baidu Map
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
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
            //Receive Location 如果定位成功需要及时关闭定位,否则耗电太快
            if (location != null) {
                mLocationClient.stop();
            }
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
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE, location.getProvince());
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY, location.getCity());
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR, location.getAddrStr());
            Logger.i("BaiduLocationApiDem", sb.toString());
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION, GsonUtil.serialized(location.getAddress()));
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE, location.getLatitude() + "");
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE, location.getLongitude() + "");
            uploadInformation();
        }
    }

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }

    @Override
    protected void setContentView() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_splash);
        /**  梁帅：设置无图 */
//     SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES, true);

    }


    @Override
    protected void initializeViews() {
        mChannelItemDao = new ChannelItemDao(this);
        final SharedPreferences sp = getSharedPreferences("showflag", 0);
        flag = sp.getBoolean("isshow", false);
        iv_splash_background = (ImageView) findViewById(R.id.iv_splash_background);
        mSplashSlogan = (ImageView) findViewById(R.id.mSplashSlogan);
        mSplashMask = (ImageView) findViewById(R.id.mSplashMask);
        mSplashContent = findViewById(R.id.mSplashContent);
        mSplashVersion = (TextView) findViewById(R.id.mSplashVersion);
        mSplashVersion.setText(getResources().getString(R.string.app_name) + " v" + getResources().getString(R.string.app_version));
        int screenHeight = DeviceInfoUtil.getScreenHeight(this);
//        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_alpha_in);
//        mSplashSlogan.setAnimation(animation);
        mSplashLine = (ImageView) findViewById(R.id.mSplashLine);
        final Animation mask = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSplashMask.setAnimation(mask);
            }
        }, 100);
        mask.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSplashMask.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        SharedPreManager.getBoolean(CommonConstant.FILE_USER, "isshowsubscription", false);
//        UserManager.registerVisitor(this,null);


        /**
         * 日志上传
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                User user = SharedPreManager.getUser(SplashAty.this);
//                String mstrUserId = "";
//                if (user != null)
//                    mstrUserId = user.getUserId();
//                else
//                    return;
//                Logger.e("ccc", "主页的数据上传====" + SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN));
//                String mReadData = SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN);
//                Gson gson = new Gson();
//                UploadLogEntity uploadLogEntity = new UploadLogEntity();
//                if (mReadData != null && mReadData.length() != 0) {
//                    uploadLogEntity = gson.fromJson(mReadData, UploadLogEntity.class);
//                }
//                List<UploadLogDataEntity> data = uploadLogEntity.getData();
//
//                int size = data.size();
//                if (size < 200) {
//                    Log.e("ccc", "条数不够！！！！=======" + size);
//                    return;
//
//                }
//                Logger.e("aaa", "data==================" + data.toString());
//                for (int i = 0; i < (size % 100 == 0 ? size / 100 : size / 100 + 1); i++) {
//                    List<UploadLogDataEntity> dataScope = null;
//                    if (data.size() > 100) {
//                        dataScope = new ArrayList<UploadLogDataEntity>(data.subList(0, 99));
//                    } else {
//                        dataScope = data;
//                    }
//
//                    Logger.e("aaa", "dataScope==================" + dataScope.toString());
//                    RequestQueue requestQueue = Volley.newRequestQueue(SplashAty.this);
//                    String url = HttpConstant.URL_UPLOAD_LOG + "uid=" + mstrUserId + "&clas=1" +
//                            "&data=" + TextUtil.getBase64(gson.toJson(dataScope));
//                    Logger.d("aaa", "url===" + url);
//                    UpLoadLogRequest<String> request = new UpLoadLogRequest<String>(Request.Method.GET, String.class, url, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                        }
//                    });
//                    requestQueue.add(request);
//
//                    data.removeAll(dataScope);
//                    Log.e("ccc", "删除完成之后的数量！！！！=======" + data.size());
//                }
//                SharedPreManager.upLoadLogDelter(CommonConstant.UPLOAD_LOG_MAIN);
//
//            }
//        }).start();
//        Logger.e("jigang", "h=" + mSplashLine.getHeight() + ",w=" + mSplashLine.getWidth());
//        if (DeviceInfoUtil.isFlyme() || "meizu".equals(AnalyticsConfig.getChannel(this))) {
//            iv_splash_background.setVisibility(View.VISIBLE);
//        }
        getAppInfo();

        //baidu Map
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();
//        getChannelList();
    }

    private PackageManager pManager;
    // 用来记录应用程序的信息
    List<AppsItemInfo> list;

    public void getAppInfo() {
        // 获取图片、应用名、包名
        pManager = SplashAty.this.getPackageManager();
        List<PackageInfo> appList = getAllApps(SplashAty.this);

        list = new ArrayList<AppsItemInfo>();

        for (int i = 0; i < appList.size(); i++) {
            PackageInfo pinfo = appList.get(i);
            AppsItemInfo shareItem = new AppsItemInfo();
//            // 设置图片
//            shareItem.setIcon(pManager
//                    .getApplicationIcon(pinfo.applicationInfo));
            // 设置应用程序名字
            shareItem.setLabel(pManager.getApplicationLabel(
                    pinfo.applicationInfo).toString());
            // 设置应用程序的包名
            shareItem.setPackageName(pinfo.applicationInfo.packageName);

            list.add(shareItem);

        }
        Logger.e("aaa", "获取所有应用的名称：" + list.toString());
        boolean isDifferent = SharedPreManager.AppInfoSaveList(list);
        Logger.e("aaa", "isDifferent==" + isDifferent);
        try {
            Logger.e("aaa", "isDifferent==" + SharedPreManager.AppInfoGetList());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static List<PackageInfo> getAllApps(Context context) {

        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> packlist = pManager.getInstalledPackages(0);
        for (int i = 0; i < packlist.size(); i++) {
            PackageInfo pak = (PackageInfo) packlist.get(i);

            // 判断是否为非系统预装的应用程序
            // 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
            // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // 添加自己已经安装的应用程序
                apps.add(pak);
            }

        }
        return apps;
    }

    @Override
    protected void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean showGuidePage = SharedPreManager.getBoolean(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE);
                if (!showGuidePage) {
                    Intent intent = new Intent(SplashAty.this, GuideLoginAty.class);
                    startActivity(intent);
                    SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE, true);
                } else {
                    Intent mainAty = new Intent(SplashAty.this, MainAty.class);
                    startActivity(mainAty);
                }
                SplashAty.this.finish();

            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long openTimes = SharedPreManager.getLong(CommonConstant.FILE_USER, CommonConstant.KEY_USER_OPEN_APP);
        if (openTimes == 0) {
            SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_OPEN_APP, 1);

        } else {
            SharedPreManager.save(CommonConstant.FILE_USER, CommonConstant.KEY_USER_OPEN_APP, openTimes + 1);
        }
        //产生用户UUID
        SharedPreManager.saveUUID();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getChannelList() {
        RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
        FeedRequest<ArrayList<ChannelItem>> feedRequest = new FeedRequest<>(Request.Method.GET, new TypeToken<ArrayList<ChannelItem>>() {
        }.getType(), HttpConstant.URL_COMMON_CHANNEL_LIST, new Response.Listener<ArrayList<ChannelItem>>() {

            @Override
            public void onResponse(final ArrayList<ChannelItem> result) {
                ChannelItemDao channelItemDao = new ChannelItemDao(SplashAty.this);
                if (result != null) {
                    channelItemDao.deletaForAll();
                    channelItemDao.insertList(result);
                    channelItemDao.insert(new ChannelItem("1000", "关注", 0, false, "0"));
                }
                Log.i("tag", "ChannelItem:" + result.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("tag", "ChannelItem:" + error.toString());
            }
        });
        HashMap<String, String> header = new HashMap<>();
        feedRequest.setRequestHeader(header);
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
    }

    //上传地理位置等信息
    private void uploadInformation() {
        if (SharedPreManager.getUser(this) != null) {
            try {
                List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
                final JSONArray array = new JSONArray();
                for (int i = 0; i < packages.size(); i++) {
                    PackageInfo packageInfo = packages.get(i);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_id", packageInfo.packageName);
                    jsonObject.put("active", 1);
                    jsonObject.put("app_name", packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                    array.put(jsonObject);
                }
                /** 设置品牌 */
                final String brand = Build.BRAND;
                /** 设置设备型号 */
                final String platform = Build.MODEL;
                final String requestUrl = HttpConstant.URL_UPLOAD_INFORMATION;
                RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
                Long uid = null;
                if (SharedPreManager.getUser(SplashAty.this) != null) {
                    uid = Long.valueOf(SharedPreManager.getUser(SplashAty.this).getMuid());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", uid);
                jsonObject.put("province", SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_PROVINCE));
                jsonObject.put("city", SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_CITY));
                jsonObject.put("area", SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_ADDR));
                jsonObject.put("brand", brand);
                jsonObject.put("model", platform);
                jsonObject.put("apps", array);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST, requestUrl,
                        jsonObject, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObj) {

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                requestQueue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
