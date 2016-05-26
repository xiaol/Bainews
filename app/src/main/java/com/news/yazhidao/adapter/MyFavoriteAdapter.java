package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/5.
 */
public class MyFavoriteAdapter extends CommonAdapter<NewsFeed> {
    private Activity mActivity;
    public static int REQUEST_CODE = 10002;


    public MyFavoriteAdapter(Activity mContext) {
        super(R.layout.myfavorite_item, mContext, null);
        mActivity = mContext;
    }

    @Override
    public void convert(CommonViewHolder holder, NewsFeed newsFeed, int positon) {
        holder.setTextViewExtendTextandTextSice(R.id.myfavorite_Title, newsFeed.getTitle());
        String time = DateUtil.getTimes(DateUtil.dateStr2Long(newsFeed.getPtime()));
        String source = newsFeed.getPname() +"  "+time;

        holder.setTextViewExtendText(R.id.myfavorite_Source,source);
        String imageUrl = newsFeed.getImageUrl();
        if(imageUrl != null && imageUrl.length() != 0 ){
            holder.setSimpleDraweeViewURI(R.id.myfavorite_img_View, imageUrl);
            holder.getView(R.id.myfavorite_img_View).setVisibility(View.VISIBLE);
        }else{
            holder.getView(R.id.myfavorite_img_View).setVisibility(View.GONE);
        }
        if(getCount() == positon+1){//去掉最后一条的线
            holder.getView(R.id.myfavorite_bottomLine).setVisibility(View.GONE);
        }
        setOnFaboriteItemClick((LinearLayout) holder.getView(R.id.mFavoriteItemLayout), newsFeed);

    }

    private void setOnFaboriteItemClick(LinearLayout mFavoriteItemLayout, final NewsFeed feed) {
        mFavoriteItemLayout.setOnClickListener(new View.OnClickListener() {
            long firstClick = 0;
            @Override
            public void onClick(View view) {
                if (System.currentTimeMillis() - firstClick <= 1500L) {
                    firstClick = System.currentTimeMillis();
                    ToastUtil.toastShort("这是长按！");
                    return;
                }

                firstClick = System.currentTimeMillis();

                Intent intent = new Intent(mContext, NewsDetailAty2.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);

                ArrayList<String> imageList = feed.getImgs();
                if(imageList != null && imageList.size() != 0){
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                }

                mActivity.startActivityForResult(intent, REQUEST_CODE);

            }
        });

    }
}
