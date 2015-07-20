package com.usc.itp476.contact.contactproject.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.usc.itp476.contact.contactproject.R;
import com.usc.itp476.contact.contactproject.slidetab.fragments.ProfileFragment;

public class FriendListGridAdapter extends BaseAdapter {

    //private Context mContext;
    String[] points;
    Context context;
    int[] imageId;
    private ViewPager mPager = null;
    boolean mDisplayCheckBox = false;
    private static LayoutInflater inflater = null;
    private static int staticPosition = 0;
    ProfileFragment p;

    public FriendListGridAdapter(Context mainActivity,
                                 String[] prgmNameList,
                                 int[] prgmImages,
                                 String[] score,
                                 Boolean displayCheckBox,
                                 ViewPager p,
                                 ProfileFragment pfrag){
        // TODO Auto-generated constructor stub
        mPager = p;
        context=mainActivity;
        imageId=prgmImages;
        inflater=(LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        points = score;
        mDisplayCheckBox = displayCheckBox;

        setProfileFragment( pfrag  );
    }

    public void setProfileFragment( ProfileFragment p  ){
        this.p = p;
    }

    public void setPager(ViewPager pager){
        mPager = pager;
    }

    @Override
    public int getCount() {
        return points.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {
        ImageView img;
        TextView points;
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
        holder.img.setImageResource(imageId[position]);
        holder.id = position;

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
            holder.points.setText( points[position]);
            holder.invited.setVisibility(View.GONE);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TextView t = (TextView)view.findViewById(R.id.imageViewScore);
                    //String temp = t.getText().toString();

                    Log.wtf("Friendlistgridadapter", ""+holder.id );

                    p.setName( ""+ holder.id );
                    mPager.setCurrentItem(2);

                    //Toast.makeText( , holder.id , Toast.LENGTH_SHORT).show();
                    //int x = Integer.getInteger(temp);
                    //mPager.setCurrentItem( 0 );
//                    CheckBox c = (CheckBox) view.findViewById(R.id.ckbxInvite);
//                    c.setChecked(!c.isChecked());
                }
            });
        }
        return rowView;
    }

}
