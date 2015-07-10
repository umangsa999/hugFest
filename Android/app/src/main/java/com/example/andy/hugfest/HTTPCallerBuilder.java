package com.example.andy.hugfest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTPCallerBuilder {
    private JSONObject send;

    public HTTPCallerBuilder(){
        send = new JSONObject();
    }

    public HTTPCallerBuilder fURL(String f) throws JSONException{
        send.put("fURL", f);
        return this;
    }

    public HTTPCallerBuilder sURL(String s) throws JSONException{
        send.put("sURL", s);
        return this;
    }

    public HTTPCallerBuilder id(String id) throws JSONException{
        send.put("id", id);
        return this;
    }

    public HTTPCallerBuilder query(String key, String value) throws JSONException{
        if (send.isNull("query")){
            send.put("query", "?" + key + "=" + value);
        }else{
            String que = send.getString("query");
            send.remove("query");
            que += "&" + key + "=" + value;
            send.put("query", que);
        }
        return this;
    }

    public HTTPCallerBuilder method(String m) throws JSONException {
        send.put("method", m);
        return this;
    }

    public HTTPCallerBuilder data(String key, String value) throws JSONException{
        if (send.isNull("data")){
            send.put("data", "{ " + key + " : " + value + " }");
        }else{
            String dat = send.getString("data");
            send.remove("data");
            dat = dat.substring(0, dat.length() - 1) +
                    " , " + key + " : " + value + "} ";
            send.put("data", dat);
        }
        return this;
    }

    public HTTPCaller executeHTTPCaller(){
        HTTPCaller h = new HTTPCaller();
        h.execute(send);
        try {
            h.get();
        }catch (Exception e) {
            Log.wtf("LOGINACTIVITY", e.getMessage());
        }
        return h;
    }
}
