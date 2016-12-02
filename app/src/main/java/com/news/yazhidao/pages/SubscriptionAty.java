package com.news.yazhidao.pages;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by fengjigang on 15/10/28.
 * 主界面
 */
public class SubscriptionAty extends BaseActivity implements View.OnClickListener {

    private TextView mtvStart;
    private GridView mgvSubscription;
    private TypedArray mTypedArray;
    private String[] mAppName, mNewAppName;
    private ArrayList<Integer> marrSelect;
    private int[] mNewTypedArray;

    @Override
    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_subscription);
    }

    @Override
    protected void initializeViews() {
        marrSelect = new ArrayList<>();
        mtvStart = (TextView) findViewById(R.id.start_imageView);
        mgvSubscription = (GridView) findViewById(R.id.subscription_gridView);
        mTypedArray = getResources().obtainTypedArray(R.array.subscription_list_image);
        mAppName = getResources().getStringArray(R.array.subscription_list_name);
        mgvSubscription.setAdapter(new SubscriptionAdapter());
        mtvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mNewTypedArray = new int[9];
        mNewAppName = new String[9];
        Random r = new Random();
        List<Integer> list = new ArrayList<>();
        int i;
        while (list.size() < 9) {
            i = r.nextInt(54);
            if (!list.contains(i)) {
                list.add(i);
            }
        }
        for (int j = 0; j < 9; j++) {
            mNewTypedArray[j] = mTypedArray.getResourceId(list.get(j), 0);
            mNewAppName[j] = mAppName[list.get(j)];
        }

    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_imageView:
                break;
        }
    }

    private class SubscriptionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNewTypedArray == null ? 0 : mNewTypedArray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
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
                convertView = LayoutInflater.from(SubscriptionAty.this).inflate(R.layout.item_subscription, null);
                holder.ivSubscription = (ImageView) convertView.findViewById(R.id.subscription_image);
                holder.tvAppName = (TextView) convertView.findViewById(R.id.app_name);
                holder.ivButton = (ImageView) convertView.findViewById(R.id.subscription_button);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.ivSubscription.setBackgroundResource(mNewTypedArray[position]);
            holder.tvAppName.setText(mNewAppName[position]);
            if (marrSelect.contains(position)) {
                holder.ivButton.setBackgroundResource(R.drawable.subscription_button_select);
            } else {
                holder.ivButton.setBackgroundResource(R.drawable.subscription_button_unselect);
            }
            holder.ivButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (marrSelect.contains(position)) {
                        marrSelect.remove(Integer.valueOf(position));
                        holder.ivButton.setBackgroundResource(R.drawable.subscription_button_unselect);
                    } else {
                        marrSelect.add(Integer.valueOf(position));
                        holder.ivButton.setBackgroundResource(R.drawable.subscription_button_select);
                    }
                    if (marrSelect.size() > 0) {
                        mtvStart.setBackgroundResource(R.color.news_subscription_color2);
                    } else {
                        mtvStart.setBackgroundResource(R.color.news_subscription_color1);
                    }
                }
            });
            return convertView;
        }
    }

    class Holder {
        ImageView ivSubscription;
        TextView tvAppName;
        ImageView ivButton;
    }
}
