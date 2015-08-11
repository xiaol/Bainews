package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.database.DiggerAlbumDao;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.FetchAlbumListListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.request.FetchAlbumListRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.DiggerPopupWindow;
import com.news.yazhidao.widget.LoginModePopupWindow;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;

import cn.sharesdk.framework.PlatformDb;


public class HomeAty extends BaseActivity {

    //PageSlidingTabStrip
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private LinearLayout ll_layer;
    private long mLastPressedBackKeyTime;
    //Viewpager 索引默认是1
    private int mViewPagerIndex = 1;
    //棱镜界面对应的fragment
    private LengjingFgt mLengJingFgt;

    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;
    private FloatingActionMenu leftCenterMenu;
    private ArrayList<Album> albumList = new ArrayList<Album>();
    private LinearLayout ll_darker_layer;

    @Override
    protected void setContentView() {

        mLengJingFgt = new LengjingFgt(this);
        GlobalParams.context = HomeAty.this;
        setContentView(R.layout.activity_viewpager);

        ll_darker_layer = (LinearLayout) findViewById(R.id.ll_darker_layer);
        ll_darker_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(leftCenterMenu != null) {
                    leftCenterMenu.close(true);
                }
            }
        });
        //判断是否是别的app分享进来的
        Intent intent = getIntent();
        final String data = intent.getStringExtra(Intent.EXTRA_TEXT);
        String type = intent.getType();
        if ("text/plain".equals(type) && !TextUtils.isEmpty(data)) {
            //把页面设置在挖掘机
            mViewPagerIndex = 2;
            //在LengJingFgt 中打开编辑页面,延时防止crash
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLengJingFgt.openEditWindow(TextUtil.getNewsTitle(data), TextUtil.getNewsUrl(data));
                }
            }, 800);
        }
    }
    @Override
    protected void initializeViews() {
        if (leftCenterButton == null) {
            addMenu();
        } else {
            ToastUtil.toastLong("adsfefvrefve");
        }

        //pagesliding
        mTintManager = new SystemBarTintManager(HomeAty.this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setNavigationBarTintEnabled(true);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //tabs 需要动态设置高度(4.4要使用沉浸式)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            tabs.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this,30)));
        }
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (position == 2) {
                    if (leftCenterButton != null) {
                        leftCenterButton.detach(null);
                        leftCenterButton.attach(starParams, null);
                    }
                } else {
                    if (leftCenterButton != null) {
                        leftCenterMenu.close(true);
                        leftCenterButton.detach(null);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        GlobalParams.tabs = tabs;

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        GlobalParams.pager = pager;
        pager.setCurrentItem(mViewPagerIndex);
        changeColor(getResources().getColor(R.color.tab_blue));
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

        final ImageView fabIconStar = new ImageView(HomeAty.this);
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

        leftCenterButton = new FloatingActionButton.Builder(HomeAty.this)
                .setContentView(fabIconStar, fabIconStarParams)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .setLayoutParams(starParams)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(HomeAty.this);

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

        ImageView lcIcon1 = new ImageView(HomeAty.this);
        ImageView lcIcon2 = new ImageView(HomeAty.this);
        ImageView lcIcon3 = new ImageView(HomeAty.this);


        lcIcon1.setImageResource(R.drawable.icon_lengjing_text);
        lcIcon2.setImageResource(R.drawable.icon_lengjing_url);
        lcIcon3.setImageResource(R.drawable.icon_lengjing_base);

        SubActionButton button1 = lCSubBuilder.setContentView(lcIcon1, blueContentParams).build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**首先判断用户是否登录,如果没有登录的话,则弹出登录框*/
                User user = SharedPreManager.getUser(HomeAty.this);
                if (user == null){
                    final LoginModePopupWindow window = new LoginModePopupWindow(HomeAty.this, new UserLoginListener() {
                        @Override
                        public void userLogin(String platform, PlatformDb platformDb) {
                            /**获取专辑列表数据*/
                            FetchAlbumListRequest.obtainAlbumList(HomeAty.this, new FetchAlbumListListener() {
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
                    window.showAtLocation(HomeAty.this.getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);
                    return;
                }
                //查看数据库中是否已经专辑数据,如果没有则联网获取
                ArrayList<DiggerAlbum> resultList = queryAlbumsFromDB();
                if (!TextUtil.isListEmpty(resultList)){
                    handleAlbumsData(resultList);
                }else{
                    /**获取专辑列表数据*/
                    FetchAlbumListRequest.obtainAlbumList(HomeAty.this, new FetchAlbumListListener() {
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
//        SubActionButton button2 = lCSubBuilder.setContentView(lcIcon2, blueContentParams).build();
//
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                albumList.clear();
//
//                Album album = new Album();
//                album.setSelected(false);
//                album.setAlbum("默认");
//
//                albumList.add(album);
//
//                DiggerPopupWindow window = new DiggerPopupWindow(mLengJingFgt, HomeAty.this, 1 + "", albumList, 2,true);
//                window.setFocusable(true);
//                window.showAtLocation(HomeAty.this.getWindow().getDecorView(), Gravity.CENTER
//                        | Gravity.CENTER, 0, 0);
//
//                leftCenterMenu.close(true);
//            }
//        });

        SubActionButton button3 = lCSubBuilder.setContentView(lcIcon3, blueContentParams).build();
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAty.this, BaseTagActivity.class);
                startActivity(intent);
                leftCenterMenu.close(true);
            }
        });

        // Build another menu with custom options
        leftCenterMenu = new FloatingActionMenu.Builder(HomeAty.this)
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

                ll_darker_layer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconStar.setRotation(135);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();

                ll_darker_layer.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void loadData() {

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
            DiggerPopupWindow window = new DiggerPopupWindow(mLengJingFgt, HomeAty.this, 1 + "", albumList, 1, true, false);

            window.setFocusable(true);
            window.showAtLocation(HomeAty.this.getWindow().getDecorView(), Gravity.CENTER
                    | Gravity.CENTER, 0, 0);

        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long pressedBackKeyTime = System.currentTimeMillis();
            if ((pressedBackKeyTime - mLastPressedBackKeyTime) < 2000) {
                finish();
            } else {
                ToastUtil.showToastWithIcon(getString(R.string.press_back_again_exit), R.drawable.release_time_logo);// (this, getString(R.string.press_back_again_exit));
                mLastPressedBackKeyTime = pressedBackKeyTime;
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean translucentStatus() {
        return true;
    }

    @Override
    protected boolean isNeedAnimation() {
        return false;
    }

    /**
     * 设置状态栏和tabbar颜色
     * @param newColor
     */
    private void changeColor(int newColor) {

        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        SystemBarTintManager.SystemBarConfig config = mTintManager.getConfig();
        currentColor = newColor;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"关注", "谷歌今日焦点", "挖掘机"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new CategoryFgt();
            } else if (position == 1) {
                return NewsFeedFgt.newInstance(position);
            } else if (position == 2) {
                return mLengJingFgt;
            } else {
                return null;
            }

        }
    }

}
