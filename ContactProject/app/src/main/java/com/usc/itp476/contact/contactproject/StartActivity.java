package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.List;

public class StartActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ellChjDHP7hNM4CBQLHrBNWzDMoOzElwUgy3MpEc", "aXSv9sdHcVcnjSIaqy8KuymGh16K5I53MiWXGgnN");
        ParseFacebookUtils.initialize(getApplicationContext());


        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);

        for (Object obj : pkgAppsList) {
            ResolveInfo resolveInfo = (ResolveInfo) obj;
            PackageInfo packageInfo = null;
            try {
                packageInfo = getPackageManager().getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String[] permissions = packageInfo.requestedPermissions;
            for( String i : permissions){
                Log.wtf(TAG, i );
            }
        }



        SharedPreferences sharedPreferences = getSharedPreferences(GameData.PREFFILE, MODE_PRIVATE);
        String id = sharedPreferences.getString(GameData.USER_ID, null);

        //the user has already logged in before
        if (id != null){
            goToHome();
        }

        btnStart = (Button) findViewById(R.id.btnStart);
        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
        edtxLast = (EditText) findViewById(R.id.edtxLast);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser user = new ParseUser();
                user.setUsername("my name");
                user.setPassword("my pass");
                user.setEmail("email@example.com");

                // other fields can be set just like with ParseObject
                user.put("phone", "650-555-0000");

                ;
                //loggin in user
//                ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
//                    @Override
//                    public void done(ParseUser user, ParseException err) {
//                        if (user == null) {
//                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
//                        } else if (user.isNew()) {
//                            Log.d("MyApp", "User signed up and logged in through Facebook!");
//                        } else {
//                            Log.d("MyApp", "User logged in through Facebook!");
//                        }
//                    }
//                });

//                user.signUpInBackground(new SignUpCallback() {
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            // Hooray! Let them use the app now.
//                        } else {
//                            // Sign up didn't succeed. Look at the ParseException
//                            // to figure out what went wrong
//                        }
//                    }
//                });

                //name is incompatible
                if (edtxFirst.getText().length() == 0 || edtxLast.getText().length() == 0){
//                    Toast.makeText(getApplicationContext(),
//                            "Please enter a valid name.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
                    Toast.makeText(getApplicationContext(),
                            "Assuming debugging.",
                            Toast.LENGTH_SHORT).show();
                }

                //TODO Make so that user can continue from last time as opposed to always resetting
                //save a working name
                SharedPreferences sharedPreferences =
                        getSharedPreferences(GameData.PREFFILE, MODE_PRIVATE);
                SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

                sharedPrefEditor.putString(GameData.FULL_NAME,
                        edtxFirst.getText().toString() + " " + edtxLast.getText().toString());

                //add a random total hugs for now
                //TODO set to 0
                sharedPrefEditor.putInt(GameData.TOTAL_HUGS, (int)(Math.random() * 200));

                //TODO add ID here

                //save the user's name asynchronously
                sharedPrefEditor.apply();

                goToHome();
            }
        });
    }


    //Called when facebook is done
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void goToHome(){
        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivity(i);
    }
}