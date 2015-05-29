package com.news.yazhidao.widget.imagewall;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.utils.adapter.Logs;
import com.news.yazhidao.utils.adapter.MapAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
                ((TextView) convertView.findViewById(R.id.pagination)).setText((position + 1) + "/" + browsedata.size());
                ((TextView) convertView.findViewById(R.id.pagination)).setShadowLayer(5, 5, 5, Color.BLACK);
                ((TextView) convertView.findViewById(R.id.txt)).setShadowLayer(5, 5, 5, Color.BLACK);
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


        android.support.v4.view.ViewPager pager = (android.support.v4.view.ViewPager) this.findViewById(R.id.wall);
        for (int i = 0; i < browsedata.size(); i++) {
            layouts.add(LayoutInflater.from(this).inflate(R.layout.wallitemlayout, null));
            getView(i);
        }

        commtAdapter.notifyDataSetChanged();

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
            }

            List list = new ArrayList();

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (list.contains(position)) {
                    return container.getChildAt(position);
                } else {
                    list.add(position);
                }
                container.addView(layouts.get(position));
                BitmapFactory.Options bf = new BitmapFactory.Options();
                bf.inSampleSize = 256;

                if (bitmap.containsKey(position)) {
                    ((ImageView) layouts.get(position).findViewById(R.id.image)).setImageBitmap(bitmap.get(position));
                } else {
                    ImageLoader.getInstance().displayImage(((Map) browsedata.get(position)).get("img").toString(),
                            ((ImageView) layouts.get(position).findViewById(R.id.image)), getImageOption(bf), new ImageLoadingListener() {
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
                }
                return layouts.get(position);
            }


        });
        pager.setCurrentItem(getIntent().getIntExtra("page", 0));
    }

    Map<Integer, Bitmap> bitmap = new HashMap<Integer, Bitmap>();

    public View getView(final int position) {


        View view = layouts.get(position);
        ((TextView) view.findViewById(R.id.pagination)).setText((position + 1) + "/" + browsedata.size());
        ((TextView) view.findViewById(R.id.pagination)).setShadowLayer(5, 3, 3, Color.BLACK);
        ((TextView) view.findViewById(R.id.txt)).setShadowLayer(5, 3, 3, Color.BLACK);
        String note = ((Map) browsedata.get(position)).get("note").toString();
        String img = ((Map) browsedata.get(position)).get("img").toString();

        ((TextView) view.findViewById(R.id.txt)).setText(note);
        return view;
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
