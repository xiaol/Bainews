package com.news.yazhidao.widget.imagewall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.List;

public class ViewWall {
    public static final int STYLE_7_232 = R.layout.wall_7_232;
    public static final int STYLE_9 = R.layout.wall_9_grid;
    public int layoutid;

    /**
     * @param ctx
     */

    public ViewWall(Context ctx) {
        super();
        this.ctx = ctx;
    }

    public Context ctx;

    public int getId() {
        return layoutid;
    }

    public void setLayoutId(int id) {
        switch (id) {
            case STYLE_7_232:
                ids = new int[]{R.id.col11, R.id.col12, R.id.col21, R.id.col22,
                        R.id.col23, R.id.col31, R.id.col32};
            case STYLE_9:
                ids = new int[]{R.id.col11, R.id.col12, R.id.col13, R.id.col21,
                        R.id.col22, R.id.col23, R.id.col31, R.id.col32, R.id.col33};
        }
        this.layoutid = id;
    }

    public View inflate() {
        if (layoutid == 0) {
            throw new RuntimeException("inner toFlate Id is zero");
        }
        int theid = layoutid;
        return inflate(theid);
    }

    public View inflate(int theid) {
        view = LayoutInflater.from(ctx).inflate(theid, null);
        return view;
    }

    public void setData() {
        if (urls == null) {
            throw new RuntimeException("urls is not setted");
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                ctx).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                        // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        int i;
        for (i = 0; i < urls.size(); i++) {

            if (ids.length >= i + 1) {
                View picwallview = ((ImageView) view.findViewById(ids[i]));
                if (picwallview != null) {
                    picwallview.setVisibility(View.VISIBLE);
                } else {

                    continue;
                }

            } else {
                if (i <= 5) {

                    view.findViewById(R.id.row3).setVisibility(View.GONE);
                }
                if (i <= 2) {
                    view.findViewById(R.id.row2).setVisibility(View.GONE);
                }
                break;
            }
            final int j = i;
            ImageLoader.getInstance().displayImage(urls.get(i).get("img"),
                    ((ImageView) view.findViewById(ids[i])),
                    new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1,
                                                      Bitmap arg2) {
                            // TODO Auto-generated method stub
                            // BitmapFactory.Options options = new
                            // BitmapFactory.Options();
                            //
                            // options.inJustDecodeBounds = true;
                            // BitmapFactory.decodeFile();

                            WallActivity.url_height.put(urls.get(j).get("img").toString(),
                                    arg2.getHeight());
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            // TODO Auto-generated method stub

                        }
                    });

            ((ImageView) view.findViewById(ids[i]))
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            int end = urls.size() > 9 ? 9 : urls.size();
                            WallActivity.browsedata = urls.subList(0, end);

                            ImageLoader.getInstance().getDiskCache()
                                    .get(urls.get(j).get("img"));
                            v.getContext().startActivity(
                                    new Intent(v.getContext(),
                                            WallActivity.class).putExtra("page", j));
                        }
                    });
        }
        if (i - 1 <= 5) {

            view.findViewById(R.id.row3).setVisibility(View.GONE);
        }
        if (i - 1 <= 2) {
            view.findViewById(R.id.row2).setVisibility(View.GONE);
        }
    }

    public View view;

    int[] ids;
    String[] localpath;

    private List<HashMap<String, String>> urls;

    public List<HashMap<String, String>> getUrls() {
        return urls;
    }

    public void setUrls(List<HashMap<String, String>> urls) {
        this.urls = urls;
    }

    public void setUrls(String jsonArrayRightValue) {
        this.urls = ((List) (new JsonUtil().fromJsonArray(jsonArrayRightValue)));
    }

    public static void add(ImageWallView parent, Object source, int layoutid) {

        ViewWall wall = new ViewWall(parent.getContext());
        wall.setLayoutId(layoutid);
        if (source instanceof String) {
            wall.setUrls(source.toString());
        } else if (source instanceof List) {
            wall.setUrls(((List) (source)));
        }
        ((ImageWallView) parent).addView(wall.inflate());
        wall.setData();
    }

}
