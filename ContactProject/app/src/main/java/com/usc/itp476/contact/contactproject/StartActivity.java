package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class StartActivity extends FragmentActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Ai9qe71kw0YSzWrsOhsqOGzJB";
    private static final String TWITTER_SECRET = "VRYJY0hRozcEupHlTNus18RbSxiLE5ioKkVCRZmjUF4ErKqL59";

    final String TAG = this.getClass().getSimpleName();
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;
    private SignInButton loginGoogle;
    final List<String> permissions = Arrays.asList("public_profile", "user_friends");
    private LoginButton loginButtonFacebook;
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback;
    private TwitterLoginButton loginButtonTwitter;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences(GameData.PREFFILE, Context.MODE_PRIVATE);
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(StartActivity.this);
    }

    //Causes app to crash
    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(StartActivity.this);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //findViewById(R.id.sign_in_button).setOnClickListener(this);
        setContentView(R.layout.activity_start);

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session and phone number
                Log.wtf(TAG, "DigitsAuth success");
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
                Log.wtf(TAG, "DigitsAuth Fail");
            }
        });

        loginButtonTwitter = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        loginButtonTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Log.wtf(TAG, "Twitter success");
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.wtf(TAG, "Twitter fail");
            }
        });

        mCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.wtf(TAG, "success");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if( profile != null){
                    //profile.
                }
            }

            @Override
            public void onCancel() {
                Log.wtf(TAG, "cancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.wtf(TAG, "error");
            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setFragment(this);
        loginButtonFacebook.setReadPermissions(permissions);
        loginButtonFacebook.registerCallback(mCallbackManager, mCallback );

        Log.wtf(TAG, printKeyHash(this));
//
//        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this, "ellChjDHP7hNM4CBQLHrBNWzDMoOzElwUgy3MpEc", "aXSv9sdHcVcnjSIaqy8KuymGh16K5I53MiWXGgnN");
//        //String appId = getString(R.string.app_id);
//        ParseFacebookUtils.initialize(this);
//
//        loginButton = (LoginButton) findViewById(R.id.login_button);
//
//        // Check if there is current user info
//        if (ParseUser.getCurrentUser() != null) {
//            // Start an intent for the logged in activity
//            Log.d("F8Debug", "onCreate, got user,  "
//                    + ParseUser.getCurrentUser().getUsername());
//            startActivity(new Intent(this, AllTabActivity.class));
//        } else {
//            // Start and intent for the logged out activity
//            Log.d("F8Debug", "onCreate, no user");
//            //startActivity(new Intent(this, SignInActivity.class));
//        }
//        final List<String> permissions = Arrays.asList("public_profile");
//        permissions.add("user_friends");
//
//        SharedPreferences sharedPreferences = getSharedPreferences(GameData.PREFFILE, MODE_PRIVATE);
//        String id = sharedPreferences.getString(GameData.USER_ID, null);
//
//        //the user has already logged in before
//        if (id != null){
//            goToHome();
//        }
//
//        btnStart = (Button) findViewById(R.id.btnStart);
//        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
//        edtxLast = (EditText) findViewById(R.id.edtxLast);
//
//
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ParseUser user = new ParseUser();
//                user.setUsername("my name");
//                user.setPassword("my pass");
//                user.setEmail("email@example.com");
//
//                // other fields can be set just like with ParseObject
//                user.put("phone", "650-555-0000");
//
//                //log them in automatically
//                ParseFacebookUtils.logInWithReadPermissionsInBackground(
//                        StartActivity.this, permissions, new LogInCallback() {
//                            @Override
//                            public void done(ParseUser user, ParseException err) {
//                        if (user == null) {
//                            Log.wtf("MyApp", "Uh oh. The user cancelled the Facebook login.");
//                        } else if (user.isNew()) {
//                            Log.wtf("MyApp", "User signed up and logged in through Facebook!");
//                        } else {
//                            Log.wtf("MyApp", "User logged in through Facebook!");
//                        }
//                    }
//                });
//
////                user.signUpInBackground(new SignUpCallback() {
////                    public void done(ParseException e) {
////                        if (e == null) {
////                            // Hooray! Let them use the app now.
////                        } else {
////                            // Sign up didn't succeed. Look at the ParseException
////                            // to figure out what went wrong
////                        }
////                    }
////                });
//
//                //name is incompatible
//                if (edtxFirst.getText().length() == 0 || edtxLast.getText().length() == 0){
////                    Toast.makeText(getApplicationContext(),
////                            "Please enter a valid name.",
////                            Toast.LENGTH_SHORT).show();
////                    return;
//                    Toast.makeText(getApplicationContext(),
//                            "Assuming debugging.",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//                //TODO Make so that user can continue from last time as opposed to always resetting
//                //save a working name
//                SharedPreferences sharedPreferences =
//                        getSharedPreferences(GameData.PREFFILE, MODE_PRIVATE);
//                SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
//
//                sharedPrefEditor.putString(GameData.FULL_NAME,
//                        edtxFirst.getText().toString() + " " + edtxLast.getText().toString());
//
//                //add a random total hugs for now
//                //TODO set to 0
//                sharedPrefEditor.putInt(GameData.TOTAL_HUGS, (int)(Math.random() * 200));
//
//                //TODO add ID here
//
//                //save the user's name asynchronously
//                sharedPrefEditor.apply();
//
//                //goToHome();
//            }
//        });
    }

    //Called when facebook is done
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult( requestCode, resultCode, data);
        loginButtonTwitter.onActivityResult(requestCode, resultCode, data);

    }
    private void goToHome(){
        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivity(i);
    }
}