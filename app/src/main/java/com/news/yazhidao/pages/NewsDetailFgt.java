package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.news.yazhidao.adapter.NewsDetailFgtAdapter;
import com.news.yazhidao.common.BaseFragment;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.RelatedEntity;
import com.news.yazhidao.entity.RelatedItemEntity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.NewsCommentRequest;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.net.volley.NewsLoveRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.UserCommentDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻详情页
 */
public class NewsDetailFgt extends BaseFragment {
    public static final String KEY_DETAIL_RESULT = "key_detail_result";
    private WebView mDetailWebView;
    private NewsDetail result;
    private SharedPreferences mSharedPreferences;
    private PullToRefreshListView mNewsDetailList;
    private NewsDetailFgtAdapter mAdapter;
    private boolean isListRefresh;
    private  User user;
    private RelativeLayout bgLayout;
    private String mDocid , mTitle , mPubName , mPubTime, mCommentCount,mNewID;
    private ArrayList<NewsDetailComment> mComments = new ArrayList<>();
    public static final String KEY_NEWS_DOCID = "key_news_docid";
    public static final String KEY_NEWS_ID = "key_news_id";
    public static final int REQUEST_CODE = 1030;
    private  LinearLayout detail_shared_FriendCircleLayout,
            detail_shared_PraiseLayout,
            mCommentLayout;
    private TextView detail_shared_PraiseText,
            detail_shared_Text,
            detail_shared_MoreComment,
            detail_shared_hotComment;
    private RelativeLayout detail_shared_ShareImageLayout,
            detail_shared_CommentTitleLayout,
            detail_shared_ViewPointTitleLayout;
    private int CommentType = 0;
    private LayoutInflater inflater;
    ViewGroup container;
    private RefreshPageBroReceiber mRefreshReceiber;
    private boolean isWebSuccess,isCommentSuccess, isCorrelationSuccess;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mDocid = arguments.getString(KEY_NEWS_DOCID);
        mNewID = arguments.getString(KEY_NEWS_ID);
        result = (NewsDetail) arguments.getSerializable(KEY_DETAIL_RESULT);
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);

        if(mRefreshReceiber == null){
            mRefreshReceiber = new RefreshPageBroReceiber();
            IntentFilter filter = new IntentFilter(NewsDetailAty2.ACTION_REFRESH_COMMENT);
            getActivity().registerReceiver(mRefreshReceiber, filter);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_news_detail_listview, null);
        this.inflater = inflater;
        this.container = container;
        user = SharedPreManager.getUser(getActivity());
        mNewsDetailList = (PullToRefreshListView) rootView.findViewById(R.id.fgt_new_detail_PullToRefreshListView);
        bgLayout = (RelativeLayout) rootView.findViewById(R.id.bgLayout);

        mNewsDetailList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        mNewsDetailList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {//刷新
                isListRefresh = true;
                mNewsDetailList.onRefreshComplete();
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {//加载
                isListRefresh = false;
                mNewsDetailList.onRefreshComplete();

            }
        });

        mAdapter = new NewsDetailFgtAdapter(getActivity());

        mNewsDetailList.setAdapter(mAdapter);
        addHeadView(inflater, container);
        loadData();

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mRefreshReceiber != null){
            getActivity().unregisterReceiver(mRefreshReceiber);
        }
    }

    public void addHeadView(LayoutInflater inflater, ViewGroup container){
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ListView lv = mNewsDetailList.getRefreshableView();
        //第1部分的WebView
        LinearLayout mNewsDetailHeaderView = (LinearLayout) inflater.inflate(R.layout.fgt_news_detail, container, false);
        mNewsDetailHeaderView.setLayoutParams(layoutParams);
        lv.addHeaderView(mNewsDetailHeaderView);
        mNewsDetailHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "webView的点击");
            }
        });

        mDetailWebView = (WebView) mNewsDetailHeaderView.findViewById(R.id.mDetailWebView);
