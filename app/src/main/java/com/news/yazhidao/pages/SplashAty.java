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
import com.google.gson.Gson;
import com.news.yazhidao.R;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.UploadLogDataEntity;
import com.news.yazhidao.entity.UploadLogEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.entity.Visitor;
import com.news.yazhidao.net.volley.RegisterVisitorRequest;
import com.news.yazhidao.net.volley.UpLoadLogRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONException;
import org.json.JSONObject;

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

        registerVisitor();

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
    }

    /**
     * 注册游客身份,获取访问所有接口数据的token
     */
    private void registerVisitor() {
        final Visitor visitor = SharedPreManager.getVisitor();
        User user = SharedPreManager.getUser(this);
        if (user == null){
            if (visitor == null) {
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("utype",2);
                    requestBody.put("platform",2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RegisterVisitorRequest jsonObjectRequest = new RegisterVisitorRequest(requestBody.toString(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Visitor visitor = Visitor.json2Visitor(response.toString());
                        SharedPreManager.saveVisitor(visitor);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.e(TAG,"error" +error.getMessage());
                    }
                });
                YaZhiDaoApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
            }
        }
    }

    @Override
    protected void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean showGuidePage = SharedPreManager.getBoolean(CommonConstant.FILE_USER, CommonConstant.KEY_USER_NEED_SHOW_GUIDE_PAGE);
                if (true) {
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
