package com.usc.itp476.contact.contactproject.slidetab.fragments;

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
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    private ImageButton imbnEdit;
    private Button btnLogout;
    private Button btnLink;
    private TextView txvwName;
    private TextView txvwTotal;
    private EditText edtxName;
    private ImageView imgPhoto = null;
    private boolean isEditing = false;
    public boolean mFriendProfile = false;
    private AllTabActivity myParent = null;
    private String friendID = null;
    public void setName(String name){
        txvwTotal.setText(name);
    }

    public void friendProfileTrue(String id, AllTabActivity parent){
        mFriendProfile = true;
        myParent = parent;
        friendID = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.activity_profile, container, false);

        imbnEdit = (ImageButton) rootView.findViewById(R.id.btnEdit);
        imgPhoto = (ImageView) rootView.findViewById(R.id.imvwPhoto);
        txvwName = (TextView) rootView.findViewById(R.id.txvwName);
        txvwTotal = (TextView) rootView.findViewById(R.id.txvwTotal);
        edtxName = (EditText) rootView.findViewById(R.id.edtxName);
        btnLink = (Button) rootView.findViewById(R.id.btnLink);
        btnLogout = (Button) rootView.findViewById(R.id.btnLogout);

        if( mFriendProfile ) {
            imbnEdit.setVisibility(View.GONE);
            loadFriendSaveData();
        }else {
            setListeners();
            loadMySaveData();
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
                HashMap<String, Object> parms = new HashMap<>();
                String tokenID;
                Profile p = Profile.getCurrentProfile();
                if (p != null) {
                    //GET OLD PARSE
                    String un = "", ps = "";
                    //TODO create a dialog for username and password
                    HashMap<String, String> map = new HashMap<>();
                    map.put("ParseUsername", un);
                    map.put("ParsePassword", ps);
                    ParseCloud.callFunctionInBackground("mergeNewFBOldParse", map, new FunctionCallback<String>() {
                        @Override
                        public void done(String s, ParseException e) {
                            if (e == null){
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
                            }else{
                                Log.wtf(TAG, e.getLocalizedMessage());
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Could not merge your accounts",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //PUT OLD PARSE IN NEW
                    parms.put("facebookID", p.getId());
                    Log.wtf(TAG, p.getId() + " is my facebookID");
                    try {
                        tokenID = ParseCloud.callFunction("getUserSessionToken", parms);
                        ParseUser.become(tokenID);
                        Log.wtf(TAG, tokenID + " is my token");
                        //TODO merge old and new FB
                    } catch (ParseException e) {
                        Log.wtf(TAG, e.getLocalizedMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Could not link accounts", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //TODO get user to sign in facebook, get their facebook id, and add to parse
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please login facebook", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadFriendSaveData(){
        try {
            ParseUser friend = ParseUser.getQuery().get(friendID);
            if (friend != null){
                txvwTotal.setText(String.valueOf(friend.getInt("totalHugs")));
                txvwName.setText(friend.getString("name"));
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

    private void loadMySaveData(){
//        SharedPreferences sharedPreferences =
//                getActivity().getSharedPreferences(GameMarker.PREFFILE, Context.MODE_PRIVATE);
//
//        String name = sharedPreferences.getString(GameMarker.FULL_NAME, null);
//        int totalhugs = sharedPreferences.getInt(GameMarker.TOTAL_HUGS, -1);
//
//        if (name != null){
//            txvwName.setText(name);
//        }
//
//        if (totalhugs != -1){
//            txvwTotal.setText(String.valueOf(totalhugs));
//        }
        ParseUser display = ParseUser.getCurrentUser();
        txvwName.setText(display.getString("name"));
        txvwTotal.setText(String.valueOf(display.getInt("totalHugs")));
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

            //save a working name
//            //TODO incorporate this for
//            SharedPreferences sharedPreferences =
//                    getActivity().getSharedPreferences(GameMarker.PREFFILE,
//                            Context.MODE_PRIVATE);
//            SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
//
//            sharedPrefEditor.putString(GameMarker.FULL_NAME,
//                    txvwName.getText().toString());
//
//            sharedPrefEditor.commit();
            if (!mFriendProfile){
                ParseUser update = ParseUser.getCurrentUser();
                update.put("name", txvwName.getText().toString());
                update.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast t = new Toast(getActivity().getApplicationContext());
                        t.setDuration(Toast.LENGTH_SHORT);
                        if (e != null){
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Could not update your name", Toast.LENGTH_SHORT).show();
                            Log.wtf(TAG, e.getLocalizedMessage());
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Name updated!", Toast.LENGTH_SHORT).show();
                        }
                        t.show();
                    }
                });
            }
        }
    }
}