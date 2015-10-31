package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/8/12.
 * 新闻频道ITEM的对应可序化队列属性
 *
 */

@DatabaseTable(tableName = "tb_news_channel")
public class ChannelItem implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -6465237897027410019L;
	/**
	 * 栏目对应ID
	 *  */
	@DatabaseField(id = true)
	private String id;
	/**
	 * 栏目对应NAME
	 *  */
	@DatabaseField
	private String name;
	/**
	 * 栏目在整体中的排序顺序  rank
	 *  */
	@DatabaseField
	private int orderId;
	/**
	 * 栏目是否选中
	 *  */
	@DatabaseField
	private boolean selected;

	public ChannelItem() {
	}

	public ChannelItem(String id, String name, int orderId, boolean selected) {
		this.id = id;
		this.name = name;
		this.orderId = orderId;
		this.selected = selected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String toString() {
		return "ChannelItem [id=" + this.id + ", name=" + this.name
				+ ", selected=" + this.selected + "]";
	}
}