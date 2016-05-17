package com.news.yazhidao.pages;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailCommentAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.database.NewsDetailCommentDao;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.Fglass;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.util.ArrayList;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyCommentAty extends BaseActivity implements View.OnClickListener, NewsDetailCommentAdapter.OnDataIsNullListener {
    private View mCommentLeftBack;
    private SimpleDraweeView mCommentBgImg;
    private SimpleDraweeView mCommentUserIcon;
    private TextView mCommentUserName;
    private TextView clip_pic;
    private ListView mCommentListView;
    private ArrayList<NewsDetailComment> newsDetailCommentItems = new ArrayList<>();
    private NewsDetailCommentDao newsDetailCommentDao;
    private int dHeight;
    private RelativeLayout comment_nor;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_comment);
    }

    @Override
    protected void initializeViews() {
        mCommentLeftBack = findViewById(R.id.mCommentLeftBack);
        mCommentLeftBack.setOnClickListener(this);
        mCommentBgImg = (SimpleDraweeView) findViewById(R.id.mCommentBgImg);
        mCommentUserIcon = (SimpleDraweeView) findViewById(R.id.mCommentUserIcon);
        mCommentUserName = (TextView) findViewById(R.id.mCommentUserName);
        mCommentListView = (ListView) this.findViewById(R.id.myCommentListView);
        newsDetailCommentDao = new NewsDetailCommentDao(this);
        clip_pic = (TextView) findViewById(R.id.clip_pic);
        comment_nor = (RelativeLayout) findViewById(R.id.layout_nor_comment);
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
        if (user != null) {
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
                    Fglass.doBlur(bitmap, 16, true);
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


            newsDetailCommentItems = newsDetailCommentDao.queryForAll(-1);
            if (newsDetailCommentItems != null && newsDetailCommentItems.size() > 0) {
                mCommentListView.setVisibility(View.VISIBLE);
                comment_nor.setVisibility(View.GONE);
                View footer = LayoutInflater.from(this).inflate(R.layout.listview_footer, null);
                mCommentListView.addFooterView(footer, null, false);
                NewsDetailCommentAdapter newsDetailCommentAdapter = new NewsDetailCommentAdapter(R.layout.user_detail_record_item, this, newsDetailCommentItems);
                newsDetailCommentAdapter.setOnDataIsNullListener(this);
                newsDetailCommentAdapter.setDaoHeight(dHeight);
                newsDetailCommentAdapter.setClip_pic(clip_pic);
                newsDetailCommentAdapter.setNewsDetailCommentDao(newsDetailCommentDao);
                mCommentListView.setAdapter(newsDetailCommentAdapter);
            } else {
                onChangeLayout();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCommentLeftBack:
                finish();
                break;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        Point outP = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(outP);
        Rect outRect = new Rect();
        this.getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                .getDrawingRect(outRect);
        dHeight = outP.y - outRect.height();
    }


    @Override
    public void onChangeLayout() {
        mCommentListView.setVisibility(View.GONE);
        comment_nor.setVisibility(View.VISIBLE);
    }
}
