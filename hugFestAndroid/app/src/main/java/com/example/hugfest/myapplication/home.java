package com.example.hugfest.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class home extends ListActivity {

    private ParseUser currentUser = null;
    private EditText editTextAddFriend = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Enable Local Datastore.
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "roLFtR6aJgjdAWMguXbD52qaFEVT1xghkC7c3Nxh", "957CLgERcM1zdclQrzspb7KA6P32gI3RmiTYZeRY");

        editTextAddFriend = (EditText) findViewById(R.id.addFriendNameField);
        Button buttonAddFriend = (Button) findViewById(R.id.buttonAddFriend);

        ListView friendListView = (ListView) findViewById(R.id.listView);

        //Get the userobjectID
        Intent intent = getIntent();
        String userObjectIDString = intent.getStringExtra(MainActivity.EXTRA_USEROBJECTID);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        try{
            currentUser = query.get( userObjectIDString );
        } catch (com.parse.ParseException e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (currentUser == null){
            //Toast.makeText(getApplicationContext(), "current User null", Toast.LENGTH_LONG).show();
        }
        else{
            //Toast.makeText(getApplicationContext(), "User found!", Toast.LENGTH_LONG).show();
        }

        //Adding friend button action listenr
       buttonAddFriend.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {

                //OLD qeury code THAT WORKS
               ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
               String enteredName = editTextAddFriend.getText().toString();
               friendQuery.whereEqualTo( "username", enteredName);
               friendQuery.findInBackground( new FindCallback<ParseUser>() {
                   @Override
                   public void done(List<ParseUser> parseUsers, ParseException e) {
                       if( parseUsers.size() == 1 ){
                           Toast.makeText(getApplicationContext(), parseUsers.get(0).getUsername(), Toast.LENGTH_SHORT).show();

                       }
                       else{
                            Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
           }
        });
    }
    public void getUserFromID(String userObjectIDString){
        ParseUser parseUserReturn = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
}
