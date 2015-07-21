package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.usc.itp476.contact.contactproject.R;

public class ResultActivity extends Activity {
    final String TAG = this.getClass().getSimpleName();
    private ImageView winnerImage, leftImage, middleImage, rightImage;
    private TextView winnerName, leftName, middleName, rightName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        winnerImage = (ImageView) findViewById(R.id.resultsWinnerImage);
        leftImage = (ImageView) findViewById(R.id.resultsLeftImage);
        middleImage = (ImageView) findViewById(R.id.resultsMiddleImage);
        rightImage = (ImageView) findViewById(R.id.resultsRightImage);

        winnerName = (TextView) findViewById(R.id.resultsWinnerName);
        leftName = (TextView) findViewById(R.id.resultsLeftName);
        middleName = (TextView) findViewById(R.id.resultsMiddleName);
        rightName = (TextView) findViewById(R.id.resultsRightName);
    }
}