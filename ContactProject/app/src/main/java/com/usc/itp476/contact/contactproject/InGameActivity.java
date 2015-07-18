package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class InGameActivity extends Activity {
    public static final String MAXPOINTS = "com.usc.itp476.contact.contactproject";
    private TextView txvwCurrentPoints;
    private TextView txvwMaxPoints;
    private int max;
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        Intent i = getIntent();
        max = i.getIntExtra(MAXPOINTS, -1);

        txvwCurrentPoints = (TextView) findViewById(R.id.txvwPoints);
        txvwMaxPoints = (TextView) findViewById(R.id.txvwMaxScore);
        setPoints();
    }

    private void setPoints(){
        txvwCurrentPoints.setText(String.valueOf(current));
        txvwMaxPoints.setText(max == -1 ? "10" : String.valueOf(max));
    }
}