package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

public class StartActivity extends Activity {
    private final String PREFFILE = "com.usc.itp476.contact.contactproject.StartActivity.PREFFILE";
    private final String USERID = "com.usc.itp476.contact.contactproject.StartActivity.USERID";
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFFILE, MODE_PRIVATE);
        String id = sharedPreferences.getString(USERID, null);

        if (id != null){
            goToHome();
        }

        btnStart = (Button) findViewById(R.id.btnStart);
        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
        edtxLast = (EditText) findViewById(R.id.edtxLast);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });
    }

    private void goToHome(){
        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivity(i);
    }
}