package com.news.yazhidao.pages;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseFragment;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.utils.TextUtil;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻详情页
 */
public class NewsDetailFgt extends BaseFragment {
    public static final String KEY_DETAIL_RESULT = "key_detail_result";
    private WebView mDetailWebView;
    private NewsDetail result;
    private SharedPreferences mSharedPreferences;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        result = (NewsDetail) arguments.getSerializable(KEY_DETAIL_RESULT);
        mSharedPreferences = getActivity().getSharedPreferences("showflag", 0);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_news_detail, null);
        mDetailWebView = (WebView)rootView.findViewById(R.id.mDetailWebView);
        if (Build.VERSION.SDK_INT >= 19) {
            mDetailWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mDetailWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mDetailWebView.getSettings().setJavaScriptEnabled(true);
        mDetailWebView.getSettings().setDatabaseEnabled(true);
        mDetailWebView.getSettings().setDomStorageEnabled(true);
        mDetailWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mDetailWebView.loadData(TextUtil.genarateHTML(result, mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL)),"text/html;charset=UTF-8",null);
        return rootView;
    }
}
