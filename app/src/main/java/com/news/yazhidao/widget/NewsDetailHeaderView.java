package com.news.yazhidao.widget;

import android.content.Context;
import android.content.Intent;
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
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;

import java.util.ArrayList;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina, mllSinaItem, mllDouBan;
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
    private TextView mNewsDetailRelate;

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
        mNewsDetailRelate=(TextView)mRootView.findViewById(R.id.mNewsDetailRelate);
        mNewsDetailHeaderImg = (ImageView) mRootView.findViewById(R.id.mNewsDetailHeaderImg);//新闻头图
        mNewsDetailHeaderTitle = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTitle);//新闻标题
        mNewsDetailHeaderTime = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTime);//新闻时间
        mNewsDetailHeaderTemperature = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTemperature);//新闻所属的温度
        mNewsDetailHeaderDesc = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
        mNewsDetailHeaderContent = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderContent);//新闻内容
        mNewsDetailHeaderSourceName = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderSourceName);//新闻来源地址
        mNewsDetailHeaderLocation = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderLocation);//新闻发生的地点
    }

    /**
     * 获取新闻详情后，填充数据到
     *
     * @param pNewsDetail
     */
    private void inflateDataToNewsheader(NewsDetail pNewsDetail) {
        if (pNewsDetail != null) {
            if (TextUtils.isValidate(pNewsDetail.imgUrl)) {
                mNewsDetailHeaderImg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (600 * 1.0 / 1280 * DeviceInfoUtil.getScreenHeight())));
                ImageLoaderHelper.dispalyImage(mContext, pNewsDetail.imgUrl, mNewsDetailHeaderImg);
            } else {
                mNewsDetailHeaderImg.setVisibility(GONE);
            }
            mNewsDetailHeaderTitle.setText(pNewsDetail.title);
            mNewsDetailHeaderTime.setText(pNewsDetail.updateTime);
            mNewsDetailHeaderTemperature.setText(TextUtil.convertTemp(pNewsDetail.root_class));
            mNewsDetailHeaderDesc.setText(pNewsDetail.abs);
            mNewsDetailHeaderContent.setText(pNewsDetail.content);
            mNewsDetailHeaderSourceName.setText(String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderSourceName),pNewsDetail.originsourceSiteName));
            mNewsDetailHeaderLocation.setText(pNewsDetail.ne.gpe.size() > 0 ? String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderLocation), pNewsDetail.ne.gpe.get(0)) : "");
        }

    }

    public void setDetailData(NewsDetail pNewsDetail) {
        inflateDataToNewsheader(pNewsDetail);
        ArrayList<NewsDetail.BaiDuBaiKe> pArrBaiDuBaiKe = pNewsDetail.arrBaiDuBaiKe;
        if (pArrBaiDuBaiKe != null && pArrBaiDuBaiKe.size() > 0) {
            for (int i = 0; i < pArrBaiDuBaiKe.size(); i++) {
                final NewsDetail.BaiDuBaiKe pBaiKe=pArrBaiDuBaiKe.get(i);
                BaiDuBaiKeView baiDuBaiKeView = new BaiDuBaiKeView(mContext);
                baiDuBaiKeView.setBaiDuBaiKeData(pArrBaiDuBaiKe.get(i));
                mllBaiKe.addView(baiDuBaiKeView);
                baiDuBaiKeView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent=new Intent(mContext,NewsDetailWebviewAty.class);
                        _Intent.putExtra("url",pBaiKe.url);
                        mContext.startActivity(_Intent);
                    }
                });
            }
        }else {
            mllBaiKe.setVisibility(GONE);
        }

        ArrayList<NewsDetail.ZhiHu> pArrZhiHu = pNewsDetail.arrZhihu;
        if (pArrZhiHu != null && pArrZhiHu.size() > 0) {
            for (int i = 0; i < pArrZhiHu.size(); i++) {
                ZhiHuView zhiHuView = new ZhiHuView(mContext);
                zhiHuView.setZhiHuData(pArrZhiHu.get(i));
                mllZhiHuItem.addView(zhiHuView);
            }
        }else {
            mllZhiHu.setVisibility(GONE);
        }

        final ArrayList<NewsDetail.DouBan> pArrDouBan = pNewsDetail.arrDouBan;
        if (pArrDouBan != null && pArrDouBan.size() > 0) {
            for (int i = 0; i < pArrDouBan.size(); i++) {
                final NewsDetail.DouBan  pDouBan=pArrDouBan.get(i);
                TextViewExtend textView = new TextViewExtend(mContext);
                textView.setTextColor(getResources().getColor(R.color.douban_item_blue));
                textView.setTextSize(19);
                textView.setText(pDouBan.title);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent=new Intent(mContext,NewsDetailWebviewAty.class);
                        _Intent.putExtra("url",pDouBan.url);
                        mContext.startActivity(_Intent);
                    }
                });
                mvDouBanItem.addView(textView);
            }
        }else {
            mllDouBan.setVisibility(GONE);
        }

        ArrayList<NewsDetail.Weibo> pArrWeibo = pNewsDetail.arrWeibo;
        if (pArrWeibo != null && pArrWeibo.size() > 0) {
            for (int i = 0; i < pArrWeibo.size(); i++) {
                final NewsDetail.Weibo pWeiBo=pArrWeibo.get(i);
                SinaView sinaView = new SinaView(mContext);
                sinaView.setSinaData(pArrWeibo.get(i));
                mllSinaItem.addView(sinaView);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) sinaView.getLayoutParams();
                layoutParams.rightMargin = 30;
                sinaView.setLayoutParams(layoutParams);
                sinaView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent=new Intent(mContext,NewsDetailWebviewAty.class);
                        _Intent.putExtra("url",pWeiBo.url);
                        mContext.startActivity(_Intent);
                    }
                });
            }
        }else {
            mllSina.setVisibility(GONE);
        }
        if(pNewsDetail!=null&&pNewsDetail.relate!=null&&pNewsDetail.relate.size()>0){

        }else{
            mNewsDetailRelate.setVisibility(GONE);
        }
    }


}
