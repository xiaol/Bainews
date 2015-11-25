package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.news.yazhidao.entity.NewsDetailForDigger;
import com.news.yazhidao.utils.Logger;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 15/11/24.
 */
public class GsonRequest<T> extends Request<T> {

    private Type mReflectType;
    private Class mClazz;
    private SuccessListener<T> mSuccessListener;
    private Gson mGson;
    private HashMap<String,String> mParams;

    public HashMap<String, String> getmParams() {
        return mParams;
    }

    public void setmParams(HashMap<String, String> mParams) {
        this.mParams = mParams;
    }

    public interface SuccessListener<T> {
        void success(T result);
    }
    private GsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);

    }

    /**
     * 使用volley进行网络请求
     */
    public GsonRequest(int method,Type reflectType,String url,SuccessListener successListener,Response.ErrorListener listener){
        this(method, url, listener);
        this.mReflectType = reflectType;
        this.mSuccessListener = successListener;
        this.mGson = new Gson();
    }
    /**
     * 使用volley进行网络请求
     */
    public GsonRequest(int method,Class clazz,String url,SuccessListener successListener,Response.ErrorListener listener){
        this(method, url, listener);
        this.mClazz = clazz;
        this.mSuccessListener = successListener;
        this.mGson = new Gson();
    }
    protected String checkJsonData(String data){
        return data;
    }
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            data = checkJsonData(data);
            T o = mGson.fromJson(data, mReflectType == null ? mClazz : mReflectType);
            Logger.e("jigang","t = "+o);
            if (o != null && ((NewsDetailForDigger)o).getStatus() == null){
                Logger.e("","");
            }
            return Response.success(o,HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (mSuccessListener != null){
            mSuccessListener.success(response);
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return getmParams();
    }
}
