package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.usc.itp476.contact.contactproject.ingamescreen.ResultActivity;
import com.usc.itp476.contact.contactproject.ingamescreen.TargetActivity;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import org.json.JSONObject;

public class CustomParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {
    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final String PARSE_JSON_CHANNEL_KEY = "com.parse.Channel";
    public static final String END = "END";
    public static final String SCORERID = "SCOREID";
    public static final String SCOREEENAME = "SCOREEENAME";
    public static final String SCORE = "SCORE";
    public static final String INVITE = "INVITE";
    public static final String NAME = "NAME";
    public static final String SCORERNAME = "NAME";
    public static final String ACTION = "ACTION";
    public static final String GAMEID = "GAMEID";
    public static final String MAXPOINTS = "MAXPOINTS";
    public static String action = "";
    private AllTabActivity mAllTabActivity;

    public final String TAG = this.getClass().getSimpleName();
    //The notification's contentIntent and deleteIntent are com.parse.push.intent.OPEN
    //com.parse.push.intent.DELETE respectively.

    //change reaction
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        /*Called when the push notification is received.
        This is called second, from here either onPushDismiss/Open will be called */
        super.onPushReceive(context, intent);
    }

    //change reaction
    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
        /*Called when the push notification is dismissed
        The user has dismissed the notif, we need still to set the activity they should get
        We saved this data into shared preferences for when the activity is resumed*/

        //TODO - set that user has NOT dismissed, so load the results screen
        SharedPreferences.Editor editor =
                context.getSharedPreferences(ContactApplication.SHARED_PREF_FILE,
                Context.MODE_PRIVATE).edit();
        editor.putString(ACTION, action);
        Bundle mBundle = intent.getExtras();
        JSONObject json = null;
        if (mBundle != null) {
            try {
                json = new JSONObject(mBundle.getString(PARSE_EXTRA_DATA_KEY));
            }catch(Exception e){
                e.printStackTrace();
            }
            editor.commit();
        }
        if( action.equals(END) ){
        }else if( action.equals(SCORE) ){
            try {
                //someone scored on you
                String scorerID = json.getString(SCORERID);
                String scorerName = json.getString(SCORERID);
                editor.putString(SCORERID, scorerID);
                editor.putString(SCORERNAME, scorerName);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(action.equals(INVITE)){
            //if someone dismisses our invite then we don't join the game
        } else{
            Log.wtf(TAG, "Unknown action!!, (On Push Dismiss)");
        }
        editor.commit();
    }

    //change change reaction
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //super.onPushOpen(context, intent);
        //User has clicked open the notification
        Bundle mBundle = intent.getExtras();
        JSONObject json = null;
        if (mBundle != null) {
            try {
                json = new JSONObject(mBundle.getString(PARSE_EXTRA_DATA_KEY));
                Log.wtf(TAG, json.toString() );
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        Intent i = null;
        if( action.equals(END) ){
            //end of the game, start new resultActivity
            i = new Intent(context, ResultActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            try {
                i.putExtra(TargetActivity.GAMEID, json.getString(GAMEID) );
            }catch(Exception e){
                e.printStackTrace();
            }
            context.startActivity(i);
        }else if( action.equals(SCORE) ){

            //this means someone scored on you and you're in the game
            //get the old target Activity and display it
            Log.wtf( TAG +"Class is: ", getActivity(context, intent).getClass().getSimpleName() );
            try {
                String scorerID = json.getString(SCORERID);
                String scoreeeName = json.getString(SCOREEENAME);
                String name = json.getString(NAME);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //someone score on you
                i = new Intent( context, TargetActivity.class);
                i.putExtra(TargetActivity.SCORERID, scorerID ); //might not need this
                i.putExtra(TargetActivity.SCOREEENAME, scoreeeName); //might not need this
                i.putExtra(TargetActivity.NAME, name); //might not need this
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(i);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(action.equals(INVITE)){

            //you got invited to a game, start new targetActivity stack
            i = new Intent( context, TargetActivity.class);
            try {
                i.putExtra(TargetActivity.MAXPOINTS, json.getInt(MAXPOINTS));
                i.putExtra(TargetActivity.GAMEID, json.getString(GAMEID));
            }catch(Exception e){
                e.printStackTrace();
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else{
            Log.wtf(TAG, "Unknown action!!");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /*A notification is received and the com.parse.push.intent.OPEN Intent is fired,
        causing the ParsePushBroadcastReceiver to call onPushReceive
        This is always called FIRST*/

        //we get the action of what we need to do and set it {END, SCORE, INVITE}
        Bundle mBundle = intent.getExtras();
        JSONObject json = null;
        if (mBundle != null) {
            try {
                json = new JSONObject(mBundle.getString(PARSE_EXTRA_DATA_KEY));
                action = json.getString("action");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        //Now we call super, which will call method onPushReceive
        super.onReceive(context, intent);
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