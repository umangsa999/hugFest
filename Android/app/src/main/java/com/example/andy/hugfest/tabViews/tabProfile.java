package com.example.andy.hugfest.tabViews;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.andy.hugfest.R;

public class tabProfile extends Activity {
    private ImageView profilePic;
    private Button buttonViewFacebook;
    private Button buttonEdit;
    private EditText editTextName;
    private boolean editMode = false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabprofile);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName.setTag(editTextName.getKeyListener());
        editTextName.setKeyListener(null);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editMode) { //editMode is false, allow us to edit
                    //change from the pencil icon to floppy disk (save)
                    buttonEdit.setBackgroundResource(R.drawable.ic_action_save_light);
                    editTextName.setKeyListener((KeyListener) editTextName.getTag());
                    editMode = true;

                    //sets the focus to the name
                    editTextName.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextName, InputMethodManager.SHOW_IMPLICIT);
                } else {

                    //we are done editing this, saving & change icon to edit pencil
                    editMode = false;
                    editTextName.setKeyListener(null);
                    buttonEdit.setBackgroundResource(R.mipmap.ic_launcher_edit);

                    //TODO server call to save
                }

            }
        });
    }
}