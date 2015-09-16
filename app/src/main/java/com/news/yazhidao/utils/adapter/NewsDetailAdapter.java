package com.news.yazhidao.utils.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.pages.NewsDetailAty;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.helper.ImageLoaderHelper;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.CommentPopupWindow;
import com.news.yazhidao.widget.InputbarPopupWindow;
import com.news.yazhidao.widget.LetterSpacingTextView;
import com.news.yazhidao.widget.LoginModePopupWindow;
import com.news.yazhidao.widget.NewsDetailHeaderView;
import com.news.yazhidao.widget.RoundedImageView;

import java.util.ArrayList;

import cn.sharesdk.framework.PlatformDb;

/**
 * Created by Berkeley on 8/17/15.
 */
public class NewsDetailAdapter extends BaseAdapter {

    //文本类型段落评论
    public static final String TEXT_PARAGRAPH = "text_paragraph";
    //文本类型全文评论
    public static final String TEXT_DOC = "text_doc";
    //语音类型段落评论
    public static final String SPEECH_PARAGRAPH = "speech_paragraph";
    //语音类型全文评论
    public static final String SPEECH_DOC = "speech_doc";

    private String[] newsList;
    private Context mContext;
    private ArrayList<NewsDetail.Point> points;
    private String sourceUrl;
    private NewsDetailHeaderView view;
    private ViewHolder holder = null;
    private int PARA_FLAG = 0;
    private int ARTICLE_FLAG = 1;

    public NewsDetailAdapter(String[] newsDetail,Context context,ArrayList<NewsDetail.Point> points) {
        newsList = newsDetail;
        mContext = context;
        this.points = points;
    }

    public void setSourceUrl(String url){
        this.sourceUrl = url;
    }

    public void setHeaderView(NewsDetailHeaderView view){
        this.view = view;
    }

