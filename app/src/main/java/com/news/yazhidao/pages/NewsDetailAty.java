package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.widget.NewsDetailHeaderView;
import com.news.yazhidao.widget.ProgressWheel;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;


public class NewsDetailAty extends BaseActivity {

    private static final String TAG = "NewsDetailAty";
    private PullToRefreshStaggeredGridView mPullToRefreshStaggeredGridView;
    private StaggeredGridView msgvNewsDetail;
    private boolean mHasRequestedMore;
    private StaggeredNewsDetailAdapter mNewsDetailAdapter;
    private ImageView mivBack;
    private NewsDetailHeaderView headerView;
    private ProgressWheel mNewsDetailProgressWheel;
    private View mNewsDetailProgressWheelWrapper;
    //从哪儿进入的详情页
    private String mSource;
    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_detail);
    }

    @Override
    protected void initializeViews() {
        mSource =getIntent().getStringExtra(HomeAty.KEY_NEWS_SOURCE);
        mNewsDetailAdapter = new StaggeredNewsDetailAdapter(this);
        headerView = new NewsDetailHeaderView(this);
        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mNewsDetailProgressWheel = (ProgressWheel) findViewById(R.id.mNewsDetailProgressWheel);
        mNewsDetailProgressWheelWrapper = findViewById(R.id.mNewsDetailProgressWheelWrapper);
        mNewsDetailProgressWheel.spin();
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
                    //TODO load more
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
        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL + getIntent().getStringExtra("url"), NetworkRequest.RequestMethod.GET);
        _Request.setCallback(new JsonCallback<NewsDetail>() {

            @Override
            public void success(NewsDetail result) {
                if(result != null) {
                    headerView.setDetailData(result, new NewsDetailHeaderView.HeaderVeiwPullUpListener() {
                        @Override
                        public void onclickPullUp(int height) {
                            msgvNewsDetail.mFlingRunnable.startScroll(-height, 1000);
                        }
                    });
                    mNewsDetailAdapter.setData(result.relate);
                    mNewsDetailAdapter.notifyDataSetChanged();

                    if (HomeAty.VALUE_NEWS_SOURCE.equals(mSource)) {
                        msgvNewsDetail.setSelection(1);
                    }
                }else{
                    ToastUtil.toastShort("新闻的内容为空，无法打开");
                    NewsDetailAty.this.finish();
                }

                mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                mNewsDetailProgressWheel.stopSpinning();
                mNewsDetailProgressWheel.setVisibility(View.GONE);

            }

            @Override
            public void failed(MyAppException exception) {
//                Logger.e(TAG, exception.getMessage());
                mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                mNewsDetailProgressWheel.stopSpinning();
                mNewsDetailProgressWheel.setVisibility(View.GONE);
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
        msgvNewsDetail.startFlingRunnable(300);
        headerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                headerView.setContentViewHeight(headerView.getContentView().getHeight());
            }
        });
    }

    public String getMacAddressAndDeviceid(Context c) {
        WifiManager wifiMan = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        String macAddr = wifiInf.getMacAddress();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId() + macAddr;
    }

    class StaggeredNewsDetailAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NewsDetail.Relate> mArrData;
        int screenWidth;

        StaggeredNewsDetailAdapter(Context context) {
            mContext = context;
            screenWidth = DeviceInfoUtil.getScreenWidth() / 2 - DensityUtil.dip2px(mContext, 24);
        }


        public void setData(ArrayList<NewsDetail.Relate> pArrData) {
            mArrData = pArrData;
        }

        @Override
        public int getCount() {
            return mArrData == null ? 0 : mArrData.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrData.get(position);
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
            final NewsDetail.Relate _Relate = mArrData.get(position);
            holder.tvContent.setText(_Relate.title);
            if (TextUtils.isEmpty(_Relate.img))
                holder.ivPicture.setVisibility(View.GONE);
            else {
                holder.ivPicture.setVisibility(View.VISIBLE);
                ImageLoaderHelper.loadImage(mContext, _Relate.img, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.ivPicture.getLayoutParams();
                        layoutParams.width = screenWidth;
                        layoutParams.height = screenWidth * loadedImage.getHeight() / loadedImage.getWidth();
                        holder.ivPicture.setLayoutParams(layoutParams);
                        holder.ivPicture.setImageBitmap(loadedImage);
                    }
                });
            }
            TextUtil.setResourceSiteIcon(holder.ivSource, _Relate.sourceSitename);
            holder.tvSource.setText(_Relate.sourceSitename);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent _Intent = new Intent(NewsDetailAty.this, NewsDetailWebviewAty.class);
                    _Intent.putExtra("url", _Relate.url);
                    startActivity(_Intent);
                    // add umeng statistic
                    HashMap<String, String> _MobParam = new HashMap<>();
                    _MobParam.put("resource_site_name", _Relate.sourceSitename);
                    MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_RELATE_ITEM_CLICK, _MobParam);
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
