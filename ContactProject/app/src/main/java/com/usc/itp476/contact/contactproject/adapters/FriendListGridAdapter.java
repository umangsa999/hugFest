package com.usc.itp476.contact.contactproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.ingamescreen.CreateGameActivity;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendListGridAdapter extends BaseAdapter {
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private HashMap<String, ParseUser> friendsList;
    private boolean displayCheckbox = false;
    private static LayoutInflater inflater = null;
    private Activity parent = null;
    private ArrayList<ParseUser> friendsArrayForm;

    public FriendListGridAdapter(Context mainActivity,
                                 Boolean displayCheckBox,
                                 Activity parentActivity){
        context=mainActivity;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsList = ContactApplication.getFriendsList();
        displayCheckbox = displayCheckBox;
        parent = parentActivity;
        friendsArrayForm = new ArrayList<>(friendsList.values());
    }

    @Override
    public int getCount() {
        if (friendsList == null) {
            return 0;
        }else{
            return friendsList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (friendsArrayForm == null)
            return null;
        else{
            return friendsArrayForm.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        friendsArrayForm = new ArrayList<>(friendsList.values());
    }

    public class Holder {
        ImageView imageViewPicture;
        TextView textViewScore;
        TextView textViewObjectID;
        TextView textViewName;
        CheckBox checkBoxInvited;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_tile, null);
        }

        //SET FRIENDS NAME AND IMAGE HERE
        holder.imageViewPicture = (ImageView) convertView.findViewById(R.id.imageViewFriend);
        holder.textViewScore = (TextView) convertView.findViewById(R.id.imageViewScore);
        holder.checkBoxInvited = (CheckBox) convertView.findViewById(R.id.ckbxInvite);
        holder.textViewObjectID = (TextView) convertView.findViewById(R.id.ObjectIdTextView);
        holder.textViewName = (TextView) convertView.findViewById(R.id.txvwFriendName);
        ParseUser me = friendsArrayForm.get(position);

        String pictureURL = me.getString("pictureLink");
        String name = me.getString("name");
        if (name != null) {
            if (name.length() > 12){
                int indexOfSpace = name.indexOf(' ');
                if (indexOfSpace < 10) {
                    char lastNameLetter = Character.toUpperCase(name.charAt(indexOfSpace + 1));
                    name = name.substring(0, indexOfSpace + 1) + lastNameLetter + ".";
                }else{
                    name = name.substring(0, indexOfSpace);
                }
            }
            holder.textViewName.setText(name);
        }else{
            holder.textViewName.setText("");
        }

        int points = me.getInt("totalHugs");
        holder.textViewScore.setText(String.valueOf(points));
        String objectID = me.getObjectId();

        Glide.with(context).load(pictureURL).error(R.mipmap.medium).into(holder.imageViewPicture);

        if (friendsList == null){
            holder.textViewObjectID.setText( "" );
        }else{
            holder.textViewObjectID.setText(objectID);
        }
        if(displayCheckbox){
            holder.textViewScore.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView id = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    CheckBox c = (CheckBox) view.findViewById(R.id.ckbxInvite);
                    c.setChecked(!c.isChecked());
                    //this is invite friends
                    if (holder.checkBoxInvited.isChecked()) {
                        Log.wtf(TAG + "Tring to add freisfsdf", "CAT");
                        CreateGameActivity.getSelectedFriendParseIDs().add(id.getText().toString());
                        Log.wtf(TAG + "size: ",
                                "" + CreateGameActivity.getSelectedFriendParseIDs().size());
                    } else {
                        CreateGameActivity.getSelectedFriendParseIDs().remove(id.getText().toString());
                    }
                }
            });
        }else{
            holder.textViewScore.setText(friendsList == null ? "" : String.valueOf(me.getInt("totalHugs")));
            holder.checkBoxInvited.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //This is where we handle pressing gridtiles
                    TextView id = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    //This is the stinkin friends list
                    Log.wtf(TAG + "Clicked", id.getText().toString());
                    ((AllTabActivity) FriendListGridAdapter.this.parent).showFriendProfile(
                            id.getText().toString());
                }
            });
        }
        return convertView;
    }
}