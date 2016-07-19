package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/8/12.
 * 新闻来源的对应可序化队列属性
 */

@DatabaseTable(tableName = "tb_release_source")
public class ReleaseSourceItem implements Serializable {
    /**
     * 栏目对应ID
     */
    @DatabaseField(id = true)
    private String id;
    /**
     * 栏目对应描述
     */
    @DatabaseField
    private String descr;
    /**
     * 时间
     */
    @DatabaseField
    private String ctime;
    /**
     * 栏目对应的图片
     */
    @DatabaseField
    private String icon;
    /**
     * 栏目数量
     */
    @DatabaseField
    private int concern;
    /**
     * 栏目是否选中
     */
    @DatabaseField
    private boolean focus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

}