package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();
    private final int TOTAL_DISPLAY = 4;
    private ImageView images[] = new ImageView[TOTAL_DISPLAY];
    private TextView names[] = new TextView[TOTAL_DISPLAY];
    private ArrayList<HashMap<String, Object>> topPlayersList = null;
    private String gameID = null;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        images[0] = (ImageView) findViewById(R.id.imageViewWinnerImage);
        images[1] = (ImageView) findViewById(R.id.imageViewLeftImage);
        images[2] = (ImageView) findViewById(R.id.imageViewMiddleImage);
        images[3] = (ImageView) findViewById(R.id.imageViewRightImage);

        names[0] = (TextView) findViewById(R.id.imageViewWinnerName);
        names[1] = (TextView) findViewById(R.id.imageViewLeftName);
        names[2] = (TextView) findViewById(R.id.imageViewMiddleName);
        names[3] = (TextView) findViewById(R.id.imageViewRightName);

        loaded = true;
        if (savedInstanceState == null){
            Intent i = getIntent();
            //expect gameID from somewhere before, may be push notification
            gameID = i.getStringExtra(ContactApplication.GAMEID);
        }else{
            gameID = savedInstanceState.getString(ContactApplication.GAMEID);
        }

        if (gameID == null){
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
            gameID = sharedPreferences.getString(ContactApplication.GAMEID, "");
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
        sharedPreferencesEditor.putString(ContactApplication.GAMEID, gameID);
        sharedPreferencesEditor.apply();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ContactApplication.GAMEID, gameID);
    }

    private void getPlayerScores(){
        HashMap<String, String> params = new HashMap<>();
        params.put("gameID", gameID);
        ParseCloud.callFunctionInBackground("getPlayerEndScores", params,
                new FunctionCallback<ArrayList<HashMap<String, Object>>>() {
                    @Override
                    public void done(ArrayList<HashMap<String, Object>> list, ParseException e) {
                        if (e == null) {
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
        ContactApplication.callCloud(ParseUser.getCurrentUser().getObjectId(), true, gameID);
        Intent intent = new Intent();
        setResult(ContactApplication.RETURN_FROM_RESULT, intent);
        this.finish();
    }

    private void updateUI(){
        //Each section is different
        //0 = winner, 1 = left, 2 = middle, 3 = right
        for (int i = 0; i < TOTAL_DISPLAY; ++i){
            if (topPlayersList.size() >= i + 1) { //if this player is actually in the game
                HashMap<String, Object> player = topPlayersList.get(i);
                if (player.containsKey("pictureLink")) {
                    Glide.with(this).load(player.get("pictureLink").toString()).error(R.mipmap.large).into(images[i]);
                }
                names[i].setText(player.get("name").toString());
            }else{
                names[i].setText("");
            }
        }
    }
}