package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

public class StartActivity extends Activity {
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivity(i);

        btnStart = (Button) findViewById(R.id.btnStart);
        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
        edtxLast = (EditText) findViewById(R.id.edtxLast);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
                startActivity(i);
            }
        });
    }
}