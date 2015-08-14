// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Chris Lee and Ryan Zhou</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class ResultActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();
    private final int TOTAL_DISPLAY = 4;
    private ArrayList<HashMap<String, Object>> topPlayersList = null;
    private ImageView images[] = new ImageView[TOTAL_DISPLAY];
    private TextView names[] = new TextView[TOTAL_DISPLAY];
    private String gameID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //This cannot be looped because each one has a slightly different name
        images[0] = (ImageView) findViewById(R.id.imageViewWinnerImage);
        images[1] = (ImageView) findViewById(R.id.imageViewLeftImage);
        images[2] = (ImageView) findViewById(R.id.imageViewMiddleImage);
        images[3] = (ImageView) findViewById(R.id.imageViewRightImage);

        names[0] = (TextView) findViewById(R.id.imageViewWinnerName);
        names[1] = (TextView) findViewById(R.id.imageViewLeftName);
        names[2] = (TextView) findViewById(R.id.imageViewMiddleName);
        names[3] = (TextView) findViewById(R.id.imageViewRightName);

        if (savedInstanceState == null){
            Intent i = getIntent();
            //expect gameID from whoever pushed us to this activity
            gameID = i.getStringExtra(ContactApplication.GAMEID);
        }else{
            //actually came from switching apps, so we have to pull the data from our local storage
            gameID = savedInstanceState.getString(ContactApplication.GAMEID);
        }

        if (gameID == null){
            Toast.makeText(getApplicationContext(),
                    "Woops, looks like you got pushed out of the game", Toast.LENGTH_SHORT).show();
            //leave the screen when accidentally pushed here
            finish();
        }else{
            //Stop user from receiving any more notifications from the game
            // this allows reused gameIDs to not overlap with people who never left old completed games
            ParsePush.unsubscribeInBackground("game" + gameID);
            getPlayerScores();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //We need to save this so that when we switch out of this app and return,
        //we can pull data from the Parse server and get the leaderboard again
        outState.putString(ContactApplication.GAMEID, gameID);
    }

    private void getPlayerScores(){
        //We have to explicitly call the server to get the results
        //They are not given through the push notification because that defeats the purpose of small, lightweight signals
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
        //When we quit from the game, we make sure to remove the player properly
        ContactApplication.callCloud(ParseUser.getCurrentUser().getObjectId(), true, gameID);
        Intent intent = new Intent();
        setResult(ContactApplication.RETURN_FROM_RESULT, intent);
        this.finish();
    }

    private void updateUI(){
        //Each section is different
        //0 = winner, 1 = left, 2 = middle, 3 = right
        //We have to load each image using Glide and each name from our data which was pulled from Parse
        //When there are less than 4 players total, it is fair to leave the name empty and we would display an image signifying no player
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