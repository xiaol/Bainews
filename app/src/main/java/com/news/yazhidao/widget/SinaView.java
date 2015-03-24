package com.news.yazhidao.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class SinaView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private ViewPager m_pCurrentPager;
    private LinearLayout m_pIndicatorLayout;
    private int iScreenWidth, m_piCount;
    private RoundedImageView[] m_pIndicators = null;
    private ArrayList<View> m_pViews;
    private FrameLayout m_pflPager;

    public SinaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.pager_sina_view, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public SinaView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.pager_sina_view, null);
        addView(mRootView);
        initVars();
        findViews();
    }





    private void initVars() {
//        iScreenWidth = U.getPreferences("width", 0);
        m_pViews = new ArrayList<View>();
    }

    private void findViews() {
        m_pflPager = (FrameLayout) findViewById(R.id.pager_layout);
        m_pCurrentPager = (ViewPager) findViewById(R.id.current_viewPager);
        m_pIndicatorLayout = (LinearLayout) findViewById(R.id.indicator);
        FrameLayout.LayoutParams lpCurrentPager = (FrameLayout.LayoutParams) m_pCurrentPager.getLayoutParams();
//        int width = iScreenWidth - U.dip2px(mContext, 20.0f);
        lpCurrentPager.width = 1000;
        lpCurrentPager.height = (int) (1000 * 425 / 1000.0f);
        m_pCurrentPager.setLayoutParams(lpCurrentPager);
        showCurrentPager();
    }

    private void setListener() {
    }

    public void showCurrentPager() {
        //data
//        m_piCount = m_parrCurrentCategoryData.size();
        m_piCount = 4;
        m_pIndicators = new RoundedImageView[m_piCount];
//        if (m_parrCurrentCategoryData != null && m_parrCurrentCategoryData.size() > 0) {
            m_pflPager.setVisibility(View.VISIBLE);
            for (int i = 0; i < m_piCount; i++) {
                RelativeLayout relativeLayout = new RelativeLayout(mContext);
                m_pViews.add(relativeLayout);
                SinaItemView imageView = new SinaItemView(mContext);
                relativeLayout.addView(imageView);
//                int width = iScreenWidth - U.dip2px(mContext, 20.0f);
                RelativeLayout.LayoutParams imageViewBg = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                imageViewBg.addRule(RelativeLayout.CENTER_IN_PARENT);
                imageViewBg.width = 800;
                imageViewBg.height = (int) (800 * 425 / 1000.0f);
                imageView.setLayoutParams(imageViewBg);
//                final TSDBCategoryData categoryData = m_parrCurrentCategoryData.get(i);
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, TSEventInfoActivity.class);
//                        intent.putExtra("CategoryWebUrl", categoryData.getM_strWebUrl());
//                        mContext.startActivity(intent);
//                    }
//                });
//                imageLoader.displayImage();
            }
//        }
        for (int i = 0; i < m_piCount; i++) {
            // 循环加入指示器
            m_pIndicators[i] = new RoundedImageView(mContext);
            m_pIndicators[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
            m_pIndicators[i].setBorderWidth(5.0f);
            m_pIndicators[i].setOval(false);
            m_pIndicators[i].setCornerRadius(5.0f);
            m_pIndicatorLayout.addView(m_pIndicators[i]);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) m_pIndicators[i].getLayoutParams();
            lp.width = 10;
            lp.height = 10;
            lp.leftMargin = 5;
            lp.rightMargin = 5;
            m_pIndicators[i].setLayoutParams(lp);
            if (i == 0) {/**/
                m_pIndicators[i].setBorderColor(getResources().getColor(R.color.red));
            } else {
                m_pIndicators[i].setBorderColor(getResources().getColor(R.color.new_yellow));
            }
            ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
            m_pIndicators[i].setImageDrawable(dw);

        }
        BasePagerAdapter m_pPagerAdapter = new BasePagerAdapter(m_pViews);
        m_pCurrentPager.setAdapter(m_pPagerAdapter); // 设置适配器
        m_pPagerAdapter.notifyDataSetChanged();
        m_pCurrentPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < m_piCount; i++) {
                    if (i == position) {/**/
                        m_pIndicators[i].setBorderColor(getResources().getColor(R.color.red));
                    } else {
                        m_pIndicators[i].setBorderColor(getResources().getColor(R.color.new_yellow));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void RemoveViews() {
        if (m_pViews != null) {
            m_pViews.clear();
            m_pIndicators = null;
            m_pIndicatorLayout.removeAllViews();
        }
    }

    public void setCurrentItem(int m_pNum) {
        if (m_pCurrentPager != null)
            m_pCurrentPager.setCurrentItem(m_pNum);
    }

    public class BasePagerAdapter extends PagerAdapter {
        private List<View> views;

        public BasePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (views.size() > position)
                ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    }


}
