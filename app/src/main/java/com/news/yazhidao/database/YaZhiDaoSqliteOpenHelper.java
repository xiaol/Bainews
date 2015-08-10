package com.news.yazhidao.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.utils.Logger;

import java.sql.SQLException;

/**
 * Created by fengjigang on 15/8/10.
 * 数据库帮助类
 */
public class YaZhiDaoSqliteOpenHelper extends OrmLiteSqliteOpenHelper {
    private final static String TAG = "YaZhiDaoSqliteOpenHelper";
    private final static String DATABASE_NAME = "yazhidao_news.db";
    private final static int DATABASE_VERSION = 1;

    public YaZhiDaoSqliteOpenHelper(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DiggerAlbum.class);
            Logger.d(TAG, ">>>>>>创建表 成功");
        } catch (SQLException e) {
            Logger.d(TAG, ">>>>>>创建表 失败");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, DiggerAlbum.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.d(TAG, ">>>>>>删除表 " + DiggerAlbum.class.getSimpleName() + "失败");
        }
    }
}
