package com.news.yazhidao.common;

import android.content.Context;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

/**
 * Created by Berkeley on 4/10/15.
 */
public class GlobalParams {

    public static int maxWidth;
    public static int maxHeight;
    public static int screenWidth;
    public static int screenHeight;
    public static int currentCatePos = -1;
    public static Context context;
    public static int split_index_top = 0;
    public static int split_index_bottom = 0;
    public static LoadingLayout loading_context;
    public static ViewPager pager;
    public static PagerSlidingTabStrip tabs;
    public static String news_detail_url;
    public static FloatingActionMenu leftCenterMenu;
    public static FloatingActionButton leftCenterButton;

}
