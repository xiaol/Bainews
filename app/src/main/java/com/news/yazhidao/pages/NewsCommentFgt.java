package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseFragment;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsCommentRequest;
import com.news.yazhidao.net.volley.NewsLoveRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.UserCommentDialog;

import java.util.ArrayList;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻评论页
 */
public class NewsCommentFgt extends BaseFragment {

    public static final int REQUEST_CODE = 1030;
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    private PullToRefreshListView mNewsCommentList;
    private String mDocid;
    private ArrayList<NewsDetailComment> mComments = new ArrayList<>();
    private CommentsAdapter mCommentsAdapter;
    private int mPageIndex = 1;
    private RefreshPageBroReceiber mRefreshReceiber;
    private User mUser;
    private NewsDetailComment mComment;
    private Holder mHolder;

    /**通知新闻详情页和评论fragment刷新评论*/
    public  class RefreshPageBroReceiber extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("jigang","detailaty refresh br");
            NewsDetailComment comment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
            mComments.add(0,comment);
            mCommentsAdapter.setData(mComments);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mDocid = arguments.getString(KEY_NEWS_DOCID);
        if(mRefreshReceiber == null){
            mRefreshReceiber = new RefreshPageBroReceiber();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            getActivity().registerReceiver(mRefreshReceiber, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRefreshReceiber != null){
            getActivity().unregisterReceiver(mRefreshReceiber);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_news_comment, null);
        mNewsCommentList = (PullToRefreshListView) rootView.findViewById(R.id.mNewsCommentList);
        mNewsCommentList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mCommentsAdapter = new CommentsAdapter(getActivity());
        mNewsCommentList.setAdapter(mCommentsAdapter);
        mNewsCommentList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        loadData();
        return rootView;
    }

    private void loadData() {
        Logger.e("jigang", "fetch comments url=" + HttpConstant.URL_FETCH_COMMENTS + "docid=" + mDocid);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        NewsCommentRequest<ArrayList<NewsDetailComment>> feedRequest = new NewsCommentRequest<ArrayList<NewsDetailComment>>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
        }.getType(), HttpConstant.URL_FETCH_COMMENTS + "docid=" + mDocid + "&page=" + (mPageIndex++), new Response.Listener<ArrayList<NewsDetailComment>>() {

            @Override
            public void onResponse(ArrayList<NewsDetailComment> result) {
                mNewsCommentList.onRefreshComplete();
                Logger.e("jigang", "network success, comment" + result);
                if (!TextUtil.isListEmpty(result)) {
                    mComments.addAll(result);
                    mCommentsAdapter.setData(mComments);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNewsCommentList.onRefreshComplete();
                Logger.e("jigang", "network fail");
            }
        });
        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LoginAty.REQUEST_CODE && data != null){
            mUser = (User) data.getSerializableExtra(LoginAty.KEY_USER_LOGIN);
            addNewsLove(mUser, mComment, mHolder);
        }
    }

    class CommentsAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<NewsDetailComment> comments;

        CommentsAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<NewsDetailComment> comments) {
            this.comments = comments;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return comments == null ? 0 : comments.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_comment1, null, false);
                holder.tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
                holder.ivHeadIcon = (SimpleDraweeView) convertView.findViewById(R.id.iv_user_icon);
                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final NewsDetailComment comment = comments.get(position);
            mComment = comment;
            mHolder = holder;
            if (!TextUtil.isEmptyString(comment.getProfile())) {
                holder.ivHeadIcon.setImageURI(Uri.parse(comment.getProfile()));
            }
            holder.tvName.setText(comment.getNickname());
            holder.tvPraiseCount.setText(comment.getLove() + "");

            holder.tvContent.setText(comment.getContent());
            holder.ivPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User user = SharedPreManager.getUser(mContext);
                    if (user == null) {
                        Intent loginAty = new Intent(mContext,LoginAty.class);
                        startActivityForResult(loginAty,REQUEST_CODE);
                    } else {
                        addNewsLove(user, comment, holder);
                    }

                }
            });
            return convertView;
        }
    }

    private void addNewsLove(User user, NewsDetailComment comment, final Holder holder) {
        Logger.e("jigang","love url=" + HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName());
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        NewsLoveRequest<String> loveRequest = new NewsLoveRequest<String>(Request.Method.PUT, new TypeToken<String>() {
        }.getType(), HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName(), new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                mNewsCommentList.onRefreshComplete();
                Logger.e("jigang", "network success, love" + result);
                if (!TextUtil.isEmptyString(result)) {
                    holder.ivPraise.setImageResource(R.drawable.bg_praised);
                    holder.tvPraiseCount.setText(result);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNewsCommentList.onRefreshComplete();
                Logger.e("jigang", "network fail");
            }
        });
        loveRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(loveRequest);
    }


    class Holder {
        SimpleDraweeView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;
    }
}
