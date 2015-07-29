package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.PicassoTrustAll;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private ImageView images[] = new ImageView[4];
    private TextView names[] = new TextView[4];
    private ArrayList<HashMap<String, Object>> topPlayersList;
    private String gameID = null;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        images[0] = (ImageView) findViewById(R.id.resultsWinnerImage);
        images[1] = (ImageView) findViewById(R.id.resultsLeftImage);
        images[2] = (ImageView) findViewById(R.id.resultsMiddleImage);
        images[3] = (ImageView) findViewById(R.id.resultsRightImage);

        names[0] = (TextView) findViewById(R.id.resultsWinnerName);
        names[1] = (TextView) findViewById(R.id.resultsLeftName);
        names[2] = (TextView) findViewById(R.id.resultsMiddleName);
        names[3] = (TextView) findViewById(R.id.resultsRightName);

        loaded = true;
        if (savedInstanceState == null){
            Intent i = getIntent();
            //expect gameID from somewhere before, may be push notification
            gameID = i.getStringExtra(TargetActivity.GAMEID);
        }else{
            gameID = savedInstanceState.getString(TargetActivity.GAMEID);
        }

        if (gameID == null){
            Log.wtf(TAG, "terribad");
            Toast.makeText(getApplicationContext(),
                    "Woops, looks like you got pushed out of the game", Toast.LENGTH_SHORT).show();
        }else{
            ParsePush.unsubscribeInBackground("game" + gameID);
            getPlayerScores();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loaded){
            SharedPreferences sharedPreferences =
                    getSharedPreferences(ContactApplication.SHARED_PREF_FILE, MODE_PRIVATE);
            gameID = sharedPreferences.getString(TargetActivity.GAMEID, "");
            loaded = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        loaded = false;

        SharedPreferences sharedPreferences =
                getSharedPreferences(ContactApplication.SHARED_PREF_FILE, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(TargetActivity.GAMEID, gameID);
        sharedPreferencesEditor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TargetActivity.GAMEID, gameID);
    }

    private void getPlayerScores(){
        HashMap<String, String> params = new HashMap<>();
        params.put("gameID", gameID);
        ParseCloud.callFunctionInBackground("getPlayerEndScores", params,
                new FunctionCallback<ArrayList<HashMap<String, Object>>>() {
                    @Override
                    public void done(ArrayList<HashMap<String, Object>> list, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < list.size(); ++i) {
                                HashMap<String, Object> map = list.get(i);
//                            Object [] keys = map.keySet().toArray();
//                            Log.wtf(TAG, "item: " + i);
//                            for (Object o : keys){
//                                Log.wtf(TAG, "\tkey: " + o.toString());
//                            }
//                            Object [] values = map.values().toArray();
//                            for (Object o : values){
//                                Log.wtf(TAG, "\tvalues: " + o.toString());
//                            }
                            }
                            //store and update what we get
                            topPlayersList = list;
                            updateUI();
                        } else {
                            Log.wtf(TAG, "could not get players results: " + e.getLocalizedMessage());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        HashMap<String, String> params = new HashMap<>();
        params.put("playerID", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("removePlayerFromGame", params, new FunctionCallback<JSONObject>() {
            @Override
            public void done(JSONObject obj, ParseException e) {
                if (e != null){
                    Log.wtf(TAG, e.getLocalizedMessage());
                }
            }
        });
        Intent intent = new Intent();
        setResult(TargetActivity.RETURN_FROM_RESULT, intent);
        this.finish();
    }

    private void updateUI(){
        //Each section is different
        //0 = winner, 1 = left, 2 = middle, 3 = right
        for (int i = 0; i < 4; ++i){
            if (topPlayersList.size() >= i + 1) { //if this player is actually in the game
                HashMap<String, Object> player = topPlayersList.get(i);
                if (player.containsKey("pictureLink")) {
                    PicassoTrustAll.getInstance(getApplicationContext())
                            .load(player.get("pictureLink").toString()).fit().into(images[i]);
                }
                names[i].setText(player.get("name").toString());
            }else{
                //TODO change image to stock no image
                names[i].setText("");
            }
        }
    }
}