package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.DisplayImageListener;
import com.news.yazhidao.listener.PraiseListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.TextUtils;
import com.news.yazhidao.net.request.PraiseRequest;
import com.news.yazhidao.pages.NewsDetailAty;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.utils.BCConvert;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.utils.image.ImageManager;
import com.news.yazhidao.utils.manager.SharedPreManager;
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
import cn.sharesdk.framework.PlatformDb;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class NewsDetailHeaderView extends FrameLayout implements CommentPopupWindow.IUpdateCommentCount, InputbarPopupWindow.IUpdateCommentCount, CommentPopupWindow.IUpdatePraiseCount {


    public static interface HeaderVeiwPullUpListener {
        void onclickPullUp(int height);
    }

    //文本类型段落评论
    public static final String TEXT_PARAGRAPH = "text_paragraph";
    //文本类型全文评论
    public static final String TEXT_DOC = "text_doc";
    //语音类型段落评论
    public static final String SPEECH_PARAGRAPH = "speech_paragraph";
    //语音类型全文评论
    public static final String SPEECH_DOC = "speech_doc";

    private int versionCode;
    private CommentListener commentListener;

    //当前新闻内容的高度
    private int mContentHeight;
    private View mRootView;
    private Context mContext;
    private int speechCount;
    private int delta_position = 0;
    private RelativeLayout rl_article_comments;
    private ShowAllListview lv_article_comments;
    private TextViewExtend tv_add_comment;
    private MyAdapter adapter;
    private LinearLayout mllBaiKe, mllZhiHu, mllZhiHuItem, mllSina, mllSinaItem, mllDouBan;
    private WordWrapView mvDouBanItem;
    private HorizontalScrollView mSinaScrollView;
    private ImageView mNewsDetailHeaderImg;
    private LetterSpacingTextView mNewsDetailHeaderTitle;
    private TextView mNewsDetailHeaderTime;
    private TextView mNewsDetailHeaderTemperature;
    private ImageView iv_user_icon_article_comment;
    private SpeechView sv_article_comment;
    private ImageView tv_des_icon;
    private RelativeLayout rl_speech_view;
    private RelativeLayout mNewsDetailHeaderContentWrapper;
    private LetterSpacingTextView mNewsDetailHeaderDesc;
    private LinearLayout mNewsDetailHeaderContentParent;
    private TextView mNewsDetailHeaderLocation;
    private TextView mNewsDetailRelate;
    private LinearLayout ll_detail_des;
    private RelativeLayout mNewsDetailEditableLayout;
    private LinearLayout mNewsDeatailTitleLayout;
    private EditText mNewsDetailEdittext;
    private Button MnewsDetailButtonConfirm;
    private Button MnewsDetailButtonCancel;
    private String[] _Split;
    private int PARA_FLAG = 0;
    private int ARTICLE_FLAG = 1;
    private ArrayList<NewsDetail.Point> marrPoint = new ArrayList<NewsDetail.Point>();
    private ArrayList<NewsDetail.Point> paraPoint = new ArrayList<NewsDetail.Point>();
    private ImageView iv_show_all_zhihu_views;
    private String sourceUrl;
    private ImageView tv_cutoff_line;

    private static int EDIT_POSITION;
    private static final int TITLE = 1;
    private static final int DESCRIPTION = 2;
    private static final int DETAIL = 3;
    private static int tag = -1;
    private int type = 0;
    private String srcText = "";
    private boolean isNewFlag = false;

    private ImageWallView mImageWall;
    //是否点击了展开全文
    private boolean isClickedPullDown = false;

    private User user;
    private String uuid;
    //当前文章隐藏的位置
    private int _CurrentPos = 0;

    private boolean add_flag = false;
    private ArrayList<NewsDetail.Point> points = new ArrayList<NewsDetail.Point>();

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

        user = SharedPreManager.getUser(mContext);
        uuid = DeviceInfoUtil.getUUID();

        //设置编辑后的新闻
        versionCode = DeviceInfoUtil.getApkVersionCode(mContext);

    }

    private void findViews() {
        rl_article_comments = (RelativeLayout) mRootView.findViewById(R.id.rl_article_comments);
        lv_article_comments = (ShowAllListview) mRootView.findViewById(R.id.lv_article_comments);
        mllBaiKe = (LinearLayout) mRootView.findViewById(R.id.baike_linerLayout);
        mllZhiHu = (LinearLayout) mRootView.findViewById(R.id.zhihu_linerLayout);
        mllZhiHuItem = (LinearLayout) mRootView.findViewById(R.id.zhihu_item_linerLayout);
        iv_show_all_zhihu_views = (ImageView) mRootView.findViewById(R.id.iv_show_all_zhihu_views);
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
        rl_speech_view = (RelativeLayout) mRootView.findViewById(R.id.rl_speech_view);
        iv_user_icon_article_comment = (ImageView) mRootView.findViewById(R.id.iv_user_icon_article_comment);
        sv_article_comment = (SpeechView) mRootView.findViewById(R.id.sv_article_comment);
        ll_detail_des = (LinearLayout) mRootView.findViewById(R.id.ll_detail_des);
        tv_des_icon = (ImageView) mRootView.findViewById(R.id.tv_des_icon);
        mNewsDetailHeaderDesc = (LetterSpacingTextView) mRootView.findViewById(R.id.mNewsDetailHeaderDesc);//新闻描述
        mNewsDetailHeaderContentWrapper = (RelativeLayout) mRootView.findViewById(R.id.mNewsDetailHeaderContentWrapper);
        mNewsDetailHeaderContentParent = (LinearLayout) mRootView.findViewById(R.id.mNewsDetailHeaderContentParent);//新闻内容
        tv_add_comment = (TextViewExtend) mRootView.findViewById(R.id.tv_add_comment);
        tv_cutoff_line = (ImageView) mRootView.findViewById(R.id.tv_cutoff_line);

//        mNewsDetailHeaderLocation = (TextView) mRootView.findViewById(R.id.mNewsDetailHeaderLocation);//新闻发生的地点
        mNewsDeatailTitleLayout = (LinearLayout) mRootView.findViewById(R.id.mNewsDeatailTitleLayout);
        mNewsDetailEditableLayout = (RelativeLayout) mRootView.findViewById(R.id.mNewsDetailEditableLayout);
        mNewsDetailEdittext = (EditText) mRootView.findViewById(R.id.mNewsDetailEdittext);
        MnewsDetailButtonConfirm = (Button) mRootView.findViewById(R.id.MnewsDetailButtonConfirm);
        MnewsDetailButtonCancel = (Button) mRootView.findViewById(R.id.MnewsDetailButtonCancel);
        mImageWall = (ImageWallView) mRootView.findViewById(R.id.mImageWall);
    }

    @Override
    public void updateCommentCount(int count, int paragraphIndex, NewsDetail.Point point, int flag, boolean isPraiseFlag) {

        if (count > 0) {
            commentListener.comment(true);
        }

        if (point != null) {
            if (!isNewFlag) {
                if (mNewsDetailHeaderContentParent != null) {
                    if (flag == PARA_FLAG) {
                        final RelativeLayout rl_para = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(paragraphIndex);
                        rl_para.setTag(paragraphIndex);
                        RelativeLayout rl_comment_content = (RelativeLayout) rl_para.findViewById(R.id.rl_comment_content);
                        final RelativeLayout rl_comment = (RelativeLayout) rl_comment_content.findViewById(R.id.rl_comment);
                        rl_comment.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                RelativeLayout rl_aa = (RelativeLayout) rl_comment.getParent().getParent();
                                int para_index = (int) rl_aa.getTag();
                                for (int m = 0; m < points.size(); m++) {
                                    if (para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                        point_para.add(points.get(m));
                                    }
                                }

                                CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, (int) rl_para.getTag(), PARA_FLAG,NewsDetailHeaderView.this);
                                window.setFocusable(true);
                                //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                          window.setBackgroundDrawable(new BitmapDrawable());
                                //防止虚拟软键盘被弹出菜单遮住、
                                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                        | Gravity.CENTER, 0, 0);
                            }
                        });


                        RoundedImageView iv_user_icon = (RoundedImageView) rl_comment.findViewById(R.id.iv_user_icon);
                        setViewBorder(iv_user_icon);
                        if (user != null) {
                            ImageLoaderHelper.dispalyImage(mContext, user.getUserIcon(), iv_user_icon);
                        }

                        TextView tv_comment_count = (TextView) rl_comment.findViewById(R.id.tv_comment_count);
                        setTextViewBg(tv_comment_count);


                        ImageView iv_add_comment = (ImageView) rl_comment.findViewById(R.id.iv_add_comment);
                        setViewBg(iv_add_comment);

                        TextView tv_comment_content = (TextView) rl_comment.findViewById(R.id.tv_comment_content);
                        setTextColor(tv_comment_content);

                        ImageView iv_none_point = (ImageView) rl_comment_content.findViewById(R.id.iv_none_point);
                        setViewBg(iv_none_point);
                        SpeechView speechView = (SpeechView) rl_comment_content.findViewById(R.id.speechView);

                        if (point != null) {

                            iv_none_point.setVisibility(View.GONE);

                            if (point.type.equals("text_paragraph")) {
                                rl_comment.setVisibility(View.VISIBLE);
                                speechView.setVisibility(View.GONE);
                                tv_comment_content.setText(point.srcText);

                                int origin_count = Integer.parseInt(tv_comment_count.getText().toString());
                                tv_comment_count.setText(origin_count + count + "");
                            } else {
                                speechView.setUrl(point.srcText, false);
                                speechView.setDuration(point.srcTextTime);
                                speechView.setVisibility(View.VISIBLE);
                                rl_comment.setVisibility(View.GONE);
                            }
                            point.paragraphIndex = String.valueOf(paragraphIndex);
                            points.add(point);
                        }
                    } else {
                        marrPoint.add(point);
                        lv_article_comments.setVisibility(View.VISIBLE);

                        if (adapter == null) {
                            adapter = new MyAdapter();
                            lv_article_comments.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

            } else {

                if (mNewsDetailHeaderContentParent != null) {
                    if (flag == PARA_FLAG) {
                        final RelativeLayout rl_para = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(paragraphIndex);
                        RelativeLayout rl_comment_content = (RelativeLayout) rl_para.findViewById(R.id.rl_comment_content);
                        final RelativeLayout rl_comment = (RelativeLayout) rl_comment_content.findViewById(R.id.rl_comment);
                        rl_comment.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                RelativeLayout rl_aa = (RelativeLayout) rl_comment.getParent().getParent();
                                int para_index = (int) rl_aa.getTag();
                                for (int m = 0; m < points.size(); m++) {
                                    if (para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                        point_para.add(points.get(m));
                                    }
                                }

                                CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, (int) rl_para.getTag(), PARA_FLAG,NewsDetailHeaderView.this);
                                window.setFocusable(true);
                                //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                          window.setBackgroundDrawable(new BitmapDrawable());
                                //防止虚拟软键盘被弹出菜单遮住、
                                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                        | Gravity.CENTER, 0, 0);
                            }
                        });

                        RoundedImageView iv_user_icon = (RoundedImageView) rl_comment.findViewById(R.id.iv_user_icon);
                        setViewBorder(iv_user_icon);

                        ImageView iv_add_comment = (ImageView) rl_comment.findViewById(R.id.iv_add_comment);
                        setViewBg(iv_add_comment);

                        if (user != null) {
                            ImageLoaderHelper.dispalyImage(mContext, user.getUserIcon(), iv_user_icon);
                        }

                        TextView tv_comment_count = (TextView) rl_comment.findViewById(R.id.tv_comment_count);
                        setTextViewBg(tv_comment_count);
                        TextView tv_comment_content = (TextView) rl_comment.findViewById(R.id.tv_comment_content);
                        setTextColor(tv_comment_content);

                        ImageView iv_none_point = (ImageView) rl_comment_content.findViewById(R.id.iv_none_point);
                        setViewBg(iv_none_point);
                        SpeechView speechView = (SpeechView) rl_comment_content.findViewById(R.id.speechView);

                        if (point != null) {

                            iv_none_point.setVisibility(View.GONE);

                            if (point.type.equals("text_paragraph")) {
                                rl_comment.setVisibility(View.VISIBLE);
                                speechView.setVisibility(View.GONE);
                                tv_comment_content.setText(point.srcText);

                                int origin_count = Integer.parseInt(tv_comment_count.getText().toString());
                                tv_comment_count.setText(origin_count + count + "");
                            } else {
                                speechView.setUrl(point.srcText, false);
                                speechView.setDuration(point.srcTextTime);
                                speechView.setVisibility(View.VISIBLE);
                                rl_comment.setVisibility(View.GONE);
                            }
                            point.paragraphIndex = String.valueOf(paragraphIndex);
                            points.add(point);

                        }
                    } else {
                        marrPoint.add(point);
                        lv_article_comments.setVisibility(View.VISIBLE);

                        if (adapter == null) {
                            adapter = new MyAdapter();
                            lv_article_comments.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

            }
        }

    }


    @Override
    public void updatePraise(int count, int paragraphIndex,ArrayList<NewsDetail.Point> marrPoint) {

        final RelativeLayout rl_para = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(paragraphIndex);
        rl_para.setTag(paragraphIndex);
        RelativeLayout rl_comment_content = (RelativeLayout) rl_para.findViewById(R.id.rl_comment_content);
        final RelativeLayout rl_comment = (RelativeLayout) rl_comment_content.findViewById(R.id.rl_comment);

        TextView tv_praise_count = (TextView) rl_comment.findViewById(R.id.tv_praise_count);
        tv_praise_count.setText(count + "");


        this.marrPoint = marrPoint;

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
    private void inflateDataToNewsheader(final Object pNewsDetail, final String sourceUrl, final HeaderVeiwPullUpListener listener, boolean isnew) {


        isNewFlag = isnew;
        if (pNewsDetail != null) {
            if (!isnew) {

                //初始化headerview的各个布局的值
                initialNewsDetailHeaderView((NewsDetail) pNewsDetail);

                //填充新闻详情的内容
                if (((NewsDetail) pNewsDetail).content != null) {
                    inflateNewsDetailHeaderView((NewsDetail) pNewsDetail);
                }

                //设置编辑后的新闻
                inflateEditNews();

            } else {

                //初始化headerview的各个布局的值
                initialNewsDetailAddHeaderView((NewsDetailAdd) pNewsDetail);

                //填充新闻详情的内容
                inflateNewsDetailAddHeaderView((NewsDetailAdd) pNewsDetail);

                //设置编辑后的新闻
                inflateEditNews();
            }
        }

    }

    private void inflateEditNews() {

        int versionCode = DeviceInfoUtil.getApkVersionCode(mContext);
        if (versionCode > 7) {
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
                                    RelativeLayout rl_ss = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(Integer.parseInt(point.paragraphIndex));
                                    LetterSpacingTextView tv = (LetterSpacingTextView) rl_ss.findViewById(R.id.lstv_para_content);
                                    tv.setText("");
                                    tv.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            if (point.paragraphIndex != null && !"".equals(point.paragraphIndex)) {
                                RelativeLayout rl_ss = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(Integer.parseInt(point.paragraphIndex));
                                LetterSpacingTextView tv_para = (LetterSpacingTextView) rl_ss.findViewById(R.id.lstv_para_content);
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

    private void inflateNewsDetailAddHeaderView(NewsDetailAdd pNewsDetail) {

        if (((NewsDetailAdd) pNewsDetail).content != null) {

            ArrayList<LinkedTreeMap<String, HashMap<String, String>>> maps = ((NewsDetailAdd) pNewsDetail).content;

            boolean flag = false;

            for (int i = 0; i < maps.size(); i++) {
                LinkedTreeMap<String, HashMap<String, String>> map = maps.get(i);

                String a = String.valueOf(i);

                HashMap<String, String> temp_map = map.get(a);

                if (temp_map != null) {
                    if (temp_map.containsKey("img")) {
                        String url = temp_map.get("img");
                        ImageView img = new ImageView(mContext);
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        ImageManager.getInstance(mContext).DisplayImage(url,img,false,new DisplayImageListener() {
                            @Override
                            public void success(int width,int height) {

                            }

                            @Override
                            public void failed() {

                            }
                        });
                        mNewsDetailHeaderContentParent.addView(img, p);

                        if (flag) {
                            addTextviewDevider();
                        }

                        if (i == 0) {
                            img.setVisibility(View.GONE);
                        }

                        flag = true;
                    } else if (temp_map.containsKey("img_info")) {
                        String img_info = temp_map.get("img_info");
                        if (flag) {
                            TextView tv = new TextView(mContext);
                            img_info = "    " + img_info;
                            tv.setText(img_info);
                            tv.setTextColor(Color.BLACK);

                            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

//                                    p.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                                    p.leftMargin = 20;
//                                    p.topMargin = 20;
//                                    p.bottomMargin = 20;
//                                    p.rightMargin = 20;
                            p.setMargins(20, 20, 20, 20);

                            tv.setLayoutParams(p);

                            mNewsDetailHeaderContentParent.addView(tv);
                            addTextviewDevider();
                        }

                    } else if (temp_map.containsKey("txt")) {
                        String text = temp_map.get("txt");
                        text = "    " + text;

                        add_flag = false;
                        //段落和评论布局
                        final RelativeLayout rl_para = (RelativeLayout) View.inflate(mContext, R.layout.rl_content_and_comment, null);
                        final LetterSpacingTextView lstv_para_content = (LetterSpacingTextView) rl_para.findViewById(R.id.lstv_para_content);
                        lstv_para_content.setFontSpacing(LetterSpacingTextView.NORMALBIG);
                        lstv_para_content.setLineSpacing(DensityUtil.dip2px(mContext, 24), 0);
                        lstv_para_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                        lstv_para_content.setTextColor(getResources().getColor(R.color.black));
                        lstv_para_content.setText(text);
                        rl_para.setTag(i + delta_position);

                        final ImageView iv_none_point = (ImageView) rl_para.findViewById(R.id.iv_none_point);
                        setViewBg(iv_none_point);

                        final RelativeLayout rl_comment = (RelativeLayout) rl_para.findViewById(R.id.rl_comment);
                        rl_comment.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                RelativeLayout rl_aa = (RelativeLayout) rl_comment.getParent().getParent();
                                int para_index = (int) rl_aa.getTag();
                                for (int m = 0; m < points.size(); m++) {
                                    if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                        point_para.add(points.get(m));
                                    }
                                }

                                CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, (int) rl_para.getTag(), PARA_FLAG,NewsDetailHeaderView.this);
                                window.setFocusable(true);
                                //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                          window.setBackgroundDrawable(new BitmapDrawable());
                                //防止虚拟软键盘被弹出菜单遮住、
                                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                        | Gravity.CENTER, 0, 0);
                            }
                        });
//                        final SpeechView speechView = (SpeechView) rl_para.findViewById(R.id.speechView);
//                        speechView.setVisibility(View.GONE);
                        if (paraPoint != null && paraPoint.size() > 0) {
                            for (int n = 0; n < paraPoint.size(); n++) {

                                NewsDetail.Point point = paraPoint.get(n);
                                if (!add_flag) {
                                    if (TEXT_PARAGRAPH.equals(point.type)) {
//                                        speechView.setVisibility(View.GONE);
                                        iv_none_point.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                                RelativeLayout rl_aa = (RelativeLayout) iv_none_point.getParent().getParent();
                                                int para_index = (int) rl_aa.getTag();
                                                for (int m = 0; m < points.size(); m++) {
                                                    if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                                        point_para.add(points.get(m));
                                                    }
                                                }
                                                CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, para_index, PARA_FLAG,NewsDetailHeaderView.this);
                                                window.setFocusable(true);
                                                //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                              window.setBackgroundDrawable(new BitmapDrawable());
                                                //防止虚拟软键盘被弹出菜单遮住、
                                                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                                window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                                        | Gravity.CENTER, 0, 0);
                                            }
                                        });


                                        TextView tv_praise_count = (TextView) rl_para.findViewById(R.id.tv_praise_count);
                                        RoundedImageView iv_user_icon = (RoundedImageView) rl_para.findViewById(R.id.iv_user_icon);
                                        setViewBorder(iv_user_icon);
                                        final ImageView iv_add_comment = (ImageView) rl_para.findViewById(R.id.iv_add_comment);
                                        setViewBg(iv_add_comment);
                                        iv_add_comment.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                InputbarPopupWindow window = new InputbarPopupWindow((NewsDetailAty) mContext, points, sourceUrl, NewsDetailHeaderView.this, ARTICLE_FLAG);
                                                window.setFocusable(true);
                                                //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                          window.setBackgroundDrawable(new BitmapDrawable());
                                                //防止虚拟软键盘被弹出菜单遮住、
                                                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                                          window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                                window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                                        | Gravity.CENTER, 0, GlobalParams.screenHeight);
                                            }
                                        });
                                        final TextView tv_comment_count = (TextView) rl_para.findViewById(R.id.tv_comment_count);
                                        setTextViewBg(tv_comment_count);
                                        tv_comment_count.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                            }
                                        });
                                        TextView tv_comment_content = (TextView) rl_para.findViewById(R.id.tv_comment_content);
                                        setTextColor(tv_comment_content);

                                        TextView tv_devider = (TextView) rl_para.findViewById(R.id.tv_devider);
                                        if (i == maps.size()) {
                                            tv_devider.setVisibility(View.GONE);
                                        }


                                        if (i == Integer.parseInt(point.paragraphIndex)) {

                                            tv_praise_count.setText(point.up);
                                            tv_comment_count.setText(point.comments_count);
                                            if (TEXT_PARAGRAPH.equals(point.type)) {
                                                tv_comment_content.setText(point.srcText);
                                            }

                                            if (point.userIcon != null && !"".equals(point.userIcon)) {
                                                ImageLoaderHelper.dispalyImage(mContext, point.userIcon, iv_user_icon);
                                            }

                                            add_flag = true;
                                            rl_comment.setVisibility(View.VISIBLE);
                                            iv_none_point.setVisibility(View.GONE);
                                        } else {
                                            rl_comment.setVisibility(View.GONE);
                                            iv_none_point.setVisibility(View.VISIBLE);
                                        }

                                    }
