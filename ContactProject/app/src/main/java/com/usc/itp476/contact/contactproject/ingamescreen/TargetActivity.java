package com.usc.itp476.contact.contactproject.ingamescreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class TargetActivity extends Activity {
    public static final String MAXPOINTS = "com.usc.itp476.contact.contactproject.MAXPOINTS";
    public static final String JOINEDGAME = "com.usc.itp476.contact.contactproject.JOINEDGAME";

    final String TAG = this.getClass().getSimpleName();
    public static final int RETURN_FROM_RESULT = 80085;
    public static final int REQUEST_ACKNOWLEDGE_RESULT = 7236;
    private TextView txvwCurrentPoints;
    private TextView txvwMaxPoints;
    private ImageView mImageView;
    private ImageView imvwTarget;
    private int max;
    private int current = 0;
    private long backPressedTime = 0;
    private final long TIME_INTERVAL = 2000;
    private Toast backToast;
    private CameraManager manager;
    private Button buttonTakePicture;
    private String mCurrentPhotoPath;
    private ParseFile currentPhoto;
    private String mImageName;
    private boolean joinedGame = true;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String gameID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        Intent i = getIntent();
        max = i.getIntExtra(MAXPOINTS, -1);
        joinedGame = i.getBooleanExtra(JOINEDGAME, true);
        gameID = i.getStringExtra("gameID");
        if (max == -1)
            max = 10;

        Button tempPoints = (Button) findViewById(R.id.btnPlus);
        tempPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increasePoints();
            }
        });

        mImageView = (ImageView) findViewById(R.id.imageViewFriend);
        txvwCurrentPoints = (TextView) findViewById(R.id.txvwPoints);
        txvwMaxPoints = (TextView) findViewById(R.id.txvwMaxScore);
        imvwTarget = (ImageView) findViewById(R.id.imvwTarget);
        buttonTakePicture = (Button) findViewById(R.id.buttonTakePicture);

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                //DispatchtakePicture starts an camera screen intent
            }
        });

        //if joinedGame means that user pressed the game marker to get here
        //TODO better assignment system
        if(joinedGame){
            //jerry just joined the game
            //put him into the parse db of players
            //give jerry a target
        }else {
            //This is a new game, get this person a target
            g
        }
        setPoints();
        backToast = Toast.makeText(getApplicationContext(),
                "Press back again to leave game.",
                Toast.LENGTH_SHORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //V only geta thumbnail
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK){
            Bitmap imageBitmap = convertToBM();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            //TODO upload pic to server
            currentPhoto = new ParseFile(mImageName, byteArray);
            ParseUser.getCurrentUser().put("image", currentPhoto);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.wtf(TAG, "could not save image: " + e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(),
                                "Could not upload image, try again", Toast.LENGTH_SHORT).show();
                    }else{
                        increasePoints();
                    }
                }
            });
        }
        else if (resultCode == RETURN_FROM_RESULT) {
            finish();
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
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            File mediaFile;
            mImageName = "MI_" + timeStamp + ".jpeg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
            mCurrentPhotoPath = mediaFile.getAbsolutePath();
            return mediaFile;
        }else{
            Log.wtf( TAG, "External storage not writable or readable" );
            return null;
        }
    }

    //use this for lower memory (probably need for gridview friends)
    private Bitmap convertToBM() {

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
        imvwTarget.setVisibility(View.GONE);

        return bitmap;
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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.wtf("TA, writable", state);
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.wtf("TA, readable", state);
            return true;
        }
        return false;
    }

    private void increasePoints(){
        ++current;
        setPoints();
        checkWin();
    }

    private void checkWin(){
        if (current == max){
            Intent i = new Intent(getApplicationContext(), ResultActivity.class);
            i.putExtra("gameID", gameID);
            startActivityForResult(i, REQUEST_ACKNOWLEDGE_RESULT);
        }
    }

    private void setPoints(){
        txvwCurrentPoints.setText(String.valueOf(current));
        if (txvwMaxPoints.getText().charAt(0) == '-')
            txvwMaxPoints.setText(String.valueOf(max));
    }

    @Override
    public void onBackPressed() {
        if (TIME_INTERVAL + backPressedTime > System.currentTimeMillis()) {
            backToast.cancel();
            removeMeFromGame();
            return;
        }else{
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void removeMeFromGame(){
        HashMap<String, String> params = new HashMap<>();
        params.put("playerID", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("removeFromGame", params, new FunctionCallback<Boolean>() {
            @Override
            public void done(Boolean isGood, ParseException e) {
                if (e == null){
                    Log.wtf(TAG, isGood.toString() + " returns back from call");
                    Intent i = new Intent();
                    setResult(CreateGameActivity.RESULT_CODE_QUIT_GAME);
                    finish();
                }else{
                    Log.wtf(TAG, "Could not leave game: " + e.getLocalizedMessage());
                    Toast.makeText(getApplicationContext(),
                            "You cannot leave this game", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}