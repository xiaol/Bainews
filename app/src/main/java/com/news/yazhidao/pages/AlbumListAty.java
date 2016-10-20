package com.news.yazhidao.pages;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.news.yazhidao.R;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.AlbumSubItemDao;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.NewsDetailForDigger;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.request.FetchAlbumSubItemsRequest;
import com.news.yazhidao.net.volley.DiggerRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 专辑列表页面
 * Created by fengjigang on 15/7/23.
 */
public class AlbumListAty extends SwipeBackActivity implements View.OnClickListener {
    /**
     * 是不是调用的新接口的数据,谷歌今日焦点不是新接口
     */
    public static final String KEY_IS_NEW_API = "key_is_new_api";
    /**
     * 是不是挖掘机打开的详情页
     */
    public static final String KEY_IS_DIGGER = "key_is_digger";
    /**
     * 挖掘新闻entity类
     */
    public static final String KEY_ALBUMSUBITEM = "key_albumsubitem";
    /**
     * 传递的bundle
     */
    public static final String KEY_BUNDLE = "key_bundle";
    public static final String KEY_DIG_SPECIAL_INTENT = "key_dig_special_intent";
    public static final String KEY_DIG_SPECIAL_BUNDLE = "key_dig_special_bundle";
    public static final String KEY_DIG_IS_NEW_ADD = "key_dig_is_new_add";
    public static final String KEY_DIG_SPECIAL_ALBUM = "key_dig_special_album";
    /**挖掘机相关状态*/
    public static final String DIGGER_STATUS_SUCCESS = "0";//挖掘完成
    public static final String DIGGER_STATUS_ERROR = "404";//挖掘错误
    public static final String DIGGER_STATUS_UNDO = "1";//尚未挖掘


    /**
     * 刷新数据
     */
    public static final String ACTION_REFRESH_DATA = "com.news.yazhidao.ACTION_REFRESH_DATA";
    public static final String KEY_SEARCH_KEY = "key_search_key";
    public static final String KEY_ALBUM_TITLE = "key_album_title";
    public static final String KEY_CREATETIME = "key_createtime";
    public static final String KEY_NEWSDETAIL_FOR_DIGGER = "key_newsdetail_for_digger";

    private ListView mSpecialLv;
    private TextView mCommonHeaderTitle;
    private View mCommonHeaderLeftBack;
    private SpecialLvAdapter mSpecialLvAdapter;
    private ArrayList<AlbumSubItem> mAlbumSubItems;
    private DiggerAlbum mDiggerAlbum;
    private HashMap<AlbumSubItem, NewsDetailForDigger> mDetailDiggers = new HashMap<>();
    private RequestQueue mRequestQueue;

