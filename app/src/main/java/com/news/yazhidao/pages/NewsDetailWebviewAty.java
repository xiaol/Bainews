package com.news.yazhidao.pages;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;

/**
 * Created by fengjigang on 15/3/27.
 */
public class NewsDetailWebviewAty extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NewsDetailWebviewAty";
    private WebView mNewsSourcesiteWebview;
    private String mNewsUrl;
    private View mNewsSourcesiteLeftBack;
    private TextView mNewsSourcesiteUrl;
    private View mNewsSourcesiteFooterPraise;
    private View mNewsSourcesiteFooterBlame;
    private View mNewsSourcesiteFooterShare;
    private TextView mNewsSourcesiteBlameNum;
    private TextView mNewsSourcesitePraiseNum;
    private ProgressDialog mProgressDialog;
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
                this.finish();
                break;
            case R.id.mNewsSourcesiteFooterPraise:
                mNewsSourcesitePraiseNum.setText(Integer.valueOf(mNewsSourcesitePraiseNum.getText().toString())+1+"");
                break;
            case R.id.mNewsSourcesiteFooterBlame:
                mNewsSourcesiteBlameNum.setText(Integer.valueOf(mNewsSourcesiteBlameNum.getText().toString()) - 1 + "");                break;
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
        mNewsSourcesiteWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logger.e(TAG,"xxx  "+newProgress);
                if(newProgress>=89&&mProgressDialog!=null&&mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }
        });
        mNewsSourcesiteWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Logger.e(TAG,"xxxx shouldOverrideUrlLoading");
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(mProgressDialog==null){
                    mProgressDialog=ProgressDialog.show(NewsDetailWebviewAty.this,null,"加载中...");
                    mProgressDialog.setCancelable(true);
                }
                Logger.e(TAG, "xxxx onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                    Logger.e(TAG, "xxxx onPageFinished");
                if(mProgressDialog!=null&&mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }
        });
        mNewsSourcesiteWebview.loadUrl(mNewsUrl);
    }

    @Override
    protected void loadData() {

    }


}
