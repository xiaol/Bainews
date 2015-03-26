package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private int iScreenWidth, m_piCount;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina,mllSinaItem, mllDouBan, mllDouBanItem;
    private HorizontalListView mlvSina;
    private HorizontalScrollView mSinaScrollView;

    public NewsDetailHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_header_view, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public NewsDetailHeaderView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_header_view, null);
        addView(mRootView);
        initVars();
        findViews();
        setData();
    }

    private void initVars() {
    }

    private void findViews() {
        mllBaiKe = (LinearLayout) mRootView.findViewById(R.id.baike_linerLayout);
        mllZhiHu = (LinearLayout) mRootView.findViewById(R.id.zhihu_linerLayout);
        mllZhiHuItem = (LinearLayout) mRootView.findViewById(R.id.zhihu_item_linerLayout);
        mllDouBan = (LinearLayout) mRootView.findViewById(R.id.douban_linerLayout);
        mllDouBanItem = (LinearLayout) mRootView.findViewById(R.id.douban_item_tabLayout);
        mllSina = (LinearLayout) mRootView.findViewById(R.id.sina_linearLayout);
        mllSinaItem = (LinearLayout) mRootView.findViewById(R.id.sina_item_layout);
        mSinaScrollView = (HorizontalScrollView) mRootView.findViewById(R.id.sina_scollView);
//        mSinaScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void setData() {
        for (int i = 0; i < 3; i++) {
            BaiDuBaiKeView mvBaiDuBaiKe = new BaiDuBaiKeView(mContext);

            mllBaiKe.addView(mvBaiDuBaiKe);
            ZhiHuView zhiHuView = new ZhiHuView(mContext);
            mllZhiHuItem.addView(zhiHuView);

            SinaView sinaView = new SinaView(mContext);
            mllSinaItem.addView(sinaView);
        }
        int iDouBanRow =7/ 3 == 0 ? 7 / 3 : 7/ 3 + 1;
        int emptyViewCount = 3 - 7 % 3;

        for (int i = 0; i < iDouBanRow; i++) {
            LinearLayout rowLinearLayout = new LinearLayout(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
//            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            rowLinearLayout.setWeightSum(3);
            rowLinearLayout.setLayoutParams(layoutParams);

            for (int j = 0; j < 3; j++) {
                TextView textView = new TextView(mContext);
                textView.setText("dddd");
                textView.setGravity(Gravity.CENTER);
                if (i == iDouBanRow - 1) {

                    if ( emptyViewCount >=3- j) {
                        textView.setVisibility(INVISIBLE);
                    }
                }
                rowLinearLayout.addView(textView);
                LinearLayout.LayoutParams tvLayoutParams = new LinearLayout.LayoutParams(textView.getLayoutParams());
                tvLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                tvLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                tvLayoutParams.weight = 1;
                textView.setLayoutParams(tvLayoutParams);
            }
            mllDouBanItem.addView(rowLinearLayout);
        }
    }


}
