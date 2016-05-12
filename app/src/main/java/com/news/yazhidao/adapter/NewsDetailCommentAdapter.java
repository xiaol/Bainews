package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.adapter.abslistview.MultiItemCommonAdapter;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.NewsDetailCommentItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.w3c.dom.Text;

import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiao on 2016/5/9.
 */
public class NewsDetailCommentAdapter extends CommonAdapter<NewsDetailCommentItem>{
    private int daoHeight;
    private TextView clip_pic;
    public static final String KEY_NEWS_FEED = "key_news_feed";
    public void setClip_pic(TextView clip_pic) {
        this.clip_pic = clip_pic;
    }

    private ArrayList<NewsDetailCommentItem> mDatas = new  ArrayList<NewsDetailCommentItem>();
    private Context mContext;
    private NewsDetailCommentDao newsDetailCommentDao = null;

    public NewsDetailCommentDao getNewsDetailCommentDao() {
        return newsDetailCommentDao;
    }

    public void setNewsDetailCommentDao(NewsDetailCommentDao newsDetailCommentDao) {
        this.newsDetailCommentDao = newsDetailCommentDao;
    }

    public NewsDetailCommentAdapter(int layoutId, Context context, ArrayList<NewsDetailCommentItem> datas){
        super(layoutId,context,datas);
        mDatas = datas;
        mContext = context;
    }
    @Override
    public void convert(CommonViewHolder holder,final NewsDetailCommentItem newsDetailCommentItem, int positon) {
        TextView pub_time = holder.getView(R.id.pub_time);
        pub_time.setText(convertTime(newsDetailCommentItem.getCreate_time()));
        TextView comment_content = holder.getView(R.id.comment_item_comment_content);
        String string = newsDetailCommentItem.getContent();
        comment_content.setText(string);
        TextView original = holder.getView(R.id.original);
        CharSequence originalStr = Html.fromHtml("<b>【原文】</b>"+newsDetailCommentItem.getOriginal());
        original.setText(originalStr.toString());
        ImageButton love_imagebt = holder.getView(R.id.love_imagebt);
        if(newsDetailCommentItem.isPraise()){
            love_imagebt.setImageResource(R.drawable.list_icon_gif_nor_icon_heart_selected);
        }else {
            love_imagebt.setImageResource(R.drawable.list_icon_gif_nor_icon_heart_nor);
        }
        int love_num = newsDetailCommentItem.getLove();
        final TextView love_count = holder.getView(R.id.love_count);
        if(love_num > 0){
            love_count.setVisibility(View.VISIBLE);
            love_count.setText(love_num+"");
        }else{
            love_count.setVisibility(View.GONE);
        }

        ImageView del_icon = holder.getView(R.id.del_icon);
        original.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NewsDetailAty2.class);
                NewsFeed newsFeed = newsDetailCommentItem.getNewsFeed();
                intent.putExtra(KEY_NEWS_FEED, newsFeed);
                mContext.startActivity(intent);
            }
        });
        del_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopupWindow(v,newsDetailCommentItem);
//                Toast.makeText(mContext, "删除该条评论",
//                        Toast.LENGTH_SHORT).show();
//                newsDetailCommentDao.delete(newsDetailCommentItem);
//                mDatas.remove(newsDetailCommentItem);
//                notifyDataSetChanged();
            }
        });
        love_imagebt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                newsDetailCommentItem.setPraise(!newsDetailCommentItem.isPraise());
                if(newsDetailCommentItem.isPraise()){
                    newsDetailCommentItem.setLove(newsDetailCommentItem.getLove()+1);
                    ((ImageButton)view).setImageResource(R.drawable.list_icon_gif_nor_icon_heart_selected);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(ObjectAnimator.ofFloat(view,
                            "scaleX", 1, 2, 1), ObjectAnimator
                            .ofFloat(view, "scaleY", 1, 2, 1));
                    set.setDuration(1 * 1000).start();
                    clip_pic.setVisibility(View.VISIBLE);
                    clip_pic.layout(
                            location[0] + view.getMeasuredWidth()
                                    / 2,
                            location[1] - daoHeight,
                            location[0]
                                    + view.getMeasuredWidth()
                                    / 2
                                    + clip_pic
                                    .getMeasuredWidth(),
                            location[1]
                                    + clip_pic
                                    .getMeasuredHeight()
                                    - daoHeight);

                    AnimatorSet set1 = new AnimatorSet();
                    set1.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    set1.playTogether(ObjectAnimator.ofFloat(
                            clip_pic, "translationY", 0, -100),
                            ObjectAnimator.ofFloat(clip_pic,
                                    "alpha", 1, 0));
                    set1.setInterpolator(new DecelerateInterpolator());
                    set1.setDuration(1 * 1000).start();


                    set1 = null;



                }else {
                    newsDetailCommentItem.setLove(newsDetailCommentItem.getLove()-1);

                    ((ImageButton)view).setImageResource(R.drawable.list_icon_gif_nor_icon_heart_nor);
                    notifyDataSetChanged();
                }


                newsDetailCommentDao.update(newsDetailCommentItem);


            }
        });
    }


    private void showPopupWindow(View view,final NewsDetailCommentItem newsDetailCommentItem){
        backgroundAlpha(0.5f);
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.popup, null);
        // 设置按钮的点击事件
        Button confirm = (Button) contentView.findViewById(R.id.popup_confirm);
        Button cancle = (Button) contentView.findViewById(R.id.popup_cancle);




        final PopupWindow popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.center_bg_notation));
        // 设置好参数之后再show
        //popupWindow.showAsDropDown(view);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                backgroundAlpha(1.0f);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                backgroundAlpha(1.0f);
                Toast.makeText(mContext, "删除该条评论",
                        Toast.LENGTH_SHORT).show();
                newsDetailCommentDao.delete(newsDetailCommentItem);
                mDatas.remove(newsDetailCommentItem);
                notifyDataSetChanged();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundAlpha(1.0f);
                popupWindow.dismiss();
            }
        });

    }




    private static String convertTime(long oldTime){
        String temp;
        long timeGap = System.currentTimeMillis()-oldTime;
        DateFormat sdf = new SimpleDateFormat("MM月dd日");
        Date old = new Date(oldTime);

        if(timeGap<60000){//一分钟
            temp = "刚刚";
        }else if(timeGap<60*60000){//一小时
            temp = (timeGap/60000)+"分钟前";
        }else if(timeGap<24*60*60000){
            temp = (timeGap/(60*60000))+"小时前";
        }else if(timeGap<yesterday(1)){
            temp = "昨天";
        }else if(timeGap<yesterday(2)){
            temp = "前天";
        }else{
            temp = sdf.format(old);
        }


        return temp;
    }

    private static long yesterday(int i){
        long l = 0;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -i);
        String yesterday = new SimpleDateFormat( "yyyy-MM-dd ").format(calendar.getTime());
        try {
            date = sdf.parse(yesterday);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        l = System.currentTimeMillis() - date.getTime();

        return l;
    }

    public void backgroundAlpha(float bgAlpha){
        WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha;
        ((Activity)mContext).getWindow().setAttributes(lp);
    }


    public int getDaoHeight() {
        return daoHeight;
    }

    public void setDaoHeight(int daoHeight) {
        this.daoHeight = daoHeight;
    }

}
