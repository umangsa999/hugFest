package com.usc.itp476.contact.contactproject.slidetab.helper;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

public class CustomParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String RECEIVE = "com.parse.push.intent.RECEIVE";
    public static  final String OPEN ="com.parse.push.intent.OPEN";
    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final String PARSE_JSON_CHANNEL_KEY = "com.parse.Channel";
    public static final String EVENT = "EVENT";
    public static final String HIT = "HIT";
    public static final String END = "END";
    public static final String FRIEND = "FRIEND";
    public static final String RESULTS = "RESULTS";
    public final String TAG = this.getClass().getSimpleName();
    //The notification's contentIntent and deleteIntent are com.parse.push.intent.OPEN
    //com.parse.push.intent.DELETE respectively.

    //change reaction
    @Override
    protected void onPushReceive(Context context, Intent intent) {

        //Called when the push notification is received.
        //tell if its the game ending or you got hugged
        String channel = intent.getExtras().getString(PARSE_JSON_CHANNEL_KEY);
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
            String event = json.getString(EVENT);
            if( event.equals(HIT) ){
                Log.wtf(TAG, "OPR HIT");
                //you just got tagged by another player
                //get the player's name and display it
            }else if(event.equals(END) ){
                //game has ended, display results
                //Get URI to results screen
                Log.wtf(TAG, "OPR END");
            }
            super.onPushReceive(context, intent);
        }catch (Exception e){
            Log.wtf(TAG, e.getLocalizedMessage());
        }
        //http://stackoverflow.com/questions/28959811/how-to-override-onpushreceive-of-parsepushbroadcastreceiver
    }

    //change reaction
    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        Log.wtf(TAG, "OPD before");
        super.onPushDismiss(context, intent);
        Log.wtf(TAG, "OPD after");
        //Called when the push notification is dismissed
    }

    //change change reaction
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
        //Called when the push notification is opened by the user.
        //TODO - bring user to the results page/friend profile page
        Log.wtf(TAG, "OPO");
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
            String event = json.getString(EVENT);
            if( event.equals(FRIEND) ){
                //get the URI to go to the friend's profile pic page
                Log.wtf(TAG, "OPO friend");
            }else if(event.equals(RESULTS) ){
                Log.wtf(TAG, "OPO results");
                //game has ended, display results
                //Get URI to results screen
            }
        }catch (Exception e){
            Log.wtf(TAG, e.getLocalizedMessage());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //A notification is received and the com.parse.push.intent.OPEN Intent is fired,
        //causing the ParsePushBroadcastReceiver to call onPushReceive.
        //TODO -  interpret then pick which method you want to do
        Log.wtf(TAG, "OR");
        String action = intent.getAction();
        if( action.equals(RECEIVE) ){
            //update game data
            Log.wtf(TAG, "OR receive");
            super.onReceive(context, intent);
        }else if( action.equals(OPEN) ){
            Log.wtf(TAG, "OR OPEN");
            super.onReceive(context, intent);
        }else{
            Log.wtf(TAG, "Unknown action!!");
            super.onReceive(context, intent);
        }
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        return super.getLargeIcon(context, intent);
        //Retrieves the large icon to be used in a Notification.
    }

    //change the text
    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Log.wtf(TAG, "getNotification");
        return super.getNotification(context, intent);
        //If either "alert" or "title" are specified in the push
        //then a Notification is constructed using getNotification
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return super.getSmallIconId(context, intent);
        //Retrieves the small icon to be used in a Notification.
    }

    //change which activity opens, URI is passed in to tell where or no URI for default start
    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return super.getActivity(context, intent);
    }
}