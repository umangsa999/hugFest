package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.adapters.FriendListGridAdapter;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private ImageButton buttonAdd;
    private Context mContext;
    private Activity mActivity;
    private GridView gridView;
    private ViewPager mPager;
    private String mInputAddFriendText = "";
    private ProfileFragment pFrag;
    private PagerAdapter mPagerAdapter;
    AlertDialog.Builder builder;
    // TODO: Replace this test id with your personal ad unit id

    public static String [] prgmNameList={"Ryan", "Chris", "Mike", "Rob", "Nathan",
            "Paulina", "Trina", "Raymond",
            "Nathan", "Paulina", "Trina", "Raymond"};
    public static int [] prgmImages={ R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large,
            R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large,
            R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large};
    public static String [] prgmPoints={"0", "1", "2", "3", "4", "5", "6", "7", "8", "5", "6", "7"};
    private ArrayList<Fragment> tabArray;
    private AllTabActivity mAllTabActivity;

    public void setPager( ViewPager p){
        mPager = p;
    }
    public void setpFrag( ProfileFragment p ){
        pFrag = p;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.activity_friends, container, false);
        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();

        buttonAdd = (ImageButton) rootView.findViewById(R.id.btnAdd);
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

        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class

        gridView.setAdapter(new FriendListGridAdapter( mContext,
                prgmNameList, prgmImages, prgmPoints, false,
                mPager, pFrag, tabArray, mPagerAdapter, mAllTabActivity) );

        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

        // On Click event for Single Gridview Item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Sending image id to FullScreenActivity
                //this shit doesn't run
            }
        });

        return rootView;
    }

    public void setTabArray(ArrayList<Fragment> tabArray) {
        this.tabArray = tabArray;
    }

    public void setPager(PagerAdapter pagerAdapter) {
        this.mPagerAdapter = pagerAdapter;
    }

    public void setAllTabActivity(AllTabActivity allTabActivity) {
        this.mAllTabActivity = allTabActivity;
    }

    @Override
    public void onDestroy() {
        //moPubView.destroy();
        super.onDestroy();
    }

}