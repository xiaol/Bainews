package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.database.DiggerAlbumDao;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.FetchAlbumListListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.request.FetchAlbumListRequest;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.DiggerPopupWindow;
import com.news.yazhidao.widget.LoginModePopupWindow;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;

import cn.sharesdk.framework.PlatformDb;

/**
 * Created by fengjigang on 15/8/17.
 * 挖掘机
 */
public class DiggerAty extends BaseActivity {

    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;
    private FloatingActionMenu leftCenterMenu;
    private ArrayList<Album> albumList = new ArrayList<Album>();
    private LengjingFgt mLengJingFgt;
    private TextView mCommonHeaderTitle;
    private View mCommonHeaderLeftBack;
    private View mLayerMask;//点击底部+号时的遮罩

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_digger);
        mCommonHeaderTitle = (TextView)findViewById(R.id.mCommonHeaderTitle);
        mCommonHeaderTitle.setText(R.string.home_digger);
        mCommonHeaderLeftBack = findViewById(R.id.mCommonHeaderLeftBack);
        mCommonHeaderLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiggerAty.this.finish();
            }
        });
        mLayerMask = findViewById(R.id.mLayerMask);
        mLayerMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftCenterMenu != null) {
                    leftCenterMenu.close(true);
                }
            }
        });
    }

    @Override
    protected void initializeViews() {
        //FIXME 目前暂时酱紫写,后面有可能还有改动
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mLengJingFgt = new LengjingFgt(this);
        transaction.add(R.id.mDiggerLayout, mLengJingFgt);
        transaction.commit();
        addMenu();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断是否是别的app分享进来的
        Intent intent = getIntent();
        final String data = intent.getStringExtra(Intent.EXTRA_TEXT);
        String type = intent.getType();
        if ("text/plain".equals(type) && !TextUtils.isEmpty(data)) {
            //把页面设置在挖掘机
            //在LengJingFgt 中打开编辑页面,延时防止crash
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLengJingFgt.openEditWindow(TextUtil.getNewsTitle(data), TextUtil.getNewsUrl(data));
                }
            }, 800);
//            mLengJingFgt.openEditWindow(TextUtil.getNewsTitle(data), TextUtil.getNewsUrl(data));
        }
    }

    private void addMenu() {

        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int imgSize = getResources().getDimensionPixelSize(R.dimen.img_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int buttonMargin = getResources().getDimensionPixelOffset(R.dimen.btn_marginbottom);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconStar = new ImageView(this);
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.icon_lengjing_digger));

        starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        starParams.setMargins(redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin);
        fabIconStar.setLayoutParams(starParams);

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        leftCenterButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconStar, fabIconStarParams)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .setLayoutParams(starParams)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(this);

        int buttonContentSize = getResources().getDimensionPixelSize(R.dimen.sub_action_button_content_size);

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(buttonContentSize, buttonContentSize);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);

        ImageView lcIcon1 = new ImageView(this);
        ImageView lcIcon2 = new ImageView(this);
        ImageView lcIcon3 = new ImageView(this);


        lcIcon1.setImageResource(R.drawable.icon_lengjing_text);
        lcIcon2.setImageResource(R.drawable.icon_lengjing_url);
        lcIcon3.setImageResource(R.drawable.icon_lengjing_base);

        SubActionButton button1 = lCSubBuilder.setContentView(lcIcon1, blueContentParams).build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**首先判断用户是否登录,如果没有登录的话,则弹出登录框*/
                User user = SharedPreManager.getUser(DiggerAty.this);
                if (user == null){
                    final LoginModePopupWindow window = new LoginModePopupWindow(DiggerAty.this, new UserLoginListener() {
                        @Override
                        public void userLogin(String platform, PlatformDb platformDb) {
                            /**获取专辑列表数据*/
                            FetchAlbumListRequest.obtainAlbumList(DiggerAty.this, new FetchAlbumListListener() {
                                @Override
                                public void success(ArrayList<DiggerAlbum> resultList) {
                                    handleAlbumsData(resultList);
                                }

                                @Override
                                public void failure() {
                                    ToastUtil.toastShort("获取专辑失败!");
                                }
                            });
                        }

                        @Override
                        public void userLogout() {

                        }
                    }, null);
                    window.showAtLocation(DiggerAty.this.getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);
                    return;
                }
                //查看数据库中是否已经专辑数据,如果没有则联网获取
                ArrayList<DiggerAlbum> resultList = queryAlbumsFromDB();
                if (!TextUtil.isListEmpty(resultList)){
                    handleAlbumsData(resultList);
                }else{
                    /**获取专辑列表数据*/
                    FetchAlbumListRequest.obtainAlbumList(DiggerAty.this, new FetchAlbumListListener() {
                        @Override
                        public void success(ArrayList<DiggerAlbum> resultList) {
                            handleAlbumsData(resultList);
                        }

                        @Override
                        public void failure() {
                            ToastUtil.toastShort("获取专辑失败!");
                        }
                    });

                }


                leftCenterMenu.close(true);

            }
        });

        SubActionButton button3 = lCSubBuilder.setContentView(lcIcon3, blueContentParams).build();
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiggerAty.this, BaseTagActivity.class);
                startActivity(intent);
                leftCenterMenu.close(true);
            }
        });

        // Build another menu with custom options
        leftCenterMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
//                .addSubActionView(button2)
                .addSubActionView(button3)
                .setRadius(redActionMenuRadius)
                .setStartAngle(-125)
                .setEndAngle(-55)
                .attachTo(leftCenterButton)
                .build();


        leftCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                fabIconStar.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 135);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
                mLayerMask.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconStar.setRotation(135);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
                mLayerMask.setVisibility(View.GONE);
            }
        });

    }
    /**
     * 从数据库获取专辑数据
     * @return
     */
    private ArrayList<DiggerAlbum> queryAlbumsFromDB(){
        DiggerAlbumDao diggerAlbumDao = new DiggerAlbumDao(this);
        return diggerAlbumDao.querForAll();
    }

    /**
     * 处理获取到的专辑列表数据
     * @param resultList
     */
    private void handleAlbumsData(ArrayList<DiggerAlbum> resultList){
        if (!TextUtil.isListEmpty(resultList)) {
            albumList.clear();
            for (int i = 0; i < resultList.size(); i++) {
                Album album = new Album();
                album.setSelected(i == 0);
                album.setAlbum(resultList.get(i).getAlbum_title());
                album.setAlbumId(resultList.get(i).getAlbum_id());
                album.setId(resultList.get(i).getAlbum_img());
                albumList.add(album);
            }
            mLengJingFgt.setDiggerAlbums(resultList);
            DiggerPopupWindow window = new DiggerPopupWindow(mLengJingFgt, DiggerAty.this, 1 + "", albumList, 1, true, false);

            window.setFocusable(true);
            window.showAtLocation(DiggerAty.this.getWindow().getDecorView(), Gravity.CENTER
                    | Gravity.CENTER, 0, 0);

        }
    }
}