    /**
     * 1.是否是新添加的挖掘内容
     * 2.此处判断从何处打开的该Activity,isNewAdd = true ,表示挖掘后立即打开,false 表示 在专辑列表中点击的专辑item
     */
    boolean isNewAdd;
    private int mScreenWidth;
    private int mScreenHeight;
    private View mCommonHeaderWrapper;


    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_topic_layout);
    }

    @Override
    protected void initializeViews() {
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        Bundle bundle = getIntent().getBundleExtra(KEY_DIG_SPECIAL_INTENT);
        mAlbumSubItems = (ArrayList<AlbumSubItem>) bundle.getSerializable(KEY_DIG_SPECIAL_BUNDLE);
        mDiggerAlbum = (DiggerAlbum) bundle.getSerializable(KEY_DIG_SPECIAL_ALBUM);
        isNewAdd = bundle.getBoolean(KEY_DIG_IS_NEW_ADD);
        mCommonHeaderWrapper = findViewById(R.id.mCommonHeaderWrapper);
        mCommonHeaderTitle = (TextView) findViewById(R.id.mCommonHeaderTitle);
        mCommonHeaderTitle.setText(mDiggerAlbum.getAlbum_title());
        mCommonHeaderLeftBack = findViewById(R.id.mCommonHeaderLeftBack);
        mCommonHeaderLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumListAty.this.finish();
            }
        });
        mSpecialLv = (ListView) findViewById(R.id.mSpecialLv);
        changeCommonHeaderColor();

    }

    @Override
    protected void loadData() {
        /**查看当前挖掘的新闻挖掘状态*/
        checkDiggerNewsStatus();

        mSpecialLvAdapter = new SpecialLvAdapter();
        mSpecialLv.setAdapter(mSpecialLvAdapter);
        mSpecialLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            long firstClick;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (System.currentTimeMillis() - firstClick <= 1500) {
                    firstClick = System.currentTimeMillis();
                    return;
                }
                firstClick = System.currentTimeMillis();
                Intent intent = new Intent(AlbumListAty.this, NewsDetailAty2.class);
                AlbumSubItem albumSubItem = mAlbumSubItems.get(position);
                /**判断是否已经挖掘完毕,挖掘完事儿后,方可打开*/
                if (DIGGER_STATUS_SUCCESS.equals(albumSubItem.getStatus())) {
                    //TODO 在这儿加入挖掘的新闻intent
                    NewsDetailForDigger detailForDigger = albumSubItem.getDetailForDigger();
                    String searchKey = albumSubItem.getSearch_key();
                    String albumTitle = albumSubItem.getDiggerAlbum().getAlbum_title();
                    String createtime = albumSubItem.getCreateTime();

                    Intent intent1 = new Intent(AlbumListAty.this,DiggerNewsDetail.class);
                    intent1.putExtra(KEY_SEARCH_KEY,searchKey);
                    intent1.putExtra(KEY_ALBUM_TITLE,albumTitle);
                    intent1.putExtra(KEY_CREATETIME,createtime);
                    intent1.putExtra(KEY_NEWSDETAIL_FOR_DIGGER,detailForDigger);
                    startActivity(intent1);

                } else {
                    ToastUtil.toastShort("正在挖掘中...");
                }
            }
        });

    }

    /**
     * 检查挖掘列表中的状态
     */
    private void checkDiggerNewsStatus() {
        if (!TextUtil.isListEmpty(mAlbumSubItems)) {
            for (final AlbumSubItem item : mAlbumSubItems) {
                /**需要请求挖掘的新闻*/
                if (!DIGGER_STATUS_SUCCESS.equals(item.getStatus())) {
                    startDiggerNews(item);
                }
            }
        }
    }

    /**
     * 开始挖掘新闻数据
     */
    private void startDiggerNews(final AlbumSubItem item) {
        mRequestQueue = Volley.newRequestQueue(this);
        DiggerRequest<NewsDetailForDigger> diggerRequest = new DiggerRequest<>(AlbumListAty.this, item, HttpConstant.URL_DIGGER_NEWS,
                new Response.Listener<NewsDetailForDigger>() {
                    @Override
                    public void onResponse(NewsDetailForDigger result) {
                        int index = mAlbumSubItems.indexOf(item);
                        AlbumSubItem subItem = mAlbumSubItems.get(index);
                        if (result != null) {
                            subItem.setStatus(DIGGER_STATUS_SUCCESS);
                            AlbumSubItemDao albumSubItemDao = new AlbumSubItemDao(AlbumListAty.this);
                            subItem.setDetailForDigger(result);
                            subItem.setImg(result.getPostImg());
                            albumSubItemDao.update(subItem);

                        } else {
                            subItem.setStatus(DIGGER_STATUS_ERROR);
                        }
                        mSpecialLvAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int index = mAlbumSubItems.indexOf(item);
                        mAlbumSubItems.get(index).setStatus(DIGGER_STATUS_ERROR);
                        mSpecialLvAdapter.notifyDataSetChanged();
                        mDetailDiggers.put(item, null);
                    }
                }
        );
        diggerRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 2, 1.0f));
        diggerRequest.setTag(AlbumListAty.this);
        mRequestQueue.add(diggerRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    /**
     * 联网获取数据
     *
     * @param pDao
     */
    private void loadDataFromServer(final AlbumSubItemDao pDao) {
        FetchAlbumSubItemsRequest.fetchAlbumSubItems(this, mDiggerAlbum.getAlbum_id(), isNewAdd, new JsonCallback<ArrayList<AlbumSubItem>>() {
            @Override
            protected void asyncPostRequest(ArrayList<AlbumSubItem> subItems) {
                //存数据库
                if (!TextUtil.isListEmpty(subItems)) {
                    for (AlbumSubItem item : subItems) {
                        item.setDiggerAlbum(mDiggerAlbum);
                        if (pDao.queryByTitleAndUrl(item.getSearch_key(), item.getSearch_url()) == null) {
                            pDao.insert(item);
                        } else {
                            pDao.update(item);
                        }

                    }
                }
            }

            @Override
            public void success(ArrayList<AlbumSubItem> result) {
                mAlbumSubItems = result;
                mSpecialLvAdapter.notifyDataSetChanged();
            }

            @Override
            public void failed(MyAppException exception) {
                ArrayList<AlbumSubItem> subItems = pDao.queryByAlbumId(mDiggerAlbum.getAlbum_id());
                mAlbumSubItems = subItems;
                mSpecialLvAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 修改标题栏的色值
     */
    private void changeCommonHeaderColor() {
        /**如果系统版本在4.4以下就使用黑色*/
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mCommonHeaderWrapper.setBackgroundResource(R.drawable.bg_common_header_gradient);
        } else {
            if ("dior".equals(Build.DEVICE) && "dior".equals(Build.PRODUCT)) {
                mCommonHeaderWrapper.setBackgroundResource(R.drawable.bg_common_header_gradient);
            }
        }
    }

    /**
     * 列表适配器
     */
    class SpecialLvAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mAlbumSubItems == null ? 0 : mAlbumSubItems.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpecialLvHolder holder;
            if (convertView == null) {
                holder = new SpecialLvHolder();
                convertView = View.inflate(AlbumListAty.this.getApplicationContext(), R.layout.aty_album_list_item, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (150.0f / 1280 * mScreenHeight)));
                holder.mSpecialItemIcon = (ImageView) convertView.findViewById(R.id.mSpecialItemIcon);
                RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams((int) (156.0f / 720 * mScreenWidth), (int) (120.0f / 1280 * mScreenHeight));
                iconParams.setMargins(DensityUtil.dip2px(AlbumListAty.this, 8), 0, 0, 0);
                iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
                holder.mSpecialItemIcon.setLayoutParams(iconParams);
                holder.mSpecialItemTitle = (TextView) convertView.findViewById(R.id.mSpecialItemTitle);
                holder.mSpecialItemOnlyOne = (TextView) convertView.findViewById(R.id.mSpecialItemOnlyOne);
                holder.mSpecialItemUrl = (TextView) convertView.findViewById(R.id.mSpecialItemUrl);
                holder.mSpecialStatusIc = (ImageView) convertView.findViewById(R.id.mSpecialStatusIc);
                convertView.setTag(holder);
            } else {
                holder = (SpecialLvHolder) convertView.getTag();
            }
            AlbumSubItem albumSubItem = mAlbumSubItems.get(position);
            String imgUrl = albumSubItem.getImg();
            if (!TextUtils.isEmpty(imgUrl)) {
                holder.mSpecialItemIcon.setImageURI(Uri.parse(imgUrl));
            }

            holder.mSpecialStatusIc.clearAnimation();
            if (DIGGER_STATUS_UNDO.equals(albumSubItem.getStatus())) {
                holder.mSpecialStatusIc.setImageResource(R.drawable.ic_digger_refresh);
                Animation anim = AnimationUtils.loadAnimation(AlbumListAty.this, R.anim.digger_refresh_rotate);
                holder.mSpecialStatusIc.startAnimation(anim);
            } else if (DIGGER_STATUS_SUCCESS.equals(albumSubItem.getStatus())) {
                holder.mSpecialStatusIc.setImageResource(R.drawable.ic_digger_completed);
            } else if (DIGGER_STATUS_ERROR.equals(albumSubItem.getStatus())) {
                holder.mSpecialStatusIc.setImageResource(R.drawable.ic_digger_error);
                holder.mSpecialStatusIc.setTag(R.id.mSpecialStatusIc, albumSubItem);//挖掘错误时,点击可以再次挖掘
            }

            holder.mSpecialStatusIc.setOnClickListener(AlbumListAty.this);
            holder.mSpecialItemTitle.setText(albumSubItem.getSearch_key());
            holder.mSpecialItemUrl.setText(albumSubItem.getSearch_url());

            holder.mSpecialItemOnlyOne.setVisibility(View.GONE);
            holder.mSpecialItemTitle.setVisibility(View.VISIBLE);
            holder.mSpecialItemUrl.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(albumSubItem.getSearch_url()) || TextUtils.isEmpty(albumSubItem.getSearch_key())) {
                holder.mSpecialItemOnlyOne.setVisibility(View.VISIBLE);
                holder.mSpecialItemTitle.setVisibility(View.GONE);
                holder.mSpecialItemUrl.setVisibility(View.GONE);
                holder.mSpecialItemOnlyOne.setText(albumSubItem.getSearch_url() + albumSubItem.getSearch_key());
                holder.mSpecialItemOnlyOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            return convertView;
        }
    }

    static class SpecialLvHolder {
        ImageView mSpecialItemIcon;
        TextView mSpecialItemTitle;
        TextView mSpecialItemOnlyOne;
        TextView mSpecialItemUrl;
        ImageView mSpecialStatusIc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSpecialStatusIc:
                AlbumSubItem item = (AlbumSubItem) v.getTag(R.id.mSpecialStatusIc);
                if (item != null && DIGGER_STATUS_ERROR.equals(item.getStatus())) {
                    ToastUtil.toastShort("开始再次挖掘");
                    ImageView imageView = (ImageView) v;
                    imageView.setImageResource(R.drawable.ic_digger_refresh);
                    Animation anim = AnimationUtils.loadAnimation(AlbumListAty.this, R.anim.digger_refresh_rotate);
                    imageView.startAnimation(anim);
                    startDiggerNews(item);
                }
                break;
        }
    }
}
