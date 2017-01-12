package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ChannelItemDao mChannelItemDao;
    private Context mContext;

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
        mContext = this;
        mChannelItemDao = new ChannelItemDao(this);
        marrSelect = new ArrayList<>();
        mtvStart = (TextView) findViewById(R.id.start_imageView);
        mgvSubscription = (GridView) findViewById(R.id.subscription_gridView);
        mTypedArray = getResources().obtainTypedArray(R.array.subscription_list_image);
        mAppName = getResources().getStringArray(R.array.subscription_list_name);
        mgvSubscription.setAdapter(new SubscriptionAdapter());
        mtvStart.setOnClickListener(this);
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
                if (NetUtil.checkNetWork(mContext) && marrSelect.size() > 0) {
                    for (int i = 0; i < marrSelect.size(); i++) {
                        addordeleteAttention(mNewAppName[marrSelect.get(i)]);
                    }
                    int position = mChannelItemDao.ResetSelectedByFocus();
                    Intent intent = new Intent(MainAty.ACTION_FOUCES);
                    intent.putExtra(MainAty.KEY_INTENT_CURRENT_POSITION, position);
                    sendBroadcast(intent);
                }
                onBackPressed();
                break;
        }
    }


    public void addordeleteAttention(String pname) {
        try {
            pname = URLEncoder.encode(pname, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        User user = SharedPreManager.getUser(this);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JSONObject json = new JSONObject();
        final String finalPname = pname;
        DetailOperateRequest request = new DetailOperateRequest(Request.Method.POST,
                HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + user.getMuid() + "&pname=" + pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //保存关注信息
                SharedPreManager.addAttention(finalPname);
                SharedPreManager.save(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID, true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);


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
