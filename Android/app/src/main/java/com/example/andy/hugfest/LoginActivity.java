package com.example.andy.hugfest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class LoginActivity extends ActionBarActivity {

    final String loginStringQuery = "?user=%s&pass=%s";
    private EditText editTextUser;
    private EditText editTextPass;
    private Boolean rememberInfo = false;
    private Boolean successfulLogin = false;
    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;
    private Button buttonNext;

    //need to make this static because we're using anonymous inner class for button listener
    private static Button buttonSignIn;

    public static final String PREFS_FILE = "userPrefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //connect widgets to variables
        editTextUser = (EditText)findViewById(R.id.editTextUser);
        editTextPass = (EditText)findViewById(R.id.editTextPass);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonNext = (Button) findViewById(R.id.buttonHome);

        //get saved settings
        prefSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);

        //set this code to execute when button is clicked
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Follow this example for use
                HTTPCaller h = null;
                try {

                    h = (new HTTPCallerBuilder()).method("GET").data("name", "Chris").data("says","hi").fURL("user").sURL("status").query("id","1234").executeHTTPCaller();
                    Log.wtf("LOGINACTIVITY", "Response Code: " + h.getResponseCode() +
                            "\nResponseMessage: " + h.getResponseMessage() +
                            "\nResult: " + (h.getServerResult() == null ? "" : h.getServerResult().toString()));
                    h = (new HTTPCallerBuilder()).method("PUT").data("id", "Chris").data("status","1").fURL("user").sURL("status").executeHTTPCaller();
                    Log.wtf("LOGINACTIVITY", "Response Code: " + h.getResponseCode() +
                            "\nResponseMessage: " + h.getResponseMessage() +
                            "\nResult: " + (h.getServerResult() == null ? "" : h.getServerResult().toString()));

                } catch (JSONException e) {
                    Log.e(LoginActivity.class.getSimpleName(), "onClick");
                }
*/
                Log.wtf("LOGINACTIVITY", "execute over");
                rememberInfo = prefSettings.getBoolean("remember", false);

                //first check if the user has logged in before & wanted info remembered
                if( rememberInfo && successfulLogin  ){

                    //set the user and pass
                    String user = prefSettings.getString("user", "");
                }

                //new user/never logged in before/didn't save
                else{
                    //get the current text from widget and convert to string
                    String user = editTextUser.getText().toString();
                    String pass = editTextPass.getText().toString();

                    //TODO -- more comprehensive login check
                    //check user has entered anything
                    if( user.length() == 0 || pass.length() == 0 ){

                        //Alert the user
                        Toast.makeText(
                                getApplicationContext(), "Please enter valid username/password", Toast.LENGTH_SHORT
                        ).show();
                    }

                    //valid user/pass entered
                    else {
                        //TODO -- check if username already exists/correct password with database
                        //TODO -- encrypt password then send

                        //Send info to the database & save in shared preferences
                        successfulLogin = true;
                        //prefEditor.putBoolean( PREFS_FILE ,successfulLogin);

                    }
                }


            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent next = new Intent(LoginActivity.this, AllTabMainActivity.class);
                startActivity(next);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
