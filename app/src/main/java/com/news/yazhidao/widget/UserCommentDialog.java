package com.news.yazhidao.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UploadCommentListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.request.UploadCommentRequest;
import com.news.yazhidao.pages.LoginModeFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;

import cn.sharesdk.framework.PlatformDb;


/**
 * Created by fengjigang on 16/2/25.
 */
@SuppressLint("ValidFragment")
public class UserCommentDialog extends DialogFragment implements View.OnClickListener {
    private  CommentPopupWindow.IUpdateCommentCount mIUpdateCommentCount;
    private Context mContext;
    private EditText mCommentContent;
    private TextView mCommentCommit;
    private String mUserCommentMsg;
    public UserCommentDialog(){}
    public UserCommentDialog(CommentPopupWindow.IUpdateCommentCount updateCommentCount) {
        this.mIUpdateCommentCount = updateCommentCount;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.UserComment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
        View rootView = inflater.inflate(R.layout.dialog_user_comment, null);
        rootView.setMinimumWidth(10000);
        rootView.setMinimumHeight(DensityUtil.dip2px(getActivity(),150));
        mCommentContent = (EditText) rootView.findViewById(R.id.mCommentContent);
        mCommentCommit = (TextView) rootView.findViewById(R.id.mCommentCommit);
        mCommentContent.addTextChangedListener(new CommentTextWatcher());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inManager = (InputMethodManager) mCommentContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 50);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        User user = SharedPreManager.getUser(mContext);
        if (user == null) {
            LoginModeFgt loginModeFgt = new LoginModeFgt(mContext, new UserLoginListener() {
                @Override
                public void userLogin(String platform, PlatformDb platformDb) {
                    submitComment();
                }

                @Override
                public void userLogout() {

                }
            }, null);
            loginModeFgt.show(((FragmentActivity) mContext).getSupportFragmentManager(), "loginModeFgt");
        } else {
            submitComment();
        }
    }
    private void submitComment(){
        final NewsDetailAdd.Point newPoint = new NewsDetailAdd.Point();
        User user = SharedPreManager.getUser(mContext);
        newPoint.userIcon = user.getUserIcon();
        newPoint.userName = user.getUserName();
        newPoint.type = UploadCommentRequest.TEXT_DOC;
        newPoint.srcText = mUserCommentMsg;

        UploadCommentRequest.uploadComment(mContext, "", mUserCommentMsg, "0", UploadCommentRequest.TEXT_DOC, 0, new UploadCommentListener() {

            @Override
            public void success(NewsDetailAdd.Point result) {
                if (result != null) {
                    UserCommentDialog.this.dismiss();
                    result.up = "0";
                    result.down = "0";
                    //通知刷新外面新闻展示界面
                    if (mIUpdateCommentCount != null) {
                        mIUpdateCommentCount.updateCommentCount(result);
                    }
                }else {
                    ToastUtil.toastShort("评论失败");
                }
            }

            @Override
            public void failed() {
                Logger.e("jigang", "+++++++comment fail==");
            }
        });
    }
    private class CommentTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Logger.e("jigang", "s=" + s);
            if (s != null && !TextUtil.isEmptyString(s.toString())) {
                mUserCommentMsg = mCommentContent.getText().toString();
                if (mUserCommentMsg.length() >= 144){
                    ToastUtil.toastShort("亲,您输入的评论过长");
                    mUserCommentMsg = mUserCommentMsg.substring(0,145);
                    mCommentContent.setText(mUserCommentMsg);
                }
                mCommentCommit.setBackgroundResource(R.drawable.bg_user_comment_commit_sel);
                mCommentCommit.setOnClickListener(UserCommentDialog.this);
            } else {
                mCommentCommit.setBackgroundResource(R.drawable.bg_user_comment_commit);
                mCommentCommit.setOnClickListener(null);
            }
        }
    }
}
