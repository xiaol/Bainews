package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/8/5.
 * 专辑中所包含的挖掘items
 */
@DatabaseTable(tableName = "tb_album_item")
public class AlbumSubItem implements Serializable {
    /**当前新闻挖掘的状态和进度*/
    @DatabaseField(columnName = "status")
    private String status;
    /**挖掘的url*/
    @DatabaseField(columnName = "search_url")
    private String search_url;
    /**当前挖掘新闻的id,唯一*/
    @DatabaseField(columnName = "inserteId")
    private String inserteId;
    /**挖掘的title*/
    @DatabaseField(columnName = "search_key")
    private String search_key;
    /**是否已经向服务器上传成功*/
    @DatabaseField(columnName = "is_uploaded")
    private String is_uploaded;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSearch_url(String search_url) {
        this.search_url = search_url;
    }

    public void setInserteId(String inserteId) {
        this.inserteId = inserteId;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch_url() {
        return search_url;
    }

    public String getInserteId() {
        return inserteId;
    }

    public String getSearch_key() {
        return search_key;
    }

    @Override
    public String toString() {
        return "<"+this.getClass().getSimpleName()+">:"+"status="+status+",search_url="+search_url+",search_key="+search_key+",inserteid="+inserteId;
    }
}
