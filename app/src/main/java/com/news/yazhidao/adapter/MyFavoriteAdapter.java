package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.pages.MyFavoriteAty;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/5.
 */
public class MyFavoriteAdapter extends CommonAdapter<NewsFeed> {
    private Activity mActivity;
    public MyFavoriteAdapter(Activity mContext) {
        super(R.layout.myfavorite_item, mContext, null);
        mActivity = mContext;
    }

    @Override
    public void convert(CommonViewHolder holder, NewsFeed newsFeed, int positon) {
        holder.setTextViewExtendText(R.id.myfavorite_Title, newsFeed.getTitle());
        String source = newsFeed.getPubName() +"  "+ newsFeed.getPubTime();
        holder.setTextViewExtendText(R.id.myfavorite_Source,source);
        String imageUrl = newsFeed.getImageUrl();
        if(imageUrl != null && imageUrl.length() != 0 ){
            holder.setSimpleDraweeViewURI(R.id.myfavorite_img_View, imageUrl);
            holder.getView(R.id.myfavorite_img_View).setVisibility(View.VISIBLE);
        }else{
            holder.getView(R.id.myfavorite_img_View).setVisibility(View.GONE);
        }

    }
}
