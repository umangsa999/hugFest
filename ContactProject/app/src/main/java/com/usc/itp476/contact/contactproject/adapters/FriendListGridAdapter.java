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

import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.ContactApplication;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.ingamescreen.CreateGameActivity;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;
import com.usc.itp476.contact.contactproject.PicassoTrustAll;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendListGridAdapter extends BaseAdapter {
    final String TAG = this.getClass().getSimpleName();
    //private Context mContext;
    Context context;
    private HashMap<String, ParseUser> friendsList;
    boolean mDisplayCheckBox = false;
    private static LayoutInflater inflater = null;
    private Activity parent = null;
    private boolean isAllTabNotCreate = false;
    private ArrayList<ParseUser> friendsArrayForm;

    public FriendListGridAdapter(Context mainActivity,
                                 Boolean displayCheckBox,
                                 Activity parentActivity, boolean isAllTabActivity){
        // TODO Auto-generated constructor stub
        context=mainActivity;
        inflater=(LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsList = ContactApplication.getFriendsList();
        mDisplayCheckBox = displayCheckBox;
        parent = parentActivity;
        isAllTabNotCreate = isAllTabActivity;
        friendsArrayForm = new ArrayList<>(friendsList.values());
    }

    @Override
    public int getCount() {
        return friendsList == null ? 0 : friendsList.size();
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
        ImageView img;
        TextView points;
        TextView objectID;
        TextView name;
        CheckBox invited;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.grid_tile, null);

        //SET FRIENDS NAME AND IMAGE HERE
        holder.img = (ImageView) rowView.findViewById(R.id.imageViewFriend);
        holder.points = (TextView) rowView.findViewById(R.id.imageViewScore);
        holder.invited = (CheckBox) rowView.findViewById(R.id.ckbxInvite);
        holder.objectID = (TextView) rowView.findViewById(R.id.ObjectIdTextView);
        holder.name = (TextView) rowView.findViewById(R.id.txvwFriendName);
        ParseUser me = friendsArrayForm.get(position);

        String pictureURL = pictureURL = me.getString("pictureLink");
        String name = me.getString("name");
        if (name.length() > 12){
            int indexOfSpace = name.indexOf(' ');
            if (indexOfSpace < 10) {
                char lastNameLetter = Character.toUpperCase(name.charAt(indexOfSpace + 1));
                name = name.substring(0, indexOfSpace + 1) + lastNameLetter + ".";
            }else{
                name = name.substring(0, indexOfSpace);
            }
        }
        holder.name.setText(name);
        int points = me.getInt("totalHugs");
        holder.points.setText( String.valueOf(points) );
        String objectID = me.getObjectId();

        PicassoTrustAll.getInstance(context).load(pictureURL).error(R.mipmap.medium).placeholder(R.mipmap.medium).fit().into(holder.img);

        holder.objectID.setText( friendsList == null ? "" : objectID);
        if(mDisplayCheckBox){
            holder.points.setVisibility(View.GONE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView id = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    CheckBox c = (CheckBox) view.findViewById(R.id.ckbxInvite);
                    c.setChecked(!c.isChecked());
                    //this is invite friends
                    if ( holder.invited.isChecked()) {
                        Log.wtf(TAG + "Tring to add freisfsdf", "CAT");
                        CreateGameActivity.getSelectedFriendParseIDs().add(id.getText().toString());
                        Log.wtf(TAG + "size: ",
                                "" + CreateGameActivity.getSelectedFriendParseIDs().size() );
                    }else{
                        CreateGameActivity.getSelectedFriendParseIDs().remove(id.getText().toString());
                    }
                }
            });
        }else{
            holder.points.setText(friendsList == null ? "" : String.valueOf(me.getInt("totalHugs")));
            holder.invited.setVisibility(View.GONE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //This is where we handle pressing gridtiles
                    TextView id = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    //This is the stinkin friends list
                    Log.wtf(TAG + "Clicked", id.getText().toString() );
                    ((AllTabActivity) FriendListGridAdapter.this.parent).showFriendProfile(
                            id.getText().toString());
                    }
                });
        }
        return rowView;
    }
}