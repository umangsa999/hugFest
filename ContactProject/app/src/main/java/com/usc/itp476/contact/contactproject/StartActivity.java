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
    private String name;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
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
        startActivity(i);
    }
}