package com.example.andy.hugfest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ryanzhou on 7/9/15.
 */
public class gameCustomAdapter extends BaseAdapter {
    private Holder holder;
    private static LayoutInflater inflater=null;
    Activity mContext;
    public gameCustomAdapter(Activity context ) {
        mContext = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 6;
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
        TextView gameName;
        TextView numPlayer;
        TextView status;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.gamelist, null);
        holder.gameName =(TextView) rowView.findViewById(R.id.textView15);
        holder.numPlayer =(TextView) rowView.findViewById(R.id.textView16);
        holder.status =(TextView) rowView.findViewById(R.id.textView17);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText( mContext, "Clicked: "+ holder.toString() ,Toast.LENGTH_SHORT).show();
            }
        } );
        return rowView;
    }
}
