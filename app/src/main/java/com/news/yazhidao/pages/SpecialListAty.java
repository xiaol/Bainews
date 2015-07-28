package com.news.yazhidao.pages;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.DigSpecial;
import com.news.yazhidao.entity.DigSpecialItem;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.digger.DigProgressView;

import java.util.ArrayList;

/**
 * 专辑列表页面
 * Created by fengjigang on 15/7/23.
 */
public class SpecialListAty extends BaseActivity {

    private ListView mSpecialLv;
    private TextView mCommonHeaderTitle;
    public static final String KEY_DIG_SPECIAL_INTENT = "key_dig_special_intent";
    public static final String KEY_DIG_SPECIAL_BUNDLE = "key_dig_special_bundle";
    private ImageView mCommonHeaderLeftBack;
    private SpecialLvAdapter mSpecialLvAdapter;
    private ArrayList<DigSpecialItem> mSpecialLvDatas ;
    private int mScreenWidth;
    private int mScreenHeight;
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_special_layout);
        overridePendingTransition(R.anim.aty_right_enter, R.anim.aty_no_ani);
    }

    @Override
    protected void initializeViews() {
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        Bundle bundle = getIntent().getBundleExtra(KEY_DIG_SPECIAL_INTENT);
        DigSpecial special = (DigSpecial) bundle.getSerializable(KEY_DIG_SPECIAL_BUNDLE);
        mSpecialLvDatas = special.getSpecialItems();

        mCommonHeaderTitle = (TextView)findViewById(R.id.mCommonHeaderTitle);
        mCommonHeaderTitle.setText(special.getTitle());
        mCommonHeaderLeftBack = (ImageView)findViewById(R.id.mCommonHeaderLeftBack);
        mCommonHeaderLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpecialListAty.this.finish();
            }
        });
        mSpecialLv = (ListView)findViewById(R.id.mSpecialLv);
    }

    @Override
    protected void loadData() {
        mSpecialLvAdapter = new SpecialLvAdapter();
        mSpecialLv.setAdapter(mSpecialLvAdapter);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.aty_left_exit);
    }

    /**
     * 列表适配器
     */
     class SpecialLvAdapter extends BaseAdapter {


         @Override
         public int getCount() {
             return mSpecialLvDatas == null ? 0:mSpecialLvDatas.size();
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
             SpecialLvHolder holder ;
             if (convertView == null){
                 holder = new SpecialLvHolder();
                 convertView = View.inflate(SpecialListAty.this.getApplicationContext(),R.layout.aty_special_list_item,null);
                 convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (250.0f / 1280 * mScreenHeight)));
                 holder.mSpecialItemTopWrapper = convertView.findViewById(R.id.mSpecialItemTopWrapper);
                 holder.mSpecialItemTopWrapper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (250.0f / 1280 * mScreenHeight) - DensityUtil.dip2px(SpecialListAty.this,50)));
                 holder.mSpecialItemIcon = (ImageView) convertView.findViewById(R.id.mSpecialItemIcon);
                 RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams((int) (156.0f / 720 * mScreenWidth), (int) (120.0f / 1280 * mScreenHeight));
                 iconParams.setMargins(DensityUtil.dip2px(SpecialListAty.this,8),0,0,0);
                 iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
                 holder.mSpecialItemIcon.setLayoutParams(iconParams);
                 holder.mSpecialItemTitle = (TextView) convertView.findViewById(R.id.mSpecialItemTitle);
                 holder.mSpecialItemOnlyOne = (TextView) convertView.findViewById(R.id.mSpecialItemOnlyOne);
                 holder.mSpecialItemUrl = (TextView) convertView.findViewById(R.id.mSpecialItemUrl);
                 holder.mSpecialItemProgress = (DigProgressView) convertView.findViewById(R.id.mSpecialItemProgress);

                 convertView.setTag(holder);
             }else {
                 holder = (SpecialLvHolder) convertView.getTag();
             }
             DigSpecialItem digSpecialItem = mSpecialLvDatas.get(position);
             holder.mSpecialItemIcon.setBackgroundColor(TextUtil.getRandomColor4Special(SpecialListAty.this));
             holder.mSpecialItemTitle.setText(digSpecialItem.getTitle());
             holder.mSpecialItemUrl.setText(digSpecialItem.getUrl());
             holder.mSpecialItemProgress.setCurrentStep(digSpecialItem.getProgress());
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
