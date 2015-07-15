package com.example.andy.hugfest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ryanzhou on 7/9/15.
 */
public class customAdapter extends BaseAdapter {
    private Holder holder;
    private static LayoutInflater inflater=null;
    Activity mContext;
    public customAdapter(Activity context ) {
        mContext = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    public class Holder{
        ImageView img;
        TextView name;
        TextView status;
        TextView hugData;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.friendslist, null);
        holder.name =(TextView) rowView.findViewById(R.id.textView1);
        holder.status =(TextView) rowView.findViewById(R.id.textView2);
        holder.hugData =(TextView) rowView.findViewById(R.id.textView3);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.img.setImageResource( R.drawable.ic_profile );

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( mContext, "Clicked: "+ holder.toString() ,Toast.LENGTH_SHORT).show();
            }
        } );
        return rowView;
    }
}
