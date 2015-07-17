package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivity(i);

    }
}