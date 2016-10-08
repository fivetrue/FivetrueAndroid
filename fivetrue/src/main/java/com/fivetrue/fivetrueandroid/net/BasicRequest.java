package com.fivetrue.fivetrueandroid.net;

import android.content.Context;
import android.util.Log;


import com.android.volley.VolleyError;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by kwonojin on 16. 3. 17..
 */
public abstract class BasicRequest<T> extends BaseApiRequest {

    private static final String TAG = "BasicRequest";

    private static final String KEY_APP_ID = "Application-ID";
    private static final String KEY_APP_KEY = "Application-Key";

    private static final String PAGE = "page";
    private static final String COUNT = "count";
    private static final String ORDER = "order";

    private BaseApiResponse.OnResponseListener<T> mOnResponseListener = null;


    public BasicRequest(Context context, String url, BaseApiResponse.OnResponseListener<T> responseListener) {
        super(context, url, null);
        setResponse(baseApiResponse);
        mOnResponseListener = responseListener;
        getHeaders().put(KEY_APP_ID, getContext().getPackageName());
        getHeaders().put(KEY_APP_KEY, "com.fivetrue");
    }

    private BaseApiResponse<T> baseApiResponse = new BaseApiResponse<>(new BaseApiResponse.OnResponseListener<T>() {
        @Override
        public void onResponse(BaseApiResponse<T> response) {
            if(mOnResponseListener != null){
                mOnResponseListener.onResponse(response);
            }
        }

        @Override
        public void onError(VolleyError error) {
            if(mOnResponseListener != null){
                mOnResponseListener.onError(error);
            }
        }
    }, getClassType());


    protected abstract Type getClassType();

    public void setCount(int count){
        putParam(COUNT, count + "");
    }

    public int getCount(){
        return convertInteger(getParams().get(COUNT));
    }

    public int getPage(){
        return convertInteger(getParams().get(PAGE));
    }

    private int convertInteger(String string){
        int count = 0;
        if(string != null){
            try{
                count = Integer.parseInt(string.trim());
            }catch (NumberFormatException e){
                Log.w(TAG, "Can not convert String to Integer: ", e);
            }
        }else{
            Log.d(TAG, "convertInteger: string is null");
        }
        return count;
    }

    public void setPage(int page){
        putParam(PAGE, page + "");
    }

    public void setObject(Object object){
        if(object != null){
            Field[] fields = object.getClass().getDeclaredFields();
            for(Field f : fields){
                f.setAccessible(true);
                try {
                    Object value = f.get(object);
                    String key = f.getName();
                    if(value != null && value instanceof String){
                        Log.i(TAG, "setObject: key / value = " + key + " / " + value.toString());
                        getParams().put(key, (String) value);
                    }else{
                        getParams().put(key, String.valueOf(value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
