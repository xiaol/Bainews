package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/9.
 */
public class NewsTopicRequestGet<T> extends GsonRequest<T> {

    private HashMap mHeader = new HashMap();
    private HashMap mParams;

    public NewsTopicRequestGet(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }

    @Override
    protected String checkJsonData(String data, NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String code = jsonObject.optString("code", "");
            if ("2000".equals(code)) {
                return jsonObject.optString("data", "");
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setRequestParams(HashMap params) {
        this.mParams = params;
    }

    public void setRequestHeader(HashMap header) {
        this.mHeader = header;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeader;
    }
}
