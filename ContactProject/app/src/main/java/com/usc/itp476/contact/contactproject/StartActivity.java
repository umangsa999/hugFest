package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.Arrays;
import java.util.List;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class StartActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_START_GAME = 1939;
    public static final int RESULT_ALLTABS_QUIT_STAY_LOGIN = 1945;
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;
    private EditText edtxPass;
    private String name;
    private String pass;
    final List<String> permissions = Arrays.asList("public_profile", "user_friends");
    private LoginButton loginButtonFacebook;
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback;
    private TwitterLoginButton loginButtonTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_start);

        //todo this might not be necessary with Parse local datastore
//        SharedPreferences sharedPreferences = getSharedPreferences(GameMarker.PREFFILE, MODE_PRIVATE);
//        String id = sharedPreferences.getString(GameMarker.USER_ID, null);
//
//        //the user has already logged in before
//        if (id != null){
//            goToHome();
//        }

        if (ParseUser.getCurrentUser() != null) {
            goToHome();
        }

        btnStart = (Button) findViewById(R.id.btnStart);
        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
        edtxLast = (EditText) findViewById(R.id.edtxLast);
        edtxPass = (EditText) findViewById(R.id.edtxPassword);
        loginButtonTwitter = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button);

        createDigitButton();
        createTwitterCallback();
        createFacebookCallback();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    //Called when facebook is done
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_ALLTABS_QUIT_STAY_LOGIN){
            finish();
        }else{
            mCallbackManager.onActivityResult( requestCode, resultCode, data);
            loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createDigitButton(){
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
    }

    private void createTwitterCallback(){
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
    }

    private void createFacebookCallback(){
        mCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.wtf(TAG, "Facebook success");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                if( profile != null){
                    Log.wtf(TAG, profile.getFirstName() + " "+ profile.getLastName() );
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
        loginButtonFacebook.setReadPermissions(permissions);
        loginButtonFacebook.registerCallback(mCallbackManager, mCallback );
    }

    private void check(){
        //name is incompatible
        name = edtxFirst.getText().toString() + " " + edtxLast.getText().toString();
        pass = edtxPass.getText().toString();
        if (edtxFirst.getText().length() == 0 ||
                edtxLast.getText().length() == 0 ||
                edtxPass.getText().length() == 0){
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid name.",
                            Toast.LENGTH_SHORT).show();
        }else{
            //TODO incorporate multiple people with same name
            ParseUser.logInInBackground(name, pass, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(),
                                "Welcome back,\n" + user.getUsername(),
                                Toast.LENGTH_SHORT).show();
                        goToHome();
                    } else {
                        saveParse();
                    }
                }
            });
        }
    }

    //TODO might not be needed anymore due to local data
    private void saveLocal(){
        //TODO Make so that user can continue from last time as opposed to always resetting
        //save a working name
        SharedPreferences sharedPreferences =
                getSharedPreferences(GameMarker.PREFFILE, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

        sharedPrefEditor.putString(GameMarker.FULL_NAME, name);
        sharedPrefEditor.putInt(GameMarker.TOTAL_HUGS, 0);

        //save the user's name asynchronously
        sharedPrefEditor.apply();
    }

    private void saveParse(){
        ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setPassword(pass);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {//TODO incorporate multiple people with same name
                if (e == null) {
                    // Hooray! Let them use the app now.
                    saveLocal();
                    goToHome();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(getApplicationContext(),
                            "Sorry, that user already exists",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToHome(){
        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivityForResult(i, REQUEST_START_GAME);
    }
}