package com.news.yazhidao.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.TextUtils;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.image.ImageManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout {

    public static interface HeaderVeiwPullUpListener {
        void onclickPullUp(int height);
    }
    //当前新闻内容的高度
    private int mContentHeight;
    private View mRootView;
    private Context mContext;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina, mllSinaItem, mllDouBan;
    private WordWrapView mvDouBanItem;
    private HorizontalScrollView mSinaScrollView;
    private ImageView mNewsDetailHeaderImg;
    private LetterSpacingTextView mNewsDetailHeaderTitle;
    private TextView mNewsDetailHeaderTime;
    private TextView mNewsDetailHeaderTemperature;
    private LetterSpacingTextView mNewsDetailHeaderDesc;
    private LinearLayout mNewsDetailHeaderContentParent;
    private TextView mNewsDetailHeaderSourceName;
    private TextView mNewsDetailHeaderLocation;
    private TextView mNewsDetailRelate;
    private TextView mNewsDetailHeaderPulldown;
    //是否点击了展开全文
    private boolean isClickedPullDown=false;
    //当前文章隐藏的位置
    private int _CurrentPos=0;


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
        mNewsDetailHeaderTitle = (LetterSpacingTextView) mRootView.findViewById(R.id.mNewsDetailHeaderTitle);//新闻标题
        mNewsDetailHeaderTime = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTime);//新闻时间
        mNewsDetailHeaderTemperature = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTemperature);//新闻所属的温度
        mNewsDetailHeaderDesc = (LetterSpacingTextView) mRootView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
        mNewsDetailHeaderContentParent = (LinearLayout) mRootView.findViewById(R.id.mNewsDetailHeaderContentParent);//新闻内容
        mNewsDetailHeaderPulldown = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderPulldown);//点击展开全文
        mNewsDetailHeaderSourceName = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderSourceName);//新闻来源地址
        mNewsDetailHeaderLocation = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderLocation);//新闻发生的地点
    }

    public void setContentViewHeight(int pHeight){
        this.mContentHeight=pHeight;
    }
    /**
     * 获取新闻内容展示的view
     * @return
     */
    public View getContentView(){
        return mNewsDetailHeaderContentParent;
    }
    /**
     * 获取新闻详情后，填充数据到
     *
     * @param pNewsDetail
     */
    private void inflateDataToNewsheader(final NewsDetail pNewsDetail, final HeaderVeiwPullUpListener listener) {
        if (pNewsDetail != null) {
            if (TextUtils.isValidate(pNewsDetail.imgUrl)) {
                ImageManager.getInstance(mContext).DisplayImage(pNewsDetail.imgUrl,mNewsDetailHeaderImg,true);
            } else {
                mNewsDetailHeaderImg.setVisibility(GONE);
            }
            mNewsDetailHeaderTitle.setFontSpacing(LetterSpacingTextView.BIGGEST);
            mNewsDetailHeaderTitle.setText(pNewsDetail.title);
            mNewsDetailHeaderTime.setText(pNewsDetail.updateTime);
            mNewsDetailHeaderTemperature.setText(TextUtil.convertTemp(pNewsDetail.root_class));
            mNewsDetailHeaderDesc.setFontSpacing(LetterSpacingTextView.BIG);
            mNewsDetailHeaderDesc.setText(pNewsDetail.abs);
            if (!android.text.TextUtils.isEmpty(pNewsDetail.content)) {
                String[] _Split = pNewsDetail.content.split("\n");
                StringBuilder _StringBuilder=new StringBuilder();
                for(int i=0;i<_Split.length;i++){
                    LetterSpacingTextView _TextVE=new LetterSpacingTextView(mContext);
                    _TextVE.setFontSpacing(LetterSpacingTextView.NORMALBIG);
                    _TextVE.setLineSpacing(DensityUtil.dip2px(mContext,32),0);
                    _TextVE.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    _TextVE.setTextColor(getResources().getColor(R.color.black));
                    _TextVE.setText(_Split[i]);
                    LinearLayout.LayoutParams _LayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    _LayoutParams.setMargins(0,0,0, DensityUtil.dip2px(mContext,22));
                    _TextVE.setLayoutParams(_LayoutParams);
                    if(_StringBuilder.append(_Split[i]).length()>200){
                        _TextVE.setVisibility(GONE);
                        if(_CurrentPos==0){
                            _CurrentPos=i;
                        }
                    }
                    mNewsDetailHeaderContentParent.addView(_TextVE);
                }
                if(_CurrentPos<2){
                    mNewsDetailHeaderPulldown.setVisibility(GONE);
                }
            }
            mNewsDetailHeaderPulldown.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //umeng statistic onclick pulldown
                    MobclickAgent.onEvent(mContext,CommonConstant.US_BAINEWS_NEWSDETAIL_CLICK_PULLDOWN);
                    if(!isClickedPullDown){
                        isClickedPullDown=true;
                        for(int i=0;i<mNewsDetailHeaderContentParent.getChildCount();i++){
                            mNewsDetailHeaderContentParent.getChildAt(i).setVisibility(VISIBLE);
                        }
                        mNewsDetailHeaderPulldown.setText(R.string.mNewsDetailHeaderOnclickLess);
                        Drawable _DrawableLeft = mContext.getResources().getDrawable(R.drawable.ic_news_detail_listview_pullup);
                        _DrawableLeft.setBounds(0, 0, _DrawableLeft.getMinimumWidth(), _DrawableLeft.getMinimumHeight());
                        mNewsDetailHeaderPulldown.setCompoundDrawables(_DrawableLeft,null,null,null);
                    }else{
                        isClickedPullDown=false;
                        if(listener!=null){
                            listener.onclickPullUp(mContentHeight);
                        }
                        for(int i=0;i<mNewsDetailHeaderContentParent.getChildCount();i++){
                            if(i>=_CurrentPos){
                                mNewsDetailHeaderContentParent.getChildAt(i).setVisibility(GONE);
                            }
                        }
                        mNewsDetailHeaderPulldown.setText(R.string.mNewsDetailHeaderOnclickMore);
                        Drawable _DrawableLeft = mContext.getResources().getDrawable(R.drawable.ic_news_detail_listview_pulldown);
                        _DrawableLeft.setBounds(0, 0, _DrawableLeft.getMinimumWidth(), _DrawableLeft.getMinimumHeight());
                        mNewsDetailHeaderPulldown.setCompoundDrawables(_DrawableLeft,null,null,null);
                    }
                }
            });
            mNewsDetailHeaderSourceName.setText(String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderSourceName), pNewsDetail.originsourceSiteName));
            if (pNewsDetail.ne != null)
                mNewsDetailHeaderLocation.setText(pNewsDetail.ne.gpe.size() > 0 ? String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderLocation), pNewsDetail.ne.gpe.get(0)) : "");
        }

    }

    public void setDetailData(NewsDetail pNewsDetail,HeaderVeiwPullUpListener listener) {
        inflateDataToNewsheader(pNewsDetail,listener);
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
                if(i==pArrWeibo.size()-1){
                    layoutParams.rightMargin = DensityUtil.dip2px(mContext,16);
                }
                layoutParams.leftMargin = DensityUtil.dip2px(mContext,16);
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
