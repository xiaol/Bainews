package com.news.yazhidao.common;

/**
 * Created by fengjigang on 15/1/21.
 */
public class HttpConstant {
    public static final String URL_GET_NEWS_LIST="http://api.deeporiginalx.com/news/baijia/fetchHome";
    public static final String URL_GET_NEWS_LIST_NEW="http://api.deeporiginalx.com/news/baijia/newsFetchHome";
    public static final String URL_GET_START_URL="http://api.deeporiginalx.com/news/baijia/startPage";
    public static final String URL_GET_CHANNEL_LIST="http://api.deeporiginalx.com/news/baijia/FetchChannelList";
    public static final String URL_PRAISE="http://api.deeporiginalx.com/news/baijia/praise";
    public static final String URL_GET_NEWS_CONTENT="http://api.deeporiginalx.com/news/baijia/point";
    /**谷歌今日焦点*/
    public static final String URL_GET_NEWS_DETAIL="http://api.deeporiginalx.com/news/baijia/fetchContent?url=";
    public static final String URL_GET_DIGGER_DETAIL="http://api.deeporiginalx.com/news/baijia/dredgeUpStatus?url=";
    public static final String URL_GET_NEWS_DETAIL_NEW="http://api.deeporiginalx.com/news/baijia/newsFetchContent";
    public static final String URL_GET_NEWS_REFRESH_TIME="http://api.deeporiginalx.com/news/baijia/fetchTime?timefeedback=1";
    public static final String URL_USER_LOGIN="http://api.deeporiginalx.com/news/baijia/fetchLogin";
    public static final String URL_UPLOAD_JPUSHID = "http://api.deeporiginalx.com/news/baijia/fetchImUser?";
    public static final String URL_UPLOAD_UMENGPUSHID = "http://api.deeporiginalx.com/news/baijia/uploadUmengPushId?";
    public static final String URL_SEND_MESSAGE = "http://api.deeporiginalx.com/news/baijia/fetchIm?";
    public static final String URL_GET_HISTORY_MESSAGE = "http://api.deeporiginalx.com/news/baijia/fetchImContent?";
    public static final String URL_GET_MESSAGE_LIST = "http://api.deeporiginalx.com/news/baijia/fetchImList?";
    /**获取专辑列表接口*/
    public static final String URL_FETCH_ALBUM_LIST = "http://api.deeporiginalx.com/news/baijia/fetchAlbumList?";
    /**挖掘机创建专辑接口*/
    public static final String URL_CREATE_DIGGER_ALBUM = "http://api.deeporiginalx.com/news/baijia/createAlbum?";
    /**往专辑中添加挖掘内容*/
    public static final String URL_DIGGER_ALBUM = "http://60.28.29.37:8080/excavator?";
    /**获取指定专辑中的挖掘内容*/
    public static final String URL_FETCH_ALBUM_SUBITEMS = "http://api.deeporiginalx.com/news/baijia/dredgeUpStatus?";
    /**获取热点话题标签*/
    public static final String URL_FETCH_ELEMENTS = "http://api.deeporiginalx.com/news/baijia/fetchElementary";
    /**获取新闻详情 api*/
    public static final String URL_POST_NEWS_DETAIL="http://api.deeporiginalx.com/news/baijia/fetchDetail";
}
