// THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
// KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
// PARTICULAR PURPOSE.
//
// <author>Ryan Zhou and Chris Lee</author>
// <email>wannabedev.ta@gmail.com</email>
// <date>2015-08-14</date>

package com.usc.itp476.contact.contactproject.adapters;

import android.app.Activity;
import android.content.Context;
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
    /* You should always write a custom class that extends Base Adapter when using GUI that requires
    an adapter. You will Override the BaseAdapter's methods and write your own custom code, aka
    what do you want to happen when this method is called.
     */
    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private HashMap<String, ParseUser> friendsList;
    private boolean displayCheckbox = false;
    private static LayoutInflater inflater = null;
    private Activity parent = null;
    private ArrayList<ParseUser> friendsArrayForm;
    private ArrayList<String> selectedFriendIds;

    public FriendListGridAdapter(Context mainActivity,
                                 Boolean displayCheckBox,
                                 Activity parentActivity){
        context=mainActivity;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsList = ContactApplication.getFriendsList();
        displayCheckbox = displayCheckBox;
        parent = parentActivity;
        /* The parameters or arguments that we have passed in are NEEDED. For example, to load an
        image with Glide (see glide cass in helper folder), Glide needs to know the current context
        that we are in. From fragments and activities we can get the current context, but not from
        our adapter class. Hence, we need to pass it in and set what is the current context we are
        working with in our constructor. Do not pass in variables you don't absolutely need, it is
        a waste of memory and makes your code less readeable and more prone to bugs.
        */
        friendsArrayForm = new ArrayList<>(friendsList.values());

        if (displayCheckbox) {
            selectedFriendIds = CreateGameActivity.getSelectedFriendParseIDs();
        }
    }

    @Override
    public int getCount() {
        /* Here is one example of method overriding. The original BaseAdapter does not know what is
        the size of the collection we are keeping track of. We know that this adapter is for the
        friendslist. Hence, it makes sense to return friendsList.size().
         */
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
        /* Our public class Holder exists for organization. Each of the friends in friendslist "has"
        a holder.*/
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_tile, null);
        }

        //SET FRIENDS NAME AND IMAGE HERE
        holder.imageViewPicture = (ImageView) convertView.findViewById(R.id.imageViewPhotoFriend);
        holder.textViewScore = (TextView) convertView.findViewById(R.id.textViewScoreFriend);
        holder.checkBoxInvited = (CheckBox) convertView.findViewById(R.id.checkBoxInvite);
        holder.textViewObjectID = (TextView) convertView.findViewById(R.id.textViewObjectID);
        holder.textViewName = (TextView) convertView.findViewById(R.id.textViewNameFriend);
        ParseUser me = friendsArrayForm.get(position);

        String pictureURL = me.getString("pictureLink");
        String name = me.getString("name");

        //Now, we do some appropriate name parsing to for First L. instead of First Last format
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
        //This loads the image given a url Link with the glide API

        if (friendsList == null){
            holder.textViewObjectID.setText( "" );
            holder.textViewScore.setText("");
        }else{
            holder.textViewObjectID.setText(objectID);
            /*This is Chris's unique way of "saving the ObjectID". The getView method call is unique
            to the individual friend. How do we access the objectID later in the onClick? We could
            save it as a pseudo textView.
             */

            holder.textViewScore.setText(String.valueOf(me.getInt("totalHugs")));
        }
        if(displayCheckbox){
            /*This if state checks if we are on viewing the friends or adding friends to a game.
            If we are adding friends to a game (aka displayCheckBox), then we don't want to see their
            score so we set holder.textViewScore as GONE.
             */
            holder.textViewScore.setVisibility(View.GONE);
            holder.checkBoxInvited.setChecked(selectedFriendIds.contains(objectID));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView id = (TextView) view.findViewById(R.id.textViewObjectID);
                    CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBoxInvite);

                    /*Here, we have the actual logic for adding friends to the selectedFriendIds
                    collection. If the friend is checked already, then this means the user wants to
                    remove him/her. If not added then we add the friend. Appropriately check/uncheck
                    the boxes GUI.
                     */

                    checkbox.setChecked( !checkbox.isChecked() );
                    if (checkbox.isChecked()) {
                        selectedFriendIds.add(id.getText().toString());
                    } else {
                        selectedFriendIds.remove(id.getText().toString());
                    }
                }
            });
        }else{
            /* This is when we are using the adapter class as viewing friends. Set the checkboxes as
            gone instead of the score.
             */
            holder.checkBoxInvited.setVisibility(View.GONE);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //This is where we handle pressing gridtiles
                    TextView id = (TextView) view.findViewById(R.id.textViewObjectID);
                    ((AllTabActivity) FriendListGridAdapter.this.parent).showFriendProfile(
                            id.getText().toString());
                }
            });
        }
        return convertView;
    }
}