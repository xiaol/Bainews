package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.news.yazhidao.utils.ImageUtils;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;


public class HomeAty extends Activity {

    private PullToRefreshListView lv_news;
    private MyAdapter list_adapter;
    private LinearLayout ll_title;
    private ArrayList<NewsFeed> feedList = new ArrayList<NewsFeed>();
    private ArrayList<NewsFeed.Source> sourceList = new ArrayList<NewsFeed.Source>();
    private int i = 0;
    private boolean flag = false;
    private boolean top_flag = false;
    private boolean visible_flag = true;
    private boolean requestMore = false;
    private String opinion;
    private ViewHolder holder = null;
    private int a = 0;
    private int mMostRecentY;
    private int currentSize = 0;
    private int contentSize = 0;
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            switch (scrollState){
                case SCROLL_STATE_IDLE:
                    if(view.getFirstVisiblePosition() == 0 && !visible_flag){

                        ll_title.setVisibility(View.VISIBLE);
                        visible_flag = true;

                    }

                    break;

                case SCROLL_STATE_FLING:

                    if(view != null){
                        int firstPos = ((ListView)view).getFirstVisiblePosition();

                        if(firstPos == 0) {
                            ll_title.setVisibility(View.VISIBLE);
                            visible_flag = true;
                        }else if(firstPos == 1) {

                            View v = ((ListView) view).getChildAt(firstPos);

                            if (v != null) {
                                int top = v.getTop();

                                if (top > 200 && visible_flag) {

                                    ll_title.setVisibility(View.GONE);
                                    visible_flag = false;

                                } else {
                                    if (top < 50 && !visible_flag) {
                                        ll_title.setVisibility(View.VISIBLE);
                                        visible_flag = true;
                                    }
                                }

                            }
                        }else if(firstPos > 1){

                            if(visible_flag){

                                ll_title.setVisibility(View.GONE);
                                visible_flag = false;

                            }

                        }
                    }


                    break;

                case SCROLL_STATE_TOUCH_SCROLL:
                    if(view != null){
                        int firstPos = ((ListView)view).getFirstVisiblePosition();

                        if(firstPos == 0) {
                            ll_title.setVisibility(View.VISIBLE);
                            visible_flag = true;
                        }else if(firstPos == 1) {

                            View v = ((ListView) view).getChildAt(firstPos);

                            if (v != null) {
                                int top = v.getTop();

                                if (top > 200 && visible_flag) {

                                    ll_title.setVisibility(View.GONE);
                                    visible_flag = false;

                                } else {
                                    if (top < 50 && !visible_flag) {
                                        ll_title.setVisibility(View.VISIBLE);
                                        visible_flag = true;
                                    }
                                }

                            }
                        }else if(firstPos > 1){

                            if(visible_flag){

                                ll_title.setVisibility(View.GONE);
                                visible_flag = false;

                            }

                        }
                    }
                    break;
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

//            int firstPos = ((ListView)view).getFirstVisiblePosition();
//
//            if(firstPos == 0) {
//                ll_title.setVisibility(View.VISIBLE);
//                visible_flag = true;
//            }else if(firstPos == 1) {
//
//                View v = ((ListView) view).getChildAt(firstPos);
//
//                if (v != null) {
//                    int top = v.getTop();
//
//                    if (top > 200 && visible_flag) {
//
//                        ll_title.setVisibility(View.GONE);
//                        visible_flag = false;
//
//                    } else {
//                        if (top < 50 && !visible_flag) {
//                            ll_title.setVisibility(View.VISIBLE);
//                            visible_flag = true;
//                        }
//                    }
//
//                }
//            }else if(firstPos > 1){
//
//                if(visible_flag){
//
//                    ll_title.setVisibility(View.GONE);
//                    visible_flag = false;
//
//                }
//
//            }

            if (firstVisibleItem + visibleItemCount == totalItemCount && !top_flag) {
                top_flag = true;
            } else
                top_flag = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        loadNewsData();

        ll_title = (LinearLayout) findViewById(R.id.ll_title);

        lv_news = (PullToRefreshListView) findViewById(R.id.lv_news);
        lv_news.setMode(PullToRefreshBase.Mode.BOTH);
        lv_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestMore = false;
                loadNewsData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestMore = true;
                loadNewsData();

            }
        });

//        lv_news.setOnScrollListener(scrollListener);

    }

    private class MyAdapter extends BaseAdapter {


        @Override
        public int getCount() {

            return currentSize;

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

            contentSize = 0;

            if (convertView == null) {
                holder=new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.ll_news_item, null);
                holder.fl_title_content = (FrameLayout) convertView.findViewById(R.id.fl_title_content);
                holder.iv_title_img = (ImageView) convertView.findViewById(R.id.iv_title_img);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_interests = (TextView) convertView.findViewById(R.id.tv_interests);
                holder.ll_source_content = (LinearLayout) convertView.findViewById(R.id.ll_source_content);
                holder.ll_source_interest = (LinearLayout) convertView.findViewById(R.id.ll_source_interest);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                holder.ll_source_content.removeAllViews();
            }


            if(position <= currentSize) {
                final NewsFeed feed = feedList.get(position);
                holder.tv_title.setText(feed.getTitle());
                holder.tv_interests.setText(feed.getOtherNum() + "家观点");

                holder.fl_title_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeAty.this, DetailAty.class);
                        intent.putExtra("url", feed.getSourceUrl());
                        startActivity(intent);
                    }
                });

