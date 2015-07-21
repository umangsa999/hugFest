package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

public class StartActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private Button btnStart;
    private EditText edtxFirst;
    private EditText edtxLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences sharedPreferences = getSharedPreferences(GameData.PREFFILE, MODE_PRIVATE);
        String id = sharedPreferences.getString(GameData.USER_ID, null);

        //the user has already logged in before
        if (id != null){
            goToHome();
        }

        btnStart = (Button) findViewById(R.id.btnStart);
        edtxFirst = (EditText) findViewById(R.id.edtxFirst);
        edtxLast = (EditText) findViewById(R.id.edtxLast);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name is incompatible
                if (edtxFirst.getText().length() == 0 || edtxLast.getText().length() == 0){
//                    Toast.makeText(getApplicationContext(),
//                            "Please enter a valid name.",
//                            Toast.LENGTH_SHORT).show();
//                    return;
                    Toast.makeText(getApplicationContext(),
                            "Assuming debugging.",
                            Toast.LENGTH_SHORT).show();
                }

                //TODO Make so that user can continue from last time as opposed to always resetting
                //save a working name
                SharedPreferences sharedPreferences =
                        getSharedPreferences(GameData.PREFFILE, MODE_PRIVATE);
                SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

                sharedPrefEditor.putString(GameData.FULL_NAME,
                        edtxFirst.getText().toString() + " " + edtxLast.getText().toString());

                //add a random total hugs for now
                //TODO set to 0
                sharedPrefEditor.putInt(GameData.TOTAL_HUGS, (int)(Math.random() * 200));

                //TODO add ID here

                //save the user's name asynchronously
                sharedPrefEditor.apply();

                goToHome();
            }
        });
    }

    private void goToHome(){
        Intent i = new Intent(getApplicationContext(), AllTabActivity.class);
        startActivity(i);
    }
}