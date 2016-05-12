package com.news.yazhidao.database;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.news.yazhidao.entity.NewsDetailCommentItem;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiao on 2016/5/6.
 * 处理评论的DAO
 */
public class NewsDetailCommentDao implements Serializable{
    private static final long serialVersionUID = 8897789878602695966L;
    private Context mContext;
    private Dao<NewsDetailCommentItem, Integer> newsDetailCommentDaoOpe;
    private DatabaseHelper helper;


    @SuppressWarnings("unchecked")
    public NewsDetailCommentDao(Context context) {
        mContext = context;
        try {
            helper = DatabaseHelper.getHelper(mContext);
            newsDetailCommentDaoOpe = helper.getDao(NewsDetailCommentItem.class);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 增加一条评论
     */
    public void add(NewsDetailCommentItem newsDetailCommentItem){
        try {
            newsDetailCommentDaoOpe.create(newsDetailCommentItem);
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void update(NewsDetailCommentItem newsDetailCommentItem){
        try {
            newsDetailCommentDaoOpe.update(newsDetailCommentItem);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //删除一个评论
    public void delete(NewsDetailCommentItem newsDetailCommentItem){
        try {
            newsDetailCommentDaoOpe.delete(newsDetailCommentItem);
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //查询该用户的所有评论，并按照时间顺序排序
    //暂时先不区分用户
    public ArrayList<NewsDetailCommentItem> queryForAll(int userId){
        List<NewsDetailCommentItem> newsDetailCommentItems = new ArrayList<>();
        try {
            QueryBuilder<NewsDetailCommentItem, Integer> builder = newsDetailCommentDaoOpe.queryBuilder();
            builder.orderBy("create_time", false);
            newsDetailCommentItems = builder.query();
            if(newsDetailCommentItems!=null&&newsDetailCommentItems.size()!=0){
                return (ArrayList<NewsDetailCommentItem>)newsDetailCommentItems;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return (ArrayList<NewsDetailCommentItem>)newsDetailCommentItems;
    }

    //删除所有评论
    public void deleteAll(List<NewsDetailCommentItem> items){
        try {
            newsDetailCommentDaoOpe.delete(items);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
