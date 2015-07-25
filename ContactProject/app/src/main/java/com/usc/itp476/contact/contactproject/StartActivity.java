package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
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
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StartActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_START_GAME = 1939;
    public static final int RESULT_ALLTABS_QUIT_STAY_LOGIN = 1945;
    public static final int RESULT_LOGOUT = 2000;
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;
    private EditText edtxPass;
    private String name;
    private String pictureURL;
    private String pass;
    private HashMap<String, ArrayList<ParseUser>> parsefriendIDs;
    int i = 1;
    private ProfileTracker mProfileTracker;
    private ParseUser user;
    private String userEmail;
    private String facebookID = "0";
    private JSONArray facebookIDs = null;
    private LoginResult mLoginResult = null;

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
            Log.wtf(TAG, "There IS a PU");
            ParseUser.logOut();
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

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                if( oldProfile != null){
                    Log.wtf("facebook - OLD profile", oldProfile.getFirstName());
                    Log.wtf("facebook - profile", oldProfile.getId());
                }
                if( newProfile != null){
                    //There is a new profile, set this to the old one
                    Log.wtf("facebook - NEW profile", newProfile.getFirstName());
                    Log.wtf("facebook - profile", newProfile.getId());
                    Profile.setCurrentProfile(newProfile);
                }
                mProfileTracker.stopTracking();
            }
        };

        mProfileTracker.startTracking();
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
        if (resultCode == RESULT_ALLTABS_QUIT_STAY_LOGIN){
            finish();
        }else{
            mCallbackManager.onActivityResult( requestCode, resultCode, data);
            loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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
            public void onSuccess(LoginResult loginResult) {
                Log.wtf(TAG, "Facebook success");
                mLoginResult = loginResult;
                AccessToken accessToken = mLoginResult.getAccessToken();
                /* TODO check if parseuser exists and just add FB to parse
                    TODO Just run this and check if it works - Chris */
                //We have the accessToken, now we want to do a graphRequest to get the user data
                findFacebookUserData();
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

    private void addFaceBookFriends(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject tempArray = response.getJSONObject();
                        try {
                            if( tempArray!= null ){
                                facebookIDs = tempArray.getJSONArray("data");
                                if( facebookIDs.length() > 0 ) {
                                    //actually have friends then proceed
                                    HashMap<String, Object> parms = new HashMap<String, Object>();
                                    parms.put("ids", facebookIDs);
                                    try {
                                        //pass in a HashMap, get back a HashMap
                                        parsefriendIDs = ParseCloud.callFunction("getParseFriendsFromFBID", parms);
                                    } catch (Exception e) {
                                        Log.wtf(TAG, e.getMessage());
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.wtf(TAG, e.getLocalizedMessage());
                        }
                      createFaceBookParseUser();
                    }
                }
        ).executeAsync();
    }

    private void findFacebookUserData(){

        //This method calls a graphrequest to get user id
        GraphRequest request = GraphRequest.newMeRequest(
            mLoginResult.getAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(
                        JSONObject object,
                        GraphResponse response) {
                    try {
                        userEmail = object.get("email").toString();
                        facebookID = object.get("id").toString();
                        name = object.get("name").toString();
                        pictureURL = object.getJSONObject("picture").getJSONObject("data").getString("url");
                        Log.wtf(TAG, pictureURL);
                    } catch (JSONException e) {
                        Log.wtf(TAG, "FindFBUSerData: "+e.getLocalizedMessage());
                    }
                    HashMap<String, Object> parms = new HashMap<String, Object>();
                    parms.put("facebookID", facebookID);
                    Log.wtf(TAG, "FB id searching for is: "+ facebookID);
                    String tokenID = null;
                    try {
                        tokenID = ParseCloud.callFunction("getUserSessionToken", parms);
                    } catch (ParseException e) {
                        //New user, so we need to do another request to get friends
                        Log.wtf(TAG, "Parseexception: "+ e.getMessage());
                        Log.wtf(TAG, "No existing user, create a new one");
                        addFaceBookFriends();
                        return;
                    }
                    if (tokenID != null) {
                        try {
                            //OLD USER
                            ParseUser.become(tokenID);
                            goToHome();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void createFaceBookParseUser() {
        //TODO - see if user granted all permissions
        Set getRecentlyGrantedPermissions = mLoginResult.getRecentlyGrantedPermissions();
        Set getDeniedPermissions = mLoginResult.getRecentlyDeniedPermissions();

        //Profile profile = Profile.getCurrentProfile();
        //V this method does not work ATM
        //String lastName = Profile.getCurrentProfile().getLastName();
        //String firstName = Profile.getCurrentProfile().getFirstName();
        //String facebookID =  Profile.getCurrentProfile().
        //Uri profileLink = profile.getLinkUri();
        //Uri profilePictureUri = profile.getProfilePictureUri(150, 150);

        //String username = firstName +lastName;
        user = new ParseUser();
        user.setUsername(name);
        user.put("name", name);
        user.setPassword(name); //TODO create a more complex password
        user.setEmail(userEmail);
        user.put("totalGames", 0);
        user.put("totalHugs", 0);
        user.put("facebookID", facebookID);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {//TODO incorporate multiple people with same name
                if (e == null) {
                    // Hooray! Let them use the app now.
                    ParseFacebookUtils.linkInBackground(user, mLoginResult.getAccessToken(), new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.wtf(TAG, "Success save");//cannot add friends until after signed up
                                ParseRelation<ParseUser> userFriends = user.getRelation("friends");
                                ArrayList<ParseUser> friends;
                                //Arrays in cloudcode are ArrayLists in Android
                                friends = parsefriendIDs.get("friends");
                                //add each friend individually and then save alltogether
                                for (int i = 0; i < friends.size(); ++i){
                                    userFriends.add(friends.get(i));
                                }

                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            goToHome();
                                        } else {
                                            Log.wtf(TAG, e.getLocalizedMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.wtf(TAG, "Not save");
                                Log.wtf(TAG, e.getLocalizedMessage());
                            }
                        }
                    });

                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.wtf(TAG, e.getLocalizedMessage() );
                    Toast.makeText(getApplicationContext(),
                            "Could not sign you up", Toast.LENGTH_SHORT).show();
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

    private void addAll(){
        for (int i = 0; i < 50; ++i){
            ParseUser.getQuery().whereEqualTo("name", "TestUser#" + i).getFirstInBackground(new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    ParseUser me = ParseUser.getCurrentUser();
                    me.getRelation("friends").add(parseUser);
                    me.saveInBackground();
                }
            });
        }
    }

    private void saveParse(){
        ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setPassword(pass);
        user.put("name", name);
        user.put("totalHugs", 0);
        user.put("totalGames", 0);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {//TODO incorporate multiple people with same name
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Toast.makeText(getApplicationContext(),
                            "Welcome aboard, " + name + "!",
                            Toast.LENGTH_SHORT).show();
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