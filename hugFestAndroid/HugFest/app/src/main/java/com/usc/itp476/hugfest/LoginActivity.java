package com.usc.itp476.hugfest;

importandroid.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.SystemClock;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View.OnClickListener;

public class LoginActivity extends ActionBarActivity {

    private EditText edtxUserName;
    private EditText edtxPassWord;
    private Button   btnSignUp;
    private LongRunningIO mLongRunningIO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO check for sharedPreference user id is mad

        edtxPassWord = (EditText) findViewById(R.id.edtxPass);
        edtxUserName = (EditText) findViewById(R.id.edtxUser);
        btnSignUp    = (Button)   findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtxUserName.getText().toString();
                String pass = edtxPassWord.getText().toString();
                //TODO create a REST call to server and send data then transition screens
                new LongRunningGetIO().execute();

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


    //ASYNC CODE
    static class LongRunningIO extends AsyncTask <Void, Integer, Void> {
        private Activity mActivity = null;
        private boolean mDone = false;
        boolean isDone() { return mDone; }
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 1; i<11; i++) {
                SystemClock.sleep(1000);
                if (mActivity!= null) {
                    publishProgress(i);
                }
            }
            mDone = true;
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (mActivity != null) {
                TextView tv = (TextView)mActivity.findViewById(R.id.status_text);
                tv.setText(Integer.toString(progress[0])+"/10");
            }
        }
        void attach(Activity a) {
            mActivity = a;
        }
        void detach() {
            mActivity = null;
        }
    }
}
