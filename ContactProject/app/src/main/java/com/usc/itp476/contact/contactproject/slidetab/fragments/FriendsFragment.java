package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usc.itp476.contact.contactproject.R;

public class FriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.activity_friends, container, false);

        return rootView;
    }
}