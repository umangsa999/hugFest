package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.usc.itp476.contact.contactproject.CustomGridAdapter;
import com.usc.itp476.contact.contactproject.R;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private ImageButton buttonAdd;
    private Context c;
    private Activity a;
    private Fragment f;
    private GridView gridView;
    private String mInputAddFriendText = "";
    AlertDialog.Builder builder;

    ArrayList prgmName;
    public static String [] prgmNameList={"Ryan", "Chris", "Mike", "Rob", "Nathan",
            "Paulina", "Trina", "Raymond"};
    public static int [] prgmImages={ R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large,
            R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large};
    public static String [] prgmPoints={"0", "1", "2", "3", "4", "5", "6", "7"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.activity_friends, container, false);
        c = getActivity().getApplicationContext();
        a = getActivity();

        buttonAdd = (ImageButton) rootView.findViewById(R.id.btnAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from( a );

                View promptView = layoutInflater.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( a );

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
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,	int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                //AlertDialog alertD = alertDialogBuilder.
                alertDialogBuilder.show();
            }
        });

        ;
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class
        gridView.setAdapter(new CustomGridAdapter(c, prgmNameList, prgmImages, prgmPoints));

        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        // On Click event for Single Gridview Item

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Sending image id to FullScreenActivity
                Toast.makeText(c, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });



        return rootView;
    }
}