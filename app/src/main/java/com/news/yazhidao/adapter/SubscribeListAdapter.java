package com.news.yazhidao.adapter;

import android.content.Context;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/20.
 */
public class SubscribeListAdapter extends CommonAdapter<AttentionListEntity> {
    private Context mContext;
    public SubscribeListAdapter(Context mContext) {
        super(R.layout.subscribelist_item, mContext, null);
        this.mContext = mContext;

    }

    @Override
    public void convert(CommonViewHolder holder, AttentionListEntity attentionListEntity, int position) {
        holder.setSimpleDraweeViewURI(R.id.img_SubscribeListItem_icon,attentionListEntity.getIcon());
        holder.setTextViewText(R.id.tv_SubscribeListItem_name,attentionListEntity.getDescr());
        Logger.e("aaa", "attentionListEntity.getConcern()==" + attentionListEntity.getConcern());
        int concern = !TextUtil.isEmptyString(attentionListEntity.getConcern()) ? Integer.parseInt(attentionListEntity.getConcern()) : 0;
        Logger.e("aaa", "concern==" + concern);
        String personNum = "";
        if(concern > 10000){
//            DecimalFormat df=new DecimalFormat("0.0");
            float result =(float)concern / 10000;
            personNum = Math.round(result*10)/10f + "万人关注";
        }else{
            personNum = concern + "人关注";
        }
        holder.setTextViewText(R.id.tv_SubscribeListItem_personNum, personNum);
    }
}
