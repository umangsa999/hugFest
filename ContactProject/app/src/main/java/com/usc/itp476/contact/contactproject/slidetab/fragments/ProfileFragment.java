package com.usc.itp476.contact.contactproject.slidetab.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.usc.itp476.contact.contactproject.POJO.GameData;
import com.usc.itp476.contact.contactproject.R;

public class ProfileFragment extends Fragment {

    private ImageButton imbnEdit;
    private TextView txvwName;
    private TextView txvwTotal;
    private EditText edtxName;
    private ImageView imgPhoto = null;
    private boolean isEditing = false;
    public boolean mFriendProfile = false;
    public void setName(String name){
        txvwTotal.setText(name);
    }

    public void friendProfileTrue(){
        mFriendProfile = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(
                R.layout.activity_profile, container, false);

        imbnEdit = (ImageButton) rootView.findViewById(R.id.btnEdit);

        if( mFriendProfile )
            imbnEdit.setVisibility(View.GONE);

        imgPhoto = (ImageView) rootView.findViewById(R.id.imvwPhoto);
        txvwName = (TextView) rootView.findViewById(R.id.txvwName);
        txvwTotal = (TextView) rootView.findViewById(R.id.txvwTotal);
        edtxName = (EditText) rootView.findViewById(R.id.edtxName);
        setListeners();
        loadSaveData();
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

    private void loadSaveData(){
        SharedPreferences sharedPreferences =
                getActivity().getSharedPreferences(GameData.PREFFILE, Context.MODE_PRIVATE);

        String name = sharedPreferences.getString(GameData.FULL_NAME, null);
        int totalhugs = sharedPreferences.getInt(GameData.TOTAL_HUGS, -1);

        if (name != null){
            txvwName.setText(name);
        }

        if (totalhugs != -1){
            txvwTotal.setText(String.valueOf(totalhugs));
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

            //save a working name
            SharedPreferences sharedPreferences =
                    getActivity().getSharedPreferences(GameData.PREFFILE,
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();

            sharedPrefEditor.putString(GameData.FULL_NAME,
                    txvwName.getText().toString());

            sharedPrefEditor.commit();
        }
    }
}