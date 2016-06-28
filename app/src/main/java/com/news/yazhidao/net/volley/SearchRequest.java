package com.news.yazhidao.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengjigang on 15/11/24.
 */
public class SearchRequest<T> extends GsonRequest<T> {

    private String mKeyWord;
    private String mPageIndex;

    public SearchRequest(int method, Class clazz, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, clazz, url, successListener, listener);
    }

    public SearchRequest(int method, Type reflectType, String url, Response.Listener successListener, Response.ErrorListener listener) {
        super(method, reflectType, url, successListener, listener);
    }

//
//    protected String checkJsonData(String data,NetworkResponse response) {
//        try {
////            Logger.d("aaa","data = "+data );
////            JSONArray jsonArray = new JSONArray(data);
////            Logger.d("aaa", "jsonArray.getString(0 = " + jsonArray.getString(0));
//            JSONObject jsonObject = new JSONObject(data);
//            String code = jsonObject.optString("code", "");
//            Log.i("tag",code+"code");
//            if ("2000".equals(code)){
//                return jsonObject.optString("data","");
//            }else {
//                return "";
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public void  setKeyWordAndPageIndex(String pKeyWord,String pPageIndex){
        this.mKeyWord = pKeyWord;
        this.mPageIndex = pPageIndex;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        HashMap<String,String> params = new HashMap<>();
        params.put("keywords",this.mKeyWord);
        params.put("p", this.mPageIndex);
        return params;
    }
}
