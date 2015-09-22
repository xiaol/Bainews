package com.news.yazhidao.widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;

import com.news.yazhidao.R;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.helper.ShareSdkHelper;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class SharePopupWindow extends PopupWindow {

    private TextViewExtend mtvClose;
    private View mMenuView;
    private Context m_pContext;
    private GridView mgvShare;
    private ShareAdapter mShareAdapter;
    private TypedArray mTypedArray;
    private String[] mShareName;
    private String[] marrSharePlatform;
    private String mstrTitle, mstrUrl;
    private ShareDismiss mShareDismiss;

    public SharePopupWindow(Context context, ShareDismiss shareDismiss) {
        super(context);
        m_pContext = context;
        mShareDismiss = shareDismiss;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_window_share, null);
        mShareAdapter = new ShareAdapter(m_pContext);
        mShareName = m_pContext.getResources().getStringArray(R.array.share_list_name);
        mTypedArray = m_pContext.getResources().obtainTypedArray(R.array.share_list_image);
        marrSharePlatform = new String[]{WechatMoments.NAME, Wechat.NAME, SinaWeibo.NAME, QQ.NAME, TencentWeibo.NAME};
        findHeadPortraitImageViews();
    }

    private void findHeadPortraitImageViews() {
        mtvClose = (TextViewExtend) mMenuView.findViewById(R.id.close_imageView);
        mgvShare = (GridView) mMenuView.findViewById(R.id.share_gridView);
        mgvShare.setAdapter(mShareAdapter);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(800);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popupWindowAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    dismiss();
                }
                return true;
            }
        });
        mtvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setTitleAndUrl(String title, String url) {
        mstrTitle = title;
        mstrUrl = url;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mShareDismiss.shareDismiss();
    }

    class ShareAdapter extends BaseAdapter {

        Context mContext;

        ShareAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mShareName == null ? 0 : mShareName.length;
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_share, null, false);
                holder.tvShare = (TextViewExtend) convertView.findViewById(R.id.share_textView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final String strShareName = mShareName[position];
            holder.tvShare.setText(strShareName);
            Drawable drawable = mContext.getResources().getDrawable(mTypedArray.getResourceId(position, 0));
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvShare.setCompoundDrawables(null, drawable, null, null);
            holder.tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("短信".equals(strShareName)) {
                        Uri smsToUri = Uri.parse("smsto:");
                        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                        intent.putExtra("sms_body", mstrTitle + mstrUrl);
                        mContext.startActivity(intent);
                    } else if ("邮件".equals(strShareName)) {
//                        String[] email = {"3802**92@qq.com"}; // 需要注意，email必须以数组形式传入
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822"); // 设置邮件格式
//                        intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
//                        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
                        intent.putExtra(Intent.EXTRA_SUBJECT, mstrTitle); // 主题
                        intent.putExtra(Intent.EXTRA_TEXT, mstrUrl); // 正文
                        mContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
                    } else if ("转发链接".equals(strShareName)) {
                        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setPrimaryClip(ClipData.newPlainText(null, mstrUrl));
                        ToastUtil.toastShort("复制成功");
//                        Log.i("eva",cmb.getText().toString().trim());
                    } else {
                        ShareSdkHelper.ShareToPlatformByNewsDetail(mContext, marrSharePlatform[position], mstrTitle, mstrUrl);
                    }
                }
            });
            return convertView;
        }
    }


    class Holder {
        TextViewExtend tvShare;
    }

    public interface ShareDismiss {
        void shareDismiss();
    }
}
