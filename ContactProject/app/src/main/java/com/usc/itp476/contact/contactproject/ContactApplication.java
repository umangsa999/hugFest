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
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.security.MessageDigest;

import io.fabric.sdk.android.Fabric;

public class ContactApplication extends Application {
	// Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Ai9qe71kw0YSzWrsOhsqOGzJB";
    private static final String TWITTER_SECRET = "VRYJY0hRozcEupHlTNus18RbSxiLE5ioKkVCRZmjUF4ErKqL59";
	
    private static ContactApplication singleton;

    public ContactApplication getSingleton(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        ParseObject.registerSubclass(GameMarker.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this,
                "ellChjDHP7hNM4CBQLHrBNWzDMoOzElwUgy3MpEc",
                "aXSv9sdHcVcnjSIaqy8KuymGh16K5I53MiWXGgnN");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new com.twitter.sdk.android.Twitter(authConfig), new Crashlytics(), new Twitter(authConfig));
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
