package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.usc.itp476.contact.contactproject.R;


public class TargetActivity extends Activity {
    public static final String MAXPOINTS = "com.usc.itp476.contact.contactproject";
    private TextView txvwCurrentPoints;
    private TextView txvwMaxPoints;
    private ImageView imvwTarget;
    private int max;
    private int current = 0;
    private long backPressedTime = 0;
    private final long TIME_INTERVAL = 2000;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        Intent i = getIntent();
        max = i.getIntExtra(MAXPOINTS, -1);

        txvwCurrentPoints = (TextView) findViewById(R.id.txvwPoints);
        txvwMaxPoints = (TextView) findViewById(R.id.txvwMaxScore);
        imvwTarget = (ImageView) findViewById(R.id.imvwTarget);
        setPoints();
        backToast = Toast.makeText(getApplicationContext(),
                "Press back again to leave game.",
                Toast.LENGTH_SHORT);
    }

    private void setPoints(){
        txvwCurrentPoints.setText(String.valueOf(current));
        txvwMaxPoints.setText(max == -1 ? "10" : String.valueOf(max));
    }

    @Override
    public void onBackPressed() {
        if (TIME_INTERVAL + backPressedTime > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}