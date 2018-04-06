package com.vies.notifikacijevesti;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Provides views to the Navigation Drawer
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    // Which View is being worked on
    private static final int TYPE_HEADER = 0;
    // Whether the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    // String Array to store the passed titles Value from MainActivity.java
    private String mNavTitles[];
    // Int Array to store the passed icons resource value from MainActivity.java
    private int mIcons[];

    private String largerText;        //String Resource for header View Name
    private String smallerText;       //String Resource for header view email

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView textView;
        ImageView imageView;
        ImageView profile;
        TextView Name;
        TextView email;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {
                textView = itemView.findViewById(R.id.rowText); // Creating TextView object with the id of textView from item_row.xml
                imageView = itemView.findViewById(R.id.rowIcon);// Creating ImageView object with the id of ImageView from item_row.xml
                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {

                Name = itemView.findViewById(R.id.name);         // Creating Text View object from header.xmlader.xml for largerText
                email = itemView.findViewById(R.id.email);       // Creating Text View object from sidebar_headerr_header.xml for email
                profile = itemView.findViewById(R.id.circleView);// Creating Image view object from header.xmlader.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }
        }
    }

    NavDrawerAdapter(String mNavTitles[], int mIcons[], String largerText, String smallerText) { // NavDrawerAdapter Constructor with titles and icons parameter
        this.largerText = largerText;
        this.smallerText = smallerText;
        this.mNavTitles = mNavTitles;
        this.mIcons = mIcons;
    }

    /* Inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xmlader.xml
     * if the viewType is TYPE_HEADER
     * and pass it to the view holder */

    @Override
    public NavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_item, parent, false); //Inflating the layout

            return new ViewHolder(v, viewType);

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_drawer_header, parent, false); //Inflating the layout

            return new ViewHolder(v, viewType);

        }
        return null;

    }

    /* Called when the item in a row is needed to be displayed, the int position
     * shows which item at which position is to be displayed and the holder id of the holder object shows
     * which view type is being created 1 for item row */
    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {
            /* The list view is going to be called after the header view so decrement the
             position by 1 and pass it to the holder while setting the text and image */
            holder.textView.setText(mNavTitles[position - 1]);
            holder.imageView.setImageResource(mIcons[position - 1]);
        } else {

            // Set the resources for header view
            holder.Name.setText(largerText);
            holder.email.setText(smallerText);
        }
    }

    // The number of items in the list will be +1 the titles including the header view.
    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}