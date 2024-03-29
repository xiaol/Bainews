package com.news.yazhidao.database;

/**
 * Created by fengjigang on 15/8/10.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.NewsDetailComment;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.ReleaseSourceItem;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "yazhidao_news.db";
    private static int DATABASE_VERSION = 34;
    private HashMap<String,Dao> mDaos;
    private Context mContext;
    private ArrayList<ChannelItem> oldChannelItems;
    private ArrayList<DiggerAlbum> oldDiggerAlbums;
    private ArrayList<AlbumSubItem> oldDiggerAlbumItems;
    private ChannelItemDao channelDao;

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mDaos = new HashMap<>();
        Logger.e("jigang","DatabaseHelper()");
    }
    private static ArrayList<ChannelItem> mChannels = new ArrayList<>();
    static {
        /**默认用户选择的频道*/
        mChannels.add(new ChannelItem("1","推荐",1,true,"1"));
        mChannels.add(new ChannelItem("44","视频",2,true,"1"));
        mChannels.add(new ChannelItem("4","科技",3,true,"1"));
//        mChannels.add(new ChannelItem("29","外媒",4,true,"1"));
        mChannels.add(new ChannelItem("35","点集",4,true,"1"));
        mChannels.add(new ChannelItem("2","社会",5,true,"1"));
        mChannels.add(new ChannelItem("7","财经",6,true,"1"));
        mChannels.add(new ChannelItem("6","体育",7,true,"1"));
        mChannels.add(new ChannelItem("5","汽车",8,true,"1"));
        mChannels.add(new ChannelItem("9","国际",9,true,"1"));
        mChannels.add(new ChannelItem("10","时尚",10,true,"1"));
        mChannels.add(new ChannelItem("14","探索",11,true,"1"));
        mChannels.add(new ChannelItem("25","科学",12,true,"1"));
        mChannels.add(new ChannelItem("3","娱乐",13,true,"1"));
        mChannels.add(new ChannelItem("23","趣图",14,true,"1"));
        mChannels.add(new ChannelItem("21","搞笑",15,true,"1"));
        mChannels.add(new ChannelItem("17","养生",16,true,"1"));
        mChannels.add(new ChannelItem("11","游戏",17,true,"1"));
        mChannels.add(new ChannelItem("16","育儿",18,true,"1"));
        mChannels.add(new ChannelItem("36","自媒体",19,true,"1"));
