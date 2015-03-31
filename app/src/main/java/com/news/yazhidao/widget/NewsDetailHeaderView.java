package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.TextUtils;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina,mllSinaItem, mllDouBan ;
    private WordWrapView mvDouBanItem;
    private HorizontalScrollView mSinaScrollView;
    private ImageView mNewsDetailHeaderImg;
    private TextView mNewsDetailHeaderTitle;
    private TextView mNewsDetailHeaderTime;
    private TextView mNewsDetailHeaderTemperature;
    private TextView mNewsDetailHeaderDesc;
    private TextView mNewsDetailHeaderContent;
    private TextView mNewsDetailHeaderSourceName;
    private TextView mNewsDetailHeaderLocation;

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
        //find NewsHeader views
        mNewsDetailHeaderImg=(ImageView)mRootView.findViewById(R.id.mNewsDetailHeaderImg);//新闻头图
        mNewsDetailHeaderTitle=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderTitle);//新闻标题
        mNewsDetailHeaderTime=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderTime);//新闻时间
        mNewsDetailHeaderTemperature=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderTemperature);//新闻所属的温度
        mNewsDetailHeaderDesc=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
        mNewsDetailHeaderContent=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderContent);//新闻内容
        mNewsDetailHeaderSourceName=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderSourceName);//新闻来源地址
        mNewsDetailHeaderLocation=(TextView)mRootView.findViewById(R.id.mNewsDetailHeaderLocation);//新闻发生的地点
    }
    /**
     * 获取新闻详情后，填充数据到
     * @param pNewsDetail
     */
    private void inflateDataToNewsheader(NewsDetail pNewsDetail){
        if(pNewsDetail!=null){
            if(TextUtils.isValidate(pNewsDetail.imgUrl)){
                mNewsDetailHeaderImg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (600*1.0/1280* DeviceInfoUtil.getScreenHeight())));
                ImageLoaderHelper.dispalyImage(mContext,pNewsDetail.imgUrl,mNewsDetailHeaderImg);
            }else {
                mNewsDetailHeaderImg.setVisibility(GONE);
            }
            mNewsDetailHeaderTitle.setText(pNewsDetail.title);
            mNewsDetailHeaderTime.setText(pNewsDetail.updateTime);
            mNewsDetailHeaderTemperature.setText(pNewsDetail.root_class);
            mNewsDetailHeaderDesc.setText(pNewsDetail.abs);
            mNewsDetailHeaderContent.setText(pNewsDetail.content);
            mNewsDetailHeaderSourceName.setText(pNewsDetail.originsourceSiteName);
            mNewsDetailHeaderLocation.setText(pNewsDetail.ne.gpe.size()>0?pNewsDetail.ne.gpe.get(0):"");
        }

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