//            holder.ll_source_interest.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(HomeAty.this,DetailAty.class);
//                    intent.putExtra("url",feed.getSourceUrl());
//                    startActivity(intent);
//                }
//            });

                if (feed != null && feed.getOtherNum() != null) {
                    if (Integer.parseInt(feed.getOtherNum()) == 0) {
                        holder.ll_source_interest.setVisibility(View.GONE);
                    }
                }

                if (feed.getImgUrl() != null && !("".equals(feed.getImgUrl()))) {
                    ImageLoaderHelper.loadImage(getApplicationContext(), feed.getImgUrl(), new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        holder.iv_title_img.setBackgroundResource(R.drawable.title_beijing);

                            Logger.i(">>>>>xxxx","yes" + loadedImage.toString());

                            holder.iv_title_img.setImageBitmap(loadedImage);
                            applyBlur(holder.iv_title_img, holder.tv_title);
                        }
                    });
                }

                sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();

                //解析新闻来源观点数据
                if (sourceList != null && sourceList.size() > 0) {

                    for (int a = 0; a < sourceList.size(); a++) {

                        final NewsFeed.Source source = sourceList.get(a);

                        LinearLayout ll_souce_view = (LinearLayout) View.inflate(getApplicationContext(), R.layout.lv_source_item, null);
                        ll_souce_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(HomeAty.this, NewsDetailWebviewAty.class);
                                intent.putExtra("url", source.getUrl());
                                startActivity(intent);
                            }
                        });
                        ImageView iv_source = (ImageView) ll_souce_view.findViewById(R.id.iv_source);
                        TextView tv_news_source = (TextView) ll_souce_view.findViewById(R.id.tv_news_source);
                        TextView tv_news_des = (TextView) ll_souce_view.findViewById(R.id.tv_news_des);

                        if (source != null) {

                            String source_name = source.getSourceSitename();

                            if (source_name != null) {
                                if(source.getUser() != null && !"".equals(source.getUser())){
                                    tv_news_source.setText(source.getUser() + ":");
                                }else{
                                    tv_news_source.setText(source_name + ":");
                                }

                                setImageSource(iv_source,source_name);

                            } else {

                                tv_news_source.setText("匿名报道:");
                            }

                            if (source.getTitle() != null) {
                                tv_news_des.setText(source.getTitle());
                            } else {
                                tv_news_des.setText("");
                            }

                        }
                        if(contentSize < 3) {
                            holder.ll_source_content.addView(ll_souce_view);
                            contentSize ++;
                        }
                    }
                }
            }

            return convertView;
        }
    }

    private void setImageSource(ImageView iv_source,String source_name) {


            if("凤凰网".equals(source_name)) {
                iv_source.setBackgroundResource(R.drawable.fenghuangwang);
            }else if("网易".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.yi);
            }else if("zhihu".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhihu);
            }else if("微博".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.weibo);
            }else if("国际在线".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.guojizaixian);
            }else if("新浪网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.xinlang);
            }else if("搜狐".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.souhu);
            }else if("腾讯".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.tengxun);
            }else if("中国经济报".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhongguojingjibao);
            }else if("中国经济网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhongguojingjiwang);
            }else if("人民网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.renminwang);
            }else if("经济参考报".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.jingjicankaobao);
            }else if("南方网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.nanfang);
            }else if("中工网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhonggongwang);
            }else if("央视网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.yangshiwang);
            }else if("金融街".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.jinrongjie);
            }else if("南海网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.nanhaiwang);
            }else if("36氪".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.thirty_six_ke);
            }else if("环球网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.huanqiuwang);
            }else if("解放牛网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.jiefangniuwang);
            }else if("21CN".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.twenty_one_cn);
            }else if("中金在线".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhongjinzaixian);
            }else if("证券之星".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhengquanzhixing);
            }else if("太平洋电脑网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.taipingyangdiannaowang);
            }else if("中关村在线".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhongguancunzaixian);
            }else if("红网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.hongwang);
            }else if("北青网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.beiqingwang);
            }else if("sports.cn".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.sportscn);
            }else if("新民网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.xinmin);
            }else if("中国山东网".equals(source_name)){
                iv_source.setBackgroundResource(R.drawable.zhongguoshandongwang);
            }else{
                iv_source.setBackgroundResource(R.drawable.other);
            }

    }


    class ViewHolder {

        ImageView iv_title_img;
        TextView tv_title;
        LinearLayout ll_source_content;
        LinearLayout ll_source_interest;
        FrameLayout fl_title_content;
        TextView tv_interests;

    }

    private void loadNewsData() {

        final NetworkRequest request = new NetworkRequest(HttpConstant.URL_GET_NEWS_LIST, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {

            public void success(ArrayList<NewsFeed> result) {
                if (result != null) {
                    feedList = result;
                    if(!requestMore) {
                        if (feedList != null && feedList.size() > 0 && feedList.size() < 3) {
                            currentSize = feedList.size();
                        } else if (feedList != null && feedList.size() >= 3) {
                            currentSize = 3;
                        }

                        list_adapter = new MyAdapter();
                        lv_news.setAdapter(list_adapter);

                    }else{
                        currentSize = feedList.size();
                        list_adapter.notifyDataSetChanged();
                    }


                } else {
                    Toast.makeText(HomeAty.this,"网络出现异常，请检查网络...",Toast.LENGTH_LONG).show();
                }
                lv_news.onRefreshComplete();
            }

            public void failed(MyAppException exception) {
//                Logger.i(">>>" + "aaa", exception.getMessage());
                Toast.makeText(HomeAty.this,"网络出现异常，请检查网络...",Toast.LENGTH_LONG).show();
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
        float scaleFactor = 3;
        float radius = 1;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
//        canvas.translate(-view.getLeft()/scaleFactor, -view.getTop()/scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除锯齿
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        overlay = ImageUtils.getRoundCornerBitmap(overlay, 3.0f);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        Log.e("xxxx", System.currentTimeMillis() - startMs + "ms");
    }

}