//                                    else {
//                                        speechView.setVisibility(View.VISIBLE);
//                                        rl_comment.setVisibility(View.GONE);
//                                        iv_none_point.setVisibility(View.GONE);
//                                        speechView.setUrl(point.srcText, false);
//                                        speechView.setDuration(point.srcTextTime);
//                                    }
                                }
                            }

                        } else {
                            rl_comment.setVisibility(View.GONE);
                            iv_none_point.setVisibility(View.VISIBLE);
                            iv_none_point.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                    RelativeLayout rl_aa = (RelativeLayout) iv_none_point.getParent().getParent();
                                    int para_index = 0;
                                    if (rl_aa.getTag() != null) {
                                        para_index = (int) rl_aa.getTag();
                                    }
                                    for (int m = 0; m < points.size(); m++) {
                                        if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                            point_para.add(points.get(m));
                                        }
                                    }
                                    CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, para_index, PARA_FLAG,NewsDetailHeaderView.this);
                                    window.setFocusable(true);
                                    //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                              window.setBackgroundDrawable(new BitmapDrawable());
                                    //防止虚拟软键盘被弹出菜单遮住、
                                    window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                    window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                            | Gravity.CENTER, 0, 0);
                                }
                            });
                        }


                        mNewsDetailHeaderContentParent.addView(rl_para);
                        addTextviewDevider();
                    }
                }
            }

        }

    }

    private void initialNewsDetailAddHeaderView(final NewsDetailAdd pNewsDetail) {

        if (TextUtils.isValidate(((NewsDetailAdd) pNewsDetail).imgUrl)) {
//            ImageManager.getInstance(mContext).DisplayImage(((NewsDetailAdd) pNewsDetail).imgUrl, mNewsDetailHeaderImg, true, new DisplayImageListener() {
//                @Override
//                public void success(int width,int height) {
//
//                        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        int left = DensityUtil.dip2px(mContext, 4);
//                        int top = DensityUtil.dip2px(mContext, 5);
//                        params.setMargins(left, height - top, left, 0);
//
//                        mNewsDetailHeaderContentWrapper.setLayoutParams(params);
//
//                }
//
//                @Override
//                public void failed() {
//
//                }
//            });

            ViewGroup.LayoutParams layoutParams = mNewsDetailHeaderImg.getLayoutParams();
            layoutParams.width = GlobalParams.screenWidth;
            layoutParams.height = (int) (GlobalParams.screenHeight * 0.40);
            mNewsDetailHeaderImg.setLayoutParams(layoutParams);


            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mNewsDetailHeaderContentWrapper.getLayoutParams();
            int left = DensityUtil.dip2px(mContext, 8);
            int top = DensityUtil.dip2px(mContext, 10);
            params.setMargins(left, (int)(GlobalParams.screenHeight * 0.4) - top, left, 0);

            mNewsDetailHeaderContentWrapper.setLayoutParams(params);
            TextViewExtend tv = new TextViewExtend(mContext);

            ImageLoaderHelper.dispalyImage(mContext, ((NewsDetailAdd) pNewsDetail).imgUrl, mNewsDetailHeaderImg, tv);


        } else {
            mNewsDetailHeaderImg.setVisibility(GONE);
        }

        points = ((NewsDetailAdd) pNewsDetail).point;
        this.sourceUrl = sourceUrl;

        //提取全文评论列表
        for (int i = 0; i < points.size(); i++) {
            NewsDetail.Point point = points.get(i);

            if (TEXT_DOC.equals(point.type) || SPEECH_DOC.equals(point.type)) {
                marrPoint.add(point);
            }
        }

        //提取段落评论列表
        for (int i = 0; i < points.size(); i++) {
            NewsDetail.Point point = points.get(i);

            if (TEXT_PARAGRAPH.equals(point.type) || SPEECH_PARAGRAPH.equals(point.type)) {
                paraPoint.add(point);
            }
        }

//            marrPoint = points;
        if (marrPoint.size() > 0) {
            adapter = new MyAdapter();
            lv_article_comments.setAdapter(adapter);

//                setLvContentParams(marrPoint.size(), lv_article_comments);
        } else {
            lv_article_comments.setVisibility(View.GONE);
        }

        //判断是否显示语音弹幕
        if (((NewsDetailAdd) pNewsDetail).isdoc == false) {
            rl_speech_view.setVisibility(View.GONE);
            tv_cutoff_line.setVisibility(View.GONE);

        } else {
            sv_article_comment.setUrl(((NewsDetailAdd) pNewsDetail).docUrl, true);
            sv_article_comment.setDuration(Integer.parseInt(((NewsDetailAdd) pNewsDetail).docTime));
        }

        //获取语音弹幕用户头像
        if (((NewsDetailAdd) pNewsDetail).docUserIcon != null && !"".equals(((NewsDetailAdd) pNewsDetail).docUserIcon)) {
            ImageLoaderHelper.dispalyImage(mContext, ((NewsDetailAdd) pNewsDetail).docUserIcon, iv_user_icon_article_comment);
        }

        mNewsDetailHeaderTitle.setFontSpacing(LetterSpacingTextView.BIGGEST);
        mNewsDetailHeaderTitle.setText(((NewsDetailAdd) pNewsDetail).title);
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
                        srcText = ((NewsDetailAdd) pNewsDetail).title;
                        mNewsDetailHeaderTitle.setText(mNewsDetailEdittext.getText());
                        mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                        mNewsDetailEditableLayout.setVisibility(View.GONE);
                        break;

                    case DESCRIPTION:
                        type = DESCRIPTION;
                        srcText = ((NewsDetailAdd) pNewsDetail).abs;
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

                            RelativeLayout rl_ss = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(tag);
                            LetterSpacingTextView tv = (LetterSpacingTextView) rl_ss.findViewById(R.id.lstv_para_content);

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
//                        modifyNewsContent(((NewsDetail)pNewsDetail), type, srcText, mNewsDetailEdittext.getText().toString(), EDIT_POSITION);

            }
        });

        tv_add_comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = SharedPreManager.getUser(mContext);
                if (user == null) {

                    final LoginModePopupWindow window = new LoginModePopupWindow(mContext, new UserLoginListener() {
                        @Override
                        public void userLogin(String platform, PlatformDb platformDb) {

                        }

                        @Override
                        public void userLogout() {

                        }
                    }, null);
                    window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);

                } else {
                    InputbarPopupWindow window = new InputbarPopupWindow((NewsDetailAty) mContext, points, sourceUrl, NewsDetailHeaderView.this, ARTICLE_FLAG);
                    window.setFocusable(true);
                    //防止虚拟软键盘被弹出菜单遮住、
                    window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, GlobalParams.screenHeight);
                }
            }
        });

        MnewsDetailButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                mNewsDetailEditableLayout.setVisibility(View.GONE);
            }
        });
        mNewsDetailHeaderTime.setText(((NewsDetailAdd) pNewsDetail).updateTime);
        mNewsDetailHeaderTemperature.setText(TextUtil.convertTemp(((NewsDetailAdd) pNewsDetail).root_class));

        if (((NewsDetailAdd) pNewsDetail).abs != null && !"".equals((((NewsDetailAdd) pNewsDetail).abs))) {
            mNewsDetailHeaderDesc.setFontSpacing(LetterSpacingTextView.BIG);
            String news_abs = BCConvert.bj2qj(((NewsDetailAdd) pNewsDetail).abs);
            mNewsDetailHeaderDesc.setText(news_abs);
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
        } else {
            ll_detail_des.setVisibility(View.GONE);
        }

    }

    private void inflateNewsDetailHeaderView(NewsDetail pNewsDetail) {

        String news_content = BCConvert.bj2qj(((NewsDetail) pNewsDetail).content);

        _Split = news_content.split("\n");
        StringBuilder _StringBuilder = new StringBuilder();
        for (int i = 0; i < _Split.length; i++) {
            if (_Split[i] != null && !"".equals(_Split[i])) {
                add_flag = false;
                //段落和评论布局
                final RelativeLayout rl_para = (RelativeLayout) View.inflate(mContext, R.layout.rl_content_and_comment, null);
                final LetterSpacingTextView lstv_para_content = (LetterSpacingTextView) rl_para.findViewById(R.id.lstv_para_content);
                lstv_para_content.setFontSpacing(LetterSpacingTextView.NORMALBIG);
                lstv_para_content.setLineSpacing(DensityUtil.dip2px(mContext, 24), 0);
                lstv_para_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                lstv_para_content.setTextColor(getResources().getColor(R.color.black));
                lstv_para_content.setText(_Split[i]);
                lstv_para_content.setTag(i);
                rl_para.setTag(i);

                final ImageView iv_none_point = (ImageView) rl_para.findViewById(R.id.iv_none_point);
                setViewBg(iv_none_point);

                final RelativeLayout rl_comment = (RelativeLayout) rl_para.findViewById(R.id.rl_comment);
                rl_comment.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                        RelativeLayout rl_aa = (RelativeLayout) rl_comment.getParent().getParent();
                        int para_index = (int) rl_aa.getTag();
                        for (int m = 0; m < points.size(); m++) {
                            if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                point_para.add(points.get(m));
                            }
                        }

                        CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, (int) rl_para.getTag(), PARA_FLAG,NewsDetailHeaderView.this);
                        window.setFocusable(true);
                        //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                          window.setBackgroundDrawable(new BitmapDrawable());
                        //防止虚拟软键盘被弹出菜单遮住、
                        window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                | Gravity.CENTER, 0, 0);
                    }
                });
