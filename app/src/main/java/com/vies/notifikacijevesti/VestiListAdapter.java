
package com.vies.notifikacijevesti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView in Tab2Vesti.
 */
public class VestiListAdapter extends RecyclerView.Adapter<VestiListAdapter.ViewHolder> {
    private static final String TAG = "VestiListAdapter";

    private ArrayList<String> mDataSet;
    private ArrayList<String> mTitlesSet;
    private ArrayList<String> mUrlsSet;

    private TextView textViewEmpty;
    private Context context;
    private TinyDB tinyDB;
    /**
     * Provide a reference to the type of views that are used (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewData;

        ViewHolder(View v)   {
            super(v);

            textViewTitle = v.findViewById(R.id.textViewTitle);
            textViewData = v.findViewById(R.id.textViewData);
            ImageButton button = (ImageButton) v.findViewById(R.id.delVestiButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != -1)
                    removeAt(getAdapterPosition());
                }
            });
            /* Define a click listener for the ViewHolder's View */
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrlsSet.get(getAdapterPosition())));
                    context.startActivity(browserIntent);
                    removeAt(getAdapterPosition());
                }
            });

        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }
        public TextView getTextViewData() { return textViewData; }
    }

    VestiListAdapter(ArrayList<String> dataSet, ArrayList<String> titlesSet, ArrayList<String> urlsSet, Context context, View view) {
        mDataSet = dataSet;
        mTitlesSet = titlesSet;
        mUrlsSet = urlsSet;
        this.context = context;
        tinyDB = new TinyDB(context);
        textViewEmpty = view.findViewById(R.id.emptyVesti);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.vesti_list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Sets title and data text, if the dataset is empty, set the empty text view as visible
    @Override
    public void
    onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewData().setText(mDataSet.get(position));
        viewHolder.getTextViewTitle().setText(mTitlesSet.get(position));
        if (mTitlesSet.size() == 0){

            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {

        return mDataSet.size();
    }

    /**
     * Adds data (body text) to the adapter and local storage
     */
    public void addData(String vest){
        mDataSet.add(0, vest);
        if (mDataSet.size() > 30){
            for (int i = 30; i < mDataSet.size(); i++){
                mDataSet.remove(i);

            }
            tinyDB.putListString("vesti", mDataSet);

        }
    }

    /**
     * Adds titles to the adapter and local storage
     */
    public void addTitle(String title){
        mTitlesSet.add(0, title);
        if (mTitlesSet.size() > 30){
            for (int i = 30; i < mTitlesSet.size(); i++){
                mTitlesSet.remove(i);
            }
            tinyDB.putListString("titles", mTitlesSet);

        }
    }

    /**
     * Adds URls to the adapter and local storage
     */
    public void addUrl(String url){
        mUrlsSet.add(0, url);

        if (mUrlsSet.size() > 30){
            for (int i = 30; i < mUrlsSet.size(); i++){
                mUrlsSet.remove(i);
            }
            tinyDB.putListString("urls", mUrlsSet);
        }
    }

    // Removes an element at a specific position and updates local storage
    private void removeAt(int position) {

        mDataSet.remove(position);
        mTitlesSet.remove(position);
        mUrlsSet.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataSet.size());

        tinyDB.putListString("vesti", mDataSet);
        tinyDB.putListString("titles", mTitlesSet);
        tinyDB.putListString("urls", mUrlsSet);
        if (mTitlesSet.size() == 0){

            textViewEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Removes an element at a specific position without updating storage
     */
    public void removeWithoutUpdatingDatabaseAt(int position){

        mDataSet.remove(position);
        mTitlesSet.remove(position);
        mUrlsSet.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataSet.size());

        if (mTitlesSet.size() == 0) {
            textViewEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Clears data from the adapter and local storage
     */
    public void clearAll(){
        int size = mDataSet.size();
        clearData();
        clearTitles();
        clearUrls();
        notifyItemRangeRemoved(0, size);
        if (mTitlesSet.size() == 0){

            textViewEmpty.setVisibility(View.VISIBLE);
        }

    }

    private void clearData(){
        mDataSet.clear();
        tinyDB.putListString("vesti", mDataSet);    }

    private void clearTitles(){
        mTitlesSet.clear();
        tinyDB.putListString("titles", mTitlesSet);
    }

    private void clearUrls(){
        mUrlsSet.clear();
        tinyDB.putListString("urls", mUrlsSet);
    }



}
