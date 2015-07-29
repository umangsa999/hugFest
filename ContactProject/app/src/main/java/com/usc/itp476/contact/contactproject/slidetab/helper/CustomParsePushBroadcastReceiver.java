package com.usc.itp476.contact.contactproject.slidetab.helper;

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
    public static final String SCORE = "SCORE";
    public static final String INVITE = "INVITE";
    public static final String SCORERNAME = "NAME";
    public static final String ACTION = "ACTION";
    public static String action = "";
    private AllTabActivity mAllTabActivity;

    public final String TAG = this.getClass().getSimpleName();
    //The notification's contentIntent and deleteIntent are com.parse.push.intent.OPEN
    //com.parse.push.intent.DELETE respectively.

    //change reaction
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        //Called when the push notification is received.
    }

    //change reaction
    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
        //Called when the push notification is dismissed
        SharedPreferences.Editor editor = context.getSharedPreferences(AllTabActivity.MY_PREFS_NAME,
                context.MODE_PRIVATE).edit();
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
        Intent i = null;
        if( action.equals(END) ){
        }else if( action.equals(SCORE) ){
            try {
                String scorerID = json.getString(SCORERID);
                String scorerName = json.getString(SCORERID);
                //someone score on you
                editor.putString(SCORERID, scorerID);
                editor.putString(SCORERNAME, scorerName);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(action.equals(INVITE)){
        }
        else{
            Log.wtf(TAG, "Unknown action!!, (On Push Dismiss)");
        }
        editor.commit();
    }

    //change change reaction
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //super.onPushOpen(context, intent);
        //User has clicke open the notification
        Bundle mBundle = intent.getExtras();
        JSONObject json = null;
        if (mBundle != null) {
            try {
                json = new JSONObject(mBundle.getString(PARSE_EXTRA_DATA_KEY));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        Intent i = null;
        if( action.equals(END) ){
            //end of the game, start new resultActivity
            i = new Intent( context, ResultActivity.class);
            i.putExtras(intent.getExtras());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }else if( action.equals(SCORE) ){

            //TODO FIX
            //this means someone scored on you and you're in the game
            //get the old target Activity and display it

            Log.wtf( TAG +"Class is: ", getActivity(context, intent).getClass().getSimpleName() );

            try {
                String scoreID = json.getString(SCORERID);
                //i.putExtra(TargetActivity.SCORERID, scoreID); //might not need this
                //i.putExtras(intent.getExtras());
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //someone score on you
                i = new Intent( context, TargetActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(i);
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if(action.equals(INVITE)){

            //you got invited to a game, start new targetActivity stack
            i = new Intent( context, TargetActivity.class);
            i.putExtras(intent.getExtras());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        else{
            Log.wtf(TAG, "Unknown action!!");
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //A notification is received and the com.parse.push.intent.OPEN Intent is fired,
        //causing the ParsePushBroadcastReceiver to call onPushReceive

        //we get the action of what we need to do and set it {END, SCORE, INVITE}
        action = intent.getAction();
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