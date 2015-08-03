package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.adapters.FriendListGridAdapter;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.HashMap;

public class FriendsFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private FriendListGridAdapter friendListAdapter = null;
    private ImageButton imageButtonAddFriend = null;
    private Context context = null;
    private Activity activity = null;
    private GridView gridView = null;
    private String addFriendName = null;
    private AllTabActivity allTabActivity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_friends, container, false);
        context = getActivity().getApplicationContext();
        activity = getActivity();

        imageButtonAddFriend = (ImageButton) rootView.findViewById(R.id.btnAdd);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        setAddListener();
        generateGridView();
        return rootView;
    }

    public void setAllTabActivity(AllTabActivity allTabActivity) {
        this.allTabActivity = allTabActivity;
    }

    private void setAddListener(){
        imageButtonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from(activity);
                View promptView = layoutInflater.inflate(R.layout.prompt_add_friend, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                // set prompts.xml to be the layout file of the alertdialog builder
                alertDialogBuilder.setView(promptView);
                final EditText input = (EditText) promptView.findViewById(R.id.userInput);
                // setup a dialog window
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                addFriendName = input.getText().toString();
                                addFriend(addFriendName);
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
            }
        });
    }

    private void generateGridView(){
        friendListAdapter = new FriendListGridAdapter(context, false, allTabActivity);
        gridView.setAdapter(friendListAdapter);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
    }

    //TODO make this code more server heavy
    private void addFriend(String inputFriendUsername){
        HashMap<String, String> params = new HashMap<>();
        params.put("hostHelenID", ParseUser.getCurrentUser().getObjectId());
        params.put("friendUN", inputFriendUsername);
        ParseCloud.callFunctionInBackground("addFriendUNMutual", params,
                new FunctionCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Added " + parseUser.get("username").toString(),
                            Toast.LENGTH_SHORT).show();

                    ContactApplication.getFriendsList()
                            .put(parseUser.getObjectId(), parseUser);
                    updateFriends();
                }else {
                    Log.wtf(TAG, e.getLocalizedMessage());
                }
            }
        });
    }

    public void updateFriends(){
        //actually display new friends
        friendListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}