package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.AttentionDetailDialog;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SubscribeListActivity extends SwipeBackActivity {

    public static final String KEY_SUBSCRIBE_LIST = "key_subscribe_list";
    private static final int REQUEST_LOGIN_CODE = 1035;
    private static final int REQUEST_SUBSCRIBE_CODE = 1036;
    public static final String KEY_ATTENTION_INDEX = "key_attention_index";
    private TextView mSubscribeListLeftBack;
    private PullToRefreshListView mAttentionListView;
    private SubscribeListAdapter mAdapter;
    private Context mContext;
    private ArrayList<AttentionListEntity> mAttentionListEntities = new ArrayList<AttentionListEntity>();
    private ArrayList<AttentionListEntity> mAttentionListTemp;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_subscribe_list);
        mContext = this;
    }

    @Override
    protected void initializeViews() {
        mSubscribeListLeftBack = (TextView) findViewById(R.id.mSubscribeListLeftBack);
        mAttentionListView = (PullToRefreshListView) findViewById(R.id.aty_SubscribeList_PullToRefreshListView);
    }

    @Override
    protected void loadData() {
        ArrayList<AttentionListEntity> subscribeList = SharedPreManager.getSubscribeList();
        if (!TextUtil.isListEmpty(subscribeList)) {
            mAttentionListEntities = subscribeList;
        } else {
            mAttentionListEntities = (ArrayList<AttentionListEntity>) getIntent().getSerializableExtra(KEY_SUBSCRIBE_LIST);
        }
        mAttentionListTemp = TextUtil.copyArrayList(mAttentionListEntities);
        mAttentionListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mAttentionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.e("jigang", "----pos=" + position);
                Intent attentionAty = new Intent(SubscribeListActivity.this, AttentionActivity.class);
                AttentionListEntity attention = mAttentionListEntities.get(position - 1);
                attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_CONPUBFLAG, attention.getFlag());
                attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_HEADIMAGE, attention.getIcon());
                attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_TITLE, attention.getName());
                attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_INDEX, position - 1 );
                startActivityForResult(attentionAty, REQUEST_SUBSCRIBE_CODE);
            }
        });
        mAdapter = new SubscribeListAdapter(mContext);

        mAdapter.setNewsFeed(mAttentionListEntities);
        mAttentionListView.setAdapter(mAdapter);
        mSubscribeListLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e("aaa", "requestCode == " + requestCode + ",resultCode == " + resultCode);
        if (resultCode == 1234) {
            if (data != null) {
                boolean attention = data.getBooleanExtra(AttentionActivity.KEY_ATTENTION_CONPUBFLAG, false);
                int position = data.getIntExtra(AttentionActivity.KEY_ATTENTION_INDEX, 0);
                AttentionListEntity entity = mAttentionListEntities.get(position);
                if (attention != (entity.getFlag() > 0)) {
                    if (attention) {
                        entity.setFlag(1);
                        entity.setConcern(entity.getConcern() + 1);
                    } else {
                        entity.setFlag(0);
                        entity.setConcern(entity.getConcern() - 1);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_LOGIN_CODE && resultCode == LoginAty.REQUEST_CODE) {
            if (data != null) {
                int position = data.getIntExtra(KEY_ATTENTION_INDEX, 0);
                changeAttentionStatus(mAdapter, mAttentionListEntities.get(position));
            }
        }
    }

    /**
     * 订阅源适配器
     */
    public class SubscribeListAdapter extends CommonAdapter<AttentionListEntity> {
        private Context mContext;

        public SubscribeListAdapter(Context mContext) {
            super(R.layout.subscribelist_item, mContext, null);
            this.mContext = mContext;

        }

        @Override
        public void convert(CommonViewHolder holder, final AttentionListEntity attentionListEntity, final int position) {
            holder.setSimpleDraweeViewURI(R.id.img_SubscribeListItem_icon, attentionListEntity.getIcon(),position);
            holder.setTextViewText(R.id.tv_SubscribeListItem_name, attentionListEntity.getName());
            Logger.e("aaa", "attentionListEntity.getConcern()==" + attentionListEntity.getConcern());
            int concern = attentionListEntity.getConcern();
            String personNum = "";
            if (concern > 10000) {
                float result = (float) concern / 10000;
                personNum = Math.round(result * 10) / 10f + "万人关注";
            } else if (concern > 0) {
                personNum = concern + "人关注";
            } else {
                personNum = "";
            }
            holder.setTextViewText(R.id.tv_SubscribeListItem_personNum, personNum);
            if (attentionListEntity.getFlag() == 0) {
                holder.setTextViewTextBackgroundResource(R.id.mAttention_btn, R.drawable.unattention_tv_shape);
                holder.setTextViewTextColor(R.id.mAttention_btn, R.color.attention_line_color);
                holder.setTextViewText(R.id.mAttention_btn, "关注");
            } else {
                holder.setTextViewTextBackgroundResource(R.id.mAttention_btn, R.drawable.attention_tv_shape);
                holder.setTextViewTextColor(R.id.mAttention_btn, R.color.unattention_line_color);
                holder.setTextViewText(R.id.mAttention_btn, "已关注");
            }
            holder.getView(R.id.mAttention_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = SharedPreManager.getUser(mContext);
                    if (user != null && user.isVisitor()) {
                        Intent loginAty = new Intent(mContext, LoginAty.class);
                        loginAty.putExtra(SubscribeListActivity.KEY_ATTENTION_INDEX, position);
                        startActivityForResult(loginAty, REQUEST_LOGIN_CODE);
                    } else {
                        changeAttentionStatus(SubscribeListAdapter.this, attentionListEntity);
                    }
                }
            });
        }
    }

    /**
     * 更改关注的状态
     *
     * @param subscribeListAdapter
     * @param attentionListEntity
     */
    private void changeAttentionStatus(SubscribeListAdapter subscribeListAdapter, AttentionListEntity attentionListEntity) {
        if (attentionListEntity.getFlag() == 1) {
            SharedPreManager.deleteAttention(attentionListEntity.getName());
            attentionListEntity.setFlag(0);
            attentionListEntity.setConcern(attentionListEntity.getConcern() - 1);
        } else {
            SharedPreManager.addAttention(attentionListEntity.getName());
            if(SharedPreManager.getBoolean(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID)){
                ToastUtil.showAttentionSuccessToast(mContext);
            }else{
                AttentionDetailDialog attentionDetailDialog = new AttentionDetailDialog(mContext,attentionListEntity.getName());
                attentionDetailDialog.show();
                SharedPreManager.save(CommonConstant.FILE_DATA, CommonConstant.KEY_ATTENTION_ID,true);
            }
            attentionListEntity.setFlag(1);
            attentionListEntity.setConcern(attentionListEntity.getConcern() + 1);
        }
        subscribeListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreManager.saveSubscribeList(mAttentionListEntities);
        /**用户退出时发送关注和取消关注的状态*/
        User user = SharedPreManager.getUser(this);
        for (int i = 0; i < mAttentionListTemp.size(); i++) {
            AttentionListEntity oldEntity = mAttentionListTemp.get(i);
            AttentionListEntity newEntity = mAttentionListEntities.get(i);
            if (oldEntity.getFlag() != newEntity.getFlag()) {
                attentionSubscribe(newEntity, user);
            }
        }
    }

    private void attentionSubscribe(final AttentionListEntity attentionListEntity, User user) {
        String pname = null;
        try {
            pname = URLEncoder.encode(attentionListEntity.getName(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject json = new JSONObject();
        final int requestMethod = attentionListEntity.getFlag() > 0 ? Request.Method.POST : Request.Method.DELETE;
        Logger.e("jigang", "attention url = " + (HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + user.getMuid() + "&pname=" + pname) + ",==" + requestMethod);
        DetailOperateRequest request = new DetailOperateRequest(requestMethod,
                HttpConstant.URL_ADDORDELETE_ATTENTION + "uid=" + user.getMuid() + "&pname=" + pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("jigang", "attention data=" + data);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "attention = network fail " + error.getMessage());
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

}
