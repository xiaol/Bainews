package com.news.yazhidao.pages;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.Fglass;
import com.news.yazhidao.utils.manager.SharedPreManager;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyCommentAty extends BaseActivity implements View.OnClickListener {
    private View mCommentLeftBack;
    private SimpleDraweeView mCommentBgImg;
    private SimpleDraweeView mCommentUserIcon;
    private TextView mCommentUserName;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_comment);
    }

    @Override
    protected void initializeViews() {
        mCommentLeftBack = findViewById(R.id.mCommentLeftBack);
        mCommentLeftBack.setOnClickListener(this);
        mCommentBgImg = (SimpleDraweeView)findViewById(R.id.mCommentBgImg);
        mCommentUserIcon = (SimpleDraweeView)findViewById(R.id.mCommentUserIcon);
        mCommentUserName = (TextView)findViewById(R.id.mCommentUserName);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        User user = SharedPreManager.getUser(this);
//        //FIXME debug
//        if (user == null){
//            user = new User();
//            user.setUserName("forward_one");
//            user.setUserIcon("http://wx.qlogo.cn/mmopen/PiajxSqBRaEIVrCBZPyFk7SpBj8OW2HA5IGjtic5f9bAtoIW2uDr8LxIRhTTmnYXfejlGvgsqcAoHgkBM0iaIx6WA/0");
//        }
        if (user != null){
            mCommentUserName.setText(user.getUserName());
            Uri uri = Uri.parse(user.getUserIcon());
            mCommentUserIcon.setImageURI(uri);
            Postprocessor redMeshPostprocessor = new BasePostprocessor() {
                @Override
                public String getName() {
                    return "redMeshPostprocessor";
                }

                @Override
                public void process(Bitmap bitmap) {
//                    Fglass.blur(mCommentUserIcon,mCommentBgImg,2,8);
                    Fglass.doBlur(bitmap,16,true);
                }
            };

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setPostprocessor(redMeshPostprocessor)
                    .build();

            PipelineDraweeController controller = (PipelineDraweeController)
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(mCommentBgImg.getController())
                            .build();
            mCommentBgImg.setController(controller);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mCommentLeftBack:
                finish();
                break;
        }
    }
}
