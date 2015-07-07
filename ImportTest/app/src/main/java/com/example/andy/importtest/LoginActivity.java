package com.example.andy.importtest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {

    private EditText editTextUser;
    private EditText editTextPass;
    private CheckBox checkBoxRemember;
    private Boolean rememberInfo = false;
    private Boolean successfulLogin = false;
    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;

    //need to make this static because we're using anonymous inner class for button listener
    private static Button buttonLogin;

    public static final String PREFS_FILE = "userPrefFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //connect widgets to variables
        editTextUser = (EditText)findViewById(R.id.editTextUser);
        editTextPass = (EditText)findViewById(R.id.editTextPass);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        //get saved settings
        prefSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);

        //code for what happens when checkBox is clicked
        checkBoxRemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( rememberInfo ){
                    checkBoxRemember.setChecked(true);
                    rememberInfo = true;

                    //User wants password to be saved
                    prefEditor.putBoolean( PREFS_FILE, true);

                }
                else{
                    checkBoxRemember.setChecked(false);
                    rememberInfo = false;
                }
            }
        });

        //set this code to execute when button is clicked
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                        prefEditor.putBoolean( PREFS_FILE ,successfulLogin);

                    }
                }


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
