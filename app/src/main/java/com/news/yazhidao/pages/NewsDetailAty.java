package com.news.yazhidao.pages;

import android.content.Context;
import android.os.Bundle;
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
    private StaggeredNewsDetailAdapter mMoreAdapter;
    private ImageView mivBack;

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
        initVars();
        NewsDetailHeaderView headerView = new NewsDetailHeaderView(this);
        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mPullToRefreshStaggeredGridView = (PullToRefreshStaggeredGridView) findViewById(R.id.news_detail_staggeredGridView);
        mPullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        msgvNewsDetail = mPullToRefreshStaggeredGridView.getRefreshableView();
        msgvNewsDetail.setSmoothScrollbarEnabled(true);
        msgvNewsDetail.addHeaderView(headerView);
        msgvNewsDetail.setAdapter(mMoreAdapter);
        mPullToRefreshStaggeredGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<StaggeredGridView>() {

            @Override
            public void onRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                if (!mHasRequestedMore) {
                    s = new String[]{"打算福克斯的减肥了会计师的反的发生的飞洒发斯蒂芬", "打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会"};
                    mMoreAdapter.setData(s);
                    mMoreAdapter.notifyDataSetChanged();
                    mPullToRefreshStaggeredGridView.onRefreshComplete();
                    mHasRequestedMore = false;
                }
            }
        });
        mMoreAdapter.notifyDataSetChanged();
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
    protected void loadData() {
        //TODO 注意 这里的URL 是测试的
        setListener();
        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL, NetworkRequest.RequestMethod.GET);
        _Request.setCallback(new JsonCallback<NewsDetail>() {

            @Override
            public void success(NewsDetail result) {
                Logger.e(TAG, result.toString());

            }

            @Override
            public void failed(MyAppException exception) {
                Logger.e(TAG, exception.getMessage());
            }
        }.setReturnType(new TypeToken<NewsDetail>() {
        }.getType()));
        _Request.execute();
    }

    private void initVars() {
        s = new String[]{"打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会", "打算福克斯的减肥了会打算福克斯的减肥了会打算福克斯的减肥了会"};
        mMoreAdapter = new StaggeredNewsDetailAdapter(this);
        mMoreAdapter.setData(s);
    }

    private int stop_position;

    private void setListener() {
        mivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvContent.setText(mStrings[position]);
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
        TextView tvContent;
    }


}