    @Override
    public int getCount() {
        return newsList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      boolean  add_flag = false;
        if(holder == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.rl_content_and_comment, null);

            //段落和评论布局
            holder.rl_para = (RelativeLayout) convertView.findViewById(R.id.rl_para);
            holder.rl_comment_content = (RelativeLayout) convertView.findViewById(R.id.rl_comment_content);
            holder.lstv_para_content = (LetterSpacingTextView) holder.rl_comment_content.findViewById(R.id.lstv_para_content);
            holder.tv_praise_count = (TextView) holder.rl_comment_content.findViewById(R.id.tv_praise_count);
            holder.iv_user_icon = (RoundedImageView) holder.rl_comment_content.findViewById(R.id.iv_user_icon);
            holder.rl_comment = (RelativeLayout) holder.rl_comment_content.findViewById(R.id.rl_comment);
            holder.iv_add_comment = (ImageView) holder.rl_comment_content.findViewById(R.id.iv_add_comment);
            holder.iv_none_point = (ImageView) holder.rl_comment_content.findViewById(R.id.iv_none_point);
            holder.tv_comment_count = (TextView) holder.rl_comment_content.findViewById(R.id.tv_comment_count);
            holder.tv_comment_content = (TextView) holder.rl_comment_content.findViewById(R.id.tv_comment_content);
            holder.tv_devider = (TextView) convertView.findViewById(R.id.tv_devider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            if (newsList[position] != null && !"".equals(newsList[position])) {

                setViewBg(holder.iv_none_point);
                holder.lstv_para_content.setFontSpacing(1);
                holder.lstv_para_content.setLineSpacing(DensityUtil.dip2px(mContext, 24), 0);
                holder.lstv_para_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                holder.lstv_para_content.setTextColor(mContext.getResources().getColor(R.color.black));
                holder.lstv_para_content.setText(newsList[position]);
                holder.lstv_para_content.setTag(position);
                holder.rl_para.setTag(position);

                holder.rl_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<NewsDetail.Point> point_para = new ArrayList<NewsDetail.Point>();
                        RelativeLayout rl_aa = (RelativeLayout) holder.rl_comment.getParent().getParent();
                        int para_index = (int) rl_aa.getTag();
                        for (int m = 0; m < points.size(); m++) {
                            if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                point_para.add(points.get(m));
                            }
                        }

                        CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, view, (int) holder.rl_para.getTag(), view);
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
                if (points != null && points.size() > 0) {
                    for (int a = 0; a < points.size(); a++) {

                        NewsDetail.Point point = points.get(a);
                        if (!add_flag) {
                            if (TEXT_PARAGRAPH.equals(point.type)) {
//                                        speechView.setVisibility(View.GONE);

                                holder.iv_none_point.setOnClickListener(new View.OnClickListener() {
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
                                            RelativeLayout rl_aa = (RelativeLayout) holder.iv_none_point.getParent().getParent();
                                            int para_index = (int) rl_aa.getTag();
                                            for (int m = 0; m < points.size(); m++) {
                                                if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                                    point_para.add(points.get(m));
                                                }
                                            }
                                            CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, view, para_index,view);
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

                                setViewBorder(holder.iv_user_icon);
                                setViewBg(holder.iv_add_comment);
                                holder.iv_add_comment.setOnClickListener(new View.OnClickListener() {
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

                                            InputbarPopupWindow window = new InputbarPopupWindow((NewsDetailAty) mContext, points, sourceUrl, view, ARTICLE_FLAG);
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


                                setTextViewBg(holder.tv_comment_count);
                                holder.tv_comment_count.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                    }
                                });



                                setTextColor(holder.tv_comment_content);
                                if (position == newsList.length) {
                                    holder.tv_devider.setVisibility(View.GONE);
                                }


                                if (position == Integer.parseInt(point.paragraphIndex)) {

                                    holder.tv_praise_count.setText(point.up);
                                    if (point != null && point.comments_count != null) {
                                        holder.tv_comment_count.setText(point.comments_count);
                                    } else {
                                        holder.tv_comment_count.setText("1");
                                    }
                                    if (TEXT_PARAGRAPH.equals(point.type)) {
                                        holder.tv_comment_content.setText(point.srcText);
                                    }

                                    if (point.userIcon != null && !"".equals(point.userIcon)) {
                                        ImageLoaderHelper.dispalyImage(mContext, point.userIcon, holder.iv_user_icon);
                                    }

                                    add_flag = true;
                                    holder.rl_comment.setVisibility(View.VISIBLE);
                                    holder.iv_none_point.setVisibility(View.GONE);
                                } else {
                                    holder.rl_comment.setVisibility(View.GONE);
                                    holder.iv_none_point.setVisibility(View.VISIBLE);
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
                    holder.rl_comment.setVisibility(View.GONE);
                    holder.iv_none_point.setVisibility(View.VISIBLE);
                    holder.iv_none_point.setOnClickListener(new View.OnClickListener() {
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
                                RelativeLayout rl_aa = (RelativeLayout) holder.iv_none_point.getParent().getParent();
                                int para_index = 0;
                                if (rl_aa.getTag() != null) {
                                    para_index = (int) rl_aa.getTag();
                                }
                                for (int m = 0; m < points.size(); m++) {
                                    if (points.get(m).paragraphIndex != null && para_index == Integer.parseInt(points.get(m).paragraphIndex)) {
                                        point_para.add(points.get(m));
                                    }
                                }
                                CommentPopupWindow window = new CommentPopupWindow((NewsDetailAty) mContext, point_para, sourceUrl, view, para_index, view);
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

            }

        return convertView;
    }


    class ViewHolder{
        RelativeLayout rl_para;
        RelativeLayout rl_comment_content;
        LetterSpacingTextView lstv_para_content;
        ImageView iv_none_point;
        RelativeLayout rl_comment;
        TextView tv_praise_count;
        RoundedImageView iv_user_icon;
        ImageView iv_add_comment;
        TextView tv_comment_count;
        TextView tv_comment_content;
        TextView tv_devider;
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
}
