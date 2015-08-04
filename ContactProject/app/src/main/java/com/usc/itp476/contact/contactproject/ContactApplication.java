package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.POJO.GamePhoto;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;

public class ContactApplication extends Application {
	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    public static final int MAX_PLAYERS = 20;

    public static final int REQUEST_CODE_CREATE_GAME = -499;
    public static final int RETURN_FROM_RESULT = 80085;
    public static final int RETURN_FROM_QUIT_GAME = 58008;
    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int COMPRESS_QUALITY = 100;
    public static final int DEFAULT_IMAGE_SIZE = 700;

    public static final String IS_LOGGED_IN = "com.usc.itp476.contact.contactproject.IS_LOGGED_IN";
    public static final String MAXPOINTS = "com.usc.itp476.contact.contactproject.MAXPOINTS";
    public static final String JOINEDGAME = "com.usc.itp476.contact.contactproject.JOINEDGAME";
    public static final String GAMEID = "com.usc.itp476.contact.contactproject.GAMEID";
    public static final String SCORERID = "com.usc.itp476.contact.contactproject.SCORERID";
    public static final String SCOREEENAME = "com.usc.itp476.contact.contactproject.SCOREEENAME";
    public static final String NAME = "com.usc.itp476.contact.contactproject.NAME";
    public static final String IMAGENAME = "com.usc.itp476.contact.contactproject.TARGETACTIVITY.IMAGENAME";
    public static final String CURRENTPHOTOPATH = "com.usc.itp476.contact.contactproject.TARGETACTIVITY.CURRENTPHOTOPATH";
    public static final String SHARED_PREF_FILE = "com.usc.itp476.contact.contactproject.ContactApplication.SHARED_PREF_FILE";
    public static String TAG = null;
    public static ParseACL defaultACL;
	
    private static ContactApplication singleton;
    private static HashMap<String, ParseUser> friendList;

    public ContactApplication getSingleton(){
        return singleton;
    }

    public static HashMap<String, ParseUser> getFriendsList(){
        return friendList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        ParseObject.registerSubclass(GameMarker.class);
        ParseObject.registerSubclass(GameData.class);
        ParseObject.registerSubclass(GamePhoto.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this,
                getResources().getString(R.string.application_id),
                getResources().getString(R.string.client_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(this); //For converting authenticated FB users to Parse users
        defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        TAG = this.getClass().getSimpleName();
        subscribeInstallation();
        friendList = new HashMap<>();

    }

    private void subscribeInstallation(){
        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
        // subscribe to the channels

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();
            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            Log.e("Package Name=", context.getApplicationContext().getPackageName());
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (Exception e1) {
            Log.e("Name not found", e1.toString());
        }
        return key;
    }

    public static void callCloud(String playerID, boolean didFinishGame, final String gameID){
        HashMap<String, String> params = new HashMap<>();
        params.put("playerID", playerID);
        params.put("didFinishGame", Boolean.toString(didFinishGame));
        ParseCloud.callFunctionInBackground("removePlayerFromGame", params, new FunctionCallback<JSONObject>() {
            @Override
            public void done(JSONObject obj, ParseException e) {
                HashMap<String, String> params = new HashMap<>();
                params.put("gameID", gameID);
                ParseCloud.callFunctionInBackground("checkDeleteGame", params, new FunctionCallback<JSONObject>() {
                    @Override
                    public void done(JSONObject jsonObject, ParseException e) {
                        //we don't care what happens, we just want it done
                    }
                });
            }
        });
    }
}
