package com.news.yazhidao.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.google.gson.Gson;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.UploadLogEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.UpLoadLogRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.GsonUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.utils.manager.UserManager;

import java.util.ArrayList;
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
            Log.i("BaiduLocationApiDem", sb.toString());
            SharedPreManager.save(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_USER_LOCATION, GsonUtil.serialized(location.getAddress()));
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
    }


    @Override
    protected void initializeViews() {
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

        UserManager.registerVisitor(this,null);

        /**
         * 日志上传
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = SharedPreManager.getUser(SplashAty.this);
                String mstrUserId = "";
                if (user != null)
                    mstrUserId = user.getUserId();
                else
                    return;
                Logger.e("ccc", "主页的数据上传====" + SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN));
                String mReadData = SharedPreManager.upLoadLogGet(CommonConstant.UPLOAD_LOG_MAIN);
                Gson gson = new Gson();
                UploadLogEntity uploadLogEntity = new UploadLogEntity();
                if (mReadData != null && mReadData.length() != 0) {
                    uploadLogEntity = gson.fromJson(mReadData, UploadLogEntity.class);
                }
                List<UploadLogDataEntity> data = uploadLogEntity.getData();

                int size = data.size();
                if (size < 200) {
                    Log.e("ccc", "条数不够！！！！=======" + size);
                    return;

                }
                Logger.e("aaa", "data==================" + data.toString());
                for (int i = 0; i < (size % 100 == 0 ? size / 100 : size / 100 + 1); i++) {
                    List<UploadLogDataEntity> dataScope = null;
                    if (data.size() > 100) {
                        dataScope = new ArrayList<UploadLogDataEntity>(data.subList(0, 99));
                    } else {
                        dataScope = data;
                    }

                    Logger.e("aaa", "dataScope==================" + dataScope.toString());
                    RequestQueue requestQueue = Volley.newRequestQueue(SplashAty.this);
                    String url = HttpConstant.URL_UPLOAD_LOG + "uid=" + mstrUserId + "&clas=1" +
                            "&data=" + TextUtil.getBase64(gson.toJson(dataScope));
                    Logger.d("aaa", "url===" + url);
                    UpLoadLogRequest<String> request = new UpLoadLogRequest<String>(Request.Method.GET, String.class, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    requestQueue.add(request);

                    data.removeAll(dataScope);
                    Log.e("ccc", "删除完成之后的数量！！！！=======" + data.size());
                }
                SharedPreManager.upLoadLogDelter(CommonConstant.UPLOAD_LOG_MAIN);

            }
        }).start();
//        Logger.e("jigang", "h=" + mSplashLine.getHeight() + ",w=" + mSplashLine.getWidth());
//        if (DeviceInfoUtil.isFlyme() || "meizu".equals(AnalyticsConfig.getChannel(this))) {
//            iv_splash_background.setVisibility(View.VISIBLE);
//        }
        //baidu Map
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();
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
}
