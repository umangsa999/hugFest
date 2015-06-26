package com.example.hugfest.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends ActionBarActivity {

    public static String EXTRA_USEROBJECTID = "com.example.hugfest.myapplication.USEROBJECTID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Local Datastore.
        Parse.enableLocalDatastore( this );
        Parse.initialize( this, "roLFtR6aJgjdAWMguXbD52qaFEVT1xghkC7c3Nxh", "957CLgERcM1zdclQrzspb7KA6P32gI3RmiTYZeRY");
        setContentView(R.layout.activity_main);

        ParseUser currentUser = null;
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            currentUser.logOut();
        }

        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        final EditText editTextPass = (EditText) findViewById(R.id.fieldPassword);
        final EditText editTextUsername = (EditText) findViewById(R.id.fieldUsername);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ParseUser user = new ParseUser();
                String userString = editTextUsername.getText().toString();
                String passString = "pass"; //= editTextPass.getText().toString();
                user.setUsername(userString);
                user.setPassword(passString);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        //Toast.makeText(getApplicationContext(), "in done", Toast.LENGTH_LONG).show();
                        if (e == null) {
                            String userObjectIDString = ParseUser.getCurrentUser().getObjectId();
                            //Toast.makeText(getApplicationContext(), ParseUser.getCurrentUser().getObjectId(), Toast.LENGTH_LONG).show();
                            Log.e("MainActivity", userObjectIDString);
                            //successful signup, let's go to the home activity
                            Intent intent;
                            intent = new Intent(getApplicationContext(), home.class);
                            intent.putExtra(EXTRA_USEROBJECTID, userObjectIDString);
                            startActivity(intent);

                        } else {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
