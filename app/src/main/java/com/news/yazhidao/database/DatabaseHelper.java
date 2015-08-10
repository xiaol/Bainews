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
import com.news.yazhidao.entity.DiggerAlbum;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "yazhidao_news.db";
    /**
     * albumDao ，每张表对于一个
     */
    private Dao<DiggerAlbum, String> albumDao;

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DiggerAlbum.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, DiggerAlbum.class, true);
            onCreate(database, connectionSource);
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

    public Dao<DiggerAlbum, String> getAlbumDao() throws SQLException {
        if (albumDao == null) {
            albumDao = getDao(DiggerAlbum.class);
        }
        return albumDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        albumDao = null;
    }



}

