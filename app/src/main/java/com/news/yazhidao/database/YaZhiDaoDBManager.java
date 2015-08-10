package com.news.yazhidao.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.news.yazhidao.utils.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengjigang on 15/8/10.
 * 全局数据库操作帮助类
 */
public class YaZhiDaoDBManager<T> {
    private final static String TAG = "YaZhiDaoDBManager";
    private YaZhiDaoSqliteOpenHelper mOpenHelper;
    private Context mContext;

    public YaZhiDaoDBManager(Context pContext) {
        mContext = pContext;
    }

    /**
     * 插入数据
     *
     * @param entity 数据库表的entity
     * @return
     */
    public int insert(T entity) {
        mOpenHelper = new YaZhiDaoSqliteOpenHelper(mContext);
        try {
            Dao dao = mOpenHelper.getDao(entity.getClass());
            return dao.create(entity);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "sqlite insert >>>>>" + e.getMessage());
        }
        return -1;
    }

    /**
     * 按id来查询指定信息
     *
     * @param clazz 表名entity
     * @param id    数据对应的id
     * @return
     */
    public T queryById(Class<T> clazz, String id) {
        mOpenHelper = new YaZhiDaoSqliteOpenHelper(mContext);
        try {
            Dao dao = mOpenHelper.getDao(clazz);
            return (T) dao.queryForId(id);
        } catch (SQLException e) {
            Logger.e("sqlite  query >>>", e.getMessage());
        } finally {
            if (mOpenHelper != null)
                mOpenHelper.close();
        }
        return null;
    }

    /**
     * 按限制条数来查询
     *
     * @param clazz
     * @param limit
     * @return
     */
    public List<T> queryAllByLimit(Class<T> clazz, long limit) {
        mOpenHelper = new YaZhiDaoSqliteOpenHelper(mContext);
        try {
            Dao dao = mOpenHelper.getDao(clazz);
            return dao.queryBuilder().limit(limit).query();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "sqlite queryAllByLimit>>>>" + e.getMessage());
        } finally {
            if (mOpenHelper != null) {
                mOpenHelper.close();
            }
        }
        return new ArrayList<T>();
    }

    /**
     * 根据数量、某一列名、是否正序排列来查询数据
     *
     * @param clazz
     * @param limit      限制多少条数据
     * @param colName    列名字段
     * @param whereValue 指定列的条件
     * @param asc        是否升序排列
     * @return
     */
    public List<T> queryAllOrderByColName(Class<T> clazz, long limit, String colName, String whereValue, boolean asc) {
        mOpenHelper = new YaZhiDaoSqliteOpenHelper(mContext);
        try {
            Dao dao = mOpenHelper.getDao(clazz);
            QueryBuilder builder = dao.queryBuilder();
            builder.limit(limit).orderBy(colName, asc);
            builder.where().eq(colName, whereValue);
            return dao.query(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "queryAllOrderByColName >>>>> " + e.getMessage());
        } finally {
            if (mOpenHelper != null) {
                mOpenHelper.close();
            }
        }
        return new ArrayList<T>();
    }

    /**
     * 查询所有的内容
     *
     * @param clazz
     * @return
     */
    public List<T> queryAll(Class<T> clazz) {
        mOpenHelper = new YaZhiDaoSqliteOpenHelper(mContext);
        try {
            Dao dao = mOpenHelper.getDao(clazz);
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "sqlite queryAll >>>>" + e.getMessage());
        } finally {
            if (mOpenHelper != null) {
                mOpenHelper.close();
            }
        }
        return new ArrayList<T>();
    }

    /**
     * 删除整个表的数据
     *
     * @param clazz
     */
    public int deleteAll(Class<T> clazz) {
        mOpenHelper = new YaZhiDaoSqliteOpenHelper(mContext);
        try {
            Dao dao = mOpenHelper.getDao(clazz);
            List<T> list = queryAll(clazz);
            if (list != null && list.size() > 0) {
                dao.delete(list);
                return list.size();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.e(TAG, "sqlite deleteAll >>>>" + e.getMessage());
        }
        return 0;
    }
}
