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
import com.news.yazhidao.utils.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "yazhidao_news.db";
    private HashMap<String,Dao> mDaos;
    private Context mContext;
    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
        mContext = context;
        mDaos = new HashMap<>();
        Logger.e("jigang","DatabaseHelper()");
    }
    private static ArrayList<ChannelItem> mChannels = new ArrayList<>();
    static {
        /**默认用户选择的频道*/
        mChannels.add(new ChannelItem("TJ0001","推荐",1,true));
        mChannels.add(new ChannelItem("RD0002","热点",2,true));
        mChannels.add(new ChannelItem("JX0003","精选",3,true));
        mChannels.add(new ChannelItem("SH0004","社会",4,true));
        mChannels.add(new ChannelItem("WM0005","外媒",5,true));
        mChannels.add(new ChannelItem("YL0006","娱乐",6,true));
        mChannels.add(new ChannelItem("KJ0007","科技",7,true));
        mChannels.add(new ChannelItem("TY0008","体育",8,true));
        mChannels.add(new ChannelItem("CJ0009","财经",9,true));
        mChannels.add(new ChannelItem("SS0010","时尚",10,true));
        mChannels.add(new ChannelItem("GX0011","搞笑",11,true));
        /**默认用户未选择的频道,并可选添加*/
        mChannels.add(new ChannelItem("YS0012","影视",1,false));
        mChannels.add(new ChannelItem("YY0013","音乐",2,false));
        mChannels.add(new ChannelItem("ZKW0014","重口味",3,false));
        mChannels.add(new ChannelItem("MC0015","萌宠",4,false));
        mChannels.add(new ChannelItem("ECY0016","二次元",5,false));
    }
    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, DiggerAlbum.class);
            TableUtils.createTableIfNotExists(connectionSource, AlbumSubItem.class);
            TableUtils.createTableIfNotExists(connectionSource, ChannelItem.class);
            Logger.e("jigang", "DatabaseHelper  onCreate()");
            ChannelItemDao dao = new ChannelItemDao(mContext);
            dao.insertList(mChannels);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DiggerAlbum.class, true);
            TableUtils.dropTable(connectionSource, AlbumSubItem.class, true);
            TableUtils.dropTable(connectionSource, ChannelItem.class, true);
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

