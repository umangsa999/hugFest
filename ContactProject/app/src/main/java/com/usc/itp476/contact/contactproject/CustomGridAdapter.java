package com.usc.itp476.contact.contactproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ryanzhou on 7/18/15.
 */
public class CustomGridAdapter extends BaseAdapter {

    //private Context mContext;
    String[] result;
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;
    private static int staticPosition = 0;

    public CustomGridAdapter(Context mainActivity, String[] prgmNameList, int[] prgmImages){
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater=(LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        staticPosition = position;
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.grid_tile, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textViewFriendName);
        holder.img=(ImageView) rowView.findViewById(R.id.imageViewFriend);
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You Clicked " + result[staticPosition], Toast.LENGTH_LONG).show();

            }
        });
        return rowView;
    }
}
