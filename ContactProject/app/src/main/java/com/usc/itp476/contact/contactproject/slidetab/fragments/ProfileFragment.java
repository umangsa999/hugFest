package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.StartActivity;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;
import com.usc.itp476.contact.contactproject.PicassoTrustAll;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    private ImageButton imbnEdit;
    private Button btnLogout;
    private Button btnLink;
    private Button btnViewFacebook;
    private TextView txvwName;
    private TextView txvwTotal;
    private EditText edtxName;
    private ImageView imgPhoto = null;
    private boolean isEditing = false;
    public boolean mFriendProfile = false;
    private AllTabActivity myParent = null;
    private String friendID = null;
    private HashMap<String, Object> parms = new HashMap<String, Object>();
    Profile p;
    String tokenID = "";
    String un = "", ps = "";
    Activity mActivity;
    private Context context;

    public void setName(String name){
        txvwTotal.setText(name);
    }

    public void friendProfileTrue(String id, boolean isFriend, AllTabActivity parent){
        mFriendProfile = isFriend;
        myParent = parent;
        friendID = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.activity_profile, container, false);

        mActivity = this.getActivity();
        imbnEdit = (ImageButton) rootView.findViewById(R.id.btnEdit);

        //v1.0 do not allow for edits from user profile
        imbnEdit.setVisibility(View.GONE);

        imgPhoto = (ImageView) rootView.findViewById(R.id.imvwPhoto);
        txvwName = (TextView) rootView.findViewById(R.id.txvwName);
        txvwTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        edtxName = (EditText) rootView.findViewById(R.id.edtxName);
        btnLink = (Button) rootView.findViewById(R.id.btnLink);
        btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnViewFacebook = (Button) rootView.findViewById(R.id.buttonViewFacebook);

        //v1.0 no viewing on faceook functionality
        //When Perry makes profile, perry saves his own facebook URI so when his friends add him his
        //friends can access Perry's FB link
        btnViewFacebook.setVisibility(View.GONE);

        btnViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - implement
                Log.wtf(TAG, "Do open facebook URI link with default browser");
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent();
                getActivity().setResult(StartActivity.RESULT_LOGOUT);
                getActivity().finish();
            }
        });

        if( mFriendProfile ) {
            //We are viewing the friend with the profile fragment
            imbnEdit.setVisibility(View.INVISIBLE);
            btnLogout.setVisibility(View.GONE);
            btnLink.setVisibility(View.GONE);
            loadFriendSaveData();

        }else {
            //Not a FRIEND, is the user himself
            btnViewFacebook.setVisibility(View.GONE);
            String picLink = ParseUser.getCurrentUser().getString("pictureLink");
            int totalHugs = ParseUser.getCurrentUser().getInt("totalHugs");
            String name = ParseUser.getCurrentUser().getString("name");
            PicassoTrustAll.getInstance( context ).load(picLink).fit().into(imgPhoto);
            txvwTotal.setText( String.valueOf(totalHugs) );
            txvwName.setText( name );
            setListeners();
        }
        return rootView;
    }

    private void setListeners(){
        imbnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchIcon();
            }
        });
        btnLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p = Profile.getCurrentProfile();
                if (p != null) {
                    //GET OLD PARSE
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View promptView = layoutInflater.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( getActivity() );
                    // set prompts.xml to be the layout file of the alertdialog builder
                    alertDialogBuilder.setView(promptView);
                    final EditText inputUsername = (EditText) promptView.findViewById(R.id.userInput);
                    final EditText inputPassword = (EditText) promptView.findViewById(R.id.editTextPass);
                    // setup a dialog window
                    alertDialogBuilder
                            .setCancelable(true)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    un = inputUsername.getText().toString();
                                    ps = inputPassword.getText().toString();
                                    sendToParse();
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create an alert dialog
                    alertDialogBuilder.show();
                }else{
                    //TODO get user to sign in facebook, get their facebook id, and add to parse
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please login facebook", Toast.LENGTH_SHORT).show();

                    ParseFacebookUtils.logInWithReadPermissionsInBackground(mActivity, StartActivity.permissions, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException err) {
                            if (user == null) {
                                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                            } else if (user.isNew()) {
                                Log.d("MyApp", "User signed up and logged in through Facebook!");
                                //TODO MERGE
                                //AccessToken acessToken = AccessToken.getCurrentAccessToken();

                            } else {
                                //TODO - merge data
                                Log.d("MyApp", "User logged in through Facebook!");
                            }
                        }
                    });

                }
            }
        });
    }

    private void sendToParse(){
        HashMap<String, String> map = new HashMap<>();
        map.put("ParseUsername", un);
        map.put("ParsePassword", ps);
        ParseCloud.callFunctionInBackground("mergeNewFBOldParse", map, new FunctionCallback<String>() {
            @Override
            public void done(String s, ParseException e) {
                if (e == null) {
                    //TODO update page or push back to gamesFragment somehow
                    ParseUser.becomeInBackground(s, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e != null) {
                                Log.wtf(TAG, e.getLocalizedMessage());
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Could not sign in as Facebook user after merge",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.wtf(TAG, e.getLocalizedMessage());
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Could not merge your accounts",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //PUT OLD PARSE IN NEW
        parms.put("facebookID", p.getId());
        try {
            tokenID = ParseCloud.callFunction("getUserSessionToken", parms);
            ParseUser.become(tokenID);
            //TODO merge old and new FB - dont do yet
        } catch (ParseException e) {
            Log.wtf(TAG, e.getLocalizedMessage());
            Toast.makeText(getActivity().getApplicationContext(),
                    "Could not link accounts", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFriendSaveData(){
        try {
            ParseUser friend = ParseUser.getQuery().get(friendID);
            Log.wtf(TAG + "friend: ", friendID);
            if (friend != null){
                txvwTotal.setText(String.valueOf(friend.getInt("totalHugs")));
                txvwName.setText(friend.getString("name"));
                PicassoTrustAll.getInstance( context ).load(friend.getString("pictureLink") ).fit().into(imgPhoto);
            }else{
                Toast.makeText(getActivity().getApplicationContext(),
                        "Could not find friend",
                        Toast.LENGTH_SHORT).show();
            }
        }catch(ParseException pe){
            Log.wtf(TAG, pe.getLocalizedMessage());
            Toast.makeText(getActivity().getApplicationContext(),
                    "Could not find friend",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void switchIcon(){
        isEditing = !isEditing;
        if (isEditing){
            imbnEdit.setBackgroundResource(R.mipmap.ic_done);
            edtxName.setVisibility(View.VISIBLE);
            txvwName.setVisibility(View.GONE);
            edtxName.setText(txvwName.getText());//TODO make the check also end editing
        }else{
            imbnEdit.setBackgroundResource(R.mipmap.ic_edit);
            txvwName.setVisibility(View.VISIBLE);
            edtxName.setVisibility(View.GONE);
            txvwName.setText(edtxName.getText());

            if (!mFriendProfile){
                ParseUser update = ParseUser.getCurrentUser();
                update.put("name", txvwName.getText().toString());
                update.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Could not update your name", Toast.LENGTH_SHORT).show();
                            Log.wtf(TAG, e.getLocalizedMessage());
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Name updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }
}