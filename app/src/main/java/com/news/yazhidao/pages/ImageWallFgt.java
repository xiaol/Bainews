package com.news.yazhidao.pages;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseFragment;
import com.news.yazhidao.utils.DeviceInfoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 用于展示图片新闻
 * Created by fengjigang on 16/3/8.
 */
public class ImageWallFgt extends BaseFragment implements ViewPager.OnPageChangeListener{
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc;
    private ArrayList<HashMap<String, String>> mImageList;
    private ArrayList<View> mViews;
    private int mScreenWidth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            mImageList = (ArrayList<HashMap<String,String>>)arguments.getSerializable(NewsDetailAty2.KEY_IMAGE_WALL_INFO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_image_wall, null);
        mImageWallVPager = (ViewPager)rootView.findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView)rootView.findViewById(R.id.mImageWallDesc);
        mScreenWidth = DeviceInfoUtil.getScreenWidth(getActivity());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViews = new ArrayList<>();
        for (int i = 0; i < mImageList.size(); i++) {
            final SimpleDraweeView imageView = new SimpleDraweeView(getActivity());
            mViews.add(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(Uri.parse(mImageList.get(i).get("img")))
                    .setTapToRetryEnabled(true)
                    .setOldController(imageView.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(
                                String id,
                                @Nullable ImageInfo imageInfo,
                                @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mImageWallVPager.getLayoutParams();
                            lp.width = mScreenWidth;
                            lp.height = (int) (mScreenWidth * imageInfo.getHeight() / (float) imageInfo.getWidth());
                            mImageWallVPager.setLayoutParams(lp);
                        }
                    })
                    .build();
            imageView.setController(controller);
        }
        mImageWallVPager.setOnPageChangeListener(this);
        mImageWallVPager.setAdapter(new ImagePagerAdapter(mViews));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mImageWallDesc.setText(position + 1 + "/" + mImageList.size() + "  "+mImageList.get(position).get("note"));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ImagePagerAdapter extends PagerAdapter{


        private List<View> views = new ArrayList<View>();

        public ImagePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    }
}
