package com.news.yazhidao.listener;

import com.news.yazhidao.entity.AlbumSubItem;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/8/5.
 *  获取指定专辑下所有的items 接口回调
 */
public interface FetchAlbumSubItemsListener {
    void fetchAlbumSubItemsDone(ArrayList<AlbumSubItem> albumSubItems);
}
