package com.news.yazhidao.listener;

/**
 * Created by fengjigang on 15/7/31.
 * 创建挖掘个人专辑网络访问接口回调
 */
public interface CreateDiggerAlbumListener {
    /**
     * 把创建好的专辑id回调回来,如果失败则pAlbumId 为null
     * @param pAlbumId
     */
    void createDiggerAlbumDone(String pAlbumId);
}
