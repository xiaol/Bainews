package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina,mllSinaItem, mllDouBan ;
    private WordWrapView mvDouBanItem;
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
        mvDouBanItem = (WordWrapView) mRootView.findViewById(R.id.douban_item_tabLayout);
        mllSina = (LinearLayout) mRootView.findViewById(R.id.sina_linearLayout);
        mllSinaItem = (LinearLayout) mRootView.findViewById(R.id.sina_item_layout);
        mSinaScrollView = (HorizontalScrollView) mRootView.findViewById(R.id.sina_scollView);
//        mSinaScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

    }

    public void setData() {
        String[] strs = new String[] { "哲学系", "新疆维吾尔族自治区", "新闻学", "心理学",
                "犯罪心理学", "明明白白", "西方文学史", "计算机", "掌声", "心太软", "生命",
                "程序开发" };
        BaiDuBaiKeView mvBaiDuBaiKe = new BaiDuBaiKeView(mContext);

        mllBaiKe.addView(mvBaiDuBaiKe);
        ZhiHuView zhiHuView = new ZhiHuView(mContext);
        mllZhiHuItem.addView(zhiHuView);
        for (int i = 0; i < 12; i++) {
            TextViewExtend textview = new TextViewExtend(mContext);
            textview.setTextColor(getResources().getColor(R.color.douban_item_blue));
            textview.setTextSize(19);
            textview.setText(strs[i]);
            mvDouBanItem.addView(textview);
        }
        for (int i = 0; i < 3; i++) {



            SinaView sinaView = new SinaView(mContext);
            mllSinaItem.addView(sinaView);
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) sinaView.getLayoutParams();
            layoutParams.rightMargin=30;
            sinaView.setLayoutParams(layoutParams);
        }

    }


}
