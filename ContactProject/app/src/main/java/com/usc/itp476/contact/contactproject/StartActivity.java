package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class StartActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;
    private EditText edtxPass;
    private String name;
    private String pass;
    private ParseUser user;
    private String userEmail;
    private JSONArray facebookIDs = null;

    private boolean hasParseAccount = false;
    //TODO -- if user has parseaccount and no facebook association, if they click fb, link it

    final List<String> permissions = Arrays.asList("public_profile", "user_friends", "email");
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
            //goToHome();
            ParseUser p = ParseUser.getCurrentUser();
            p.logOut();
        }else{

            //goToHome and log the person in
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
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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

    private void createTwitterCallback() {
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
            public void onSuccess(final LoginResult loginResult) {
                Log.wtf(TAG, "Facebook success");
                //TODO check if parseuser exists and just add FB to parse

                //createParseUser(loginResult);
                // App code
                final GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                Log.wtf(TAG, response.toString());
                                try {
                                    userEmail = object.get("email").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //Gets all friends that have logged in with the app
                                GraphRequestAsyncTask friends = new GraphRequest(
                                        AccessToken.getCurrentAccessToken(),
                                        "/me/friends",
                                        null,
                                        HttpMethod.GET,
                                        new GraphRequest.Callback() {
                                            public void onCompleted(GraphResponse response) {
                                                Log.wtf(TAG, response.toString());
                                                facebookIDs = response.getJSONArray();
                                                try {
                                                    if( facebookIDs!= null ){
                                                        for( int i = 0; i< facebookIDs.length(); i++)
                                                            Log.wtf(TAG, facebookIDs.get(i).toString() );
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                createParseUser(loginResult);
                                            }
                                        }
                                ).executeAsync();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();

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
        loginButtonFacebook.registerCallback(mCallbackManager, mCallback);
    }

    private void createParseUser(final LoginResult loginResult) {
        AccessToken accessToken = loginResult.getAccessToken();

        Date expirationDate = accessToken.getExpires();
        String token = accessToken.getToken();

        Set getRecentlyGrantedPermissions = loginResult.getRecentlyGrantedPermissions();
        Set getDeniedPermissions = loginResult.getRecentlyDeniedPermissions();
        Profile profile = Profile.getCurrentProfile();
        String lastName = profile.getLastName();
        String firstName = profile.getFirstName();
        String facebookID = profile.getId();
        Uri profileLink = profile.getLinkUri();
        Uri profilePictureUri = profile.getProfilePictureUri(150, 150);

        JSONObject facebookAuth = new JSONObject();
        JSONObject subData = new JSONObject();
        try {
            subData.put("id", facebookID);
            subData.put("access_token", token);
            subData.put("expiration_date", expirationDate.toString());
            facebookAuth.put("facebook", subData);
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }

        StringWriter out = new StringWriter();
        //Log.wtf(TAG, facebookAuth.toString() );

        String username = firstName +lastName;
        user = new ParseUser();
        user.setUsername(username);
        user.setPassword(username);
        //user.put("authData", facebookAuth);
        user.setEmail(userEmail);
        user.put("totalGames", 0);
        user.put("totalHugs", 0);

//        if( facebookIDs != null) {
//            for (int index = 0; index < facebookIDs.length(); index++) {
//
//            }
//        }
//        ParseUser.getQuery().whereEqualTo("name", "TestUser#" + i).getFirstInBackground(new GetCallback<ParseUser>() {
//            @Override
//            public void done(ParseUser parseUser, ParseException e) {
//                ParseUser me = ParseUser.getCurrentUser();
//                me.getRelation("friends").add(parseUser);
//                me.saveInBackground();
//            }
//        });

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {//TODO incorporate multiple people with same name
                if (e == null) {
                    // Hooray! Let them use the app now.
                    ParseFacebookUtils.linkInBackground(user, loginResult.getAccessToken(), new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.wtf(TAG, "Success save");
                            } else {
                                Log.wtf(TAG, "Not save");
                            }
                        }
                    });

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
        //user.setPassword(pass); //TODO random password generator

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
//            ParseUser.logInInBackground(name, pass, new LogInCallback() {
//                @Override
//                public void done(ParseUser user, ParseException e) {
//                    if (e == null) {
//                        Toast.makeText(getApplicationContext(),
//                                "Welcome back,\n" + user.getUsername(),
//                                Toast.LENGTH_SHORT).show();
//                        goToHome();
//                    } else {
//                        saveParse();
//                    }
//                }
//            });
        }
    }

//    For Android, your code to make your users query-able by Facebook ID would look like this:
//
//            ParseFacebookUtils.logIn(this, new LogInCallback() {
//        @Override
//        public void done(ParseUser user, ParseException error) {
//            // When your user logs in, immediately get and store its Facebook ID
//            if (user != null) {
//                getFacebookIdInBackground();
//            }
//        }
//    });
//
//    private static void getFacebookIdInBackground() {
//        Request.executeMeRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
//            @Override
//            public void onCompleted(GraphUser user, Response response) {
//                if (user != null) {
//                    ParseUser.getCurrentUser().put("fbId", user.getId());
//                    ParseUser.getCurrentUser().saveInBackground();
//                }
//            }
//        });
//    }
//    Then, when you are ready to search for your user's friends, you would issue another request:
//
//            Request.executeMyFriendsRequestAsync(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {
//
//        @Override
//        public void onCompleted(List<GraphUser> users, Response response) {
//            if (users != null) {
//                List<String> friendsList = new ArrayList<String>();
//                for (GraphUser user : users) {
//                    friendsList.add(user.getId());
//                }
//
//                // Construct a ParseUser query that will find friends whose
//                // facebook IDs are contained in the current user's friend list.
//                ParseQuery friendQuery = ParseQuery.getUserQuery();
//                friendQuery.whereContainedIn("fbId", friendsList);
//
//                // findObjects will return a list of ParseUsers that are friends with
//                // the current user
//                List<ParseObject> friendUsers = friendQuery.find();
//            }
//        }
//    });

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
        startActivity(i);
    }
}