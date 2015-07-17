package com.example.andy.hugfest.verificationViews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.andy.hugfest.HomeActivity;
import com.example.andy.hugfest.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class BufferActivity extends FragmentActivity {

    private Button buttonLogin;
    private Button buttonSignUp;
    private LoginButton loginButtonFacebook;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        setContentView(R.layout.activity_buffer);



        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button);
        loginButtonFacebook.setReadPermissions("user_friends");
        // If using in a fragment
        //loginButtonFacebook.setFragment(this);
        // Other app specific specialization
        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButtonFacebook.registerCallback(callbackManager ,new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getApplicationContext(), "Success LOGIN!" , Toast.LENGTH_SHORT);
                Log.i("BufferActivity", "facebook login success" );
                Intent next = new Intent(BufferActivity.this, HomeActivity.class);
                startActivity(next);
                // App code
            }
            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel" , Toast.LENGTH_SHORT);
                Log.i("BufferActivity", "facebook login cancel" );
                Intent next = new Intent(BufferActivity.this, HomeActivity.class);
                startActivity(next);
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Error!" , Toast.LENGTH_SHORT);
                // App code
            }
        });


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(BufferActivity.this, HomeActivity.class);
                startActivity(next);
            }

        });

        buttonLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent next = new Intent(BufferActivity.this, LoginActivity.class);
                startActivity(next);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buffer, menu);
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

    //App Events let you measure installs on your mobile app ads, create high value audiences for
    //targeting, and view analytics including user demographics. To log an app activation event
    // //add the following code to the onResume() method of your app's default activity class:
    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    //To accurately track the time people spend in your app, you should also log a deactivate event
    //in the onPause() method of each activity where you added the activateApp() method above:
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