//                        final SpeechView speechView = (SpeechView) rl_para.findViewById(R.id.speechView);
//                        speechView.setVisibility(View.GONE);
                if (paraPoint != null && paraPoint.size() > 0) {
                    for (int a = 0; a < paraPoint.size(); a++) {

                        NewsDetail.Point point = paraPoint.get(a);
                        if (!add_flag) {
                            if (TEXT_PARAGRAPH.equals(point.type)) {
//                                        speechView.setVisibility(View.GONE);

                                iv_none_point.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        User user = SharedPreManager.getUser(mContext);
                                        if (user == null) {

                                            final LoginModePopupWindow window = new LoginModePopupWindow(mContext, new UserLoginListener() {
                                                @Override
                                                public void userLogin(String platform, PlatformDb platformDb) {

                                                }

                                                @Override
                                                public void userLogout() {

                                                }
                                            }, null);
                                            window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                                                    | Gravity.CENTER, 0, 0);

                                        } else {

                                            ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                            RelativeLayout rl_aa = (RelativeLayout) iv_none_point.getParent().getParent();
                                            int para_index = (int) rl_aa.getTag();
                                            for (int m = 0; m < points.size(); m++) {
                                                if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                                    point_para.add(points.get(m));
                                                }
                                            }
                                            CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, para_index, PARA_FLAG,NewsDetailHeaderView.this);
                                            window.setFocusable(true);
                                            //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                              window.setBackgroundDrawable(new BitmapDrawable());
                                            //防止虚拟软键盘被弹出菜单遮住、
                                            window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                            window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                                    | Gravity.CENTER, 0, 0);
                                        }
                                    }
                                });


                                TextView tv_praise_count = (TextView) rl_para.findViewById(R.id.tv_praise_count);
                                RoundedImageView iv_user_icon = (RoundedImageView) rl_para.findViewById(R.id.iv_user_icon);
                                setViewBorder(iv_user_icon);
                                final ImageView iv_add_comment = (ImageView) rl_para.findViewById(R.id.iv_add_comment);
                                setViewBg(iv_add_comment);
                                iv_add_comment.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        User user = SharedPreManager.getUser(mContext);
                                        if (user == null) {

                                            final LoginModePopupWindow window = new LoginModePopupWindow(mContext, new UserLoginListener() {
                                                @Override
                                                public void userLogin(String platform, PlatformDb platformDb) {

                                                }

                                                @Override
                                                public void userLogout() {

                                                }
                                            }, null);
                                            window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                                                    | Gravity.CENTER, 0, 0);

                                        } else {

                                            InputbarPopupWindow window = new InputbarPopupWindow((NewsDetailAty) mContext, points, sourceUrl, NewsDetailHeaderView.this, ARTICLE_FLAG);
                                            window.setFocusable(true);
                                            //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                          window.setBackgroundDrawable(new BitmapDrawable());
                                            //防止虚拟软键盘被弹出菜单遮住、
                                            window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                                          window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                            window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                                    | Gravity.CENTER, 0, GlobalParams.screenHeight);
                                        }
                                    }
                                });
                                final TextView tv_comment_count = (TextView) rl_para.findViewById(R.id.tv_comment_count);

                                setTextViewBg(tv_comment_count);
                                tv_comment_count.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                    }
                                });


                                TextView tv_comment_content = (TextView) rl_para.findViewById(R.id.tv_comment_content);
                                setTextColor(tv_comment_content);

                                TextView tv_devider = (TextView) rl_para.findViewById(R.id.tv_devider);
                                if (i == _Split.length) {
                                    tv_devider.setVisibility(View.GONE);
                                }


                                if (i == Integer.parseInt(point.paragraphIndex)) {

                                    tv_praise_count.setText(point.up);
                                    if (point != null && point.comments_count != null) {
                                        tv_comment_count.setText(point.comments_count);
                                    } else {
                                        tv_comment_count.setText("1");
                                    }
                                    if (TEXT_PARAGRAPH.equals(point.type)) {
                                        tv_comment_content.setText(point.srcText);
                                    }

                                    if (point.userIcon != null && !"".equals(point.userIcon)) {
                                        ImageLoaderHelper.dispalyImage(mContext, point.userIcon, iv_user_icon);
                                    }

                                    add_flag = true;
                                    rl_comment.setVisibility(View.VISIBLE);
                                    iv_none_point.setVisibility(View.GONE);
                                } else {
                                    rl_comment.setVisibility(View.GONE);
                                    iv_none_point.setVisibility(View.VISIBLE);
                                }

                            }
