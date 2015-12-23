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
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.Element;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
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
    private NewsFeedFgt mSearchResultFgt;
    private View mSearchHotLabelLayout;

    private ArrayList<Element> mHotLabels;
    private int mTotalPage;
    private int mCurrPageIndex;

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
        mSearchResultFgt = (NewsFeedFgt)getSupportFragmentManager().findFragmentById(R.id.mSearchResultFgt);
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
                mSearchResultFgt.setSearchKeyWord(mSearchContent.getText().toString());
                mSearchHotLabelLayout.setVisibility(View.GONE);
                break;
            case R.id.mDoSearchChangeBatch:
                setHotLabelLayoutData(mCurrPageIndex++);
                break;
        }
    }

    @Override
    protected void loadData() {
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
                }
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.e("jigang", "-----fetch hot label fail~");
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
//                        mSearchContent.setText(element.getTitle());
//                        mSearchContent.setSelection(element.getTitle().length());
//                        mSearchResultFgt.setSearchKeyWord(mSearchContent.getText().toString());
//                        mSearchHotLabelLayout.setVisibility(View.GONE);
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
                mSearchClear.setVisibility(View.VISIBLE);
                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_can_do));
                mDoSearch.setOnClickListener(TopicSearchAty.this);
            } else {
                mSearchClear.setVisibility(View.GONE);
                mDoSearch.setTextColor(TopicSearchAty.this.getResources().getColor(R.color.do_search_not_do));
                mDoSearch.setOnClickListener(null);
            }
        }
    }
}
