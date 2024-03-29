package com.news.yazhidao.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.DataCleanManager;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * Created by fengjigang on 16/4/7.
 */
public class SettingAty extends Activity implements View.OnClickListener {

    public final static int RESULT_CODE = 1008;
    public static final String KEY_NEED_NOT_SETTING = "key_need_not_setting";
    private static final String TAG = "SettingAty";

    private TextView mSetttingLogout;
    private User user;
    private View mSettingPushSwitch;
    private ImageView mSettingPushImg;
    private RadioGroup mRadioGroup;
    private View mSettingClearCache;
    private View mSettingtLeftBack;
    private View mSettingAbout;
    private View mSettingPrivacyPolicy;
    private View mSettingUpdate;
    private View mSettingOfferWall;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView();
        initializeViews();
        loadData();
    }

//    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_setting);
//        NccOfferWallAPI.setPlatformId("4723e8b862a0ad34598189a35cf713b8");
//        NccOfferWallAPI.init(this);
//        NccOfferWallAPI
//                .setOnCloseListener(new NccOfferWallListener<Void>() {
//                    @Override
//                    public void onSucceed(Void result) {
//
//                        Logger.e("aaa","应用墙关闭了！");
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorMsg) {
//                    }
//                });
//        NccOfferWallAPI
//                .setOnActivatedListener(new NccOfferWallListener<Point>() {
//                    @Override
//                    public void onSucceed(Point result) {
//                        Toast.makeText(
//                                SettingAty.this,
//                                "应用激活了：balance=" + result.balance + " total="
//                                        + result.total + " used=" + result.used,
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorMsg) {
//                    }
//                });
    }

//    @Override
    protected void initializeViews() {
        mSettingtLeftBack = findViewById(R.id.mSettingtLeftBack);
        mSettingtLeftBack.setOnClickListener(this);
        mSetttingLogout = (TextView) findViewById(R.id.mSetttingLogout);
        mSetttingLogout.setOnClickListener(this);
        mSettingPushSwitch = findViewById(R.id.mSettingPushSwitch);
        mSettingPushSwitch.setOnClickListener(this);
        mSettingPushImg = (ImageView) findViewById(R.id.mSettingPushImg);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);
        mSharedPreferences = getSharedPreferences("showflag", MODE_PRIVATE);
        int saveFont = mSharedPreferences.getInt("textSize", 0);
        switch (saveFont) {
            case CommonConstant.TEXT_SIZE_NORMAL:
                mRadioGroup.check(R.id.mRadioNormal);
                break;
            case CommonConstant.TEXT_SIZE_BIG:
                mRadioGroup.check(R.id.mRadioBig);
                break;
            case CommonConstant.TEXT_SIZE_BIGGER:
                mRadioGroup.check(R.id.mRadioHuge);
                break;
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mRadioNormal:
                        mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_NORMAL).commit();
                        break;
                    case R.id.mRadioBig:
                        mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_BIG).commit();
                        break;
                    case R.id.mRadioHuge:
                        mSharedPreferences.edit().putInt("textSize", CommonConstant.TEXT_SIZE_BIGGER).commit();
                        break;
                }
            }
        });
        mSettingClearCache = findViewById(R.id.mSettingClearCache);
        mSettingClearCache.setOnClickListener(this);
        mSettingAbout = findViewById(R.id.mSettingAbout);
        mSettingAbout.setOnClickListener(this);
        mSettingPrivacyPolicy = findViewById(R.id.mSettingPrivacyPolicy);
        mSettingPrivacyPolicy.setOnClickListener(this);
        mSettingUpdate = findViewById(R.id.mSettingUpdate);
        mSettingUpdate.setOnClickListener(this);
        mSettingOfferWall = findViewById(R.id.mSettingOfferWall);
        mSettingOfferWall.setOnClickListener(this);
    }

//    @Override
//    protected boolean translucentStatus() {
//        return false;
//    }
//
//    @Override
    protected void loadData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = SharedPreManager.getUser(this);
        if (user != null && !user.isVisitor()) {
            mSetttingLogout.setText("退出登录");
            mSetttingLogout.setTextColor(getResources().getColor(R.color.new_color2));
        } else {
            mSetttingLogout.setText("点击登录");
            mSetttingLogout.setTextColor(getResources().getColor(R.color.new_color1));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSettingtLeftBack:
                finish();
                break;
            case R.id.mSettingPushSwitch:
                PushAgent pushAgent = PushAgent.getInstance(this);
                if (pushAgent.isEnabled()) {
                    pushAgent.disable();
                    mSettingPushImg.setImageResource(R.drawable.ic_setting_push_off);
                } else {
                    pushAgent.enable();
                    mSettingPushImg.setImageResource(R.drawable.ic_setting_push_on);
                }
                break;
            case R.id.mSettingClearCache:
                AlertDialog.Builder clearBuilder = new AlertDialog.Builder(this);
                clearBuilder.setMessage("缓存文件可以帮助您节约流量,但较大时会占用较多的磁盘空间。\n确定开始清理吗?");
                clearBuilder.setTitle("提示");
                clearBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//                1.清理fresco 中缓存的图片数据
//                        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                        imagePipeline.clearCaches();
//                2.清理webview 中缓存的数据
                        DataCleanManager.clearWebViewCache(SettingAty.this);
//                3.删除缓存的新闻数据
                        NewsFeedDao newsFeedDao = new NewsFeedDao(SettingAty.this);
                        newsFeedDao.deleteAllData();
                        try {
                            ToastUtil.toastShort("清理缓存已完成");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                clearBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                clearBuilder.create().show();
                break;
            case R.id.mSettingAbout:
                Intent aboutAty = new Intent(this, AboutAty.class);
                startActivity(aboutAty);
                break;
            case R.id.mSettingPrivacyPolicy:
                Intent privacyAty = new Intent(this, PrivacyPolicyAty.class);
                startActivity(privacyAty);
                break;
            case R.id.mSettingUpdate:
                Logger.e("jigang","update=");
                UmengUpdateAgent.setUpdateAutoPopup(false);
                UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                    @Override
                    public void onUpdateReturned(int status, UpdateResponse updateResponse) {
                        if (status == UpdateStatus.Yes) {
                            UmengUpdateAgent.showUpdateDialog(SettingAty.this, updateResponse);
                            Logger.e("jigang","update=");
                        } else {
                            ToastUtil.toastShort("已是最新版本");
                        }
                    }
                });
                UmengUpdateAgent.update(this);
                break;
            case R.id.mSetttingLogout:
                if (user != null && !user.isVisitor()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("确认退出吗?");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SharedPreManager.deleteUser(SettingAty.this);
                            setResult(RESULT_CODE, null);
                            sendBroadcast(new Intent(MainAty.ACTION_USER_LOGOUT));
                            finish();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();

                } else {
                    Intent loginAty = new Intent(this, LoginAty.class);
                    loginAty.putExtra(KEY_NEED_NOT_SETTING, true);
                    startActivity(loginAty);
                }
                break;
            case R.id.mSettingOfferWall:
//                NccOfferWallAPI.open(this, new NccOfferWallListener<Void>() {
//                    @Override
//                    public void onSucceed(Void result) {
//                        Logger.e("aaa","打开应用墙成功！");
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorMsg) {
//                        Logger.e("aaa","打开应用墙失败 --> " + errorCode + " --> "
//                                + errorMsg);
//                    }
//                });
                break;
        }
    }
    @Override
    protected void onDestroy() {
//        NccOfferWallAPI.destroy(this);
        super.onDestroy();
    }

}
