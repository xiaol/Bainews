package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/8/12.
 * Feed流操作Dao
 */
public class NewsFeedDao {
    private static final String TAG = "NewsFeedDao";

    private Context mContext;
    private Dao<NewsFeed, String> mNewsFeedDao;
    private DatabaseHelper mDbHelper;

    public NewsFeedDao(Context pContext) {
        this.mContext = pContext;
        mDbHelper = DatabaseHelper.getHelper(pContext);
        try {
            mNewsFeedDao = mDbHelper.getDao(NewsFeed.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一组新闻数据
     *
     * @param arrNewsFeed 新闻对象
     */
    public void insert(ArrayList<NewsFeed> arrNewsFeed) {
        try {
            if (arrNewsFeed != null && arrNewsFeed.size() > 0) {
                for (NewsFeed newsFeed : arrNewsFeed) {
                    mNewsFeedDao.create(newsFeed);
                }
                Logger.e(TAG, "insert " + NewsFeed.class.getSimpleName() + " success >>>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + NewsFeed.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }

    /**
     * 更新一组新闻数据
     *
     * @param arrNewsFeed 新闻对象
     */
    public void update(ArrayList<NewsFeed> arrNewsFeed) {
        try {
            if (arrNewsFeed != null && arrNewsFeed.size() > 0) {
                for (NewsFeed newsFeed : arrNewsFeed) {
                    mNewsFeedDao.createOrUpdate(newsFeed);
                }
                Logger.e(TAG, "insert " + NewsFeed.class.getSimpleName() + " success >>>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "insert " + NewsFeed.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }
    /**
     * 更新一个新闻数据
     *
     * @param newsFeed 新闻对象
     */
    public void update(NewsFeed newsFeed) {
        try {
            if (newsFeed != null ) {
                QueryBuilder<NewsFeed, String> queryBuilder = mNewsFeedDao.queryBuilder();
                queryBuilder.where().eq(NewsFeed.COLUMN_NEWS_ID,newsFeed.getNewsId());
                List<NewsFeed> result = queryBuilder.query();
                if (!TextUtil.isListEmpty(result)){
                    NewsFeed feed = result.get(0);
                    feed.setRead(true);
                    Dao.CreateOrUpdateStatus orUpdate = mNewsFeedDao.createOrUpdate(feed);
                    Logger.e("jigang","update---linechange="+orUpdate.getNumLinesChanged()+",isUpdated=" + orUpdate.isUpdated() +","+orUpdate.isCreated());
                }
                Logger.e(TAG, "update " + NewsFeed.class.getSimpleName() + " success >>>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "update " + NewsFeed.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
    }


    /**
     * 根据专辑id获取Feed流
     *
     * @param channelId
     * @return
     */
    public ArrayList<NewsFeed> queryByChannelId(String channelId) {
        List subItems = new ArrayList();
        try {
            QueryBuilder<NewsFeed, String> builder = mNewsFeedDao.queryBuilder();
            builder.where().eq(NewsFeed.COLUMN_CHANNEL_ID, channelId);
            builder.orderBy(NewsFeed.COLUMN_UPDATE_TIME, false);
            subItems = builder.query();
            if (TextUtil.isListEmpty(subItems)) {
                return new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "queryByAlbumId " + NewsFeed.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return new ArrayList<NewsFeed>(subItems);
    }

    public int executeRaw(String sql){
        try {
            return mNewsFeedDao.executeRaw(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "executeRaw " + NewsFeed.class.getSimpleName() + " failure >>>" + e.getMessage());
        }
        return -1;
    }
}