//        if (Build.VERSION.SDK_INT >= 19) {//防止视频加载不出来。
//            mDetailWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            mDetailWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }

        mDetailWebView.getSettings().setJavaScriptEnabled(true);
        mDetailWebView.getSettings().setDatabaseEnabled(true);
        mDetailWebView.getSettings().setDomStorageEnabled(true);
        mDetailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mDetailWebView.loadData(TextUtil.genarateHTML(result, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL)), "text/html;charset=UTF-8", null);
        mDetailWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isWebSuccess = true;
                isBgLayoutSuccess();
            }
        });

        //第2部分的CommentTitle
        View mCommentTitleView = inflater.inflate(R.layout.detail_shared_layout, container, false);
        mCommentTitleView.setLayoutParams(layoutParams);
        mNewsDetailHeaderView.addView(mCommentTitleView);
        detail_shared_FriendCircleLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_FriendCircleLayout);
        detail_shared_PraiseLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_PraiseLayout);
        detail_shared_PraiseText = (TextView) mCommentTitleView.findViewById(R.id.detail_shared_PraiseText);
        mCommentLayout = (LinearLayout) mCommentTitleView.findViewById(R.id.detail_shared_Layout);
        detail_shared_CommentTitleLayout = (RelativeLayout) mCommentTitleView.findViewById(R.id.detail_shared_TitleLayout);

        detail_shared_FriendCircleLayout.getParent().requestDisallowInterceptTouchEvent(true);
        detail_shared_FriendCircleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "点击朋友圈");
            }
        });
        detail_shared_PraiseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "点击点赞");
            }
        });
        ////第3部分的CommentContent(这个内容是动态的获取数据后添加)

        //第4部分的viewPointContent
        View mViewPointLayout = inflater.inflate(R.layout.detail_shared_layout, container, false);
        mViewPointLayout.setLayoutParams(layoutParams);
        mNewsDetailHeaderView.addView(mViewPointLayout);


        detail_shared_ShareImageLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_ShareImageLayout);
        detail_shared_Text = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_Text);
        detail_shared_MoreComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_MoreComment);
        detail_shared_hotComment = (TextView) mViewPointLayout.findViewById(R.id.detail_shared_hotComment);
        detail_shared_ViewPointTitleLayout = (RelativeLayout) mViewPointLayout.findViewById(R.id.detail_shared_TitleLayout);

        detail_shared_ShareImageLayout.setVisibility(View.GONE);
        detail_shared_Text.setVisibility(View.GONE);
        detail_shared_MoreComment.setVisibility(View.VISIBLE);

        detail_shared_hotComment.setText("相关观点");


    }



