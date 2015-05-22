package com.news.yazhidao.widget.imagewall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.utils.adapter.Logs;
import com.news.yazhidao.utils.adapter.MapAdapter;
import com.news.yazhidao.utils.adapter.MapContent;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WallActivity extends Activity {
	public static List browsedata = null;
	public static Map<String, Integer> url_height = new HashMap<String, Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.walllayout);
		// ((Gallery)findViewById(R.layout.walllayout));
		MapAdapter.AdaptInfo staggeredinfo = new MapAdapter.AdaptInfo();
		staggeredinfo.listviewItemLayoutId = R.layout.wallitemlayout;
		staggeredinfo.objectFields = new String[] { "img", "note" };
		staggeredinfo.viewIds = new Integer[] { R.id.image, R.id.txt };
		MapAdapter commtAdapter = new MapAdapter(this, staggeredinfo) {

			@Override
			protected void getViewInDetail(Object item, int position,
					View convertView) {
				// TODO Auto-generated method stub
				super.getViewInDetail(item, position, convertView);
				((TextView)convertView.findViewById(R.id.pagination)).setText((position+1)+"/"+getCount());
			}

			@Override
			protected boolean findAndBindView(View convertView, int pos,
					Object item, String name, Object value) {
				// TODO Auto-generated method stub
				if (name.equals("img")) {
					Logs.e("============== " + value + " " + getCount() + ""
							+ convertView.findViewById(R.id.image));

					convertView.findViewById(R.id.image).getLayoutParams().height = url_height
							.get(value.toString());
					ImageLoader.getInstance().displayImage(value.toString(),
							((ImageView) convertView.findViewById(R.id.image)));
				}
				return super.findAndBindView(convertView, pos, item, name,
						value);
			}

		};
		if (browsedata == null) {
			throw new RuntimeException("browsedata invalidly is null");
		}
		commtAdapter.setItemDataSrc(new MapContent(browsedata));

		((Gallery) findViewById(R.id.wall)).setAdapter(commtAdapter);
		((Gallery) findViewById(R.id.wall)).setSelection(getIntent()
				.getIntExtra("page", 0));
		commtAdapter.notifyDataSetChanged();

	}
}
