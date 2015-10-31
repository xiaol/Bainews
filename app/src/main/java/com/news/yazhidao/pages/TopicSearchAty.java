package com.news.yazhidao.pages;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
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
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.HotLabelsLayout;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/10/29.
 */
public class TopicSearchAty extends BaseActivity implements View.OnClickListener {

    /**标签页的容量*/
    private final static int PAGE_CAPACITY = 10;

    private ImageView mSearchLeftBack;
    private EditText mSearchContent;
    private View mSearchClear;
    private TextView mDoSearch;
    private HotLabelsLayout mHotLabelsLayout;
    private TextView mDoSearchChangeBatch;

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
                ToastUtil.toastShort("search " + mSearchContent.getText().toString());
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
                Element element = elements.get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                int margin = DensityUtil.dip2px(this,6);
                lp.setMargins(margin,margin,margin,margin);
                textView.setLayoutParams(lp);
                int padding = DensityUtil.dip2px(this,8);
                textView.setPadding(padding, padding, padding, padding);
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                Drawable drawable = getResources().getDrawable(R.drawable.bg_search_hotlabel);
                drawable.setColorFilter(new
                        PorterDuffColorFilter(TextUtil.getRandomColor4Hotlabel(this), PorterDuff.Mode.SRC_IN));
                textView.setBackgroundDrawable(drawable);
                textView.setText(element.getTitle());
                mHotLabelsLayout.addView(textView);
            }
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
