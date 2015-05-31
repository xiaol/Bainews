package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
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
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.GlobalParams;
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
import com.news.yazhidao.widget.imagewall.BitmapUtil;
import com.news.yazhidao.widget.imagewall.ViewWall;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.base.task.BackRunnable;
import app.base.task.CallbackRunnable;
import app.base.task.Compt;


public class NewsDetailAty extends SwipeBackActivity {

    private static final String TAG = "NewsDetailAty";
    private PullToRefreshStaggeredGridView mPullToRefreshStaggeredGridView;
    private StaggeredGridView msgvNewsDetail;
    private StaggeredNewsDetailAdapter mNewsDetailAdapter;
    private ImageView mivBack;
    private NewsDetailHeaderView headerView;
    private ProgressWheel mNewsDetailProgressWheel;
    private View mNewsDetailProgressWheelWrapper;
    //从哪儿进入的详情页
    private String mSource;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.aty_detail);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void initializeViews() {
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        mSource = getIntent().getStringExtra(NewsFeedFragment.KEY_NEWS_SOURCE);
        mNewsDetailAdapter = new StaggeredNewsDetailAdapter(this);
        headerView = new NewsDetailHeaderView(this);

        mivBack = (ImageView) findViewById(R.id.back_imageView);
        mNewsDetailProgressWheel = (ProgressWheel) findViewById(R.id.mNewsDetailProgressWheel);
        mNewsDetailProgressWheelWrapper = findViewById(R.id.mNewsDetailProgressWheelWrapper);
        mNewsDetailProgressWheel.spin();
        mPullToRefreshStaggeredGridView = (PullToRefreshStaggeredGridView) findViewById(R.id.news_detail_staggeredGridView);
        mPullToRefreshStaggeredGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        msgvNewsDetail = mPullToRefreshStaggeredGridView.getRefreshableView();
//        msgvNewsDetail.setSmoothScrollbarEnabled(true);
        msgvNewsDetail.addHeaderView(headerView);
        msgvNewsDetail.setAdapter(mNewsDetailAdapter);
        setListener();
    }

    @Override
    protected void loadData() {
        GlobalParams.news_detail_url = getIntent().getStringExtra("url");
//        String picwalldetail = "http://121.41.75.213:9999/news/baijia/fetchContent?url=http://news.163.com/photoview/00AO0001/90611.html";
        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_GET_NEWS_DETAIL + GlobalParams.news_detail_url, NetworkRequest.RequestMethod.GET);
        _Request.setCallback(new JsonCallback<NewsDetail>() {

            @Override
            public void success(final NewsDetail result) {
                if (result != null) {
                    headerView.setDetailData(result, new NewsDetailHeaderView.HeaderVeiwPullUpListener() {
                        @Override
                        public void onclickPullUp(int height) {
                            msgvNewsDetail.mFlingRunnable.startScroll(-height, 1000);
                        }
                    });
                    mNewsDetailAdapter.setData(result.relate);
                    mNewsDetailAdapter.notifyDataSetChanged();

                    if (NewsFeedFragment.VALUE_NEWS_SOURCE.equals(mSource)) {
                        msgvNewsDetail.setSelection(1);
                    }
                    new Compt().putTask(new BackRunnable() {
                        @Override
                        public void run() {
                            for (Map<String, String> m : result.imgWall) {
                                String url = m.get("img").toString();

                                BitmapFactory.Options op = null;
                                while (op == null)
                                    try {
                                        op = BitmapUtil.getBitmapFactoryOptions(url);
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        continue;
                                    }


                                m.put("w", "" + op.outWidth);
                                m.put("h", "" + op.outHeight);


                            }


                            List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>(result.imgWall);
                            List<HashMap<String, String>> minor = new ArrayList<HashMap<String, String>>();
                            get3Matched(resultList, minor);
                            get2Matched(resultList, minor);
                            get1Matched(resultList, minor);


                        }
                    }
                            , new CallbackRunnable() {
                        @Override
                        public boolean run(Message message, boolean b, Activity activity) throws Exception {
                            return false;
                        }
                    }).run();


                } else

                {
                    ToastUtil.toastShort("新闻的内容为空，无法打开");
                    NewsDetailAty.this.finish();
                }

                mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                mNewsDetailProgressWheel.stopSpinning();
                mNewsDetailProgressWheel.setVisibility(View.GONE);

            }

            private List<HashMap<String, String>> get1Matched(List<HashMap<String, String>> resultList, List<HashMap<String, String>> minor) {
                return minor;
            }

            private List<HashMap<String, String>> get2Matched(List<HashMap<String, String>> maps, List source) {
                int stepcnst = 50;
                float ratio = 1f;
                float constW = 0;
                for (int i = stepcnst; i < GlobalParams.screenWidth; i += stepcnst) {
                    if (i == GlobalParams.screenWidth - 1) {
                        if (ratio == 1f)
                            break;
                        ratio -= 0.1;

                        i = 1;
                        continue;
                    }
                    int scaledw1 = i;
                    int scaledw2 = GlobalParams.screenWidth - scaledw1;
                    int scaledh1 = 0;
                    int scaledh2 = 0;
                    HashMap<String, String> m1 = null;
                    HashMap<String, String> m2 = null;
                    List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(source);

                    for (HashMap<String, String> first : list) {
                        int whead = Integer.parseInt(first.get("w").toString());
                        int hhead = Integer.parseInt(first.get("h").toString());

                        if (scaledh1 == 0) {

                            scaledh1 = hhead * scaledw1 / whead;
                            m1 = first;
                        }
                        List<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>(list);
                        for (HashMap<String, String> m : list2) {

                            int w = Integer.parseInt(m.get("w").toString());
                            int h = Integer.parseInt(m.get("h").toString());


                            scaledh2 = h * scaledw2 / w;

                            if (Math.abs(scaledh1 - scaledh2) < 10) {
                                if (scaledh2 * ratio >= scaledh1) {
                                    m2 = m;
                                    m2.put("units", "2");
                                    m2.put("position", "2");
                                    m2.put("scaledh", "" + scaledh2);
                                    m2.put("scaledw", "" + scaledw2);
                                    m1.put("units", "2");
                                    m1.put("position", "1");
                                    m1.put("scaledh", "" + scaledh1);
                                    m1.put("scaledw", "" + scaledw1);
                                    maps.add(m1);
                                    maps.add(m2);
                                    source.remove(m2);
                                    source.remove(m1);
                                    return source;
                                } else {

                                    scaledh2 = 0;
                                    m2 = null;
                                    continue;
                                }
                            }

                        }
                    }
                }
                return source;
            }

            private List<HashMap<String, String>> get3Matched(List<HashMap<String, String>> maps, List source) {
                float ratio1 = 1f;
                float constW = 0;
                int stepcnst = 50;

                List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(source);
                HashMap<String, String> m1 = null;
                for (HashMap<String, String> first : list) {

                    int whead = Integer.parseInt(first.get("w").toString());
                    int hhead = Integer.parseInt(first.get("h").toString());
                    for (int i = stepcnst; i < GlobalParams.screenWidth; i += stepcnst) {
                        if (i == GlobalParams.screenWidth - 1) {
                            if (ratio1 == 1f)
                                break;
                            ratio1 -= 0.1;

                            i = 1;
                            continue;
                        }
                        int scaledw1 = i;
                        int scaledw2 = GlobalParams.screenWidth - scaledw1;
                        int scaledh1 = 0;
                        int scaledh2 = 0;
                        if (scaledh1 == 0) {

                            scaledh1 = hhead * scaledw1 / whead;
                            m1 = first;
                        }
                        List<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>(list);
                        HashMap<String, String> m2 = null;
                        float ratio2 = 1f;
                        for (HashMap<String, String> m : list2) {


                            int w = Integer.parseInt(m.get("w").toString());
                            int h = Integer.parseInt(m.get("h").toString());

                            for (int i2 = stepcnst; i2 < GlobalParams.screenWidth; i2 += stepcnst) {
                                if (i2 == GlobalParams.screenWidth - 1) {
                                    if (ratio2 == 1f)
                                        break;
                                    ratio2 -= 0.1;

                                    i = 1;
                                    continue;
                                }
                                scaledw2 = i;
                                int scaledw3 = GlobalParams.screenWidth - scaledw1 - scaledw2;
                                int scaledh3 = 0;
                                if (scaledh1 == 0) {

                                    scaledh1 = hhead * scaledw1 / whead;
                                    m1 = first;
                                }
                                if (scaledh2 == 0) {

                                    scaledh2 = h * scaledw2 / w;
                                    m2 = m;
                                }
                                scaledh3 = h * scaledw3 / w;
                                HashMap<String, String> m3 = null;
                                for (HashMap<String, String> mm : list2) {


                                    int w3 = Integer.parseInt(m.get("w").toString());
                                    int h3 = Integer.parseInt(m.get("h").toString());


                                    if (Math.abs(scaledh1 - scaledh2) < 10 && Math.abs(scaledh2 - scaledh3) < stepcnst) {
                                        if (scaledh2 * ratio1 >= scaledh1 && scaledh3 * ratio2 >= scaledh2) {
                                            m3 = mm;
                                            m3.put("units", "3");
                                            m3.put("position", "3");
                                            m3.put("scaledh", "" + scaledh3);
                                            m3.put("scaledw", "" + scaledw3);
                                            m2.put("units", "3");
                                            m2.put("position", "2");
                                            m2.put("scaledh", "" + scaledh2);
                                            m2.put("scaledw", "" + scaledw2);
                                            m1.put("units", "3");
                                            m1.put("position", "1");
                                            m1.put("scaledh", "" + scaledh1);
                                            m1.put("scaledw", "" + scaledw1);
                                            maps.add(m1);
                                            maps.add(m2);
                                            maps.add(m3);
                                            source.remove(m3);
                                            source.remove(m2);
                                            source.remove(m1);
                                            return source;
                                        } else {

                                            scaledh2 = 0;
                                            scaledh1 = 0;
                                            scaledh3 = 0;
                                            m2 = null;
                                            m1 = null;
                                            m3 = null;
                                            continue;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                return source;
            }

            @Override
            public void failed(MyAppException exception) {
                mNewsDetailProgressWheelWrapper.setVisibility(View.GONE);
                mNewsDetailProgressWheel.stopSpinning();
                mNewsDetailProgressWheel.setVisibility(View.GONE);
            }
        }

                .

                        setReturnType(new TypeToken<NewsDetail>() {
                                }

                                        .

                                                getType()

                        ));
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

    @Override
    public void finish() {
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (NewsFeedFragment.VALUE_NEWS_NOTIFICATION.equals(mSource)) {
            Intent intent = new Intent(this, HomeAty.class);
            startActivity(intent);
        }
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
