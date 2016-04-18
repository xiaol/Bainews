package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.Element;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.volley.SearchRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.HotLabelsLayout;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/10/29.
 */
public class TopicSearchAty extends BaseActivity implements View.OnClickListener {

    public final static String KEY_NOT_NEED_OPEN_HOME_ATY = "KEY_NOT_NEED_OPEN_HOME_ATY";
    /**标签页的容量*/
    private final static int PAGE_CAPACITY = 10;

    private ImageView mSearchLeftBack;
    private EditText mSearchContent;
    private View mSearchClear;
    private TextView mDoSearch;
    private HotLabelsLayout mHotLabelsLayout;
    private TextView mDoSearchChangeBatch;
    private PullToRefreshListView mSearchListView;
    private View mSearchHotLabelLayout;
    private NewsFeedAdapter mNewsFeedAdapter;
    private View mSearchLoaddingWrapper;
    private ImageView mSearchTipImg;
    private TextView mSearchTip;
    private ProgressBar mSearchProgress;
    private ArrayList<Element> mHotLabels;
    private ArrayList<NewsFeed> mNewsFeedLists = new ArrayList<>();
    private int mTotalPage;
    private int mCurrPageIndex;
    private String mKeyWord;
    private int mPageIndex = 1;//搜索index

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_topic_search);
    }

    @Override
    protected void initializeViews() {
        mSearchLeftBack = (ImageView) findViewById(R.id.mSearchLeftBack);
        mSearchLeftBack.setOnClickListener(this);
        mSearchContent = (EditText) findViewById(R.id.mSearchContent);
        mSearchContent.addTextChangedListener(new TopicTextWatcher());
        mSearchClear = findViewById(R.id.mSearchClear);
        mSearchClear.setOnClickListener(this);
        mDoSearch = (TextView) findViewById(R.id.mDoSearch);
        mHotLabelsLayout = (HotLabelsLayout) findViewById(R.id.mHotLabelsLayout);
        mDoSearchChangeBatch = (TextView) findViewById(R.id.mDoSearchChangeBatch);
        mDoSearchChangeBatch.setOnClickListener(this);
        mSearchHotLabelLayout = findViewById(R.id.mSearchHotLabelLayout);
        mSearchLoaddingWrapper = findViewById(R.id.mSearchLoaddingWrapper);
        mSearchTipImg = (ImageView)findViewById(R.id.mSearchTipImg);
        mSearchTip = (TextView)findViewById(R.id.mSearchTip);
        mSearchProgress = (ProgressBar)findViewById(R.id.mSearchProgress);
        mNewsFeedAdapter = new NewsFeedAdapter(this,null,null);
        mSearchListView = (PullToRefreshListView) findViewById(R.id.mSearchListView);
        mSearchListView.setAdapter(mNewsFeedAdapter);
        mSearchListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mSearchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadNewsData(mKeyWord,++mPageIndex + "");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mSearchLeftBack:
                finish();
                break;
            case R.id.mSearchClear:
                mSearchContent.setText(null);
                break;
            case R.id.mDoSearch:
                hideKeyboard(v);
                mSearchLoaddingWrapper.setVisibility(View.VISIBLE);
                mSearchTipImg.setVisibility(View.GONE);
                mSearchTip.setVisibility(View.GONE);
                mSearchTip.setText("暂无搜索结果");
                mSearchProgress.setVisibility(View.VISIBLE);
                List<String> oldlist = SharedPreManager.getSearchWord();
                if (!TextUtil.isListEmpty(oldlist) &&!TextUtil.isEmptyString(oldlist.get(0)) && !mKeyWord.equals(oldlist.get(0))){
                        mNewsFeedLists.clear();
                        mNewsFeedAdapter.setSearchKeyWord(null);
                        mNewsFeedAdapter.setNewsFeed(null);
                }
                mNewsFeedLists.clear();
                SharedPreManager.saveSearchWord(mKeyWord);
                mPageIndex = 1;
                loadNewsData(mKeyWord,mPageIndex+"");
                mSearchHotLabelLayout.setVisibility(View.GONE);
                break;
            case R.id.mDoSearchChangeBatch:
                setHotLabelLayoutData(mCurrPageIndex++);
                break;
        }
    }

    /**
     * 获取新闻数据
     */
    private void loadNewsData(String pKeyWord,String pPageIndex){
        RequestQueue requestQueue = Volley.newRequestQueue(TopicSearchAty.this);
        SearchRequest<ArrayList<NewsFeed>> searchRequest = new SearchRequest<>(Request.Method.POST,new TypeToken<ArrayList<NewsFeed>>(){}.getType(),"http://api.deeporiginalx.com/news/baijia/search",new Response.Listener<ArrayList<NewsFeed>>(){

            @Override
            public void onResponse(ArrayList<NewsFeed> response) {
                mSearchListView.onRefreshComplete();
                if (!TextUtil.isListEmpty(response)){
                    mNewsFeedLists.addAll(response);
                    mNewsFeedAdapter.setSearchKeyWord(mKeyWord);
                    mNewsFeedAdapter.setNewsFeed(mNewsFeedLists);
                    mNewsFeedAdapter.notifyDataSetChanged();
                    mSearchLoaddingWrapper.setVisibility(View.GONE);
                }else {
                    Logger.e("jigang","response is null");

                    if (mPageIndex > 1){
                        ToastUtil.toastShort("没有更多数据");
                    }else {
                        ToastUtil.toastShort("没有搜索到与\""+mKeyWord+"\"相关的数据");
                        mSearchTipImg.setVisibility(View.VISIBLE);
                        mSearchTip.setVisibility(View.VISIBLE);
                        mSearchProgress.setVisibility(View.GONE);

                    }
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                mSearchListView.onRefreshComplete();
                Logger.e("jigang",""+error.getMessage());
                mSearchTipImg.setVisibility(View.VISIBLE);
                mSearchTip.setVisibility(View.VISIBLE);
            }
        });
        searchRequest.setKeyWordAndPageIndex(pKeyWord,pPageIndex);
        searchRequest.setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 1, 1.0f));
        requestQueue.add(searchRequest);
    }

    @Override
    protected void loadData() {
        mSearchLoaddingWrapper.setVisibility(View.VISIBLE);
        mSearchTip.setText("暂无热门搜索热词");
        final NetworkRequest request = new NetworkRequest(HttpConstant.URL_FETCH_ELEMENTS, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs = new ArrayList<>();
        request.setParams(pairs);
        request.setCallback(new JsonCallback<ArrayList<Element>>() {
            @Override
            public void success(ArrayList<Element> result) {
                mHotLabels = result;
                if (!TextUtil.isListEmpty(mHotLabels)) {
                    int temp = mHotLabels.size() % PAGE_CAPACITY;
                    mTotalPage = (temp == 0) ? mHotLabels.size() / PAGE_CAPACITY : mHotLabels.size() / PAGE_CAPACITY + 1;
                    setHotLabelLayoutData(mCurrPageIndex++);
                    mSearchLoaddingWrapper.setVisibility(View.GONE);
                }else {
                        mSearchTipImg.setVisibility(View.VISIBLE);
                        mSearchTip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.e("jigang", "-----fetch hot label fail~");
                mSearchTipImg.setVisibility(View.VISIBLE);
                mSearchTip.setVisibility(View.VISIBLE);
            }
        }.setReturnType(new TypeToken<ArrayList<Element>>() {
        }.getType()));
        request.execute();
    }
    public void setHotLabelLayoutData(int mCurrPageIndex){
        if (mTotalPage >= 1){
            mHotLabelsLayout.removeAllViews();
            int index = mCurrPageIndex % mTotalPage;
            List<Element> elements = mHotLabels.subList(index * PAGE_CAPACITY, index * PAGE_CAPACITY + ((index == mTotalPage -1) ? (mHotLabels.size() % PAGE_CAPACITY) : PAGE_CAPACITY));
            for (int i = 0; i < elements.size(); i ++){
                TextView textView = new TextView(this);
                final Element element = elements.get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int margin = DensityUtil.dip2px(this,6);
                lp.setMargins(margin,margin,margin,margin);
                textView.setLayoutParams(lp);
                int padding = DensityUtil.dip2px(this,8);
                textView.setPadding(padding, padding, padding, padding);
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                Drawable drawable = getResources().getDrawable(R.drawable.bg_search_hotlabel);
                drawable.setColorFilter(new
                        PorterDuffColorFilter(TextUtil.getRandomColor4Hotlabel(this), PorterDuff.Mode.SRC_IN));
                textView.setBackgroundDrawable(drawable);
                textView.setText(element.getTitle());
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKeyboard(v);
                        Intent diggerIntent = new Intent(TopicSearchAty.this,DiggerAty.class);
                        diggerIntent.setType("text/plain");
                        diggerIntent.putExtra(Intent.EXTRA_TEXT,element.getTitle());
                        diggerIntent.putExtra(KEY_NOT_NEED_OPEN_HOME_ATY,true);
                        startActivity(diggerIntent);
                    }
                });
                mHotLabelsLayout.addView(textView);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewsFeedAdapter.REQUEST_CODE){
            String newsId = data.getStringExtra(NewsFeedAdapter.KEY_NEWS_ID);
            Logger.e("jigang","newsid = " + newsId);
            if (!TextUtil.isListEmpty(mNewsFeedLists)){
                for (NewsFeed item : mNewsFeedLists){
                    if (item != null && newsId.equals(item.getUrl())){
                        item.setRead(true);
                    }
                }
                mNewsFeedAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param view
     */
    private void hideKeyboard(View view) {
        IBinder token = view.getWindowToken();
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    private class TopicTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Logger.e("jigang", "s=" + s + ",start=" + start + ",count=" + count + ",after=" + after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Logger.e("jigang", "s=" + s + ",start=" + start + ",before=" + before + ",count=" + count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Logger.e("jigang", "s=" + s);
            if (s != null && !TextUtil.isEmptyString(s.toString())) {
                mKeyWord = mSearchContent.getText().toString();
                mSearchClear.setVisibility(View.VISIBLE);
                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_can_do));
                mDoSearch.setOnClickListener(TopicSearchAty.this);
            } else {
                mKeyWord = "";
                mSearchClear.setVisibility(View.GONE);
                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_not_do));
                mDoSearch.setOnClickListener(null);
            }
        }
    }
}
