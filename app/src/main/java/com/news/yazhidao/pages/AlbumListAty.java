package com.news.yazhidao.pages;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.listener.FetchAlbumSubItemsListener;
import com.news.yazhidao.net.request.FetchAlbumSubItemsRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.digger.DigProgressView;

import java.util.ArrayList;


/**
 * 专辑列表页面
 * Created by fengjigang on 15/7/23.
 */
public class AlbumListAty extends BaseActivity {
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

    private ListView mSpecialLv;
    private TextView mCommonHeaderTitle;
    private View mCommonHeaderLeftBack;
    private SpecialLvAdapter mSpecialLvAdapter;
    private ArrayList<AlbumSubItem> mAlbumSubItems;
    private DiggerAlbum mDiggerAlbum;
    /**
     * 是否是新添加的挖掘内容
     */
    boolean isNewAdd;
    private int mScreenWidth;
    private int mScreenHeight;

    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initializeViews();
        loadData();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_special_layout);
    }

    @Override
    protected void initializeViews() {
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        Bundle bundle = getIntent().getBundleExtra(KEY_DIG_SPECIAL_INTENT);
        mDiggerAlbum = (DiggerAlbum) bundle.getSerializable(KEY_DIG_SPECIAL_BUNDLE);
        isNewAdd = bundle.getBoolean(KEY_DIG_IS_NEW_ADD);
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


    }

    @Override
    protected void loadData() {
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
                Intent intent = new Intent(AlbumListAty.this, NewsDetailAty.class);
                AlbumSubItem albumSubItem = mAlbumSubItems.get(position);
                /**判断是否已经挖掘完毕,挖掘完事儿后,方可打开*/
                if ("0".equals(albumSubItem.getStatus())) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(KEY_IS_DIGGER, true);
                    bundle.putSerializable(KEY_ALBUMSUBITEM, albumSubItem);
                    intent.putExtra(KEY_BUNDLE, bundle);
                    intent.putExtra(KEY_IS_NEW_API, true);
                    startActivityForResult(intent, 0);

                } else {
                    ToastUtil.toastShort("正在挖掘中,请回退页面查看!");
                }
            }
        });
        FetchAlbumSubItemsRequest.fetchAlbumSubItems(this, mDiggerAlbum.getAlbum_id(), isNewAdd, new FetchAlbumSubItemsListener() {
            @Override
            public void fetchAlbumSubItemsDone(ArrayList<AlbumSubItem> albumSubItems) {
                mAlbumSubItems = albumSubItems;
                mSpecialLvAdapter.notifyDataSetChanged();
            }
        });
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
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (250.0f / 1280 * mScreenHeight)));
                holder.mSpecialItemTopWrapper = convertView.findViewById(R.id.mSpecialItemTopWrapper);
                holder.mSpecialItemTopWrapper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (250.0f / 1280 * mScreenHeight) - DensityUtil.dip2px(AlbumListAty.this, 50)));
                holder.mSpecialItemIcon = (ImageView) convertView.findViewById(R.id.mSpecialItemIcon);
                RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams((int) (156.0f / 720 * mScreenWidth), (int) (120.0f / 1280 * mScreenHeight));
                iconParams.setMargins(DensityUtil.dip2px(AlbumListAty.this, 8), 0, 0, 0);
                iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
                holder.mSpecialItemIcon.setLayoutParams(iconParams);
                holder.mSpecialItemTitle = (TextView) convertView.findViewById(R.id.mSpecialItemTitle);
                holder.mSpecialItemOnlyOne = (TextView) convertView.findViewById(R.id.mSpecialItemOnlyOne);
                holder.mSpecialItemUrl = (TextView) convertView.findViewById(R.id.mSpecialItemUrl);
                holder.mSpecialItemProgress = (DigProgressView) convertView.findViewById(R.id.mSpecialItemProgress);

                convertView.setTag(holder);
            } else {
                holder = (SpecialLvHolder) convertView.getTag();
            }
            AlbumSubItem albumSubItem = mAlbumSubItems.get(position);
            holder.mSpecialItemIcon.setBackgroundColor(TextUtil.getRandomColor4Special(AlbumListAty.this));
            holder.mSpecialItemTitle.setText(albumSubItem.getSearch_key());

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

            holder.mSpecialItemProgress.setCurrentStep(Integer.valueOf(albumSubItem.getStatus()));
            return convertView;
        }
    }

    static class SpecialLvHolder {
        View mSpecialItemTopWrapper;
        ImageView mSpecialItemIcon;
        TextView mSpecialItemTitle;
        TextView mSpecialItemOnlyOne;
        TextView mSpecialItemUrl;
        DigProgressView mSpecialItemProgress;
    }
}
