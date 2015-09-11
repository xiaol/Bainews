package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.PraiseListener;
import com.news.yazhidao.listener.UploadCommentListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.request.PraiseRequest;
import com.news.yazhidao.net.request.UploadCommentRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.MediaPlayerManager;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.InputBar.InputBar;
import com.news.yazhidao.widget.InputBar.InputBarDelegate;
import com.news.yazhidao.widget.InputBar.InputBarType;

import java.util.ArrayList;

import cn.sharesdk.framework.PlatformDb;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class CommentPopupWindow extends PopupWindow implements InputBarDelegate, Handler.Callback {

    private ImageView mivClose;
    private View mMenuView;
    private Context m_pContext;
    //    private Context m_pContext;
    private ListView mlvComment;
    private DateAdapter mCommentAdapter;
    private InputBar mInputBar;
    private RelativeLayout mrlRecord;
    private Handler mHandler;
    private int PARA_FLAG = 0;
    private int ARTICLE_FLAG = 1;
    private int comment_flag;
    private double mRecordVolume;// 麦克风获取的音量值
    private TextViewExtend mtvVoiceTips, mtvVoiceTimes;
    private ImageView mivRecord;
    private ArrayList<NewsDetail.Point> marrPoints;
    private IUpdateCommentCount mIUpdateCommentCount;
    private IUpdatePraiseCount mIUpdatePraiseCount;
    private int miCount, mParagraphIndex;
    private String sourceUrl;
    private ArrayList<NewsDetail.Point> marrPoint;
    private int paraindex;
    private NewsDetail.Point point;
    private RelativeLayout rl_popup;
    private boolean praiseFlag = false;
    private int praiseCount;

    /**
     * 评论界面
     *
     * @param context
     * @param points
     * @param sourceUrl
     * @param updateCommentCount
     * @param paraindex
     * @param flag               判断是段落还是全文评论
     * @param updatePraiseCount
     */
    public CommentPopupWindow(Context context, ArrayList<NewsDetail.Point> points, String sourceUrl, IUpdateCommentCount updateCommentCount, int paraindex, int flag, IUpdatePraiseCount updatePraiseCount) {
        super(context);
        m_pContext = context;
        this.paraindex = paraindex;
        marrPoints = points;
        this.sourceUrl = sourceUrl;
        comment_flag = flag;
        if(paraindex == -1){
            if (!TextUtil.isListEmpty(marrPoints)){
                mParagraphIndex = Integer.valueOf(marrPoints.get(0).paragraphIndex);
            }else {
                mParagraphIndex = 0;
            }
        }else {
            mParagraphIndex = paraindex;
        }


        mIUpdateCommentCount = updateCommentCount;
        mIUpdatePraiseCount = updatePraiseCount;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_window_comment, null);
        mCommentAdapter = new DateAdapter(m_pContext);
        mHandler = new Handler(this);
        findHeadPortraitImageViews();
        loadData();

//        setViewBgColor(rl_popup);

    }

    private void findHeadPortraitImageViews() {

        //录音动画
        mrlRecord = (RelativeLayout) mMenuView.findViewById(R.id.voice_record_layout_wins);
        mtvVoiceTips = (TextViewExtend) mMenuView.findViewById(R.id.tv_voice_tips);
        mtvVoiceTimes = (TextViewExtend) mMenuView.findViewById(R.id.voice_record_time);
        mivRecord = (ImageView) mMenuView.findViewById(R.id.iv_record);
        //输入框
        mInputBar = (InputBar) mMenuView.findViewById(R.id.input_bar_view);
        mInputBar.setActivityAndHandler(m_pContext, mHandler);
        mInputBar.setDelegate(this);
        mivClose = (ImageView)
                mMenuView.findViewById(R.id.close_imageView);
        rl_popup = (RelativeLayout) mMenuView.findViewById(R.id.rl_popup);
        rl_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mlvComment = (ListView) mMenuView.findViewById(R.id.comment_list_view);
        mlvComment.setAdapter(mCommentAdapter);
        mCommentAdapter.setData(marrPoints);
        mCommentAdapter.notifyDataSetChanged();
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        mMenuView.setOnTouchListener(new OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    dismiss();
//                }
//                return true;
//            }
//        });
        mivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //退出评论页面时，关闭正在播放的语音评论
        MediaPlayerManager.getInstance().stop();
        if (mIUpdateCommentCount != null){
            mIUpdateCommentCount.updateCommentCount(miCount, mParagraphIndex, point, comment_flag, praiseFlag);
        }
        if (mIUpdatePraiseCount != null){
            mIUpdatePraiseCount.updatePraise(praiseCount,mParagraphIndex,marrPoint);
        }
    }

    private void loadData() {

    }

    @Override
    public void submitThisMessage(InputBarType argType, String argContent, int speechDuration) {
        mrlRecord.setVisibility(View.INVISIBLE);
        if (marrPoints != null && marrPoints.size() > 0) {
            NewsDetail.Point point = marrPoints.get(0);
        }
        NewsDetail.Point newPoint = new NewsDetail.Point();
        String type;
        if (argType == InputBarType.eRecord) {
            type = UploadCommentRequest.SPEECH_PARAGRAPH;
            newPoint.srcText = argContent;
            newPoint.srcTextTime = speechDuration / 1000;
        } else {
            type = UploadCommentRequest.TEXT_PARAGRAPH;
            newPoint.srcText = argContent;
        }
        User user = SharedPreManager.getUser(m_pContext);
        newPoint.userIcon = user.getUserIcon();
        newPoint.userName = user.getUserName();
        newPoint.type = type;
        marrPoints.add(newPoint);
        point = newPoint;
        mCommentAdapter.setData(marrPoints);
        mCommentAdapter.notifyDataSetChanged();
        Logger.i("jigang", type + "----url==" + argContent + "-------duration===" + speechDuration);
        UploadCommentRequest.uploadComment(m_pContext, sourceUrl, argContent, paraindex + "", type, speechDuration, new UploadCommentListener() {
            @Override
            public void success() {
                miCount += 1;
                Log.i("tag", "111");
                ToastUtil.toastLong("发表成功");
            }

            @Override
            public void failed() {
                Log.i("tag", "222");
            }
        });
        Log.i("tag", argContent);
    }

    @Override
    public void recordDidBegin(InputBar argView) {
        mtvVoiceTips.setText("手指上滑,取消发送");
        mtvVoiceTips.setTextColor(Color.WHITE);
        mrlRecord.setVisibility(View.VISIBLE);
    }

    @Override
    public void recordDidCancel(InputBar argView) {
        mrlRecord.setVisibility(View.INVISIBLE);
    }

    @Override
    public void cancelVoiceTipsType1() {
        mtvVoiceTips.setText("松开手指，取消发送");
        mtvVoiceTips.setTextColor(Color.RED);
    }

    @Override
    public void cancelVoiceTipsType2() {
        mtvVoiceTips.setText("手指上滑,取消发送");
        mtvVoiceTips.setTextColor(Color.WHITE);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case InputBar.RECORD_NO:// 不在录音
                if (mInputBar.getRecordState() == InputBar.RECORD_ING) {
                    // 停止录音
                    mInputBar.finishRecord();
                    // 初始化录音音量
                    mRecordVolume = 0;
                    // 话筒隐藏
                    mrlRecord.setVisibility(View.INVISIBLE);
                    // 录音达到最大时长，结束录音并发送语音
                    ToastUtil.toastShort("录音时间不能超过30秒");
                }

                break;
            case InputBar.RECORD_ING:// 正在录音
                // 显示录音时间
                mtvVoiceTimes.setText((int) mInputBar.getCurDuration() + "/" + InputBar.MAX_TIME + "″");
                // 音量大小的动画
                mRecordVolume = mInputBar.getRecordVolume();
                if (mInputBar.isLong()) {
                    mivRecord.setBackgroundResource(R.drawable.voice_cancle);
                } else if (mRecordVolume < 500.0) {
                    mivRecord.setBackgroundResource(R.drawable.voice_1);
                } else if (mRecordVolume >= 500.0 && mRecordVolume < 2000) {
                    mivRecord.setBackgroundResource(R.drawable.voice_2);
                } else if (mRecordVolume >= 2000.0 && mRecordVolume < 8000) {
                    mivRecord.setBackgroundResource(R.drawable.voice_3);
                } else if (mRecordVolume >= 8000.0) {
                    mivRecord.setBackgroundResource(R.drawable.voice_4);
                }
                break;
        }
        return false;
    }


    private void setViewBgColor(RelativeLayout tv_comment_content) {

        switch (GlobalParams.currentCatePos) {

            case 0:
                tv_comment_content.setBackgroundColor(new Color().parseColor("#ff1652"));

                break;

            case 1:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#ee6270"));

                break;

            case 2:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#6279a3"));

                break;

            case 3:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#f788a2"));

                break;

            case 4:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#37ccd9"));

                break;

            case 5:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#b56f40"));

                break;

            case 6:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#35e4c1"));

                break;

            case 8:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#f633a2"));

                break;

            case 9:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#35a6fb"));

                break;

            case 10:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#e2ab4b"));

                break;

            case 11:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#2bc972"));

                break;

            case 12:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#9153c6"));

                break;

            case 13:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#ffda59"));

                break;

            case 14:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#7174ff"));

                break;

            case 15:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#ff44b2"));

                break;

            default:

                tv_comment_content.setBackgroundColor(new Color().parseColor("#ff44b2"));

                break;

        }


    }

    class DateAdapter extends BaseAdapter {

        Context mContext;


        DateAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<NewsDetail.Point> arrPoint) {
            marrPoint = arrPoint;
        }

        @Override
        public int getCount() {
            return marrPoint == null ? 0 : marrPoint.size();
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
                holder.tvContent = (TextViewExtend) convertView.findViewById(R.id.tv_comment_content);
                holder.mSpeechView = (SpeechView) convertView.findViewById(R.id.mSpeechView);
                holder.ivHeadIcon = (SimpleDraweeView) convertView.findViewById(R.id.iv_user_icon);
                holder.tvName = (TextViewExtend) convertView.findViewById(R.id.tv_user_name);
                holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);
                holder.tvPraiseCount = (TextViewExtend) convertView.findViewById(R.id.tv_praise_count);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final NewsDetail.Point point = marrPoint.get(position);
            if (point.userIcon != null && !point.userIcon.equals(""))
                holder.ivHeadIcon.setImageURI(Uri.parse(point.userIcon));
            holder.tvName.setText(point.userName);
            if (point.up != null) {
                holder.tvPraiseCount.setText(point.up);
            } else {
                holder.tvPraiseCount.setText("0");
            }

            if (position == 0) {
                praiseCount = Integer.parseInt(holder.tvPraiseCount.getText().toString());
            }

            holder.ivPraise = (ImageView) convertView.findViewById(R.id.iv_praise);


            if ("1".equals(point.isPraiseFlag)) {
                holder.ivPraise.setImageResource(R.drawable.bg_praised);
            } else {
                holder.ivPraise.setImageResource(R.drawable.bg_normal_praise);
            }


            if (point.type.equals("text_paragraph")) {
                holder.tvContent.setText(point.srcText);
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.mSpeechView.setVisibility(View.GONE);
            } else if (point.type.equals("text_doc")) {
                holder.tvContent.setText(point.srcText);
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.mSpeechView.setVisibility(View.GONE);
            } else {
                Logger.i("jigang", point.srcTextTime + "--adapter--" + point.srcText);
                holder.mSpeechView.setUrl(point.srcText, false);
                holder.mSpeechView.setDuration(point.srcTextTime);
                holder.mSpeechView.setVisibility(View.VISIBLE);
                holder.tvContent.setVisibility(View.GONE);
            }
            holder.ivPraise.setOnClickListener(new View.OnClickListener() {
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
                        if ("1".equals(point.isPraiseFlag)) {
                            ToastUtil.toastLong("您已经点过赞了");
                        } else {
                            holder.ivPraise.setImageResource(R.drawable.bg_praised);
                            point.isPraiseFlag = "1";
                            int count = 0;
                            if (holder.tvPraiseCount != null && holder.tvPraiseCount.getText() != null && !"".equals(holder.tvPraiseCount.getText())) {
                                count = Integer.parseInt(holder.tvPraiseCount.getText().toString());
                            }
                            holder.tvPraiseCount.setText(count + 1 + "");

                            if (position == 0) {
                                praiseCount = praiseCount + 1;
                            }

                            if (point.up != null) {
                                point.up = count + 1 + "";
                            } else {
                                point.up = "1";
                            }

                            String uuid = DeviceInfoUtil.getUUID();

                            NewsDetail.Point point_item = marrPoint.get(position);
                            if (user != null) {
                                PraiseRequest.Praise(mContext, user.getUserId(), user.getPlatformType(), uuid, sourceUrl, point_item.commentId, new PraiseListener() {
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
                }
            });
            return convertView;
        }
    }


    class Holder {
        SimpleDraweeView ivHeadIcon;
        TextViewExtend tvName;
        TextViewExtend tvContent;
        TextViewExtend tvPraiseCount;
        ImageView ivPraise;
        SpeechView mSpeechView;
    }

    public interface IUpdateCommentCount {
        void updateCommentCount(int count, int paragraphIndex, NewsDetail.Point point, int flag, boolean isPraiseFlag);
    }


    public interface IUpdatePraiseCount {
        void updatePraise(int count, int paragraphIndex, ArrayList<NewsDetail.Point> marrPoint);
    }
}