//    addNewsLoveListener addNewsLoveListener = new addNewsLoveListener() {
//        @Override
//        public void addLove(NewsDetailComment comment, int position) {
//            addNewsLove(comment);
//        }
//    };

    private void loadData() {

        Logger.e("jigang", "fetch comments url=" + HttpConstant.URL_FETCH_COMMENTS + "docid=" + mDocid );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        NewsCommentRequest<ArrayList<NewsDetailComment>> feedRequest = null;
        NewsDetailRequest<RelatedEntity> related = null;
        try {
            feedRequest = new NewsCommentRequest<ArrayList<NewsDetailComment>>(Request.Method.GET, new TypeToken<ArrayList<NewsDetailComment>>() {
            }.getType(), HttpConstant.URL_FETCH_COMMENTS + "docid=" + URLEncoder.encode(mDocid,"utf-8") + "&page=" + (1), new Response.Listener<ArrayList<NewsDetailComment>>() {

                @Override
                public void onResponse(ArrayList<NewsDetailComment> result) {
                    isCommentSuccess = true;
                    isBgLayoutSuccess();
                    mNewsDetailList.onRefreshComplete();
                    Logger.e("jigang", "network success, comment" + result);

                    if (!TextUtil.isListEmpty(result)) {
                        mComments = result;
//                        mAdapter.setCommentList(mComments);
//                        mAdapter.notifyDataSetChanged();
                        Logger.d("aaa", "评论加载完毕！！！！！！");
                        addCommentContent(result);
                    }else{
                        detail_shared_CommentTitleLayout.setVisibility(View.GONE);
                        detail_shared_MoreComment.setVisibility(View.GONE);

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isCommentSuccess = true;
                    isBgLayoutSuccess();
                    mNewsDetailList.onRefreshComplete();
                    Logger.e("jigang", "network fail");
                }
            });
            related = new NewsDetailRequest<RelatedEntity>(Request.Method.GET,
                    new TypeToken<RelatedEntity>() {
                    }.getType(),
                    HttpConstant.URL_NEWS_RELATED + "url=" + TextUtil.getBase64(mNewID),
                    new Response.Listener<RelatedEntity>() {
                        @Override
                        public void onResponse(RelatedEntity response) {
                            isCorrelationSuccess = true;
                            isBgLayoutSuccess();

                            Logger.e("jigang", "network success RelatedEntity~~" + response);
                            ArrayList<RelatedItemEntity> relatedItemEntities = response.getSearchItems();
                            if(!TextUtil.isListEmpty(relatedItemEntities)){
                                Logger.e("aaa","time:================比较前=================");
                                for(int i=0;i<relatedItemEntities.size();i++){
                                    Logger.e("aaa","time:==="+relatedItemEntities.get(i).getUpdateTime());
                                }
                                Collections.sort(relatedItemEntities);
                                Logger.e("aaa","time:================比较====后=================");
                                for(int i=0;i<relatedItemEntities.size();i++){
                                    Logger.e("aaa","time:==="+relatedItemEntities.get(i).getUpdateTime());
                                }
                                mAdapter.setNewsFeed(relatedItemEntities);
                                mAdapter.notifyDataSetChanged();
                                detail_shared_ViewPointTitleLayout.setVisibility(View.VISIBLE);
                            }else{
                                RelatedItemEntity entity = new RelatedItemEntity();
                                entity.setUrl("-1");
                                relatedItemEntities.add(entity);
                                mAdapter.setNewsFeed(relatedItemEntities);
                                mAdapter.notifyDataSetChanged();
                                detail_shared_ViewPointTitleLayout.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            isCorrelationSuccess = true;
                            isBgLayoutSuccess();
                            Logger.e("jigang", "network error~~");
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        related.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));

        requestQueue.add(feedRequest);
        requestQueue.add(related);


    }
    private void addNewsLove(NewsDetailComment comment,final  int position, final CommentHolder holder) {
        try {
            String name = URLEncoder.encode(user.getUserName(),"utf-8");
            String cid = URLEncoder.encode(comment.getId(),"utf-8");
            user.setUserName(name);
            comment.setId(cid);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.e("jigang","love url=" + HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName());
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        NewsLoveRequest<String> loveRequest = new NewsLoveRequest<String>(Request.Method.PUT, new TypeToken<String>() {
        }.getType(), HttpConstant.URL_LOVE_COMMENT + "cid=" + comment.getId() + "&uuid=" + user.getUserId() + "&unam=" + user.getUserName(), new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "network success, love" + result);
                if (!TextUtil.isEmptyString(result)) {
                    mComments.get(position).setPraise(true);
                    mComments.get(position).setLove(Integer.parseInt(result));
                    holder.ivPraise.setImageResource(R.drawable.bg_praised);
                    holder.tvPraiseCount.setText(result);
//                    viewList.get(position).invalidate();//刷新界面
//                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "network fail");
            }
        });
        loveRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(loveRequest);
    }

    ArrayList<CommentHolder> holderList = new ArrayList<CommentHolder>();
    ArrayList<View> viewList = new ArrayList<View>();
    private View mCCView;
    public void addCommentContent(final ArrayList<NewsDetailComment> result){
        int listSice = result.size();
        if(listSice == 0){
            CommentType = 0;
            detail_shared_CommentTitleLayout.setVisibility(View.GONE);
            detail_shared_MoreComment.setVisibility(View.GONE);
//        }else if(listSice == 1){
//            ShowCommentBar();
//            mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
//            CommentHolder holder = new CommentHolder(mCCView);
//            holderList.add(holder);
//
//        }else if(listSice == 2){
//            ShowCommentBar();
//            for(int i = 0; i<listSice ;i++){
//                mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
//                CommentHolder holder = new CommentHolder(mCCView);
//                holderList.add(holder);
//            }
        }else{
            ShowCommentBar();
            for(int i = 0; i<listSice&&i<3 ;i++){
                CommentType = i+1;
                mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
                CommentHolder holder = new CommentHolder(mCCView);

                int position = i;
                NewsDetailComment comment = result.get(i);

                UpdateCCView(holder,comment,position);
                holderList.add(holder);
                viewList.add(mCCView);

                mCommentLayout.addView(mCCView);

            }
        }
    }



    public void ShowCommentBar(){
        if(detail_shared_CommentTitleLayout.getVisibility() == View.GONE){
            detail_shared_CommentTitleLayout.setVisibility(View.VISIBLE);
        }

        if(mComments.size()>3){
            if(detail_shared_MoreComment.getVisibility() == View.GONE){
                detail_shared_MoreComment.setVisibility(View.VISIBLE);
            }
        }
    }

    class CommentHolder {
        SimpleDraweeView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;
        public CommentHolder(View convertView){
            tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
            ivHeadIcon = (SimpleDraweeView) convertView.findViewById(R.id.iv_user_icon);
            tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
            ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
            tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
        }
    }
    /**通知新闻详情页和评论fragment刷新评论*/
    public  class RefreshPageBroReceiber extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("jigang","detailaty refresh br");
            NewsDetailComment comment = (NewsDetailComment) intent.getSerializableExtra(UserCommentDialog.KEY_ADD_COMMENT);
            mComments.add(0,comment);
            UpdateCCOneData();

        }
    }
    public void UpdateCCOneData(){
        if(CommentType == 3){
            CCViewNotifyDataSetChanged();
            ShowCommentBar();
        }else{
            CommentType = CommentType + 1;
            mCCView = inflater.inflate(R.layout.adapter_list_comment1,container,false);
            CommentHolder holder = new CommentHolder(mCCView);
            holderList.add(holder);
            viewList.add(mCCView);
            CCViewNotifyDataSetChanged();
            mCommentLayout.addView(mCCView);
            ShowCommentBar();
        }

    }
    public void CCViewNotifyDataSetChanged(){
        for (int i = 0; i < CommentType;i++ ) {
            CommentHolder holder = holderList.get(i);
            NewsDetailComment newsDetailComment = mComments.get(i);
            UpdateCCView(holder, newsDetailComment, i);
        }
    }


    public void UpdateCCView(final CommentHolder holder, final NewsDetailComment comment ,final int position){
        final User user = SharedPreManager.getUser(getActivity());
        if (!TextUtil.isEmptyString(comment.getProfile())) {
            holder.ivHeadIcon.setImageURI(Uri.parse(comment.getProfile()));
        }
        holder.tvName.setText(comment.getNickname());
        holder.tvPraiseCount.setText(comment.getLove() + "");

        holder.tvContent.setText(comment.getContent());
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.e("aaa", "点击内容");
            }
        });
        if (!comment.isPraise()){
            holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
        }else {
            holder.ivPraise.setImageResource(R.drawable.bg_praised);
        }
        String commentUserid = comment.getUuid();
        if(commentUserid != null && commentUserid.length() != 0){
            if(user.getUserId().equals(comment.getUuid())){
                holder.ivPraise.setVisibility(View.GONE);
            }else{
                holder.ivPraise.setVisibility(View.VISIBLE);
            }
        }

        holder.ivPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user == null) {
                    Intent loginAty = new Intent(getActivity(), LoginAty.class);
                    startActivityForResult(loginAty,REQUEST_CODE );
                }else {
                    addNewsLove(comment,position,holder);

                }

            }
        });
    }
    public void isBgLayoutSuccess(){
        if (isCommentSuccess && isWebSuccess && isCorrelationSuccess&&bgLayout.getVisibility() == View.VISIBLE) {
            bgLayout.setVisibility(View.GONE);
        }
    }

}
