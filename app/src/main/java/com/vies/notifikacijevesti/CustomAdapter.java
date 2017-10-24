
package com.vies.notifikacijevesti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<String> mDataSet;
    private ArrayList<String> mTitlesSet;
    private ArrayList<String> mUrlsSet;
    private Context context;
    private TinyDB tinyDB;
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewData;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrlsSet.get(getAdapterPosition())));
                    context.startActivity(browserIntent);
                    removeAt(getAdapterPosition());
//                    ArrayList<String> logLista = tinyDB.getListString("vesti");
//                    ArrayList<String> logTitles = tinyDB.getListString("titles");
//                    ArrayList<String> logUrls = tinyDB.getListString("urls");
//
//                    for (int i = 0; i < logLista.size(); i++){
//                        Log.d("testlog", "element: " + i +  " " + logLista.get(i));
//                    }
//
//                    for (int i = 0; i < logTitles.size(); i++){
//                        Log.d("testlog", "element: " + i +  " " + logTitles.get(i));
//                    }
//
//                    for (int i = 0;i < logUrls.size(); i++){
//                        Log.d("testlog", "element: " + i +  " " + logUrls.get(i));
//                    }
                }
            });
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            textViewData = (TextView) v.findViewById(R.id.textViewData);
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }
        public TextView getTextViewData() { return textViewData; }
    }

    public CustomAdapter(ArrayList<String> dataSet, ArrayList<String> titlesSet, ArrayList<String> urlsSet, Context context) {
        mDataSet = dataSet;
        mTitlesSet = titlesSet;
        mUrlsSet = urlsSet;
        this.context = context;
        tinyDB = new TinyDB(context);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");
        viewHolder.getTextViewData().setText(mDataSet.toArray(new String[0])[position]);
        viewHolder.getTextViewTitle().setText(mTitlesSet.toArray(new String[0]) [position]);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addData(String vest){
        mDataSet.add(0, vest);
        tinyDB.putListString("vesti", mDataSet);
    }

    public void addTitle(String title){
        mTitlesSet.add(0, title);
        tinyDB.putListString("titles", mTitlesSet);
    }

    public void addUrl(String url){
        mUrlsSet.add(0, url);
        tinyDB.putListString("urls", mUrlsSet);
    }


    public void removeAt(int position) {

        mDataSet.remove(position);
        mTitlesSet.remove(position);
        mUrlsSet.remove(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataSet.size());

        tinyDB.putListString("vesti", mDataSet);
        tinyDB.putListString("titles", mTitlesSet);
        tinyDB.putListString("urls", mUrlsSet);

    }

    public void clearAll(){
        int size = mDataSet.size();
        clearData();
        clearTitles();
        clearUrls();
        notifyItemRangeRemoved(0, size);
    }

    public void clearData(){
        mDataSet.clear();
        ArrayList<String> emptyList = new ArrayList<>();
        emptyList.add(0, "");
        tinyDB.putListString("vesti", emptyList);    }

    public void clearTitles(){
        mTitlesSet.clear();
        ArrayList<String> emptyList = new ArrayList<>();
        emptyList.add(0, "");
        tinyDB.putListString("titles", emptyList);
    }

    public void clearUrls(){
        mUrlsSet.clear();
        ArrayList<String> emptyList = new ArrayList<>();
        emptyList.add(0, "http://www.google.com");
        tinyDB.putListString("urls", emptyList);
    }

}
