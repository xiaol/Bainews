package com.news.yazhidao.pages;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.utils.ToastUtil;

/**
 * Created by fengjigang on 15/3/27.
 */
public class NewsDetailWebviewAty extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NewsDetailWebviewAty";
    private WebView mNewsSourcesiteWebview;
    private String mNewsUrl="http://www.hinews.cn/news/system/2015/03/27/017433273.shtml";
    private View mNewsSourcesiteLeftBack;
    private TextView mNewsSourcesiteUrl;
    private View mNewsSourcesiteFooterPraise;
    private View mNewsSourcesiteFooterBlame;
    private View mNewsSourcesiteFooterShare;
    private TextView mNewsSourcesiteBlameNum;
    private TextView mNewsSourcesitePraiseNum;
    private boolean isRedirected=false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_webview_sourcesite);
        mNewsSourcesiteLeftBack=findViewById(R.id.mNewsSourcesiteLeftBack);
        mNewsSourcesiteLeftBack.setOnClickListener(this);
        mNewsSourcesiteUrl=(TextView)findViewById(R.id.mNewsSourcesiteUrl);
        mNewsSourcesiteWebview = (WebView) findViewById(R.id.mNewsSourcesiteWebview);
        mNewsSourcesiteFooterPraise=findViewById(R.id.mNewsSourcesiteFooterPraise);
        mNewsSourcesiteFooterPraise.setOnClickListener(this);
        mNewsSourcesiteFooterBlame=findViewById(R.id.mNewsSourcesiteFooterBlame);
        mNewsSourcesiteFooterBlame.setOnClickListener(this);
        mNewsSourcesiteFooterShare=findViewById(R.id.mNewsSourcesiteFooterShare);
        mNewsSourcesiteFooterShare.setOnClickListener(this);
        mNewsSourcesitePraiseNum= (TextView) findViewById(R.id.mNewsSourcesitePraiseNum);
        mNewsSourcesiteBlameNum=(TextView)findViewById(R.id.mNewsSourcesiteBlameNum);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mNewsSourcesiteLeftBack:
//                ToastUtil.toastShort("点击了leftback");
                this.finish();
                break;
            case R.id.mNewsSourcesiteFooterPraise:
                ToastUtil.toastShort("点击了点赞");
                break;
            case R.id.mNewsSourcesiteFooterBlame:
                ToastUtil.toastShort("点击了鄙视");
                break;
            case R.id.mNewsSourcesiteFooterShare:
                ToastUtil.toastShort("点击了分享");
                break;
            default:
                break;
        }
    }
    @Override
    protected void initializeViews() {
        mNewsUrl=getIntent().getStringExtra("url");
        mNewsSourcesiteUrl.setText(mNewsUrl);
        mNewsSourcesiteWebview.getSettings().setJavaScriptEnabled(true);
        mNewsSourcesiteWebview.loadUrl(mNewsUrl);
        mNewsSourcesiteWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                isRedirected=true;
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                ToastUtil.toastShort("xxxx onPageStarted");
                isRedirected=false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isRedirected){
//                    ToastUtil.toastShort("xxxx onPageFinished");
                }
            }
        });
    }

    @Override
    protected void loadData() {

    }


}
