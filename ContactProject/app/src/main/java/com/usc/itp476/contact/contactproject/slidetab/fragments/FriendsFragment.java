package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.usc.itp476.contact.contactproject.CustomGridAdapter;
import com.usc.itp476.contact.contactproject.R;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private Context c;
    private Fragment f;
    private GridView gridView;
    ArrayList prgmName;
    public static String [] prgmNameList={"Ryan", "Chris", "Bob", "Smith", "Ryan",
            "Chris", "Bob", "Smith"};
    public static int [] prgmImages={ R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large,
            R.mipmap.large, R.mipmap.large ,R.mipmap.large ,R.mipmap.large};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.activity_friends, container, false);

        c = getActivity().getApplicationContext();
        gridView = (GridView) rootView.findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class
        gridView.setAdapter(new CustomGridAdapter(c, prgmNameList, prgmImages));

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