package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.TextUtils;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.image.ImageManager;
import com.news.yazhidao.widget.imagewall.BitmapUtil;
import com.news.yazhidao.widget.imagewall.ImageWallView;
import com.news.yazhidao.widget.imagewall.ViewWall;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.base.task.BackRunnable;
import app.base.task.CallbackRunnable;
import app.base.task.Compt;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout {


    public static interface HeaderVeiwPullUpListener {
        void onclickPullUp(int height);
    }

    //当前新闻内容的高度
    private int mContentHeight;
    private View mRootView;
    private Context mContext;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina, mllSinaItem, mllDouBan;
    private WordWrapView mvDouBanItem;
    private HorizontalScrollView mSinaScrollView;
    private ImageView mNewsDetailHeaderImg;
    private LetterSpacingTextView mNewsDetailHeaderTitle;
    private TextView mNewsDetailHeaderTime;
    private TextView mNewsDetailHeaderTemperature;
    private LetterSpacingTextView mNewsDetailHeaderDesc;
    private LinearLayout mNewsDetailHeaderContentParent;
    private TextView mNewsDetailHeaderSourceName;
    private TextView mNewsDetailHeaderLocation;
    private TextView mNewsDetailRelate;
    private TextView mNewsDetailHeaderPulldown;
    private RelativeLayout mNewsDetailEditableLayout;
    private LinearLayout mNewsDeatailTitleLayout;
    private EditText mNewsDetailEdittext;
    private Button MnewsDetailButtonConfirm;
    private Button MnewsDetailButtonCancel;
    private String[] _Split;

    private static int EDIT_POSITION;
    private static final int TITLE = 1;
    private static final int DESCRIPTION = 2;
    private static final int DETAIL = 3;
    private static int tag = -1;
    private int type = 0;
    private String srcText = "";

    private ImageWallView mImageWall;
    //是否点击了展开全文
    private boolean isClickedPullDown = false;
    //当前文章隐藏的位置
    private int _CurrentPos = 0;


    public NewsDetailHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_header_view, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public NewsDetailHeaderView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.aty_news_detail_header_view, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    private void initVars() {
    }

    private void findViews() {
        mllBaiKe = (LinearLayout) mRootView.findViewById(R.id.baike_linerLayout);
        mllZhiHu = (LinearLayout) mRootView.findViewById(R.id.zhihu_linerLayout);
        mllZhiHuItem = (LinearLayout) mRootView.findViewById(R.id.zhihu_item_linerLayout);
        mllDouBan = (LinearLayout) mRootView.findViewById(R.id.douban_linerLayout);
        mvDouBanItem = (WordWrapView) mRootView.findViewById(R.id.douban_item_tabLayout);
        mllSina = (LinearLayout) mRootView.findViewById(R.id.sina_linearLayout);
        mllSinaItem = (LinearLayout) mRootView.findViewById(R.id.sina_item_layout);
        mSinaScrollView = (HorizontalScrollView) mRootView.findViewById(R.id.sina_scollView);
        mNewsDetailRelate = (TextView) mRootView.findViewById(R.id.mNewsDetailRelate);
        mNewsDetailHeaderImg = (ImageView) mRootView.findViewById(R.id.mNewsDetailHeaderImg);//新闻头图
        mNewsDetailHeaderTitle = (LetterSpacingTextView) mRootView.findViewById(R.id.mNewsDetailHeaderTitle);//新闻标题
        mNewsDetailHeaderTime = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTime);//新闻时间
        mNewsDetailHeaderTemperature = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderTemperature);//新闻所属的温度
        mNewsDetailHeaderDesc = (LetterSpacingTextView) mRootView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
        mNewsDetailHeaderContentParent = (LinearLayout) mRootView.findViewById(R.id.mNewsDetailHeaderContentParent);//新闻内容
        mNewsDetailHeaderPulldown = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderPulldown);//点击展开全文
        mNewsDetailHeaderSourceName = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderSourceName);//新闻来源地址
        mNewsDetailHeaderLocation = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderLocation);//新闻发生的地点
        mNewsDeatailTitleLayout = (LinearLayout) mRootView.findViewById(R.id.mNewsDeatailTitleLayout);
        mNewsDetailEditableLayout = (RelativeLayout) mRootView.findViewById(R.id.mNewsDetailEditableLayout);
        mNewsDetailEdittext = (EditText) mRootView.findViewById(R.id.mNewsDetailEdittext);
        MnewsDetailButtonConfirm = (Button) mRootView.findViewById(R.id.MnewsDetailButtonConfirm);
        MnewsDetailButtonCancel = (Button) mRootView.findViewById(R.id.MnewsDetailButtonCancel);
        mImageWall = (ImageWallView) mRootView.findViewById(R.id.mImageWall);
    }

    public void setContentViewHeight(int pHeight) {
        this.mContentHeight = pHeight;
    }

    /**
     * 获取新闻内容展示的view
     *
     * @return
     */
    public View getContentView() {
        return mNewsDetailHeaderContentParent;
    }

    /**
     * 获取新闻详情后，填充数据到
     *
     * @param pNewsDetail
     */
    private void inflateDataToNewsheader(final NewsDetail pNewsDetail, final HeaderVeiwPullUpListener listener) {
        if (pNewsDetail != null) {
            if (TextUtils.isValidate(pNewsDetail.imgUrl)) {
                ImageManager.getInstance(mContext).DisplayImage(pNewsDetail.imgUrl, mNewsDetailHeaderImg, true);
            } else {
                mNewsDetailHeaderImg.setVisibility(GONE);
            }
            mNewsDetailHeaderTitle.setFontSpacing(LetterSpacingTextView.BIGGEST);
            mNewsDetailHeaderTitle.setText(pNewsDetail.title);
            mNewsDetailHeaderTitle.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    mNewsDeatailTitleLayout.setVisibility(View.GONE);
                    mNewsDetailEditableLayout.setVisibility(View.VISIBLE);
                    mNewsDetailEdittext.setText(mNewsDetailHeaderTitle.getText());
                    mNewsDetailEdittext.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            return true;
                        }
                    });
                    EDIT_POSITION = TITLE;

                    return true;
                }
            });

            MnewsDetailButtonConfirm.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch (EDIT_POSITION) {
                        case TITLE:
                            type = TITLE;
                            srcText = pNewsDetail.title;
                            mNewsDetailHeaderTitle.setText(mNewsDetailEdittext.getText());
                            mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                            mNewsDetailEditableLayout.setVisibility(View.GONE);
                            break;

                        case DESCRIPTION:
                            type = DESCRIPTION;
                            srcText = pNewsDetail.abs;
                            mNewsDetailHeaderDesc.setText(mNewsDetailEdittext.getText());
                            if ("".equals(mNewsDetailEdittext.getText())) {
                                mNewsDetailHeaderDesc.setVisibility(View.GONE);
                            }
                            mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                            mNewsDetailEditableLayout.setVisibility(View.GONE);
                            break;

                        case DETAIL:
                            type = DETAIL;
                            if (tag <= mNewsDetailHeaderContentParent.getChildCount()) {
                                LetterSpacingTextView tv = (LetterSpacingTextView) mNewsDetailHeaderContentParent.getChildAt(tag);
                                srcText = _Split[tag];
                                if (tv != null) {
                                    tv.setText(mNewsDetailEdittext.getText());
                                }
                            }
                            mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                            mNewsDetailEditableLayout.setVisibility(View.GONE);
                            break;
                    }

                    //修改新闻内容
                    modifyNewsContent(pNewsDetail, type, srcText, mNewsDetailEdittext.getText().toString(), EDIT_POSITION);

                }
            });

            MnewsDetailButtonCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                    mNewsDetailEditableLayout.setVisibility(View.GONE);
                }
            });
            mNewsDetailHeaderTime.setText(pNewsDetail.updateTime);
            mNewsDetailHeaderTemperature.setText(TextUtil.convertTemp(pNewsDetail.root_class));
            mNewsDetailHeaderDesc.setFontSpacing(LetterSpacingTextView.BIG);
            mNewsDetailHeaderDesc.setText(pNewsDetail.abs);
            mNewsDetailHeaderDesc.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    mNewsDeatailTitleLayout.setVisibility(View.GONE);
                    mNewsDetailEditableLayout.setVisibility(View.VISIBLE);
                    mNewsDetailEdittext.setText(mNewsDetailHeaderDesc.getText());
                    EDIT_POSITION = DESCRIPTION;

                    return true;
                }
            });

            if (!android.text.TextUtils.isEmpty(pNewsDetail.content)) {
                _Split = pNewsDetail.content.split("\n");
                StringBuilder _StringBuilder = new StringBuilder();
                for (int i = 0; i < _Split.length; i++) {
                    final LetterSpacingTextView _TextVE = new LetterSpacingTextView(mContext);
                    _TextVE.setFontSpacing(LetterSpacingTextView.NORMALBIG);
                    _TextVE.setLineSpacing(DensityUtil.dip2px(mContext, 24), 0);
                    _TextVE.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    _TextVE.setTextColor(getResources().getColor(R.color.black));
                    _TextVE.setText(_Split[i]);
                    _TextVE.setTag(i);
                    _TextVE.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            mNewsDeatailTitleLayout.setVisibility(View.GONE);
                            mNewsDetailEditableLayout.setVisibility(View.VISIBLE);
                            mNewsDetailEdittext.setText(_TextVE.getText());
                            EDIT_POSITION = DETAIL;
                            tag = (int) _TextVE.getTag();

                            return true;
                        }
                    });
                    LinearLayout.LayoutParams _LayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    _LayoutParams.setMargins(0, 0, 0, DensityUtil.dip2px(mContext, 22));
                    _TextVE.setLayoutParams(_LayoutParams);
                    if (i >= 2 && _StringBuilder.append(_Split[i]).length() > 200) {
                        _TextVE.setVisibility(GONE);
                        if (_CurrentPos == 0) {
                            _CurrentPos = i;
                        }
                    }
                    mNewsDetailHeaderContentParent.addView(_TextVE);
                }
                if (_CurrentPos < 2) {
                    mNewsDetailHeaderPulldown.setVisibility(GONE);
                }
            }
            mNewsDetailHeaderPulldown.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //umeng statistic onclick pulldown
                    MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_CLICK_PULLDOWN);
                    if (!isClickedPullDown) {
                        isClickedPullDown = true;
                        for (int i = 0; i < mNewsDetailHeaderContentParent.getChildCount(); i++) {
                            mNewsDetailHeaderContentParent.getChildAt(i).setVisibility(VISIBLE);
                        }
                        mNewsDetailHeaderPulldown.setText(R.string.mNewsDetailHeaderOnclickLess);
                        Drawable _DrawableLeft = mContext.getResources().getDrawable(R.drawable.ic_news_detail_listview_pullup);
                        _DrawableLeft.setBounds(0, 0, _DrawableLeft.getMinimumWidth(), _DrawableLeft.getMinimumHeight());
                        mNewsDetailHeaderPulldown.setCompoundDrawables(_DrawableLeft, null, null, null);
                    } else {
                        isClickedPullDown = false;
                        if (listener != null) {
                            listener.onclickPullUp(mContentHeight);
                        }
                        for (int i = 0; i < mNewsDetailHeaderContentParent.getChildCount(); i++) {
                            if (i >= _CurrentPos) {
                                mNewsDetailHeaderContentParent.getChildAt(i).setVisibility(GONE);
                            }
                        }
                        mNewsDetailHeaderPulldown.setText(R.string.mNewsDetailHeaderOnclickMore);
                        Drawable _DrawableLeft = mContext.getResources().getDrawable(R.drawable.ic_news_detail_listview_pulldown);
                        _DrawableLeft.setBounds(0, 0, _DrawableLeft.getMinimumWidth(), _DrawableLeft.getMinimumHeight());
                        mNewsDetailHeaderPulldown.setCompoundDrawables(_DrawableLeft, null, null, null);
                    }

                    mNewsDetailHeaderPulldown.setVisibility(View.GONE);
                }
            });
            mNewsDetailHeaderSourceName.setText("摘要来自：棱镜");
            if (pNewsDetail.ne != null)
                mNewsDetailHeaderLocation.setText(pNewsDetail.ne.gpe.size() > 0 ? String.format(mContext.getResources().getString(R.string.mNewsDetailHeaderLocation), pNewsDetail.ne.gpe.get(0)) : "");


