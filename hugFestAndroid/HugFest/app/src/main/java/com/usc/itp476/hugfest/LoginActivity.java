package com.usc.itp476.hugfest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {

    private EditText edtxUserName;
    private EditText edtxPassWord;
    private Button   btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO check for sharedPreference user id is made

        edtxPassWord = (EditText) findViewById(R.id.edtxPass);
        edtxUserName = (EditText) findViewById(R.id.edtxUser);
        btnSignUp    = (Button)   findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtxUserName.getText().toString();
                String pass = edtxPassWord.getText().toString();
                if (user.length() == 0 || pass.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill in a username and a password.", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO create a REST call to server and send data then transition screens
                    //Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    //TODO store the user id in shared preferences
                    //startActivity(i);
                    Toast.makeText(getApplicationContext(), "Logged in?", Toast.LENGTH_SHORT).show();
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
