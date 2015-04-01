package com.news.yazhidao.pages;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.widget.NewsDetailHeaderView;


public class NewsDetailAty extends BaseActivity {

    private static final String TAG = "NewsDetailAty";
    private PullToRefreshStaggeredGridView mPullToRefreshStaggeredGridView;
    private StaggeredGridView msgvNewsDetail;
    private String[] s;
    private boolean mHasRequestedMore;
    private StaggeredNewsDetailAdapter mNewsDetailAdapter;
    private ImageView mivBack;
    private NewsDetailHeaderView headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_detail);
    }

    @Override
    protected void initializeViews() {
        s = new String[]{"2"};
        mNewsDetailAdapter = new StaggeredNewsDetailAdapter(this);
        mNewsDetailAdapter.setData(s);
        headerView = new NewsDetailHeaderView(this);
        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mPullToRefreshStaggeredGridView = (PullToRefreshStaggeredGridView) findViewById(R.id.news_detail_staggeredGridView);
        mPullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        msgvNewsDetail = mPullToRefreshStaggeredGridView.getRefreshableView();
        msgvNewsDetail.setSmoothScrollbarEnabled(true);
        msgvNewsDetail.addHeaderView(headerView);
        msgvNewsDetail.setAdapter(mNewsDetailAdapter);
        mPullToRefreshStaggeredGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<StaggeredGridView>() {

            @Override
            public void onRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                if (!mHasRequestedMore) {
                    s = new String[]{"打算福克斯的减肥了会计师的反的发生的飞洒发斯蒂芬", "打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会"};
                    mNewsDetailAdapter.setData(s);
                    mNewsDetailAdapter.notifyDataSetChanged();
                    mPullToRefreshStaggeredGridView.onRefreshComplete();
                    mHasRequestedMore = false;
                }
            }
        });
        setListener();
    }

    @Override
    protected void loadData() {
        //TODO 注意 这里的URL 是测试的
        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL+getIntent().getStringExtra("url"), NetworkRequest.RequestMethod.GET);
        _Request.setCallback(new JsonCallback<NewsDetail>() {

            @Override
            public void success(NewsDetail result) {
                Logger.e(TAG, result.toString());
                headerView.setDetailData(result);
                mNewsDetailAdapter.notifyDataSetChanged();
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.e(TAG, exception.getMessage());
            }
        }.setReturnType(new TypeToken<NewsDetail>() {
        }.getType()));
        _Request.execute();
    }


    private void setListener() {
        mivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        msgvNewsDetail.setOnTouchListener(new View.OnTouchListener() {
            float _StartY = 0;
            float _DeltaY = 0;
            RelativeLayout.LayoutParams _MivBackLayout = (RelativeLayout.LayoutParams) mivBack.getLayoutParams();
            int _MivBackTopMargin = _MivBackLayout.topMargin;
            int _MivBackSelfHeigh = mivBack.getHeight();

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        _StartY = event.getY();
                        Logger.i("down", v.getScrollY() + "");
                        break;
                    case MotionEvent.ACTION_UP:
                        _DeltaY = event.getY() - _StartY;
                        if (_DeltaY < 0) {
                            //往上滑动
                            if (mivBack.getVisibility() == View.GONE) {
                                return false;
                            }
                            if (Math.abs(_DeltaY) > _MivBackTopMargin + _MivBackSelfHeigh) {
                                mivBack.setVisibility(View.GONE);
                            }

                        } else {
                            //往下滑动
                            if (mivBack.getVisibility() == View.VISIBLE) {
                                return false;
                            }
                            if (Math.abs(_DeltaY) > _MivBackTopMargin + _MivBackSelfHeigh) {
                                mivBack.setVisibility(View.VISIBLE);
                            }
                        }
                        Logger.i("xxx", _DeltaY + "");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        msgvNewsDetail.startFlingRunnable(300);
//        headerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                Logger.i("down", bottom + "bottom=00000");
//                msgvNewsDetail.mFlingRunnable.startScroll(bottom, 9000);
//            }
//        });
        Log.i("tag", getMacAddressAndDeviceid(this)+"aaaaaa");

    }

    public String getMacAddressAndDeviceid(Context c) {
        WifiManager wifiMan = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId()+macAddr;
    }

    class StaggeredNewsDetailAdapter extends BaseAdapter {

        Context mContext;
        String[] mStrings;

        StaggeredNewsDetailAdapter(Context context) {
            mContext = context;
        }


        public void setData(String[] strings) {
            mStrings = strings;
        }

        @Override
        public int getCount() {
            return mStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return mStrings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_staggered_gridview_news_detail, null, false);
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.picture_imageView);
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                holder.ivSource = (ImageView) convertView.findViewById(R.id.source_imageView);
                holder.tvSource = (TextView) convertView.findViewById(R.id.source_textView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvContent.setText(mStrings[position]);
            if (position > 2)
                holder.ivPicture.setVisibility(View.GONE);
            else
                holder.ivPicture.setVisibility(View.VISIBLE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(NewsDetailAty.this, "+" + position, Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }


    class Holder {
        ImageView ivPicture;
        TextView tvContent;
        ImageView ivSource;
        TextView tvSource;
    }


}
