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
        if (!send.isNull("fURL")) {
            send.remove("fURL");
        }
        send.put("fURL", f);
        return this;
    }

    public HTTPCallerBuilder sURL(String s) throws JSONException{
        if (!send.isNull("sURL")) {
            send.remove("sURL");
        }
        send.put("sURL", s);
        return this;
    }

    public HTTPCallerBuilder id(String id) throws JSONException{
        if (!send.isNull("id")) {
            send.remove("id");
        }
        send.put("id", id);
        return this;
    }

    public HTTPCallerBuilder query(String key, String value) throws JSONException{
        if (send.isNull("query")){
            send.put("query", "?" + key + "=" + value);
        }else{ //append to the end of the last
            String que = send.getString("query");
            send.remove("query");
            que += "&" + key + "=" + value;
            send.put("query", que);
        }
        return this;
    }

    public HTTPCallerBuilder method(String m) throws JSONException {
        if (!send.isNull("method")) {
            send.remove("method");
        }
        send.put("method", m);
        return this;
    }

    public HTTPCallerBuilder data(String key, String value) throws JSONException{
        if (send.isNull("data")){
            send.put("data", "{ " + key + " : " + value + " }");
        }else{ //append to the end of the last
            String dat = send.getString("data");
            send.remove("data");
            dat = dat.substring(0, dat.length() - 1) +
                    " , " + key + " : " + value + "} ";
            send.put("data", dat);
        }
        return this;
    }

    public HTTPCaller executeHTTPCaller(){
        HTTPCaller h = new HTTPCaller(); //create the AsyncTask
        h.execute(send); //send the call on send
        Log.wtf(this.getClass().getSimpleName(), "send is: " + send);
        try {
            h.get(); //wait for reply
        }catch (Exception e) {
            Log.wtf("LOGINACTIVITY", e.getMessage());
        }
        return h;
    }
}