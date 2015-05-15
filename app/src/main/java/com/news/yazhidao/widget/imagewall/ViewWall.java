package com.news.yazhidao.widget.imagewall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.HashMap;
import java.util.ArrayList;

public class ViewWall {
	public static final int STYLE_7_232 = R.layout.wall_7_232;
	public static final int STYLE_9 = R.layout.wall_9_grid;
	public int layoutid;

	/***
	 *
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
			ids = new int[] { R.id.col11, R.id.col12, R.id.col21, R.id.col22,
					R.id.col23, R.id.col31, R.id.col32 };
		case STYLE_9:
			ids = new int[] { R.id.col11, R.id.col12, R.id.col13, R.id.col21,
					R.id.col22, R.id.col23, R.id.col31, R.id.col32, R.id.col33 };
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

		for (int i = 0; i < urls.size(); i++) {
			if (ids.length >= i + 1) {
				((ImageView) view.findViewById(ids[i]))
						.setVisibility(View.VISIBLE);
			} else {
				break;
			}
			ImageLoader.getInstance().displayImage(urls.get(i).get("img"),
					((ImageView) view.findViewById(ids[i])));

		}
	}

	public View view;

	int[] ids;
	String[] localpath;

	private ArrayList<HashMap<String, String>> urls;

	public ArrayList<HashMap<String, String>> getUrls() {
		return urls;
	}

	public void setUrls(ArrayList<HashMap<String, String>> urls) {
		this.urls = urls;
	}

	public void setUrls(String jsonArrayRightValue) {
		this.urls = ((ArrayList) (new JsonUtil().fromJsonArray(jsonArrayRightValue)));
	}

	public static void add(ImageWallView parent, Object source, int layoutid) {

		ViewWall wall = new ViewWall(parent.getContext());
		wall.setLayoutId(layoutid);
		if (source instanceof String) {
			wall.setUrls(source.toString());
		} else if (source instanceof ArrayList) {
			wall.setUrls(((ArrayList) (source)));
		}
		((ImageWallView) parent).addView(wall.inflate());
		wall.setData();
	}

}