//                                    else {
//                                        speechView.setVisibility(View.VISIBLE);
//                                        rl_comment.setVisibility(View.GONE);
//                                        iv_none_point.setVisibility(View.GONE);
//                                        speechView.setUrl(point.srcText, false);
//                                        speechView.setDuration(point.srcTextTime);
//                                    }
                        }
                    }

                } else {
                    rl_comment.setVisibility(View.GONE);
                    iv_none_point.setVisibility(View.VISIBLE);
                    iv_none_point.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            User user = SharedPreManager.getUser(mContext);
                            if (user == null) {

                                final LoginModePopupWindow window = new LoginModePopupWindow(mContext, new UserLoginListener() {
                                    @Override
                                    public void userLogin(String platform, PlatformDb platformDb) {

                                    }

                                    @Override
                                    public void userLogout() {

                                    }
                                }, null);
                                window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                                        | Gravity.CENTER, 0, 0);

                            } else {

                                ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                                RelativeLayout rl_aa = (RelativeLayout) iv_none_point.getParent().getParent();
                                int para_index = 0;
                                if (rl_aa.getTag() != null) {
                                    para_index = (int) rl_aa.getTag();
                                }
                                for (int m = 0; m < points.size(); m++) {
                                    if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                        point_para.add(points.get(m));
                                    }
                                }
                                CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, NewsDetailHeaderView.this, para_index, PARA_FLAG,NewsDetailHeaderView.this);
                                window.setFocusable(true);
                                //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
