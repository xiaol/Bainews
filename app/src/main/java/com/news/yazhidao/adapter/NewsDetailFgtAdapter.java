package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailFgtAdapter extends CommonAdapter<RelatedItemEntity> {

    private Activity mContext;
    public static final int REQUEST_CODE = 1030;
    private int mCardWidth, mCardHeight;
    private int mScreenWidth;

    public NewsDetailFgtAdapter(Activity context) {
        super(R.layout.item_news_detail_relate_attention, context, null);
        this.mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity, int position) {
        if (relatedItemEntity.getUrl().equals("-1")) {//没有数据时也可以让listView滑动
            holder.getView(R.id.attentionlayout).setVisibility(View.GONE);
            Logger.e("aaa", "没有数据时的状况！！！！！！！！！！！！！");
//            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(0, 0);
//            holder.getView(R.id.attentionlayout).setLayoutParams(layoutParams);
            return;
        }
//        Calendar calendar = DateUtil.strToCalendarLong(relatedItemEntity.getPtime());
//
//        int thisYear = calendar.get(Calendar.YEAR);//获取年份
//        int month = calendar.get(Calendar.MONTH) + 1;//获取月份
//        int day = calendar.get(Calendar.DATE);//获取日
//        Logger.d("ccc", "thisYear===" + thisYear);
//
//        if (position == 0) {
//            holder.getView(R.id.attention_onceHave).setVisibility(View.VISIBLE);
//            holder.getView(R.id.attention_Year).setVisibility(View.GONE);
//            holder.getView(R.id.attention_line1).setVisibility(View.INVISIBLE);
//            holder.getView(R.id.rounded_imageView2).setVisibility(View.GONE);
//            holder.getView(R.id.attention_line2).setVisibility(View.GONE);
//            holder.setTextViewExtendText(R.id.attention_Year, thisYear + "");
////            holder.setTextViewTextBackgroundResource(R.id.attention_MonthandDay, R.drawable.time_haveyear_bg);
//        } else {
//            holder.getView(R.id.attention_onceHave).setVisibility(View.GONE);
//            boolean yearFrist = relatedItemEntity.getYearFrist();
//            if (yearFrist) {
//                holder.getView(R.id.attention_line1).setVisibility(View.VISIBLE);
//                holder.getView(R.id.attention_Year).setVisibility(View.VISIBLE);
//                holder.getView(R.id.rounded_imageView2).setVisibility(View.VISIBLE);
//                holder.getView(R.id.attention_line2).setVisibility(View.VISIBLE);
//                holder.setTextViewExtendText(R.id.attention_Year, thisYear + "");
//
////                holder.setTextViewTextBackgroundResource(R.id.attention_MonthandDay, R.drawable.time_haveyear_bg);
//
//            } else {
//                holder.getView(R.id.attention_line1).setVisibility(View.VISIBLE);
//                holder.getView(R.id.attention_Year).setVisibility(View.GONE);
//                holder.getView(R.id.rounded_imageView2).setVisibility(View.GONE);
//                holder.getView(R.id.attention_line2).setVisibility(View.GONE);
//
////                holder.setTextViewTextBackgroundResource(R.id.attention_MonthandDay, R.drawable.time_nohaveyear_bg);
//            }
//        }
//        String form = relatedItemEntity.getFrom();
//        if (form.indexOf("百度") != -1 || form.indexOf("谷歌") != -1 || form.toLowerCase().indexOf("google") != -1 || form.toLowerCase().indexOf("baidu") != -1) {
//            holder.setTextViewTextBackgroundResource(R.id.attention_MonthandDay, R.drawable.time_haveyear_bg);
//        } else {
//            holder.setTextViewTextBackgroundResource(R.id.attention_MonthandDay, R.drawable.time_nohaveyear_bg);
//        }
//        holder.setTextViewText(R.id.attention_MonthandDay,
//                (month < 10 ? "0" : "") + month + "/" + (day < 10 ? "0" : "") + day);
//        String strTitle =  relatedItemEntity.getTitle().replace("<font color='#0091fa' >","").replace("</font>","");
//        holder.setRelatedTitleTextViewExtendTextandTextSice(R.id.attention_Title, strTitle);
//
//
//        holder.setTextViewExtendText(R.id.attention_Source, relatedItemEntity.getPname());
//        String imageUrl = relatedItemEntity.getImgUrl();
//        ImageView ivCard = holder.getView(R.id.attention_img_View);
//        if (imageUrl != null && imageUrl.length() != 0) {
//            ivCard.setVisibility(View.VISIBLE);
//            holder.getView(R.id.attention_haveImageShow).setVisibility(View.VISIBLE);
//            String imgUrl = relatedItemEntity.getImgUrl();
//            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
//            lpCard.width = mCardWidth;
//            lpCard.height = mCardHeight;
//            ivCard.setLayoutParams(lpCard);
//            Glide.with(mContext).load(imgUrl).centerCrop().placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCard);
//        } else {
//            ivCard.setVisibility(View.GONE);
//            holder.getView(R.id.attention_haveImageShow).setVisibility(View.GONE);
//        }
//
//        onAttentionItemClickListener((RelativeLayout) holder.getView(R.id.attentionlayout), relatedItemEntity);
//        if (getCount() == position + 1) {//去掉最后一条的线
//            holder.getView(R.id.attention_bottomLine).setVisibility(View.GONE);
//        }else{
//            holder.getView(R.id.attention_bottomLine).setVisibility(View.VISIBLE);
//        }
        onAttentionItemClickListener((RelativeLayout) holder.getView(R.id.attentionlayout), relatedItemEntity);
        TextViewExtend title = holder.getView(R.id.attention_Title);
        String strTitle =  relatedItemEntity.getTitle().replace("<font color='#0091fa' >","").replace("</font>","");
        title.setText(strTitle);
        holder.setTextViewExtendText(R.id.attention_Source, relatedItemEntity.getPname());

        if (getCount() == position + 1) {//去掉最后一条的线
            holder.getView(R.id.attention_bottomLine).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.attention_bottomLine).setVisibility(View.VISIBLE);
        }
        ImageView ivCard = holder.getView(R.id.title_img_View);
        String imgUrl = relatedItemEntity.getImgUrl();
        if (TextUtil.isEmptyString(imgUrl)) {
            ivCard.setVisibility(View.GONE);
        } else {
            ivCard.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
            lpCard.width = mCardWidth;
            lpCard.height = mCardHeight;
            ivCard.setLayoutParams(lpCard);
            Glide.with(mContext).load(imgUrl).centerCrop().placeholder(R.drawable.bg_load_default_small).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivCard);
        }
    }

    public void onAttentionItemClickListener(RelativeLayout mAttentionlayout, final RelatedItemEntity relatedItemEntity) {
        mAttentionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webviewIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                String zhihuUrl = relatedItemEntity.getUrl();
                webviewIntent.putExtra(NewsDetailWebviewAty.KEY_URL, zhihuUrl);
                mContext.startActivity(webviewIntent);
                MobclickAgent.onEvent(mContext, "qidian_user_view_relate_point");

            }
        });
    }


}
