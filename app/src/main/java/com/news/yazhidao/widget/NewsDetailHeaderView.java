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
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.TextUtils;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.umeng.analytics.MobclickAgent;

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
    private TextView mNewsDetailHeaderPulldown;

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
        mNewsDetailRelate = (TextView) mRootView.findViewById(R.id.mNewsDetailRelate);
        mNewsDetailHeaderImg = (ImageView) mRootView.findViewById(R.id.mNewsDetailHeaderImg);//新闻头图
        mNewsDetailHeaderTitle = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTitle);//新闻标题
        mNewsDetailHeaderTime = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTime);//新闻时间
        mNewsDetailHeaderTemperature = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTemperature);//新闻所属的温度
        mNewsDetailHeaderDesc = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
        mNewsDetailHeaderContent = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderContent);//新闻内容
        mNewsDetailHeaderPulldown = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderPulldown);//点击展开全文
        mNewsDetailHeaderSourceName = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderSourceName);//新闻来源地址
        mNewsDetailHeaderLocation = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderLocation);//新闻发生的地点
    }

    /**
     * 获取新闻详情后，填充数据到
     *
     * @param pNewsDetail
     */
    private void inflateDataToNewsheader(final NewsDetail pNewsDetail) {
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
            if (!android.text.TextUtils.isEmpty(pNewsDetail.content)) {
                String[] split = pNewsDetail.content.split("\n");
                if (split.length > 3) {
                    if ((split[0] + "\n" + split[1]).length() > 160) {
                        mNewsDetailHeaderContent.setText(split[0] + "\n" + split[1]);
                    } else {
                        mNewsDetailHeaderContent.setText(split[0] + "\n" + split[1] + "\n" + split[2]);
                    }
                } else {
                    mNewsDetailHeaderContent.setText(pNewsDetail.content);
                }
            }
            mNewsDetailHeaderPulldown.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNewsDetailHeaderContent.setText(pNewsDetail.content);
                    mNewsDetailHeaderPulldown.setVisibility(GONE);
                }
            });
            mNewsDetailHeaderSourceName.setText(String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderSourceName), pNewsDetail.originsourceSiteName));
            if (pNewsDetail.ne != null)
                mNewsDetailHeaderLocation.setText(pNewsDetail.ne.gpe.size() > 0 ? String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderLocation), pNewsDetail.ne.gpe.get(0)) : "");
        }

    }

    public void setDetailData(NewsDetail pNewsDetail) {
        inflateDataToNewsheader(pNewsDetail);
        ArrayList<NewsDetail.BaiDuBaiKe> pArrBaiDuBaiKe = pNewsDetail.baike;
        if (pArrBaiDuBaiKe != null && pArrBaiDuBaiKe.size() > 0) {
            for (int i = 0; i < pArrBaiDuBaiKe.size(); i++) {
                final NewsDetail.BaiDuBaiKe pBaiKe = pArrBaiDuBaiKe.get(i);
                BaiDuBaiKeView baiDuBaiKeView = new BaiDuBaiKeView(mContext);
                baiDuBaiKeView.setBaiDuBaiKeData(pArrBaiDuBaiKe.get(i));
                mllBaiKe.addView(baiDuBaiKeView);
                baiDuBaiKeView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                        _Intent.putExtra("url", pBaiKe.url);
                        mContext.startActivity(_Intent);
                        //add umeng statistic baidubaike
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_BAIDUBAIKU);
                    }
                });
            }
        } else {
            mllBaiKe.setVisibility(GONE);
        }

        ArrayList<NewsDetail.ZhiHu> pArrZhiHu = pNewsDetail.zhihu;
        if (pArrZhiHu != null && pArrZhiHu.size() > 0) {
            for (int i = 0; i < pArrZhiHu.size(); i++) {
                final NewsDetail.ZhiHu pZhihu = pArrZhiHu.get(i);
                ZhiHuView zhiHuView = new ZhiHuView(mContext);
                zhiHuView.setZhiHuData(pArrZhiHu.get(i));
                zhiHuView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                        _Intent.putExtra("url", pZhihu.url);
                        mContext.startActivity(_Intent);
                        //add umeng statistic zhihu
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_ZHIHU);
                    }
                });
                mllZhiHuItem.addView(zhiHuView);
            }
        } else {
            mllZhiHu.setVisibility(GONE);
        }

        final ArrayList<ArrayList<String>> pArrDouBan = pNewsDetail.douban;
        if (pArrDouBan != null && pArrDouBan.size() > 0) {
            for (int i = 0; i < pArrDouBan.size(); i++) {
                final ArrayList<String> pDouBan = pArrDouBan.get(i);
                TextViewExtend textView = new TextViewExtend(mContext);
                textView.setTextColor(getResources().getColor(R.color.douban_item_blue));
                textView.setTextSize(19);
                textView.setText(pDouBan.get(0));
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                        _Intent.putExtra("url", pDouBan.get(1));
                        mContext.startActivity(_Intent);
                        //add umeng statistic douban
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_DOUBAI);
                    }
                });
                mvDouBanItem.addView(textView);
            }
        } else {
            mllDouBan.setVisibility(GONE);
        }

        ArrayList<NewsDetail.Weibo> pArrWeibo = pNewsDetail.weibo;
        if (pArrWeibo != null && pArrWeibo.size() > 0) {
            for (int i = 0; i < pArrWeibo.size(); i++) {
                final NewsDetail.Weibo pWeiBo = pArrWeibo.get(i);
                SinaView sinaView = new SinaView(mContext);
                sinaView.setSinaData(pArrWeibo.get(i));
                mllSinaItem.addView(sinaView);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) sinaView.getLayoutParams();
                layoutParams.rightMargin = 30;
                sinaView.setLayoutParams(layoutParams);
                sinaView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                        _Intent.putExtra("url", pWeiBo.url);
                        mContext.startActivity(_Intent);
                        //add umeng statistic weibo
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_WEIBO);
                    }
                });
            }
        } else {
            mllSina.setVisibility(GONE);
        }
        if (pNewsDetail != null && pNewsDetail.relate != null && pNewsDetail.relate.size() > 0) {

        } else {
            mNewsDetailRelate.setVisibility(GONE);
        }
    }


}
