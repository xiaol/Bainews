package com.news.yazhidao.pages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

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
//    private ProgressDialog mProgressDialog;
    private TextView mNewsSourcesiteBlameNumReduce;
    private TextView mNewsSourcesitePraiseNumIncrease;
    private int mClickNum=0;
    private ProgressBar mNewsSourcesiteProgress;
    private SwipeBackLayout mSwipeBackLayout;
    private View mLeftBack;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_webview_sourcesite);
        mNewsSourcesiteProgress=(ProgressBar)findViewById(R.id.mNewsSourcesiteProgress);
        mNewsSourcesiteLeftBack=findViewById(R.id.mNewsSourcesiteLeftBack);
        mNewsSourcesiteLeftBack.setOnClickListener(this);
        mNewsSourcesiteUrl=(TextView)findViewById(R.id.mNewsSourcesiteUrl);
        mNewsSourcesiteWebview = (WebView) findViewById(R.id.mNewsSourcesiteWebview);
        mLeftBack=findViewById(R.id.mLeftBack);
        mLeftBack.setOnClickListener(this);

        mNewsSourcesiteFooterPraise=findViewById(R.id.mNewsSourcesiteFooterPraise);
        mNewsSourcesiteFooterPraise.setOnClickListener(this);
        mNewsSourcesiteFooterBlame=findViewById(R.id.mNewsSourcesiteFooterBlame);
        mNewsSourcesiteFooterBlame.setOnClickListener(this);
        mNewsSourcesiteFooterShare=findViewById(R.id.mNewsSourcesiteFooterShare);
        mNewsSourcesiteFooterShare.setOnClickListener(this);
        mNewsSourcesitePraiseNum= (TextView) findViewById(R.id.mNewsSourcesitePraiseNum);
        mNewsSourcesiteBlameNum=(TextView)findViewById(R.id.mNewsSourcesiteBlameNum);
        mNewsSourcesitePraiseNumIncrease= (TextView) findViewById(R.id.mNewsSourcesitePraiseNumIncrease);
        mNewsSourcesiteBlameNumReduce= (TextView) findViewById(R.id.mNewsSourcesiteBlameNumReduce);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mNewsSourcesiteLeftBack:
                this.finish();
                break;
            case R.id.mNewsSourcesiteFooterPraise:
                if(mClickNum==0){
                    mClickNum++;
                    performAnimator(mNewsSourcesitePraiseNumIncrease);
                    mNewsSourcesitePraiseNum.setText(Integer.valueOf(mNewsSourcesitePraiseNum.getText().toString())+1+"");
                }else if(mClickNum==1){
                    ToastUtil.toastShort("您已经赞过");
                }else if(mClickNum==-1){
                    ToastUtil.toastShort("您已经踩过");
                }
                break;
            case R.id.mNewsSourcesiteFooterBlame:
                if(mClickNum==0){
                    mClickNum--;
                    performAnimator(mNewsSourcesiteBlameNumReduce);
                    mNewsSourcesiteBlameNum.setText(Integer.valueOf(mNewsSourcesiteBlameNum.getText().toString()) - 1 + "");                break;
                }else if(mClickNum==1){
                    ToastUtil.toastShort("您已经赞过");
                }else if(mClickNum==-1){
                    ToastUtil.toastShort("您已经踩过");
                }
                break;
            case R.id.mNewsSourcesiteFooterShare:
                ToastUtil.toastShort("点击了分享");
                break;
            case R.id.mLeftBack:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 执行动画
     * @param pView
     */
    private void performAnimator(final View pView) {
        pView.setVisibility(View.VISIBLE);
        AnimatorSet _AnimatorSet=new AnimatorSet();
        _AnimatorSet.playTogether(
                ObjectAnimator.ofFloat(pView, "translationY", -50),
                ObjectAnimator.ofFloat(pView, "alpha", 1f,0f),
                ObjectAnimator.ofFloat(pView, "scaleX", 1f,.5f),
                ObjectAnimator.ofFloat(pView, "scaleY", 1f,.5f)

        );
        _AnimatorSet.setDuration(800);
        _AnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pView.setVisibility(View.GONE);
            }
        });
        _AnimatorSet.start();
    }

    @Override
    protected void initializeViews() {

        mNewsUrl=getIntent().getStringExtra(NewsFeedFragment.KEY_URL);
        mNewsSourcesiteUrl.setText(mNewsUrl);
        mNewsSourcesiteWebview.getSettings().setUseWideViewPort(true);                    //让webview读取网页设置的viewport
        mNewsSourcesiteWebview.getSettings().setLoadWithOverviewMode(true);           //设置一个默认的viewport=800，如果网页自己没有设置viewport，就用800
        mNewsSourcesiteWebview.getSettings().setJavaScriptEnabled(true);
        mNewsSourcesiteWebview.getSettings().setSupportZoom(true);
        mNewsSourcesiteWebview.getSettings().setBuiltInZoomControls(true);
        mNewsSourcesiteWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Logger.e(TAG, "xxx  " + newProgress);
//                if(newProgress>=89&&mProgressDialog!=null&&mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
                mNewsSourcesiteProgress.setProgress(newProgress);
                if (newProgress == 100) {
                    mNewsSourcesiteProgress.setVisibility(View.GONE);
                }
            }
        });
        mNewsSourcesiteWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Logger.e(TAG, "xxxx shouldOverrideUrlLoading");
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                if(mProgressDialog==null){
//                    mProgressDialog=ProgressDialog.show(NewsDetailWebviewAty.this,null,"加载中...");
//                    mProgressDialog.setCancelable(true);
//                }
                Logger.e(TAG, "xxxx onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.e(TAG, "xxxx onPageFinished");
//                if(mProgressDialog!=null&&mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
            }
        });

        mNewsSourcesiteWebview.loadUrl(mNewsUrl);
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void finish() {
        if (mNewsSourcesiteWebview != null) {
            mNewsSourcesiteWebview.removeAllViews();
        }
        super.finish();
    }
}
