// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Chris Lee and Ryan Zhou</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.CustomParsePushBroadcastReceiver;
import com.usc.itp476.contact.contactproject.POJO.GamePhoto;
import com.usc.itp476.contact.contactproject.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;


public class TargetActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();
    private final double TIME_INTERVAL = 2000;
    private TextView textViewCurrentPoints;
    private TextView textViewMaxPoints;
    private TextView textViewName;
    private ImageView imageViewTarget;
    private GamePhoto currentGamePhoto;
    private ParseFile currentPhoto;
    private ParseUser target;
    private String imageName;
    private String currentPhotoPath;
    private String gameID;
    private int max;
    private int current = 0;
    private double backPressedTime = 0.0;
    private boolean joinedGame;
    private boolean loaded = false;
    private boolean takingPicture = false;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        loaded = true;
        if (savedInstanceState == null) {
            Intent i = getIntent();
            max = i.getIntExtra(ContactApplication.MAXPOINTS, 10);
            joinedGame = i.getBooleanExtra(ContactApplication.JOINEDGAME, true);
            gameID = i.getStringExtra(ContactApplication.GAMEID);
            imageName = i.getStringExtra(ContactApplication.IMAGENAME);
            currentPhotoPath = i.getStringExtra(ContactApplication.CURRENTPHOTOPATH);
            target = null;
            getNewTarget();
        }else{
            max = savedInstanceState.getInt(ContactApplication.MAXPOINTS);
            joinedGame = savedInstanceState.getBoolean(ContactApplication.JOINEDGAME);
            gameID = savedInstanceState.getString(ContactApplication.GAMEID);
            imageName = savedInstanceState.getString(ContactApplication.IMAGENAME);
            currentPhotoPath = savedInstanceState.getString(ContactApplication.CURRENTPHOTOPATH);
            getLatestSelf();
            updateTarget();
        }

        subscribeToGame();

        imageViewTarget = (ImageView) findViewById(R.id.imageViewTarget);
        textViewCurrentPoints = (TextView) findViewById(R.id.textViewCurrentPoints);
        textViewMaxPoints = (TextView) findViewById(R.id.textViewMaxScore);
        textViewName = (TextView) findViewById(R.id.textViewNameTarget);
        Button buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takingPicture = true;
                dispatchTakePictureIntent();
                //DispatchtakePicture starts an camera screen intent
            }
        });
        backToast = Toast.makeText(getApplicationContext(), "Press again to quit game", Toast.LENGTH_SHORT);
    }

    @Override
    public void onResume(){
        /* When we resume, get check if the game is over. We are able to do this becuse the server
        will push a notification to the saved preferences that persist. We check for the
        prefererences and change accordingly.
         */
        super.onResume();
        SharedPreferences prefs =
                getSharedPreferences(ContactApplication.SHARED_PREF_FILE, MODE_PRIVATE);
        String restoredText = prefs.getString(CustomParsePushBroadcastReceiver.ACTION, null);

        if (restoredText != null) {
            String action = prefs.getString(CustomParsePushBroadcastReceiver.ACTION, null);
            if( action != null){
                if(action.equals(CustomParsePushBroadcastReceiver.SCORE)){
                    String name = prefs.getString(CustomParsePushBroadcastReceiver.SCORERNAME, null);
                    Toast.makeText(this.getApplicationContext(), name, Toast.LENGTH_LONG ).show();
                }
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(CustomParsePushBroadcastReceiver.ACTION);
            editor.remove(CustomParsePushBroadcastReceiver.SCORERNAME);
            editor.apply();
        }

        if (!loaded && !takingPicture){
            max = prefs.getInt(ContactApplication.MAXPOINTS, 10);
            joinedGame = prefs.getBoolean(ContactApplication.JOINEDGAME, true);
            gameID = prefs.getString(ContactApplication.GAMEID, "");
            imageName = prefs.getString(ContactApplication.IMAGENAME, "");
            currentPhotoPath = prefs.getString(ContactApplication.CURRENTPHOTOPATH, "");
            loaded = true;
            getLatestSelf();
            updateTarget();
        }
    }

    @Override
    protected void onPause() {
        //See onResume, it is for the same purpose
        super.onPause();
        Log.wtf(TAG, "onPause");
        if (!takingPicture) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(ContactApplication.SHARED_PREF_FILE, MODE_PRIVATE);
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putInt(ContactApplication.MAXPOINTS, max);
            sharedPreferencesEditor.putBoolean(ContactApplication.JOINEDGAME, joinedGame);
            sharedPreferencesEditor.putString(ContactApplication.GAMEID, gameID);
            sharedPreferencesEditor.putString(ContactApplication.CURRENTPHOTOPATH, currentPhotoPath);
            sharedPreferencesEditor.putString(ContactApplication.IMAGENAME, imageName);
            sharedPreferencesEditor.apply();
            loaded = false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ContactApplication.JOINEDGAME, joinedGame);
        outState.putInt(ContactApplication.MAXPOINTS, max);
        outState.putString(ContactApplication.GAMEID, gameID);
        outState.putString(ContactApplication.CURRENTPHOTOPATH, currentPhotoPath);
        outState.putString(ContactApplication.IMAGENAME, imageName);
    }

    @Override
    public void onBackPressed() {
        if (TIME_INTERVAL + backPressedTime > System.currentTimeMillis()) {
            backToast.cancel();
            ParsePush.unsubscribeInBackground("game" + gameID);
            ContactApplication.callCloud(ParseUser.getCurrentUser().getObjectId(), false, gameID);
            setResult(ContactApplication.RETURN_FROM_QUIT_GAME);
            finish();
        }else{
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void subscribeToGame(){
        //Here, we subscribe to the unique game channel so we can receive push notification updates
        ParsePush.subscribeInBackground("game" + gameID, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.wtf(TAG, e.getLocalizedMessage());
            }
        });
    }

    private void getLatestSelf() {
        String myID = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include("currentTarget");
        try {
            ParseUser me = query.get(myID);
            current = me.getInt("currentHugs");
            target = ParseUser.getCurrentUser().getParseUser("currentTarget");
        } catch (ParseException e) {
            Log.wtf(TAG, "self: " + e.getLocalizedMessage());
        }
    }

    private void getNewTarget(){
        HashMap<String, String> params = new HashMap<>();
        params.put("hunter", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("getNewTarget", params,
                new FunctionCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            target = parseUser;
                            updateTarget();
                        } else {
                            Log.wtf(TAG, e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void increasePoints(){
        ++current;
        setPoints();
    }

    private void updateTarget(){
        getImage();
        setName();
        setPoints();
    }

    private void getImage(){
        Glide.with(this).load(target.getString("pictureLink")).error(R.mipmap.large).into(imageViewTarget);
    }

    private void setName(){
        textViewName.setText(target.getString("name"));
    }

    private void setPoints(){
        textViewCurrentPoints.setText(String.valueOf(current));
        textViewMaxPoints.setText(String.valueOf(max));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* When we take a picture, we start a new activity and say we want a result back.
        When that activity ends we get a code that we can check.
         */
        if (requestCode == ContactApplication.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            Bitmap imageBitmap = convertToBM();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, ContactApplication.COMPRESS_QUALITY, stream);
            byte[] byteArray = stream.toByteArray();
            currentPhoto = new ParseFile(imageName, byteArray);
            signalIncrease();
            takingPicture = false;
        }
        else if (resultCode == ContactApplication.RETURN_FROM_RESULT) {
            finish();
        }
    }

    //use this for lower memory (probably need for gridview friends)
    private Bitmap convertToBM() {

        // Get the dimensions of the View
        int targetW = imageViewTarget.getWidth();
        int targetH = imageViewTarget.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor;
        if( targetH != 0 && targetW != 0){
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }else if(targetW != 0){
            scaleFactor = photoW / targetW;
        }else{
            scaleFactor = Math.min(photoW/ContactApplication.DEFAULT_IMAGE_SIZE,
                    photoH/ContactApplication.DEFAULT_IMAGE_SIZE);
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                //getOutputMediaFile gets a appropriate, unique filepathname
                photoFile = getOutputMediaFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
                Log.wtf("TargetActivity", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, ContactApplication.REQUEST_TAKE_PHOTO);
            }
        }
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if( isExternalStorageWritable() && isExternalStorageReadable() ) {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + getApplicationContext().getPackageName()
                    + "/Files");

            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = SimpleDateFormat.getDateTimeInstance().toString();
            File mediaFile;
            imageName = "MI_" + timeStamp + ".png";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageName);
            currentPhotoPath = mediaFile.getAbsolutePath();
            return mediaFile;
        }else{
            Log.wtf( TAG, "External storage not writable or readable" );
            return null;
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return ( Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) );
    }

    private void signalIncrease(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("userID", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("increaseScore", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean didWin, ParseException e) {
                if (e == null) {
                    increasePoints();
                    if (!didWin) {
                        getNewTarget();
                    } else {
                        Intent i = new Intent(getApplicationContext(), ResultActivity.class);
                        i.putExtra(ContactApplication.GAMEID, gameID);
                        startActivity(i);
                    }
                } else {
                    Log.wtf(TAG, "Cannot increase score " + e.getLocalizedMessage());
                }
            }
        });

        Log.wtf(TAG, "making game photo");
        GamePhoto gamePhoto = new GamePhoto();
        gamePhoto.setGameID(gameID);
        gamePhoto.setHunterName(ParseUser.getCurrentUser().getString("name"));
        gamePhoto.setImage(currentPhoto);
        gamePhoto.setTargetName(target.getString("name"));
        gamePhoto.setLocationTaken(
                ContactApplication.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        currentGamePhoto = gamePhoto;

        gamePhoto.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.wtf(TAG, "save game photo callback");
                if (e != null) {
                    Toast.makeText(getApplicationContext(),
                            "Could not upload image, try again", Toast.LENGTH_SHORT).show();
                }else{
                    Log.wtf(TAG, "fetch game photo");
                    try {
                        currentGamePhoto.fetch();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    HashMap<String, String> params = new HashMap<>();
                    params.put("photoID", currentGamePhoto.getID());
                    params.put("gameID", gameID);
                    ParseCloud.callFunctionInBackground("addPhotoToGame", params, new FunctionCallback<JSONObject>() {
                        @Override
                        public void done(JSONObject jsonObject, ParseException e) {
                            //nothing needed
                            Log.wtf(TAG, "photo added to game");
                        }
                    });
                }
            }
        });
    }
}