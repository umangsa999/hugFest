package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.List;

public class ProfileFragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    private ImageButton imbnEdit;
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
    }

    private void loadFriendSaveData(){
        try {
            ParseUser friend = ParseUser.getQuery().get(friendID);
            if (friend != null){
                txvwTotal.setText(String.valueOf(friend.getInt("totalHugs")));
                txvwName.setText(friend.getUsername());
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
        txvwName.setText(display.getUsername());
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
                update.put("username", txvwName.getText().toString());
                update.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast t = new Toast(getActivity().getApplicationContext());
                        t.setDuration(Toast.LENGTH_SHORT);
                        if (e == null){
                            t.setText("Could not update your name.");
                        }else{
                            t.setText("Name updated!");
                        }
                        t.show();
                    }
                });
            }
        }
    }
}