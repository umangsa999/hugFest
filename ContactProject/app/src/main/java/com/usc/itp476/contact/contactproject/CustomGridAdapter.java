package com.usc.itp476.contact.contactproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomGridAdapter extends BaseAdapter {

    //private Context mContext;
    String[] result;
    String[] points;
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;
    private static int staticPosition = 0;

    public CustomGridAdapter(Context mainActivity, String[] prgmNameList, int[] prgmImages, String[] score){
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater=(LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        points = score;
    }
    @Override
    public int getCount() {
        return result.length;
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
        TextView tv;
        ImageView img;
        TextView points;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        staticPosition = position;
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.grid_tile, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textViewFriendName);
        holder.img = (ImageView) rowView.findViewById(R.id.imageViewFriend);
        holder.points = (TextView) rowView.findViewById(R.id.imageViewScore);
        holder.tv.setText(result[position].length() > 5 ?
                result[position].substring(0,5) + "..." : result[position]);
        holder.img.setImageResource(imageId[position]);
        holder.points.setText(points[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You Clicked " + result[staticPosition], Toast.LENGTH_SHORT).show();

            }
        });
        return rowView;
    }
}
