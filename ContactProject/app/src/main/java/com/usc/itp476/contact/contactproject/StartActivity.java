package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

public class StartActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;
    private EditText edtxPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ellChjDHP7hNM4CBQLHrBNWzDMoOzElwUgy3MpEc", "aXSv9sdHcVcnjSIaqy8KuymGh16K5I53MiWXGgnN");

        SharedPreferences sharedPreferences = getSharedPreferences(GameMarker.PREFFILE, MODE_PRIVATE);
        String id = sharedPreferences.getString(GameMarker.USER_ID, null);

        //the user has already logged in before
        if (id != null){
            goToHome();
        }

        btnStart = (Button) findViewById(R.id.btnStart);
        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
        edtxLast = (EditText) findViewById(R.id.edtxLast);
        edtxPass = (EditText) findViewById(R.id.edtxPassword);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }

    private void check(){
        //name is incompatible
        if (edtxFirst.getText().length() == 0 || edtxLast.getText().length() == 0){
                    Toast.makeText(getApplicationContext(),
                            "Please enter a valid name.",
                            Toast.LENGTH_SHORT).show();
        }else if (ParseUser.getCurrentUser() != null) {
            goToHome();
        }else{
            ParseUser.logInInBackground(
                    edtxFirst.getText().toString() + " " + edtxLast.getText().toString(),
                    edtxPass.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(),
                                        "Welcome back,\nuser.getUsername()",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                saveParse();
                            }
                        }
            });
        }
    }

    private void saveLocal(){
        //TODO Make so that user can continue from last time as opposed to always resetting
        //save a working name
        SharedPreferences sharedPreferences =
                getSharedPreferences(GameMarker.PREFFILE, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

        sharedPrefEditor.putString(GameMarker.FULL_NAME,
                edtxFirst.getText().toString() + " " + edtxLast.getText().toString());

        //add a random total hugs for now
        //TODO set to 0
        sharedPrefEditor.putInt(GameMarker.TOTAL_HUGS, (int)(Math.random() * 200));

        //TODO add ID here

        //save the user's name asynchronously
        sharedPrefEditor.apply();
    }

    private void saveParse(){
        ParseUser user = new ParseUser();
        user.setUsername("my name");
        user.setPassword("my pass");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
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