package com.usc.itp476.contact.contactproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.slidetab.AllTabActivity;

import java.util.ArrayList;

public class FriendListGridAdapter extends BaseAdapter {
    final String TAG = this.getClass().getSimpleName();
    //private Context mContext;
    Context context;
    private ArrayList<ParseUser> friendsList;
    boolean mDisplayCheckBox = false;
    private static LayoutInflater inflater = null;
    private static int staticPosition = 0;
    private AllTabActivity mAllTabActivity = null;

    public FriendListGridAdapter(Context mainActivity,
                                 ArrayList<ParseUser> displayThese,
                                 Boolean displayCheckBox,
                                 AllTabActivity allTabActivity){
        // TODO Auto-generated constructor stub
        context=mainActivity;
        inflater=(LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsList = displayThese;
        mDisplayCheckBox = displayCheckBox;
        this.mAllTabActivity = allTabActivity;
    }

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int i) {
        return friendsList.get(i);
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
        int id = 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        staticPosition = position;
        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.grid_tile, null);

        holder.img = (ImageView) rowView.findViewById(R.id.imageViewFriend);
        holder.points = (TextView) rowView.findViewById(R.id.imageViewScore);
        holder.invited = (CheckBox) rowView.findViewById(R.id.ckbxInvite);
        holder.objectID = (TextView) rowView.findViewById(R.id.ObjectIdTextView);
        holder.img.setImageResource(R.mipmap.medium);
        holder.id = position;
        holder.objectID.setText(friendsList.get(position).getObjectId());

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
            holder.points.setText( String.valueOf(friendsList.get(position).getInt("totalHugs")));
            holder.invited.setVisibility(View.GONE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            //This is where we handle pressing gridtiles
                    TextView t = (TextView) view.findViewById(R.id.ObjectIdTextView);
                    mAllTabActivity.showFriendProfile(t.getText().toString());
                }
            });
        }
        return rowView;
    }
}