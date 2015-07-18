package com.usc.itp476.contact.contactproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGameActivity extends Activity {
    private Button btnCreate;
    private TextView txvwMax;
    private SeekBar skbrMax;
    private ListView lsvwInvite;
    private int maxPoints = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        Intent i = getIntent();

        btnCreate = (Button) findViewById(R.id.btnCreate);
        txvwMax = (TextView) findViewById(R.id.txvwMax);
        skbrMax = (SeekBar) findViewById(R.id.skbrMax);
        lsvwInvite = (ListView) findViewById(R.id.lsvwInvite);

        setListeners();
    }

    private void setListeners(){
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreateGameActivity.this.getApplicationContext(), InGameActivity.class);
                i.putExtra(InGameActivity.MAXPOINTS, maxPoints);
                startActivity(i);
            }
        });

        skbrMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxPoints = 1 + progress;
                txvwMax.setText(String.valueOf(maxPoints));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}