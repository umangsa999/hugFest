package com.example.andy.importtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends ActionBarActivity {

    private EditText editTextUser;
    private EditText editTextPass;
    private CheckBox checkBoxRemember;
    private Boolean rememberInfo = false;
    private Boolean successfulLogin = false;
    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;
    private Button buttonNext;

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
        buttonNext = (Button) findViewById(R.id.buttonNextPage);

        //get saved settings
        prefSettings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);

        /*code for what happens when checkBox is clicked
        //Didn't place a checkbox so it crashes
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
        });*/

        //set this code to execute when button is clicked
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new OpenURL().execute();
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

        buttonNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent next = new Intent(LoginActivity.this, NewPageActivity.class);
                startActivity(next);
            }

        });

    }

    public class OpenURL extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {

            try {
                return getIndex();
            }
            catch( Exception e){
                Log.e("OPEN URL", "BAD");
            }
            return null;
        }

        public String getIndex() throws IOException{
            URL url = new URL("http://52.8.44.124");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                urlConnection.setReadTimeout(8000);
                urlConnection.setConnectTimeout(10000);

                urlConnection.connect();
                Log.d("LOGINACTIVITY CLASS", "RESPONSE CODE ABOUT TO CALL ");
                int response = urlConnection.getResponseCode();
                //Toast.makeText( getApplicationContext(), response, Toast.LENGTH_SHORT ).show();
                Log.d("LOGINACTIVITY CLASS", "RESPONSE CODE-> " + response);

                String responseMessage = urlConnection.getResponseMessage();
                Log.d("LOGIN ACTIVITY CLASS", "RESPONSE MESSAGE <- " + responseMessage);
            }
            catch( Exception e){
                Log.d("URL CONNECTION", e.getMessage());
            }

            InputStream input = new BufferedInputStream(urlConnection.getInputStream());

            final int LENGTH = 50;
            char[] array = new char[LENGTH];

            for (int i = 0; i < LENGTH; ++i) {
                array[i] = 0;
            }
            InputStreamReader reader = new InputStreamReader(input, "UTF-8");
            reader.read(array);

            int count = 0;
            while (array[count] != 0){

                Log.wtf("LOGINACTIVITY CLASS", " " + count + ": " + array[count]);
                count++;
            }

            String result = new String(array, 0, count);
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.d("LOGINACTIVITY CLASS", "RESULT-> "+ result);
            return result;
            //JSONObject obj = new JSONObject(i);
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
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
