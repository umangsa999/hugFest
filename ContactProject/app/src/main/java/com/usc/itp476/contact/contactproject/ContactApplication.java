package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ContactApplication extends Application {
	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Ai9qe71kw0YSzWrsOhsqOGzJB";
    private static final String TWITTER_SECRET = "VRYJY0hRozcEupHlTNus18RbSxiLE5ioKkVCRZmjUF4ErKqL59";
    private static HashMap<String, ParseUser> friendList;
    public final String TAG = this.getClass().getSimpleName();
	
    private static ContactApplication singleton;

    public ContactApplication getSingleton(){
        return singleton;
    }

    public static HashMap<String, ParseUser> getFriendsList(){
        return friendList;
    }

    public void setFriendsList(HashMap<String, ParseUser> inList){
        friendList = inList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        ParseObject.registerSubclass(GameMarker.class);
        ParseObject.registerSubclass(GameData.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this,
                "ellChjDHP7hNM4CBQLHrBNWzDMoOzElwUgy3MpEc",
                "aXSv9sdHcVcnjSIaqy8KuymGh16K5I53MiWXGgnN");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseFacebookUtils.initialize(this); //For converting authenticated FB users to Parse users
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
        // subscribe to the channels
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if( e == null){
                    Log.wtf(TAG, "Success done" );
                }else{
                    Log.wtf(TAG, e.getLocalizedMessage() );
                }

            }
        });

        //get set of channels subscribed to
        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
        //TODO subscribe to more as necessary

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new com.twitter.sdk.android.Twitter(authConfig), new Crashlytics(), new Twitter(authConfig));

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
        friendList = new HashMap<>();
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

}
