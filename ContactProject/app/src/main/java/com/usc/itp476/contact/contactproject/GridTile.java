package com.usc.itp476.contact.contactproject;

/**
 * Created by ryanzhou on 7/17/15.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GridTile extends LinearLayout {


    //load tile without attributes
    public GridTile(Context context) {

        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_tile,this);

    }


    //load Tile with attributes
    public GridTile(Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.grid_tile,this);

        //load all attributes
        TypedArray styleAttributes = null; //context.obtainStyledAttributes(attrs, R.styleable.GridTile);

        //get attribute values
        String image = null; // = styleAttributes.getString(R.styleable.GridTile_artist_image) ;
        String name = null; // = styleAttributes.getString(R.styleable.GridTile_artist_name) ;
        Boolean has_button = true; //Boolean.parseBoolean( styleAttributes.getString(R.styleable.GridTile_has_button) ) ;

        //load views and set values
        if (image != null){
            ImageView imageArtist = (ImageView) findViewById(R.id.imageArtist);

            Resources r = this.getResources();
            int id = r.getIdentifier(image, null, context.getPackageName());

            imageArtist.setImageResource ( id );
        }

        if (!has_button)
        {
            Button buttonChoose = (Button) findViewById(R.id.buttonChoose);
            buttonChoose.setVisibility(View.GONE);
        }

        TextView textArtist = (TextView) findViewById(R.id.textArtist);
        textArtist.setText(name);
        styleAttributes.recycle();
    }
}
