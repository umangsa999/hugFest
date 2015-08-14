// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Ryan Zhou and Chris Lee</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;

public class ProfileFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private Button buttonLogout = null;
    private TextView textViewName = null;
    private TextView textViewTotal = null;
    private ImageView imageViewPhoto = null;
    public boolean mFriendProfile = false;
    private String friendID = null;

    public void setName(String name){
        textViewTotal.setText(name);
    }

    public void friendProfileTrue(String id, boolean isFriend){
        mFriendProfile = isFriend;
        friendID = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.activity_profile, container, false);

        imageViewPhoto = (ImageView) rootView.findViewById(R.id.imageViewProfileImage);
        textViewName = (TextView) rootView.findViewById(R.id.textViewNameProfile);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        buttonLogout = (Button) rootView.findViewById(R.id.buttonLogout);

        if( mFriendProfile ) {
            //We are viewing the friend with the profile fragment
            buttonLogout.setVisibility(View.GONE);
            loadFriendSaveData();

        }else {
            //Not a FRIEND, is the user himself
            String picLink = ParseUser.getCurrentUser().getString("pictureLink");
            int totalHugs = ParseUser.getCurrentUser().getInt("totalHugs");
            String name = ParseUser.getCurrentUser().getString("name");
            Glide.with(this).load(picLink).into(imageViewPhoto);
            textViewTotal.setText(String.valueOf(totalHugs));
            textViewName.setText(name);
            setListeners();
        }
        return rootView;
    }

    private void setListeners(){
        //log out of the game and return to the start screen
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                LoginManager.getInstance().logOut();
                getActivity().setResult(ContactApplication.RESULT_LOGOUT);
                getActivity().finish();
            }
        });
    }

    private void loadFriendSaveData(){
        try {
            //immediately get the latest version of this friend
            ParseUser friend = ParseUser.getQuery().get(friendID);
            if (friend != null){
                //set their information
                textViewTotal.setText(String.valueOf(friend.getInt("totalHugs")));
                textViewName.setText(friend.getString("name"));
                Glide.with(this).load(friend.getString("pictureLink")).error(R.mipmap.large).into(imageViewPhoto);
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
}