//        mChannels.add(new ChannelItem("42","视频",21,true,"1"));
        /**默认用户未选择的频道,并可选添加*/
        mChannels.add(new ChannelItem("1000","关注",0,false,"0"));
        mChannels.add(new ChannelItem("24","健康",1,false,"1"));
        mChannels.add(new ChannelItem("30","影视",2,false,"1"));
        mChannels.add(new ChannelItem("31","奇闻",3,false,"1"));
        mChannels.add(new ChannelItem("32","萌宠",4,false,"1"));
        mChannels.add(new ChannelItem("22","互联网",5,false,"1"));
        mChannels.add(new ChannelItem("20","股票",6,false,"1"));
        mChannels.add(new ChannelItem("8","军事",7,false,"1"));
        mChannels.add(new ChannelItem("13","历史",8,false,"1"));
        mChannels.add(new ChannelItem("18","故事",9,false,"1"));
        mChannels.add(new ChannelItem("12","旅游",10,false,"1"));
        mChannels.add(new ChannelItem("19","美文",11,false,"1"));
        mChannels.add(new ChannelItem("15","美食",12,false,"1"));
        mChannels.add(new ChannelItem("26","美女",13,false,"1"));
    }
    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {

            TableUtils.createTableIfNotExists(connectionSource, DiggerAlbum.class);
            TableUtils.createTableIfNotExists(connectionSource, AlbumSubItem.class);
            TableUtils.createTableIfNotExists(connectionSource, ChannelItem.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsFeed.class);
            TableUtils.createTableIfNotExists(connectionSource, NewsDetailComment.class);
            TableUtils.createTableIfNotExists(connectionSource, ReleaseSourceItem.class);
            /**初始化数据库或者升级数据库的时候,插入默认值*/
            channelDao = new ChannelItemDao(mContext);
            if (!TextUtil.isListEmpty(oldChannelItems)){
                channelDao.insertList(oldChannelItems);
            }else {
                channelDao.insertList(mChannels);
            }
            /**升级数据库,保留以前的专辑*/
            DiggerAlbumDao albumDao = new DiggerAlbumDao(mContext);
            if (!TextUtil.isListEmpty(oldDiggerAlbums)){
                albumDao.insertList(oldDiggerAlbums);
            }
            /**升级数据库,保留以前的专辑内容*/
            AlbumSubItemDao albumSubItemDao = new AlbumSubItemDao(mContext);
            if (!TextUtil.isListEmpty(oldDiggerAlbumItems)){
                albumSubItemDao.insertList(oldDiggerAlbumItems);
            }

            Logger.e("jigang", "DatabaseHelper  onCreate()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
//            /***查询数据库升级前的频道列表*/
//            ChannelItemDao channelDao = new ChannelItemDao(mContext);
//            oldChannelItems = channelDao.queryForAll();
//            //删除所有老版本上的频道
//            if (oldVersion <= DATABASE_VERSION){
//               oldChannelItems.clear();
//            }
//            /**查询数据库升级前的专辑列表*/
//            DiggerAlbumDao albumDao = new DiggerAlbumDao(mContext);
//            oldDiggerAlbums = albumDao.querForAll();
//            /**查询专辑列表下的新闻*/
//            AlbumSubItemDao albumSubItemDao = new AlbumSubItemDao(mContext);
//            if (oldVersion <= 9){
//                albumSubItemDao.executeRaw("ALTER TABLE `tb_album_item` ADD COLUMN detailForDigger SERIALIZABLE;");
//            }
//            /**在feed流表中添加 isRead(用户是否阅读过该新闻)</> 字段*/
            NewsFeedDao newsFeedDao = new NewsFeedDao(mContext);
//            if (oldVersion <= 25){
//                newsFeedDao.executeRaw("ALTER TABLE `tb_news_feed` ADD COLUMN isRead BOOLEAN;");
//                newsFeedDao.executeRaw("ALTER TABLE `tb_news_feed` ADD COLUMN rtype INTEGER;");
//            }
//            NewsDetailCommentDao newsDetailCommentDao = new NewsDetailCommentDao(mContext);
//            if (oldVersion <= 25){
//                newsDetailCommentDao.executeRaw("ALTER TABLE `tb_news_detail_comment_item` ADD COLUMN uid STRING;");
//            }
//
//
//            oldDiggerAlbumItems = albumSubItemDao.queryForAll();
            if (oldVersion<=34)
            {
                newsFeedDao.executeRaw("ALTER TABLE `tb_news_feed` ADD COLUMN icon TEXT;");
                newsFeedDao.executeRaw("ALTER TABLE `tb_news_feed` ADD COLUMN clicktimes INTEGER;");
            }

            TableUtils.dropTable(connectionSource, DiggerAlbum.class, true);
            TableUtils.dropTable(connectionSource, AlbumSubItem.class, true);
            TableUtils.dropTable(connectionSource, ChannelItem.class, true);
            TableUtils.dropTable(connectionSource, NewsFeed.class, true);
            TableUtils.dropTable(connectionSource, NewsDetailComment.class, true);
            TableUtils.dropTable(connectionSource, ReleaseSourceItem.class, true);
            onCreate(database, connectionSource);
            Logger.e("jigang", "DatabaseHelper  onUpgrade()");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }


    /**
     * 按Class 获取Dao对象
     * @param clazz
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class clazz) throws SQLException
    {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (mDaos.containsKey(className))
        {
            dao = mDaos.get(className);
        }
        if (dao == null)
        {
            dao = super.getDao(clazz);
            mDaos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : mDaos.keySet())
        {
            Dao dao = mDaos.get(key);
            dao = null;
        }
    }



}

