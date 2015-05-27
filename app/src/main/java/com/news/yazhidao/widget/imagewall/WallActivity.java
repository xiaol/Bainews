package com.news.yazhidao.widget.imagewall;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.utils.adapter.Logs;
import com.news.yazhidao.utils.adapter.MapAdapter;
import com.news.yazhidao.utils.adapter.MapContent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallActivity extends Activity {
    public static List browsedata = null;
    public static Map<String, Integer> url_height = new HashMap<String, Integer>();
    List<View> layouts = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.walllayout);
        // ((Gallery)findViewById(R.layout.walllayout));
        MapAdapter.AdaptInfo staggeredinfo = new MapAdapter.AdaptInfo();
        staggeredinfo.listviewItemLayoutId = R.layout.wallitemlayout;
        staggeredinfo.objectFields = new String[]{"img", "note"};
        staggeredinfo.viewIds = new Integer[]{R.id.image, R.id.txt};
        MapAdapter commtAdapter = new MapAdapter(this, staggeredinfo) {

            @Override
            protected void getViewInDetail(Object item, int position,
                                           View convertView) {
                // TODO Auto-generated method stub
                super.getViewInDetail(item, position, convertView);
                ((TextView) convertView.findViewById(R.id.pagination)).setText((position + 1) + "/" + getCount());
                ((TextView) convertView.findViewById(R.id.pagination)).setShadowLayer(50, 5, 5, Color.BLACK);
                ((TextView) convertView.findViewById(R.id.txt)).setShadowLayer(50, 5, 5, Color.BLACK);
            }

            @Override
            protected boolean findAndBindView(View convertView, int pos,
                                              Object item, String name, Object value) {
                // TODO Auto-generated method stub
                if (name.equals("img")) {
                    Logs.e("============== " + value + " " + getCount() + ""
                            + convertView.findViewById(R.id.image));


                }
                return super.findAndBindView(convertView, pos, item, name,
                        value);
            }


        };
        if (browsedata == null) {
            throw new RuntimeException("browsedata invalidly is null");
        }
//        commtAdapter.setItemDataSrc(new MapContent(browsedata));
//        ((Gallery) findViewById(R.id.wall)).setAdapter(commtAdapter);
//        ((Gallery) findViewById(R.id.wall)).setSelection(getIntent().getIntExtra("page", 0));

        android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) this.findViewById(R.id.wall);
        for (int i = 0; i < browsedata.size(); i++) {
            layouts.add(LayoutInflater.from(this).inflate(R.layout.wallitemlayout, null));

        }

        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return browsedata.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                ((ViewPager) container).removeView(layouts.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                ((ViewPager) container).addView(layouts.get(position));
                View view =

                        layouts.get(position);
                ((TextView) view.findViewById(R.id.pagination)).setText((position + 1) + "/" + getCount());
                ((TextView) view.findViewById(R.id.pagination)).setShadowLayer(50, 5, 5, Color.BLACK);
                ((TextView) view.findViewById(R.id.txt)).setShadowLayer(50, 5, 5, Color.BLACK);
                String note = ((Map) browsedata.get(position)).get("note").toString();
                String img = ((Map) browsedata.get(position)).get("img").toString();

                ((TextView) view.findViewById(R.id.txt)).setText(note);


                BitmapFactory.Options bf = new BitmapFactory.Options();
                bf.inSampleSize = 256;


                ImageLoader.getInstance().displayImage(img.toString(),
                        ((ImageView) view.findViewById(R.id.image)), getImageOption(bf), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                                view.getLayoutParams().height = loadedImage.getHeight() * GlobalParams.screenWidth / loadedImage.getWidth();


                                view.invalidate();


                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });

                return view;
            }

        });

//        List<PageInfo> pageInfos = new ArrayList<PageInfo>(0);
//        pageInfos.add( genPageInfo(getClass(),
//                this, getClass(),R.layout.wallitemlayout));
//        for(int i = 0;i<browsedata.size();i++){
//            pageInfos.add( genPageInfo(getClass(),
//                    this, getClass(),R.layout.wallitemlayout));
//        }
//
//        ViewPagerHelper viewPagerHelper = new ViewPagerHelper(this, null);
//        viewPagerHelper.setLayoutid(R.id.wall);
//        viewPagerHelper.setup(this, 0, pageInfos);
//        pager.setCurrentItem(getIntent()
//                .getIntExtra("page", 0));
        commtAdapter.notifyDataSetChanged();
        pager.setCurrentItem(getIntent().getIntExtra("page", 0));
    }

    public DisplayImageOptions getImageOption(
            BitmapFactory.Options decodingOptions) {
        final DisplayImageOptions options = new DisplayImageOptions.Builder()

                .cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .decodingOptions(decodingOptions)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }
}
