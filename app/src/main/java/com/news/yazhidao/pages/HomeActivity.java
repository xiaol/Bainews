package com.news.yazhidao.pages;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.widget.NewsDetailListView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";
    private NewsDetailListView mNewsDetailLsv;
    private NewsDetailAdapter mNewsDetailAdapter;
    private List<List<Map<String, String>>> mNewsDetailContentAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_home);

    }

    @Override
    protected void initializeViews() {
        mNewsDetailLsv = (NewsDetailListView) findViewById(R.id.mNewsDetailLsv);
        View mImageViewHeader = View.inflate(this, R.layout.aty_news_detail_content_header, null);
        ImageView mImageView = (ImageView) mImageViewHeader.findViewById(R.id.mNewsDetailHeaderImg);
//        mNewsDetailLsv.setParallaxImageView(mImageView);
        mNewsDetailLsv.addHeaderView(mImageViewHeader);
        mNewsDetailAdapter = new NewsDetailAdapter();
        mNewsDetailLsv.setAdapter(mNewsDetailAdapter);
        View mImageViewFooter = View.inflate(this, R.layout.aty_news_detail_content_footer, null);
        mImageViewFooter.setOnClickListener(this);
        mNewsDetailLsv.addFooterView(mImageViewFooter);
    }

    @Override
    protected void loadData() {
        NetworkRequest request = new NetworkRequest("http://121.41.75.213:9527/eagle/FetchContent?id=http://jandan.net/2014/01/03/selfies-cosplay.html", NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<NewsDetail>() {
            @Override
            public void success(NewsDetail result) {
                if (result.content != null && result.content.size() > 5) {
                    mNewsDetailContentAll = result.content;
                    mNewsDetailAdapter.setNewsList(result.content.subList(0, 3));
                    mNewsDetailAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.i(TAG, ">>> failed result " + exception.getMessage());
            }
        }.setReturnType(new TypeToken<NewsDetail>() {
        }.getType()));
        request.execute();
    }

    @Override
    public void onClick(View v) {
        int _ViewId = v.getId();
        if (_ViewId == R.id.mNewsDetailFooter) {
            v.setVisibility(View.GONE);
            mNewsDetailAdapter.setNewsList(mNewsDetailContentAll);
            mNewsDetailAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 新闻详情listview 适配器
     */
    class NewsDetailAdapter extends BaseAdapter {
        private List<List<Map<String, String>>> mNewsContentList;

        public void setNewsList(List<List<Map<String, String>>> pNewsContentList) {
            this.mNewsContentList = pNewsContentList;
        }

        @Override
        public int getCount() {
            return mNewsContentList == null ? 0 : mNewsContentList.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewsContentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(HomeActivity.this, R.layout.aty_news_detail_content_item, null);
                holder.mNewsDetailItemImg = (ImageView) convertView.findViewById(R.id.mNewsDetailItemImg);
                holder.mNewsDetailItemTxt = (TextView) convertView.findViewById(R.id.mNewsDetailItemTxt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mNewsDetailItemImg.setVisibility(View.VISIBLE);
            holder.mNewsDetailItemTxt.setVisibility(View.VISIBLE);
            mNewsDetailLsv.setDividerHeight(DensityUtil.dip2px(HomeActivity.this, 8));
            List<Map<String, String>> section = mNewsContentList.get(position);
            String imgUrl = null;
            String text = null;
            for (Map<String, String> map : section) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if ("img".equals(entry.getKey())) {
                        imgUrl = entry.getValue();
                    } else if ("txt".equals(entry.getKey())) {
                        text = entry.getValue();
                    }
                }
            }
            holder.mNewsDetailItemImg.setTag(imgUrl);
            if (imgUrl != null) {
                holder.mNewsDetailItemImg.setImageResource(R.drawable.bg_news_detail_listview_header);
                ImageLoaderHelper.loadImage(HomeActivity.this, imgUrl,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if(imageUri.equals(holder.mNewsDetailItemImg.getTag())){
                            holder.mNewsDetailItemImg.setImageBitmap(loadedImage);
                        }
                    }

                });
            } else {
                holder.mNewsDetailItemImg.setVisibility(View.GONE);
            }
            if (text != null) {
                holder.mNewsDetailItemTxt.setText(text.trim());

            } else {
                holder.mNewsDetailItemTxt.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(null);
            convertView.setEnabled(false);
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView mNewsDetailItemImg;
        TextView mNewsDetailItemTxt;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
//            mNewsDetailLsv.setViewsBounds(NewsDetailListView.ZOOM_X2);
        }
    }
}
