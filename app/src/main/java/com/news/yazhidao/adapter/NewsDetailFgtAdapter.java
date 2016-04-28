package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.DiggerNewsDetail;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.widget.TextViewExtend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/22.
 */
public class NewsDetailFgtAdapter extends CommonAdapter<RelatedItemEntity>{

    private Activity mContext;
    public static final int REQUEST_CODE = 1030;
    private int YearShow = 1;

    public NewsDetailFgtAdapter(Activity context) {
        super(R.layout.item_news_detail_relate_attention,context,null);
        this.mContext = context;
    }


    @Override
    public void convert(CommonViewHolder holder, RelatedItemEntity relatedItemEntity,int position) {
        if(relatedItemEntity.getUrl().equals("-1")){//没有数据时也可以让listView滑动
            holder.getView(R.id.attentionlayout).setVisibility(View.GONE);
            Logger.e("aaa", "没有数据时的状况！！！！！！！！！！！！！");
            return;
        }
        Calendar calendar = DateUtil.strToCalendarLong(relatedItemEntity.getUpdateTime());

        int thisYear = calendar.get(Calendar.YEAR);//获取年份
        int month=calendar.get(Calendar.MONTH)+1;//获取月份
        int day=calendar.get(Calendar.DATE);//获取日

        if(position == 0){
            holder.getView(R.id.attention_Year).setVisibility(View.GONE);
            holder.getView(R.id.attention_line1).setVisibility(View.INVISIBLE);
            holder.getView(R.id.rounded_imageView2).setVisibility(View.GONE);
            holder.getView(R.id.attention_line2).setVisibility(View.GONE);
            holder.setTextViewExtendText(R.id.attention_Year, thisYear + "");
            holder.setTextViewExtendTextBackground(R.id.attention_MonthandDay, R.color.new_color2);
            YearShow = thisYear;
        }else {
            if (thisYear != YearShow) {
                holder.getView(R.id.attention_line1).setVisibility(View.VISIBLE);
                holder.getView(R.id.attention_Year).setVisibility(View.VISIBLE);
                holder.getView(R.id.rounded_imageView2).setVisibility(View.VISIBLE);
                holder.getView(R.id.attention_line2).setVisibility(View.VISIBLE);
                holder.setTextViewExtendText(R.id.attention_Year, thisYear + "");

                holder.setTextViewExtendTextBackground(R.id.attention_MonthandDay, R.color.new_color2);


                YearShow = thisYear;
            } else {
                holder.getView(R.id.attention_line1).setVisibility(View.VISIBLE);
                holder.getView(R.id.attention_Year).setVisibility(View.GONE);
                holder.getView(R.id.rounded_imageView2).setVisibility(View.GONE);
                holder.getView(R.id.attention_line2).setVisibility(View.GONE);

                holder.setTextViewExtendTextBackground(R.id.attention_MonthandDay, R.color.title_user_had_read);

            }
        }
        holder.setTextViewExtendText(R.id.attention_MonthandDay,
                (month<10?"0":"")+month+"/"+(day<10?"0":"")+day);


        holder.setTextViewExtendText(R.id.attention_Title,relatedItemEntity.getTitle());
        holder.setTextViewExtendText(R.id.attention_Source,relatedItemEntity.getSourceSite());
        String imageUrl = relatedItemEntity.getImgUrl();
        if(imageUrl != null&&imageUrl.length()!= 0){
            holder.setSimpleDraweeViewURI(R.id.attention_img_View,imageUrl);
        }else{
            holder.getView(R.id.attention_img_View).setVisibility(View.GONE);
        }
        onAttentionItemClickListener((RelativeLayout) holder.getView(R.id.attentionlayout),relatedItemEntity);
    }
    public void onAttentionItemClickListener(RelativeLayout mAttentionlayout,final RelatedItemEntity relatedItemEntity){
       mAttentionlayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent webviewIntent = new Intent(mContext, NewsDetailWebviewAty.class);
               String zhihuUrl =  relatedItemEntity.getUrl();
               webviewIntent.putExtra(NewsDetailWebviewAty.KEY_URL, zhihuUrl);
               mContext.startActivity(webviewIntent);
           }
       });
    }




}