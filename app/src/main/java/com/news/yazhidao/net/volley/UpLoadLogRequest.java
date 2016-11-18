package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/18.
 */
public class UpLoadLogRequest<T> extends GsonRequest<T> {

    private HashMap mHeader;

    public UpLoadLogRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }

    public UpLoadLogRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }

    public void setRequestHeader(HashMap header) {
        this.mHeader = header;
    }
}
