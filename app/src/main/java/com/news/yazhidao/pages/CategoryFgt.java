package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.widget.LetterSpacingTextView;


public class CategoryFgt extends Fragment {

    private View rootView;
    private GridView mgvCategory;
    private CategoryAdapter mAdapter;
    private String[] marrCategoryName;
    private TypedArray marrCategoryDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {
        marrCategoryName = getResources().getStringArray(R.array.category_list_name);
        marrCategoryDrawable = getResources().obtainTypedArray(R.array.bg_category_list);
        mAdapter = new CategoryAdapter(getActivity());
        mAdapter.setData(marrCategoryName, marrCategoryDrawable);
    }

    private void findViews() {
        mgvCategory = (GridView) rootView.findViewById(R.id.category_gridview);
        mgvCategory.setAdapter(mAdapter);
        mgvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalParams.pager.setCurrentItem(1);
                GlobalParams.currentCatePos = position;
                Intent intent = new Intent("sendposition");
                GlobalParams.context.sendBroadcast(intent);
                if(GlobalParams.tabs != null) {
                    setTabTitle(position);
                }
            }
        });
    }

    private void setTabTitle(int position) {

        switch (position) {
            case 0:
                GlobalParams.tabs.updateSelection2(1, "谷歌今日焦点");
                break;
            case 1:
                GlobalParams.tabs.updateSelection2(1, "实事");
                break;
            case 2:
                GlobalParams.tabs.updateSelection2(1, "娱乐");
                break;
            case 3:
                GlobalParams.tabs.updateSelection2(1, "科技");
                break;
            case 4:
                GlobalParams.tabs.updateSelection2(1, "国际");
                break;
            case 5:
                GlobalParams.tabs.updateSelection2(1, "体育");
                break;
            case 6:
                GlobalParams.tabs.updateSelection2(1, "财经");
                break;
            case 7:
                GlobalParams.tabs.updateSelection2(1, "港台");
                break;
            case 8:
                GlobalParams.tabs.updateSelection2(1, "社会");
                break;
        }

    }

    class CategoryAdapter extends BaseAdapter {

        Context mContext;
        String[] marrCategoryName;
        TypedArray marrCategoryDrawable;

        public CategoryAdapter(Context context) {
            mContext = context;
        }

        public void setData(String[] categoryName, TypedArray categoryDrawable) {
            marrCategoryName = categoryName;
            marrCategoryDrawable = categoryDrawable;
        }

        @Override
        public int getCount() {
            return marrCategoryName.length;
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
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_category, null, false);
                holder.ivBgIcon = (ImageView) convertView.findViewById(R.id.iv_bg_icon);
                holder.tvName = (LetterSpacingTextView) convertView.findViewById(R.id.tv_name);
                holder.tvName.setFontSpacing(5);
                holder.tvName.setTextSize(16);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.ivBgIcon.setBackgroundResource(marrCategoryDrawable.getResourceId(position, 0));
            holder.tvName.setText(marrCategoryName[position]);
            return convertView;
        }
    }

    class Holder {
        ImageView ivBgIcon;
        LetterSpacingTextView tvName;
    }


}