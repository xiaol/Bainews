package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 16/2/26.
 */
public class FeedRequest<T> extends GsonRequest<T> {
    private HashMap mParams;

    public FeedRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    public FeedRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }
    public void setRequestParams(HashMap params){
        this.mParams = params;
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }
}