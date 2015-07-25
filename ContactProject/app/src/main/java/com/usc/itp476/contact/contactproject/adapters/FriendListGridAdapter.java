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
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;
import com.usc.itp476.contact.contactproject.slidetab.helper.PicassoTrustAll;

import java.util.ArrayList;

public class FriendListGridAdapter extends BaseAdapter {
    final String TAG = this.getClass().getSimpleName();
    //private Context mContext;
    Context context;
    private ArrayList<ParseUser> friendsList;
    boolean mDisplayCheckBox = false;
    private static LayoutInflater inflater = null;
    private Activity parent = null;
    private boolean isAllTabNotCreate = false;

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
    }

    public void setFriendsList(ArrayList<ParseUser> list){
        friendsList = list;
    }

    @Override
    public int getCount() {
        return friendsList == null ? 0 : friendsList.size();
    }

    @Override
    public Object getItem(int i) {
        return friendsList == null ? null : friendsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        ImageView img;
        TextView points;
        TextView objectID;
        CheckBox invited;
        String friendParseUserID;
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
        holder.img.setImageResource(R.mipmap.medium);

        String pictureURL = ContactApplication.getFriendsList().get(position).getString("pictureLink");
        String name = ContactApplication.getFriendsList().get(position).getString("name");
        int points = ContactApplication.getFriendsList().get(position).getInt("totalHugs");
        holder.points.setText( String.valueOf(points) );
        holder.friendParseUserID = ContactApplication.getFriendsList().get(position).getString("objectId");

        PicassoTrustAll.getInstance( context )
                .load(pictureURL).fit().into(holder.img);

        holder.objectID.setText( friendsList == null ? "" : name );
        if(mDisplayCheckBox){
            holder.points.setVisibility(View.GONE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox c = (CheckBox) view.findViewById(R.id.ckbxInvite);
                    c.setChecked(!c.isChecked());
                }
            });
        }else{
            holder.points.setText( friendsList == null ?
                    "" :
                    String.valueOf(friendsList.get(position).getInt("totalHugs")));
            holder.invited.setVisibility(View.GONE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //This is where we handle pressing gridtiles
                    TextView name = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    //TextView score = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    if (isAllTabNotCreate) {
                        //This is the stinkin friends list
                        Log.wtf(TAG + "Clicked", holder.friendParseUserID );
                        ((AllTabActivity) FriendListGridAdapter.this.parent).showFriendProfile(holder.friendParseUserID);
                    }
                    else{
                        //this is invite friends

                    }
                }
            });
        }
        return rowView;
    }
}