package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.DigSpecial;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.DiggerPopupWindow;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;


public class HomeAty extends BaseActivity {

    //PageSlidingTabStrip
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    private MyPagerAdapter adapter;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    private long mLastPressedBackKeyTime;
    //Viewpager 索引默认是1
    private int mViewPagerIndex = 1;
    //棱镜界面对应的fragment
    private LengjingFgt mLengJingFgt;

    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;
    private FloatingActionMenu leftCenterMenu;
    private ArrayList<Album> albumList = new ArrayList<Album>();

    @Override
    protected void setContentView() {
        mLengJingFgt =new LengjingFgt(this);
        GlobalParams.context = HomeAty.this;
        setContentView(R.layout.activity_viewpager);
        //判断是否是别的app分享进来的
        Intent intent = getIntent();
        final String data = intent.getStringExtra(Intent.EXTRA_TEXT);
        String type = intent.getType();
        if ("text/plain".equals(type) && !TextUtils.isEmpty(data)) {
            Log.e("jigang", type + "-----data=" + data);
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
        addMenu();

        //pagesliding
        mTintManager = new SystemBarTintManager(HomeAty.this);
        // enable status bar tint
        mTintManager.setStatusBarTintEnabled(false);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 2) {
                    if (leftCenterButton != null) {
                        leftCenterButton.attach(starParams, null);
                    }
                } else {
                    leftCenterMenu.close(true);
                    if (leftCenterButton != null) {
                        leftCenterButton.detach(null);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        GlobalParams.tabs = tabs;

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        GlobalParams.pager = pager;
        pager.setCurrentItem(mViewPagerIndex);
        changeColor(getResources().getColor(R.color.tab_blue));
        pager.setCurrentItem(1);

    }

    private void addMenu() {

        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int imgSize = getResources().getDimensionPixelSize(R.dimen.img_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
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

                albumList.clear();

                Log.e("jigang", "----1albumList size =" + albumList.size());
                ArrayList<DigSpecial> specialDatas = mLengJingFgt.getSpecialDatas();
                for (int i = 0;i < specialDatas.size();i++){
                    Album album = new Album();
                    album.setSelected(i == 0);
                    album.setAlbum(specialDatas.get(i).getTitle());
                    albumList.add(album);
                }
                DiggerPopupWindow window = new DiggerPopupWindow(mLengJingFgt,HomeAty.this, 1 + "", albumList,1);
                window.setFocusable(true);
                window.showAtLocation(HomeAty.this.getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });
        SubActionButton button2 = lCSubBuilder.setContentView(lcIcon2, blueContentParams).build();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                albumList.clear();

                Album album = new Album();
                album.setSelected(false);
                album.setAlbum("默认");

                albumList.add(album);
                Log.e("jigang", "----2albumList size =" + albumList.size());

                DiggerPopupWindow window = new DiggerPopupWindow(mLengJingFgt,HomeAty.this, 1 + "", albumList,2);
                window.setFocusable(true);
                window.showAtLocation(HomeAty.this.getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });

        SubActionButton button3 = lCSubBuilder.setContentView(lcIcon3, blueContentParams).build();
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeAty.this, BaseTagActivity.class);
                startActivity(intent);
            }
        });

        // Build another menu with custom options
        leftCenterMenu = new FloatingActionMenu.Builder(HomeAty.this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .setRadius(redActionMenuRadius)
                .setStartAngle(-140)
                .setEndAngle(-40)
                .attachTo(leftCenterButton)
                .build();


        leftCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                fabIconStar.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 135);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconStar.setRotation(135);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
            }
        });

    }

    @Override
    protected void loadData() {

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
            }
            mLastPressedBackKeyTime = pressedBackKeyTime;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void changeColor(int newColor) {

        tabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
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
                return NewsFeedFragment.newInstance(position);
            } else if (position == 2) {
                return mLengJingFgt;
            } else {
                return null;
            }

        }
    }

}