//            设置编辑后的新闻
            int versionCode = DeviceInfoUtil.getApkVersionCode(mContext);
            if (versionCode > 7) {
                ArrayList<NewsDetail.Point> points = pNewsDetail.point;
                if (points != null && points.size() > 0) {
                    for (int i = 0; i < points.size(); i++) {
                        NewsDetail.Point point = points.get(i);
                        if ("title".equals(point.type)) {
                            mNewsDetailHeaderTitle.setText(point.desText);
                        } else if ("abstract".equals(point.type)) {
                            if ("".equals(point.desText)) {
                                mNewsDetailHeaderDesc.setVisibility(View.GONE);
                            } else {
                                mNewsDetailHeaderDesc.setText(point.desText);
                            }
                        } else if ("content".equals(point.type)) {
                            if ("".equals(point.desText)) {
                                if (point.paragraphIndex != null && !"".equals(point.paragraphIndex)) {
                                    int index = Integer.parseInt(point.paragraphIndex);
                                    if (mNewsDetailHeaderContentParent != null && index > mNewsDetailHeaderContentParent.getChildCount()) {
                                        mNewsDetailHeaderContentParent.removeViewAt(index);
//                                        v.setVisibility(View.GONE);
                                    } else {
                                        LetterSpacingTextView tv = (LetterSpacingTextView) mNewsDetailHeaderContentParent.getChildAt(index);
                                        tv.setText("");
                                        tv.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                if (point.paragraphIndex != null && !"".equals(point.paragraphIndex)) {
                                    TextView tv_para = (TextView) mNewsDetailHeaderContentParent.getChildAt(Integer.parseInt(point.paragraphIndex));
                                    if (tv_para != null) {
                                        tv_para.setText(point.desText);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

    }

    //修改新闻内容
    private void modifyNewsContent(NewsDetail pNewsDetail, int type, String srcText, String desText, int edit_position) {
        String url = HttpConstant.URL_GET_NEWS_CONTENT;
        final long start = System.currentTimeMillis();
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.POST);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sourceUrl", GlobalParams.news_detail_url));
        params.add(new BasicNameValuePair("srcText", srcText));
        params.add(new BasicNameValuePair("desText", desText));
        params.add(new BasicNameValuePair("paragraphIndex", tag + ""));
        if (edit_position == 1) {
            params.add(new BasicNameValuePair("type", "title"));
        } else if (edit_position == 2) {
            params.add(new BasicNameValuePair("type", "abstract"));
        } else if (edit_position == 3) {
            params.add(new BasicNameValuePair("type", "content"));
        }
        params.add(new BasicNameValuePair("uuid", "11111"));
        params.add(new BasicNameValuePair("userIcon", ""));
        params.add(new BasicNameValuePair("userName", "ariesy"));
        request.setParams(params);
        request.setCallback(new JsonCallback<Object>() {

            public void success(Object result) {


                long delta = System.currentTimeMillis() - start;
                Logger.i("ariesy", result + "");
            }

            public void failed(MyAppException exception) {
            }
        }.setReturnType(new TypeToken<Object>() {
        }.getType()));
        request.execute();
    }

    public void setDetailData(final NewsDetail pNewsDetail, HeaderVeiwPullUpListener listener) {
        if (pNewsDetail == null) {
            return;
        }
        inflateDataToNewsheader(pNewsDetail, listener);
        if (pNewsDetail != null) {
            ArrayList<NewsDetail.BaiDuBaiKe> pArrBaiDuBaiKe = pNewsDetail.baike;
            if (pArrBaiDuBaiKe != null && pArrBaiDuBaiKe.size() > 0) {
                for (int i = 0; i < pArrBaiDuBaiKe.size(); i++) {
                    final NewsDetail.BaiDuBaiKe pBaiKe = pArrBaiDuBaiKe.get(i);
                    BaiDuBaiKeView baiDuBaiKeView = new BaiDuBaiKeView(mContext);
                    baiDuBaiKeView.setBaiDuBaiKeData(pArrBaiDuBaiKe.get(i));
                    mllBaiKe.addView(baiDuBaiKeView);
                    baiDuBaiKeView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                            _Intent.putExtra("url", pBaiKe.url);
                            mContext.startActivity(_Intent);
                            //add umeng statistic baidubaike
                            MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_BAIDUBAIKU);
                        }
                    });
                }
            } else {
                mllBaiKe.setVisibility(GONE);
            }
            //图片墙的相关显示


            if (pNewsDetail.imgWall != null) {
//                if (pNewsDetail.imgWall != null) {
//
//
//                    mImageWall.setVisibility(View.VISIBLE);
//                    mImageWall.addSource(pNewsDetail.imgWall, ViewWall.STYLE_9);
//                } else {
//                    mImageWall.setVisibility(GONE);
//                }
//                if(true){
//                    return;
//                }
                new Compt().putTask(new BackRunnable() {
                    @Override
                    public void run() {
                        for (Map<String, String> m : pNewsDetail.imgWall) {
                            String url = m.get("img").toString();

                            BitmapFactory.Options op = null;
                            while (op == null)
                                try {
                                    op = BitmapUtil.getBitmapFactoryOptions(url);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    continue;
                                }


                            m.put("w", "" + op.outWidth);
                            m.put("h", "" + op.outHeight);


                        }


                        List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();
                        List<HashMap<String, String>> minor = new ArrayList<HashMap<String, String>>(pNewsDetail.imgWall);
                        this.object = composeMatch(resultList, minor);


                    }


                }
                        , new CallbackRunnable() {
                    @Override
                    public boolean run(Message message, boolean b, Activity activity) throws Exception {

                        mImageWall.setVisibility(View.VISIBLE);
                        mImageWall.addSource(getBackRunnable().getObject(), ViewWall.STYLE_9);
                        return false;
                    }
                }).run();

            } else {
                mImageWall.setVisibility(GONE);
            }
            ArrayList<NewsDetail.ZhiHu> pArrZhiHu = pNewsDetail.zhihu;
            if (pArrZhiHu != null && pArrZhiHu.size() > 0) {
                for (int i = 0; i < pArrZhiHu.size(); i++) {
                    final NewsDetail.ZhiHu pZhihu = pArrZhiHu.get(i);
                    ZhiHuView zhiHuView = new ZhiHuView(mContext);
                    zhiHuView.setZhiHuData(pArrZhiHu.get(i));
                    zhiHuView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                            _Intent.putExtra("url", pZhihu.url);
                            mContext.startActivity(_Intent);
                            //add umeng statistic zhihu
                            MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_ZHIHU);
                        }
                    });
                    mllZhiHuItem.addView(zhiHuView);
                }
            } else {
                mllZhiHu.setVisibility(GONE);
            }

            final ArrayList<ArrayList<String>> pArrDouBan = pNewsDetail.douban;
            if (pArrDouBan != null && pArrDouBan.size() > 0) {
                for (int i = 0; i < pArrDouBan.size(); i++) {
                    final ArrayList<String> pDouBan = pArrDouBan.get(i);
                    TextViewExtend textView = new TextViewExtend(mContext);
                    textView.setTextColor(getResources().getColor(R.color.douban_item_blue));
                    textView.setTextSize(19);
                    textView.setText(pDouBan.get(0));
                    textView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                            _Intent.putExtra("url", pDouBan.get(1));
                            mContext.startActivity(_Intent);
                            //add umeng statistic douban
                            MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_DOUBAI);
                        }
                    });
                    mvDouBanItem.addView(textView);
                }
            } else {
                mllDouBan.setVisibility(GONE);
            }

            ArrayList<NewsDetail.Weibo> pArrWeibo = pNewsDetail.weibo;
            if (pArrWeibo != null && pArrWeibo.size() > 0) {
                for (int i = 0; i < pArrWeibo.size(); i++) {
                    final NewsDetail.Weibo pWeiBo = pArrWeibo.get(i);
                    SinaView sinaView = new SinaView(mContext);
                    sinaView.setSinaData(pArrWeibo.get(i));
                    mllSinaItem.addView(sinaView);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) sinaView.getLayoutParams();
                    if (i == pArrWeibo.size() - 1) {
                        layoutParams.rightMargin = DensityUtil.dip2px(mContext, 16);
                    }
                    layoutParams.leftMargin = DensityUtil.dip2px(mContext, 16);
                    sinaView.setLayoutParams(layoutParams);
                    if (pWeiBo.isCommentFlag == null || "".equals(pWeiBo.isCommentFlag)) {
                        sinaView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent _Intent = new Intent(mContext, NewsDetailWebviewAty.class);
                                _Intent.putExtra("url", pWeiBo.url);
                                mContext.startActivity(_Intent);
                                //add umeng statistic weibo
                                MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_NEWSDETAIL_WEIBO);
                            }
                        });
                    }
                }
            } else {
                mllSina.setVisibility(GONE);
            }
        }
        if (pNewsDetail != null && pNewsDetail.relate != null && pNewsDetail.relate.size() > 0) {

        } else {
            mNewsDetailRelate.setVisibility(GONE);
        }
    }

    private List<HashMap<String, String>> composeMatch(List<HashMap<String, String>> resultList, List<HashMap<String, String>> minor) {
//                        for (int i = 0; i < 10000; i++) {
        getMatched(resultList, minor, "2-3-2");
//                                for (int j = 0; j < 10000; j++) {
//
//                                    if (get2Matched(resultList, minor, j) != -1) {
//                                        for (int l = 0; l < 10000; l++) {
//                                            if (get3Matched(resultList, minor, l) != -1) {
//                                return resultList;
//                                            }else{
//                                                break;
//                                            }
//
//                                        }
//                                    }else{
//                                        break;
//                                    }
//                                }
//                            } else {
//                                break;
//                            }
//
//                        }

        return resultList;
    }

    private int getMatched(List<HashMap<String, String>> resultList, List<HashMap<String, String>> minor, String type) {
        int y = 0;
        int i = 0;
        if (type.length() == 0) {
            return 0;
        } else if (type.length() == 1) {
            i = Integer.parseInt(type);
            type = "";
        } else {
            String[] ts = type.split("-");
            i = Integer.parseInt(ts[0]);
            type = type.substring(2);
        }
        if (i == 2) {
            y = get2Matched(resultList, minor, type);
        } else if (i == 3) {
            y = get3Matched(resultList, minor, type);
        }
        return y;
    }

    private List<HashMap<String, String>> get1Matched(List<HashMap<String, String>> resultList, List<HashMap<String, String>> minor) {
        return minor;
    }

    private int get2Matched(List<HashMap<String, String>> maps, List source, String sq) {
        int th = 0;

        int which = 0;

        int stepcnst = 50;
        float ratio = 1.8f;
        float constW = 0;
        for (int i = stepcnst; i < GlobalParams.screenWidth; i += stepcnst) {
            if (i >= GlobalParams.screenWidth - 1) {
                if (ratio == 1f)
                    break;
                ratio -= 0.1;

                i = 1;
                continue;
            }
            int scaledw1 = i;
            int scaledw2 = GlobalParams.screenWidth - scaledw1;
            int scaledh1 = 0;
            int scaledh2 = 0;
            HashMap<String, String> m1 = null;
            HashMap<String, String> m2 = null;
            List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(source);

            for (HashMap<String, String> first : list) {
                int whead = Integer.parseInt(first.get("w").toString());
                int hhead = Integer.parseInt(first.get("h").toString());

                if (scaledh1 == 0) {

                    scaledh1 = hhead * scaledw1 / whead;
                    m1 = first;
                }
                List<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>(list);
                for (HashMap<String, String> m : list2) {
                    if (first.get("img").toString().equals(m.get("img").toString())) {
                        continue;
                    }
                    int w = Integer.parseInt(m.get("w").toString());
                    int h = Integer.parseInt(m.get("h").toString());


                    scaledh2 = h * scaledw2 / w;

                    if (Math.abs(scaledh1 - scaledh2) < stepcnst) {
                        if ((scaledh2 < scaledh1 && scaledh2 * ratio >= scaledh1) || (scaledh1 < scaledh2 && scaledh1 * ratio >= scaledh2)) {

                            int scaledh = Math.max(scaledh2,scaledh1);
                                m2 = m;
                            m2.put("units", "2");
                            m2.put("position", "2");
                            m2.put("scaledh", "" + scaledh);
                            m2.put("scaledw", "" + scaledw2);
                            m1.put("units", "2");
                            m1.put("position", "1");
                            m1.put("scaledh", "" + scaledh);
                            m1.put("scaledw", "" + scaledw1);
                                maps.add(m1);
                                maps.add(m2);
                                source.remove(m2);
                                source.remove(m1);
                            try {
                                return getMatched(maps, source, sq) + (th++);
                            }catch(Exception ex){
                                ex.printStackTrace();
                            }
                        } else {
                            scaledh1 = 0;
                            scaledh2 = 0;
                            m2 = null;
                            m1 = null;
                            continue;
                        }
                    }

                }
            }
        }
        return -1;
    }

    private int get3Matched(List<HashMap<String, String>> maps, List source, String sq) {
        int th = 0;

        float ratio1 = 2f;
        int which = 0;
        float constW = 0;
        int stepcnst = 60;

        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(source);
        HashMap<String, String> m1 = null;
        for (HashMap<String, String> first : list) {

            int whead = Integer.parseInt(first.get("w").toString());
            int hhead = Integer.parseInt(first.get("h").toString());
            for (int i = stepcnst; i < GlobalParams.screenWidth; i += stepcnst) {

                if (i >= GlobalParams.screenWidth - 1) {
                    if (ratio1 == 1f)
                        break;
                    ratio1 -= 0.1;

                    i = 1;
                    continue;
                }
                int scaledw1 = i;
                int scaledw2 = GlobalParams.screenWidth - scaledw1;
                int scaledh1 = 0;
                int scaledh2 = 0;


                    scaledh1 = hhead * scaledw1 / whead;
                    m1 = first;
                List<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>(list);
                HashMap<String, String> m2 = null;
                float ratio2 = 1f;
                for (HashMap<String, String> m : list2) {
                    if (first.get("img").toString().equals(m.get("img").toString())) {
                        continue;
                    }

                    int w = Integer.parseInt(m.get("w").toString());
                    int h = Integer.parseInt(m.get("h").toString());

                    for (int i2 = stepcnst; i2 < GlobalParams.screenWidth; i2 += stepcnst) {
                        if (i2 >= GlobalParams.screenWidth - 1) {
                            if (ratio2 == 1f)
                                break;
                            ratio2 -= 0.1;

                            i2 = 1;
                            continue;
                        }
                        scaledw2 = i2;
                        int scaledw3 = GlobalParams.screenWidth - scaledw1 - scaledw2;
                        int scaledh3 = 0;


                            scaledh2 = h * scaledw2 / w;
                            m2 = m;

                        HashMap<String, String> m3 = null;
                        for (HashMap<String, String> mm : list2) {
                            if (m.get("img")!=null&&mm.get("img")!=null&&m.get("img").toString().equals(mm.get("img").toString())) {
                                continue;
                            }
                            if (m1.get("img")!=null&&mm.get("img")!=null&&m1.get("img").toString().equals(mm.get("img").toString())) {
                                continue;
                            }
                            int w3 = Integer.parseInt(mm.get("w").toString());
                            int h3 = Integer.parseInt(mm.get("h").toString());
                            scaledh3 = h3 * scaledw3 / w3;

                            if (Math.abs(scaledh1 - scaledh2) < stepcnst && Math.abs(scaledh2 - scaledh3) < stepcnst) {
                                if (((scaledh2 - scaledh1 < 0 && scaledh2 * ratio1 >= scaledh1) || (scaledh1 - scaledh2 < 0 && scaledh1 * ratio1 >= scaledh2)) && ((scaledh2 - scaledh3 < 0 && scaledh2 * ratio1 >= scaledh3) || (scaledh3 - scaledh2 < 0 && scaledh3 * ratio1 >= scaledh2))) {


                                    int scaledh = Math.max(Math.max(scaledh2,scaledh1),Math.max(scaledh2,scaledh3));
                                        m3 = mm;
                                        m3.put("units", "3");
                                        m3.put("position", "3");
                                        m3.put("scaledh", "" + scaledh);
                                        m3.put("scaledw", "" + scaledw3);
                                        m2.put("units", "3");
                                        m2.put("position", "2");
                                        m2.put("scaledh", "" + scaledh);
                                        m2.put("scaledw", "" + scaledw2);
                                        m1.put("units", "3");
                                        m1.put("position", "1");
                                        m1.put("scaledh", "" + scaledh);
                                        m1.put("scaledw", "" + scaledw1);
                                        maps.add(m1);
                                        maps.add(m2);
                                        maps.add(m3);
                                        source.remove(m3);
                                        source.remove(m2);
                                        source.remove(m1);

                                        return getMatched(maps,source,sq)+th++;
                                   } else {
                                    scaledh1 = 0;
                                    m1 = null;
                                    scaledh2 = 0;
                                    m2 = null;
                                    scaledh3 = 0;
                                    m3 = null;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }

        }
        return -1;
    }
}
