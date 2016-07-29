package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.news.yazhidao.R;
import com.news.yazhidao.entity.ReleaseSourceItem;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/10/28.
 * 新闻频道操作dao
 */
public class ReleaseSourceItemDao {
    private static final String TAG = "ReleaseSourceItemDao";
    /**
     * 频道排序id
     */
    private static final String COLUMN_ORDERID = "orderId";
    /**
     * 用户是否选择了该频道
     */
    private static final String COLUMN_SELECTED = "selected";
    private static final String COLUMN_ONLINE = "online";
    private final Context mContext;
    private String[] colorArr;
    private Dao<ReleaseSourceItem, String> mReleaseSourceItemDao;

    public ReleaseSourceItemDao(Context pContext) {
        this.mContext = pContext;
        try {
            mReleaseSourceItemDao = DatabaseHelper.getHelper(mContext).getDao(ReleaseSourceItem.class);
            colorArr = mContext.getResources().getStringArray(R.array.bg_focus_colors);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, ReleaseSourceItem.class + " dao create failure >>>" + e.getMessage());
        }
    }

    public HashMap<String,Integer> queryReleaseSourceItem() {
        HashMap<String,Integer> items = new HashMap<>();
        try {
            List<ReleaseSourceItem> list = mReleaseSourceItemDao.queryForAll();
            for (ReleaseSourceItem item : list) {
                items.put(item.getpName(),item.getBackground());
            }
            if (!TextUtil.isListEmpty(list)) {
                return items;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void insertOrUpdate(ReleaseSourceItem pItem) {
        try {
            mReleaseSourceItemDao.createOrUpdate(pItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
