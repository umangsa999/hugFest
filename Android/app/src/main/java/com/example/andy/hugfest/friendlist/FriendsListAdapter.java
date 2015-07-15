package com.example.andy.hugfest.friendlist;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andy.hugfest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsListAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private static LayoutInflater inflater=null;
    private ArrayList<JSONObject> data;
    Activity mContext;
    public FriendsListAdapter(Activity context, ArrayList<JSONObject> d) {
        mContext = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = d;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i; //TODO fix this
    }

    public class ViewHolder {
        ImageView img;
        TextView name;
        TextView status;
        TextView hugData;
        String userID;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        viewHolder = new ViewHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.friendslist, null);
        viewHolder.name =(TextView) rowView.findViewById(R.id.textView1);
        viewHolder.status =(TextView) rowView.findViewById(R.id.textView2);
        viewHolder.hugData =(TextView) rowView.findViewById(R.id.textView3);
        viewHolder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        viewHolder.img.setImageResource(R.drawable.ic_profile);
        Log.wtf(this.getClass().getSimpleName(), "inflated");
        if (data.size() <= 0){
            viewHolder.name.setText("No value");
        }else {
            try {
                viewHolder.userID = data.get(i).getString("_id");
                viewHolder.name.setText(data.get(i).getString("name"));
                viewHolder.status.setText(data.get(i).getInt("status"));
                viewHolder.hugData.setText(data.get(i).getInt("currentHugs") +
                        " hugs/" + data.get(i).getInt("games") + "games");
                Log.wtf(this.getClass().getSimpleName(), "all good");
            } catch (JSONException e) {
                viewHolder.name.setText("no name");
                viewHolder.status.setText(0);
                viewHolder.hugData.setText("0 hugs/0 games");
                Log.wtf(this.getClass().getSimpleName(), "could not load name for " + i);
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "Clicked: " + viewHolder.userID, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return rowView;
    }
}