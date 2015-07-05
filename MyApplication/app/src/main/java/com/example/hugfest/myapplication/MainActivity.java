package com.example.hugfest.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    //May not need this, only if need to send user/pass to next activity
    public static String EXTRA_USER = "com.example.hugfest.myapplication.USER";
    public static String EXTRA_PASS = "com.example.hugfest.myapplication.PASS";

    private EditText editTextUser = null;
    private EditText editTextPass = null;
    private static Button buttonLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if logged in before, if so bring to next activity automatically

        //Connect the UI to the variables in the class
        editTextUser = (EditText) findViewById(R.id.editTextUser);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        //Set action when user clicks button
        buttonLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set if user has entered in user/pass
                if( editTextPass.getText().toString() == "" || editTextUser.getText().toString()=="" ){
                    Toast.makeText(getApplicationContext(), "Please enter username/password", Toast.LENGTH_SHORT ).show();
                }
                else{

                    //get the username and password
                    String user = editTextPass.getText().toString();
                    String pass = editTextUser.getText().toString();

                    //TODO -- Send the info to the server

                    //start new activity
                    Intent i = new Intent( getApplicationContext(), tabActivity.class );
                    startActivity(i);

                }
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
