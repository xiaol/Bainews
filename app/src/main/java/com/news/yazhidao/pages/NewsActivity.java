package com.news.yazhidao.pages;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.FastBlur;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;


public class NewsActivity extends Activity {

    private PullToRefreshListView lv_news;
    private MyAdapter list_adapter;
    private ArrayList<NewsFeed> feedList = new ArrayList<NewsFeed>();
    private ArrayList<NewsFeed.Source> sourceList = new ArrayList<NewsFeed.Source>();
    private int i = 0;
    private boolean flag = false;
    private String opinion;
    private ViewHolder holder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        loadNewsData();

        lv_news = (PullToRefreshListView) findViewById(R.id.lv_news);
        lv_news.setMode(PullToRefreshBase.Mode.BOTH);
        lv_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadNewsData();
                flag = false;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadNewsData();
                flag = true;
            }
        });


    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            if(feedList != null && feedList.size() > 0){
                return feedList.size();
            }
            return 0;

        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                holder=new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item, null);
                holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_interests = (TextView) convertView.findViewById(R.id.tv_interests);
                holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.ll_source_content.removeAllViews();
            }

            NewsFeed feed = feedList.get(position);
            holder.tv_title.setText(feed.getTitle());
            holder.tv_interests.setText(feed.getOtherNum() + "家观点");
            if(feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                ImageLoaderHelper.loadImage(getApplicationContext(),feed.getImgUrl(),new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.iv_title_img.setImageBitmap(loadedImage);
                    }
                });
            }else{
                holder.iv_title_img.setBackgroundResource(R.drawable.title_beijing);
            }
            applyBlur(holder.iv_title_img, holder.tv_title);

            sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();

            //解析新闻来源观点数据
            if(sourceList != null && sourceList.size() > 0){

                for(int i = 0;i < sourceList.size();i ++) {

                    LinearLayout ll_souce_view = (LinearLayout) View.inflate(getApplicationContext(), R.layout.lv_source_item, null);
                    ImageView iv_source = (ImageView) ll_souce_view.findViewById(R.id.iv_source);
                    TextView tv_news_source = (TextView) ll_souce_view.findViewById(R.id.tv_news_source);
                    TextView tv_news_des = (TextView) ll_souce_view.findViewById(R.id.tv_news_des);

                    NewsFeed.Source source = sourceList.get(i);
                    if(source != null){
                        if(source.getUrl() != null && !("".equals(source.getUrl()))) {
                            ImageLoaderHelper.loadImage(getApplicationContext(),source.getUrl(),new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    holder.iv_title_img.setImageBitmap(loadedImage);
                                }
                            });
                        }else{
                            iv_source.setBackgroundResource(R.drawable.weibo);
                        }
                        iv_source.setBackgroundResource(R.drawable.weibo);
                        if(source.getSourceSitename() != null) {
                            tv_news_source.setText(source.getSourceSitename());
                        }else{
                            tv_news_source.setText("");
                        }

                        if(source.getTitle() != null) {
                            tv_news_des.setText(source.getTitle());
                        }else{
                            tv_news_des.setText("");
                        }

                    }

                    holder.ll_source_content.addView(ll_souce_view);
                }
            }

            return convertView;
        }
    }


    class ViewHolder {

        ImageView iv_title_img;
        TextView tv_title;
        LinearLayout ll_source_content;
        TextView tv_interests;

    }

    private void loadNewsData() {

        final NetworkRequest request = new NetworkRequest(HttpConstant.URL_GET_NEWS_LIST, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {

            public void success(ArrayList<NewsFeed> result) {
                Logger.i(">>>" + "aaaa", result.toString());
                if (result != null) {
                    if(!flag) {
                        feedList = result;

                        list_adapter = new MyAdapter();
                        lv_news.setAdapter(list_adapter);
                    }

                } else {

                }
                lv_news.onRefreshComplete();
            }

            public void failed(MyAppException exception) {
                Logger.i(">>>" + "aaa", exception.getMessage());
                lv_news.onRefreshComplete();
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        request.execute();
    }


    private void applyBlur(final ImageView mImageView, final TextView mTextview) {
        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                mImageView.buildDrawingCache();

                Bitmap bmp = mImageView.getDrawingCache();
                blur(bmp, mTextview);
                return true;
            }
        });
    }

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        float radius = 2;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        Log.e("xxxx", System.currentTimeMillis() - startMs + "ms");
    }

}
