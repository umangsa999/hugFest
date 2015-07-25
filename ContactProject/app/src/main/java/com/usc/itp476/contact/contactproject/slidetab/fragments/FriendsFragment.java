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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.adapters.FriendListGridAdapter;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    private ArrayList<ParseUser> friendList;
    private FriendListGridAdapter mFriendListAdapter;
    private ImageButton buttonAdd;
    private Context mContext;
    private Activity mActivity;
    private GridView gridView;
    private String mInputAddFriendText = "";
    private AllTabActivity mAllTabActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_friends, container, false);
        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();

        buttonAdd = (ImageButton) rootView.findViewById(R.id.btnAdd);
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        setAddListener();
        generateGridView();

        return rootView;
    }

    public void setAllTabActivity(AllTabActivity allTabActivity, ArrayList<ParseUser> f) {
        this.mAllTabActivity = allTabActivity;
        friendList = f;
    }

    private void setAddListener(){
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from(mActivity);

                View promptView = layoutInflater.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

                // set prompts.xml to be the layout file of the alertdialog builder
                alertDialogBuilder.setView(promptView);
                final EditText input = (EditText) promptView.findViewById(R.id.userInput);
                // setup a dialog window
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                mInputAddFriendText = input.getText().toString();
                                addFriend(mInputAddFriendText);
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                //AlertDialog alertD = alertDialogBuilder.
                alertDialogBuilder.show();
            }
        });
    }

    private void generateGridView(){
        mFriendListAdapter = new FriendListGridAdapter( mContext, false, mAllTabActivity, true);
        gridView.setAdapter( mFriendListAdapter );
        mFriendListAdapter.setFriendsList(friendList);

        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        // On Click event for Single Gridview Item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Sending image id to FullScreenActivity
                //this shit doesn't run
            }
        });
    }

    //TODO make this code more server heavy
    private void addFriend(String inputFriendUsername){
        ParseQuery<ParseUser> findFriend = ParseUser.getQuery();
        findFriend.whereEqualTo("username", inputFriendUsername);
        findFriend.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e != null) {
                    Log.wtf(TAG, e.getLocalizedMessage());
                } else if (list.size() != 1) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Could not find user.", Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser friend = list.get(0);
                    ParseUser me = ParseUser.getCurrentUser();
                    ParseRelation<ParseUser> myFriends = me.getRelation("friends");
                    //TODO need to check for duplicates before adding
                    myFriends.add(friend);
                    me.saveInBackground();

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Added " + friend.getUsername(), Toast.LENGTH_SHORT).show();

                    friendList.add(friend);
                    updateFriends(friendList);
                    mFriendListAdapter.notifyDataSetChanged();
                    //TODO make a push notification from me to friend asking for permission
                    //TODO this is because we do not have ACL authority
                }
            }
        });
    }

    private void askUpdateFriends(){
        mAllTabActivity.updateFriends();
    }

    public void updateFriends(ArrayList<ParseUser> p){
        friendList = p;
        mFriendListAdapter.setFriendsList(friendList);
        mFriendListAdapter.notifyDataSetChanged(); //actually tell people to display
    }

    @Override
    public void onResume() {
        super.onResume();
        askUpdateFriends();
    }

    @Override
    public void onDestroy() {
        //moPubView.destroy();
        super.onDestroy();
    }
}