//                                              window.setBackgroundDrawable(new BitmapDrawable());
                                //防止虚拟软键盘被弹出菜单遮住、
                                window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                                window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                                        | Gravity.CENTER, 0, 0);
                            }
                        }
                    });
                }

                mNewsDetailHeaderContentParent.addView(rl_para);
            }
        }


    }

    private void initialNewsDetailHeaderView(final NewsDetail pNewsDetail) {

        if (TextUtils.isValidate(((NewsDetail) pNewsDetail).imgUrl)) {

            ViewGroup.LayoutParams layoutParams = mNewsDetailHeaderImg.getLayoutParams();
            layoutParams.width = GlobalParams.screenWidth;
            layoutParams.height = (int) (GlobalParams.screenHeight * 0.40);
            mNewsDetailHeaderImg.setLayoutParams(layoutParams);


            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mNewsDetailHeaderContentWrapper.getLayoutParams();
            int left = DensityUtil.dip2px(mContext, 8);
            int top = DensityUtil.dip2px(mContext, 10);
            params.setMargins(left, (int)(GlobalParams.screenHeight * 0.4) - top, left, 0);

            mNewsDetailHeaderContentWrapper.setLayoutParams(params);
            TextViewExtend tv = new TextViewExtend(mContext);

            ImageLoaderHelper.dispalyImage(mContext, ((NewsDetail) pNewsDetail).imgUrl, mNewsDetailHeaderImg, tv);

//            ImageManager.getInstance(mContext).DisplayImage(((NewsDetail) pNewsDetail).imgUrl, mNewsDetailHeaderImg, true, new DisplayImageListener() {
//                @Override
//                public void success(int width,int height) {
////                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mNewsDetailHeaderContentWrapper.getLayoutParams();
////                        int left = DensityUtil.dip2px(mContext, 8);
////                        int top = DensityUtil.dip2px(mContext, 10);
////                        params.setMargins(left, height - top, left, 0);
////
////                        mNewsDetailHeaderContentWrapper.setLayoutParams(params);
//                }
//
//                @Override
//                public void failed() {
//
//                }
//            });
        } else {
            mNewsDetailHeaderImg.setVisibility(GONE);
        }

        points = ((NewsDetail) pNewsDetail).point;
        this.sourceUrl = sourceUrl;

        //提取全文评论列表
        for (int i = 0; i < points.size(); i++) {
            NewsDetail.Point point = points.get(i);

            if (TEXT_DOC.equals(point.type) || SPEECH_DOC.equals(point.type)) {
                marrPoint.add(point);
            }
        }

        //提取段落评论列表
        for (int i = 0; i < points.size(); i++) {
            NewsDetail.Point point = points.get(i);

            if (TEXT_PARAGRAPH.equals(point.type) || SPEECH_PARAGRAPH.equals(point.type)) {
                paraPoint.add(point);
            }
        }

//            marrPoint = points;
        if (marrPoint.size() > 0) {
            adapter = new MyAdapter();
            lv_article_comments.setAdapter(adapter);

//                setLvContentParams(marrPoint.size(), lv_article_comments);
        } else {
            lv_article_comments.setVisibility(View.GONE);
        }

        //判断是否显示语音弹幕
        if (((NewsDetail) pNewsDetail).isdoc == false) {
            rl_speech_view.setVisibility(View.GONE);
            tv_cutoff_line.setVisibility(View.GONE);

        } else {
            sv_article_comment.setUrl(((NewsDetail) pNewsDetail).docUrl, true);
            sv_article_comment.setDuration(Integer.parseInt(((NewsDetail) pNewsDetail).docTime));
        }

        //获取语音弹幕用户头像
        if (((NewsDetail) pNewsDetail).docUserIcon != null && !"".equals(((NewsDetail) pNewsDetail).docUserIcon)) {
            ImageLoaderHelper.dispalyImage(mContext, ((NewsDetail) pNewsDetail).docUserIcon, iv_user_icon_article_comment);
        }

        mNewsDetailHeaderTitle.setFontSpacing(LetterSpacingTextView.BIGGEST);
        mNewsDetailHeaderTitle.setText(((NewsDetail) pNewsDetail).title);
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
                        srcText = ((NewsDetail) pNewsDetail).title;
                        mNewsDetailHeaderTitle.setText(mNewsDetailEdittext.getText());
                        mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                        mNewsDetailEditableLayout.setVisibility(View.GONE);
                        break;

                    case DESCRIPTION:
                        type = DESCRIPTION;
                        srcText = ((NewsDetail) pNewsDetail).abs;
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

                            RelativeLayout rl_ss = (RelativeLayout) mNewsDetailHeaderContentParent.getChildAt(tag);
                            LetterSpacingTextView tv = (LetterSpacingTextView) rl_ss.findViewById(R.id.lstv_para_content);

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
                modifyNewsContent(((NewsDetail) pNewsDetail), type, srcText, mNewsDetailEdittext.getText().toString(), EDIT_POSITION);

            }
        });

        tv_add_comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = SharedPreManager.getUser(mContext);
                if (user == null) {

                    final LoginModePopupWindow window = new LoginModePopupWindow(mContext, new UserLoginListener() {
                        @Override
                        public void userLogin(String platform, PlatformDb platformDb) {

                        }

                        @Override
                        public void userLogout() {

                        }
                    }, null);
                    window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);

                } else {
                    InputbarPopupWindow window = new InputbarPopupWindow((NewsDetailAty) mContext, points, sourceUrl, NewsDetailHeaderView.this, ARTICLE_FLAG);
                    window.setFocusable(true);
                    //防止虚拟软键盘被弹出菜单遮住、
                    window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    window.showAtLocation(((NewsDetailAty) (mContext)).getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, GlobalParams.screenHeight);
                }
            }
        });

        MnewsDetailButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mNewsDeatailTitleLayout.setVisibility(View.VISIBLE);
                mNewsDetailEditableLayout.setVisibility(View.GONE);
            }
        });
        mNewsDetailHeaderTime.setText(((NewsDetail) pNewsDetail).updateTime);
        mNewsDetailHeaderTemperature.setText(TextUtil.convertTemp(((NewsDetail) pNewsDetail).root_class));

        if (((NewsDetail) pNewsDetail).abs != null && !"".equals((((NewsDetail) pNewsDetail).abs))) {
            mNewsDetailHeaderDesc.setFontSpacing(LetterSpacingTextView.BIG);
            String news_abs = BCConvert.bj2qj(((NewsDetail) pNewsDetail).abs);
            mNewsDetailHeaderDesc.setText(news_abs);
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
        } else {
            ll_detail_des.setVisibility(View.GONE);
        }

    }

    private void setTextColor(TextView tv_comment_content) {

        switch (GlobalParams.currentCatePos) {

            case 0:
                tv_comment_content.setTextColor(new Color().parseColor("#ff1652"));

                break;

            case 1:

                tv_comment_content.setTextColor(new Color().parseColor("#ee6270"));

                break;

            case 2:

                tv_comment_content.setTextColor(new Color().parseColor("#6279a3"));

                break;

            case 3:

                tv_comment_content.setTextColor(new Color().parseColor("#f788a2"));

                break;

            case 4:

                tv_comment_content.setTextColor(new Color().parseColor("#37ccd9"));

                break;

            case 5:

                tv_comment_content.setTextColor(new Color().parseColor("#b56f40"));

                break;

            case 6:

                tv_comment_content.setTextColor(new Color().parseColor("#35e4c1"));

                break;

            case 8:

                tv_comment_content.setTextColor(new Color().parseColor("#f633a2"));

                break;

            case 9:

                tv_comment_content.setTextColor(new Color().parseColor("#35a6fb"));

                break;

            case 10:

                tv_comment_content.setTextColor(new Color().parseColor("#e2ab4b"));

                break;

            case 11:

                tv_comment_content.setTextColor(new Color().parseColor("#2bc972"));

                break;

            case 12:

                tv_comment_content.setTextColor(new Color().parseColor("#9153c6"));

                break;

            case 13:

                tv_comment_content.setTextColor(new Color().parseColor("#ffda59"));

                break;

            case 14:

                tv_comment_content.setTextColor(new Color().parseColor("#7174ff"));

                break;

            case 15:

                tv_comment_content.setTextColor(new Color().parseColor("#ff44b2"));

                break;

            default:

                tv_comment_content.setTextColor(new Color().parseColor("#ff44b2"));

                break;

        }


    }

    private void setViewBorder(RoundedImageView iv_user_icon) {

        switch (GlobalParams.currentCatePos) {

            case 0:
                iv_user_icon.setBorderColor(new Color().parseColor("#ff1652"));
                break;
            case 1:
                iv_user_icon.setBorderColor(new Color().parseColor("#ee6270"));
                break;
            case 2:
                iv_user_icon.setBorderColor(new Color().parseColor("#6279a3"));
                break;
            case 3:
                iv_user_icon.setBorderColor(new Color().parseColor("#f788a2"));
                break;
            case 4:
                iv_user_icon.setBorderColor(new Color().parseColor("#37ccd9"));
                break;
            case 5:
                iv_user_icon.setBorderColor(new Color().parseColor("#b56f40"));
                break;
            case 6:
                iv_user_icon.setBorderColor(new Color().parseColor("#35e4c1"));
                break;
            case 8:
                iv_user_icon.setBorderColor(new Color().parseColor("#f633a2"));
                break;
            case 9:
                iv_user_icon.setBorderColor(new Color().parseColor("#35a6fb"));
                break;
            case 10:
                iv_user_icon.setBorderColor(new Color().parseColor("#e2ab4b"));
                break;
            case 11:
                iv_user_icon.setBorderColor(new Color().parseColor("#2bc972"));
                break;
            case 12:
                iv_user_icon.setBorderColor(new Color().parseColor("#9153c6"));
                break;
            case 13:
                iv_user_icon.setBorderColor(new Color().parseColor("#ffda59"));
                break;
            case 14:
                iv_user_icon.setBorderColor(new Color().parseColor("#7174ff"));
                break;
            case 15:
                iv_user_icon.setBorderColor(new Color().parseColor("#ff44b2"));
                break;
            default:
                iv_user_icon.setBorderColor(new Color().parseColor("#ff44b2"));
                break;
        }

    }

    private void setViewBg(ImageView iv_none_point) {

        switch (GlobalParams.currentCatePos) {
            case 0:
                iv_none_point.setBackgroundResource(R.drawable.img_jinrijiaodian);
                break;
            case 1:
                iv_none_point.setBackgroundResource(R.drawable.img_remenzhuangti);
                break;
            case 2:
                iv_none_point.setBackgroundResource(R.drawable.img_zhongkouwei);
                break;
            case 3:
                iv_none_point.setBackgroundResource(R.drawable.img_guiquan);
                break;
            case 4:
                iv_none_point.setBackgroundResource(R.drawable.img_woxinle);
                break;
            case 5:
                iv_none_point.setBackgroundResource(R.drawable.img_takegroundass);
                break;
            case 6:
                iv_none_point.setBackgroundResource(R.drawable.img_zhinan);
                break;
            case 8:
                iv_none_point.setBackgroundResource(R.drawable.img_guwangjin);
                break;
            case 9:
                iv_none_point.setBackgroundResource(R.drawable.img_kexue);
                break;
            case 10:
                iv_none_point.setBackgroundResource(R.drawable.img_gaobige);
                break;
            case 11:
                iv_none_point.setBackgroundResource(R.drawable.img_zhuiju);
                break;
            case 12:
                iv_none_point.setBackgroundResource(R.drawable.img_yinchi);
                break;
            case 13:
                iv_none_point.setBackgroundResource(R.drawable.img_mengshi);
                break;
            case 14:
                iv_none_point.setBackgroundResource(R.drawable.img_xingren);
                break;
            case 15:
                iv_none_point.setBackgroundResource(R.drawable.img_googlenews);
                break;

            default:
                iv_none_point.setBackgroundResource(R.drawable.img_googlenews);
                break;
        }
    }

    private void setTextViewBg(TextView tv_comment_count) {

        switch (GlobalParams.currentCatePos) {
            case 0:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_jinrijiaodian);
                break;
            case 1:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_remenzhuan);
                break;
            case 2:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_zhongkouwei);
                break;
            case 3:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_guiquan);
                break;
            case 4:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_woxinle);
                break;
            case 5:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_takeground);
                break;
            case 6:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_zhinan);
                break;
            case 8:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_guwangjinlai);
                break;
            case 9:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_kexue);
                break;
            case 10:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_gaobige);
                break;
            case 11:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_zhuiju);
                break;
            case 12:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_yinchi);
                break;
            case 13:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_mengshi);
                break;
            case 14:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_xingren);
                break;
            case 15:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_google);
                break;
            default:
                tv_comment_count.setBackgroundResource(R.drawable.bg_comment_count_google);
                break;
        }
    }

    private void addTextviewDevider() {
        TextView tv_devider = new TextView(mContext);
        tv_devider.setBackgroundColor(new Color().parseColor("#dfe6e9"));

        LinearLayout.LayoutParams param_devider = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);
        tv_devider.setLayoutParams(param_devider);
        delta_position++;
        mNewsDetailHeaderContentParent.addView(tv_devider);
    }

    private void setLvContentParams(int size, ListView listView) {

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        switch (size) {
            case 1:
                params.height = 100;
                break;
            case 2:
                params.height = 250;

                break;
            case 3:
                params.height = 360;
                break;
            case 4:
                params.height = 520;
                break;
            case 5:
                params.height = 730;
                break;
            default:
                params.height = 730;
                break;
        }

        listView.setLayoutParams(params);

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
        User user = SharedPreManager.getUser(mContext);
        params.add(new BasicNameValuePair("userId", user == null ? "" : user.getUserId()));
        params.add(new BasicNameValuePair("userIcon", user == null ? "" : user.getUserIcon()));
        params.add(new BasicNameValuePair("userName", user == null ? "" : user.getUserName()));
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

    public void setDetailData(final Object pNewsDetail, String sourceUrl, HeaderVeiwPullUpListener listener, boolean isnew, CommentListener commentListener) {
        this.commentListener = commentListener;
        this.sourceUrl = sourceUrl;

        if (pNewsDetail == null) {
            return;
        }
        if (!isnew) {
            inflateDataToNewsheader(pNewsDetail, sourceUrl, listener, false);
            if (pNewsDetail != null) {
                ArrayList<NewsDetail.BaiDuBaiKe> pArrBaiDuBaiKe = ((NewsDetail) pNewsDetail).baike;
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


                if (((NewsDetail) pNewsDetail).imgWall != null) {
                    new Compt().putTask(new BackRunnable() {
                        @Override
                        public void run() {
                            for (Map<String, String> m : ((NewsDetail) pNewsDetail).imgWall) {
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
                            List<HashMap<String, String>> minor = new ArrayList<HashMap<String, String>>(((NewsDetail) pNewsDetail).imgWall);
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
                final ArrayList<NewsDetail.ZhiHu> pArrZhiHu = ((NewsDetail) pNewsDetail).zhihu;
                if (pArrZhiHu != null && pArrZhiHu.size() > 0) {
                    if (pArrZhiHu.size() > 3) {
                        for (int i = 0; i < 3; i++) {
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
                        iv_show_all_zhihu_views.setVisibility(View.GONE);
                    }
                } else {
                    mllZhiHu.setVisibility(GONE);
                }


                iv_show_all_zhihu_views.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mllZhiHuItem.removeAllViews();
                        iv_show_all_zhihu_views.setVisibility(View.GONE);
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
                        }
                    }
                });

                final ArrayList<ArrayList<String>> pArrDouBan = ((NewsDetail) pNewsDetail).douban;
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

                ArrayList<NewsDetail.Weibo> pArrWeibo = ((NewsDetail) pNewsDetail).weibo;
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
            if (pNewsDetail != null && ((NewsDetail) pNewsDetail).relate != null && ((NewsDetail) pNewsDetail).relate.size() > 0) {

            } else {
                mNewsDetailRelate.setVisibility(GONE);
            }
        } else {

            inflateDataToNewsheader(pNewsDetail, sourceUrl, listener, true);
            if (pNewsDetail != null) {
                ArrayList<NewsDetail.BaiDuBaiKe> pArrBaiDuBaiKe = ((NewsDetailAdd) pNewsDetail).baike;
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
                    if(mllBaiKe != null) {
                        mllBaiKe.setVisibility(GONE);
                    }
                }
                //图片墙的相关显示


                if (((NewsDetailAdd) pNewsDetail).imgWall != null) {
                    new Compt().putTask(new BackRunnable() {
                        @Override
                        public void run() {
                            for (Map<String, String> m : ((NewsDetailAdd) pNewsDetail).imgWall) {
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
                            List<HashMap<String, String>> minor = new ArrayList<HashMap<String, String>>(((NewsDetailAdd) pNewsDetail).imgWall);
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
                final ArrayList<NewsDetail.ZhiHu> pArrZhiHu = ((NewsDetailAdd) pNewsDetail).zhihu;
                if (pArrZhiHu != null && pArrZhiHu.size() > 0) {
                    if (pArrZhiHu.size() > 3) {
                        for (int i = 0; i < 3; i++) {
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
                        iv_show_all_zhihu_views.setVisibility(View.GONE);
                    }
                } else {
                    mllZhiHu.setVisibility(GONE);
                }


                iv_show_all_zhihu_views.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mllZhiHuItem.removeAllViews();
                        iv_show_all_zhihu_views.setVisibility(View.GONE);
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
                        }
                    }
                });

                final ArrayList<ArrayList<String>> pArrDouBan = ((NewsDetailAdd) pNewsDetail).douban;
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

                ArrayList<NewsDetail.Weibo> pArrWeibo = ((NewsDetailAdd) pNewsDetail).weibo;
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
            if (pNewsDetail != null && ((NewsDetailAdd) pNewsDetail).relate != null && ((NewsDetailAdd) pNewsDetail).relate.size() > 0) {

            } else {
                mNewsDetailRelate.setVisibility(GONE);
            }

        }
    }

    private List<HashMap<String, String>> composeMatch(List<HashMap<String, String>> resultList, List<HashMap<String, String>> minor) {
        getMatched(resultList, minor, "2-3-2");
        Log.i("", "--------------- " + resultList);
        return resultList;
    }

    private int getMatched(List<HashMap<String, String>> resultList, List<HashMap<String, String>> minor, String type) {
        String afterpart;
        int y = 0;
        int i = 0;
        if (type.length() == 0) {
            return 0;
        } else if (type.length() == 1) {
            i = Integer.parseInt(type);
            if (i == 1) {
                return 0;
            }
            type = "";
            afterpart = "";
        } else {
            String[] ts = type.split("-");
            i = Integer.parseInt(ts[0]);
            type = type.substring(2);
            afterpart = "-" + type;
            if (i == 1) {
                return getMatched(resultList, minor, type);
            }
        }
        if (i == 2) {
            y = get2Matched(resultList, minor, type);
        } else if (i == 3) {
            y = get3Matched(resultList, minor, type);
        }
        if (y == -1) {
            return getMatched(resultList, minor, (i - 1) + afterpart);
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

                scaledh1 = hhead * scaledw1 / whead;
                m1 = first;
                List<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>(source);
                for (HashMap<String, String> m : list2) {
                    try {
                        if (first.get("img").toString().equals(m.get("img").toString())) {
                            continue;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                    int w = Integer.parseInt(m.get("w").toString());
                    int h = Integer.parseInt(m.get("h").toString());


                    scaledh2 = h * scaledw2 / w;

                    if (Math.abs(scaledh1 - scaledh2) < stepcnst) {
                        if ((scaledh2 < scaledh1 && scaledh2 * ratio >= scaledh1) || (scaledh1 < scaledh2 && scaledh1 * ratio >= scaledh2)) {

                            int scaledh = Math.max(scaledh2, scaledh1);
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
                            ;
                            try {
                                return getMatched(maps, source, sq) + (th++);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            scaledh1 = 0;
                            scaledh2 = 0;
                            m2 = null;
                            m1 = null;
                        }
                    }

                }
            }
        }
        return -1;
    }

    private int get3Matched(List<HashMap<String, String>> maps, List source, String sq) {
        int th = 0;

        float ratio1 = 1.5f;
        int which = 0;
        float constW = 0;
        int stepcnst = 80;

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
                    try {
                        if (first.get("img").toString().equals(m.get("img").toString())) {
                            continue;
                        }
                    } catch (Exception e) {
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
                            try {
                                if (m != null && mm != null && m.get("img").toString().equals(mm.get("img").toString())) {
                                    continue;
                                }
                                if (m1 != null && mm != null && m1.get("img").toString().equals(mm.get("img").toString())) {
                                    continue;
                                }
                            } catch (Exception e) {
                                continue;
                            }
                            int w3 = Integer.parseInt(mm.get("w").toString());
                            int h3 = Integer.parseInt(mm.get("h").toString());
                            scaledh3 = h3 * scaledw3 / w3;

                            if (Math.abs(scaledh1 - scaledh2) < stepcnst && Math.abs(scaledh2 - scaledh3) < stepcnst) {
                                if (((scaledh2 - scaledh1 < 0 && scaledh2 * ratio1 >= scaledh1) || (scaledh1 - scaledh2 < 0 && scaledh1 * ratio1 >= scaledh2)) && ((scaledh2 - scaledh3 < 0 && scaledh2 * ratio1 >= scaledh3) || (scaledh3 - scaledh2 < 0 && scaledh3 * ratio1 >= scaledh2))) {


                                    int scaledh = Math.max(Math.max(scaledh2, scaledh1), Math.max(scaledh2, scaledh3));
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
                                    scaledh1 = 0;
                                    m1 = null;
                                    scaledh2 = 0;
                                    m2 = null;
                                    scaledh3 = 0;
                                    m3 = null;
                                    return getMatched(maps, source, sq) + th++;
                                } else {
                                    scaledh1 = 0;
                                    m1 = null;
                                    scaledh2 = 0;
                                    m2 = null;
                                    scaledh3 = 0;
                                    m3 = null;
                                }
                            }
                        }
                    }
                }
            }

        }
        return -1;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (marrPoint.size() == 0) {
                return 0;
            } else if (marrPoint.size() < 5) {
                return marrPoint.size();
            } else {
                return 5;
            }
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
                holder.rl_comment_content = (RelativeLayout) convertView.findViewById(R.id.rl_comment_content);
                holder.tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
                holder.mSpeechView = (SpeechView) convertView.findViewById(R.id.mSpeechView);
                holder.ivHeadIcon = (RoundedImageView) convertView.findViewById(R.id.iv_user_icon);
                holder.ivHeadIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final NewsDetail.Point point = marrPoint.get(position);
            if (point != null && point.userIcon != null && !point.userIcon.equals(""))
                ImageManager.getInstance(mContext).DisplayImage(point.userIcon, holder.ivHeadIcon, false, new DisplayImageListener() {
                    @Override
                    public void success(int width,int height) {

                    }

                    @Override
                    public void failed() {

                    }
                });
            else
                holder.ivHeadIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_comment_para));

            if (point != null) {
                holder.tvName.setText(point.userName);
            }


            if (point != null && point.up != null) {
                holder.tvPraiseCount.setText(point.up);
            } else {
                holder.tvPraiseCount.setText("0");
            }

            holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
            if (point != null) {
                if ("1".equals(point.isPraiseFlag)) {
                    holder.ivPraise.setBackgroundResource(R.drawable.bg_praised);
                } else {
                    holder.ivPraise.setBackgroundResource(R.drawable.bg_normal_praise);
                }

                if (point.type.equals(TEXT_DOC)) {
                    holder.tvContent.setText(point.srcText);
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.mSpeechView.setVisibility(View.GONE);
                } else {
                    holder.mSpeechView.setUrl(point.srcText, false);
                    holder.mSpeechView.setDuration(point.srcTextTime);
                    holder.mSpeechView.setVisibility(View.VISIBLE);
                    holder.tvContent.setVisibility(View.GONE);
                }
            }


            holder.rl_comment_content.setOnClickListener(new View.OnClickListener() {

                boolean flag = false;

                @Override
                public void onClick(View v) {

                    User user = SharedPreManager.getUser(mContext);
                    if (user == null) {

                        final LoginModePopupWindow window = new LoginModePopupWindow(mContext, new UserLoginListener() {
                            @Override
                            public void userLogin(String platform, PlatformDb platformDb) {

                            }

                            @Override
                            public void userLogout() {

                            }
                        }, null);
                        window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                                | Gravity.CENTER, 0, 0);

                    } else {

                        NewsDetail.Point point_item = marrPoint.get(position);
                        holder.ivPraise.setBackgroundResource(R.drawable.bg_praised);
                        int count = Integer.parseInt(holder.tvPraiseCount.getText().toString());
                        if (!flag && "0".equals(point_item.isPraiseFlag)) {
                            holder.tvPraiseCount.setText(count + 1 + "");
                            flag = true;
                        }

                        if (user != null) {
                            PraiseRequest.Praise(mContext, user.getUserId(), user.getPlatformType(), uuid, sourceUrl, point_item.commentId, new PraiseListener() {
                                @Override
                                public void success() {
                                }

                                @Override
                                public void failed() {

                                }
                            });
                        } else {
                            PraiseRequest.Praise(mContext, "", "", uuid, sourceUrl, point_item.commentId, new PraiseListener() {
                                @Override
                                public void success() {

                                }

                                @Override
                                public void failed() {

                                }
                            });
                        }
                    }
                }
            });
            return convertView;
        }
    }

    class Holder {
        RelativeLayout rl_comment_content;
        RoundedImageView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;
        SpeechView mSpeechView;
    }

    public interface CommentListener {
        void comment(boolean istrue);
    }

}
