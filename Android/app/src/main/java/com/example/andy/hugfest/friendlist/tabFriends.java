package com.example.andy.hugfest.friendlist;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.andy.hugfest.HTTPCaller;
import com.example.andy.hugfest.HTTPCallerBuilder;
import com.example.andy.hugfest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class tabFriends extends Activity {
    private ListView listViewFriends;
    public static final String PREFS_FILE = "userPrefFile";
    public static final String USERID = "com.example.andy.hugfest.USERID";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabfriends);

        listViewFriends = (ListView)findViewById(R.id.listView5);

        //SharedPreferences sp = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        //String id = sp.getString(USERID, null );
        String id = "559efd6f25b7daf82691a703"; //hard code test
        Log.wtf(this.getClass().getSimpleName(), "about to execute");
        if (id == null){
            Log.wtf(this.getClass().getSimpleName(), "no ID, user is actually logged out");
            //TODO create some kind of view for not logged in
        }else{
            new FriendIDObtain().execute(id);
        }
    }

    private class FriendIDObtain extends AsyncTask<String, Integer, ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> d = new ArrayList<>();
            try {
                //do a query for this user's friends
                Log.wtf(this.getClass().getSimpleName(), "about to query");
                HTTPCaller h = (new HTTPCallerBuilder()).method("GET").fURL("friends").query("id", params[0]).executeHTTPCaller();
                JSONObject obj = h.getServerResult();
                if (obj == null)
                    return null;
                Log.wtf(this.getClass().getSimpleName(), "query complete: " + obj.toString());
                JSONArray arr = obj.getJSONArray("result");
                for (int i = 0; i < arr.length(); ++i){
                    if (arr.get(i) != null){
                        d.add((String) arr.get(i)); //we get back a list of
                        Log.wtf(this.getClass().getSimpleName(), "added " + i);
                    }else{
                        Log.wtf(this.getClass().getSimpleName(), "null object in array");
                    }
                }
                Log.wtf(this.getClass().getSimpleName(), "about to return inBackground");
                return d;
            } catch (JSONException e) {
                Log.wtf(this.getClass().getSimpleName(), "FriendIDObtain failed to query friends: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            Log.wtf(this.getClass().getSimpleName(), "about to postExecute");
            if (strings == null)
                return;

            ArrayList<JSONObject> d = new ArrayList<>();
            for (String s : strings){
                Log.wtf(this.getClass().getSimpleName(), "friend s is: " + s);
                try { //query each userID to get their actuall data
                    HTTPCaller h = (new HTTPCallerBuilder()).fURL("user").query("id", s).executeHTTPCaller();
                    JSONObject obj = h.getServerResult();
                    if (obj != null) {
                        d.add(obj);
                        Log.wtf(this.getClass().getSimpleName(), "added friend " + s);
                    }else{
                        Log.wtf(this.getClass().getSimpleName(), "null Friend from array, ID: " + s);
                    }
                } catch (JSONException e) {
                    Log.wtf(this.getClass().getSimpleName(), "FriendIDObtain failed with friendID: " + s + e.getMessage());
                }
            }
            //adapt to show these friends
            listViewFriends.setAdapter(new FriendsListAdapter(tabFriends.this, d));
            Log.wtf(this.getClass().getSimpleName(), "adapter set");
        }
    }
}