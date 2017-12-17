package com.vies.notifikacijevesti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by milan on 27-Oct-17.
 */

public class IstorijaListAdapter extends RecyclerView.Adapter<IstorijaListAdapter.ViewHolder> {

    private ArrayList<String> mDataSet;
    private ArrayList<String> mTitlesSet;
    private ArrayList<String> mUrlsSet;
    private Context mContext;
    private View rootView;
    private TinyDB tinyDB;
    TextView empty;


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleText;
        public TextView mDataText;
        public ViewHolder (View v){
            super(v);
            mTitleText = v.findViewById(R.id.textViewIstorijaTitle);
            mDataText = v.findViewById(R.id.textViewIstorijaData);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != -1){

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrlsSet.get(getAdapterPosition())));
                        mContext.startActivity(browserIntent);
                        //removeAt(getAdapterPosition());
                    }
                }
            });
        }

        public TextView getTextViewTitle() { return mTitleText; }

        public TextView getTextViewData(){ return  mDataText; }
    }

    public IstorijaListAdapter( ArrayList<String> myTitlesSet, ArrayList<String> myDataSet, ArrayList<String> myUrlsSet, Context context, View myRootView){
        mTitlesSet = myTitlesSet;
        mDataSet = myDataSet;
        mUrlsSet = myUrlsSet;
        mContext = context;
        rootView = myRootView;
        tinyDB = new TinyDB(mContext);
        empty = rootView.findViewById(R.id.emptyVesti1);
    }

    @Override
    public IstorijaListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.istorija_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(IstorijaListAdapter.ViewHolder holder, int position) {
        holder.getTextViewData().setText(mDataSet.get(position));
        holder.getTextViewTitle().setText(mTitlesSet.get(position));
        if (mTitlesSet.size() == 0){

            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void addTitle(String title){
        mTitlesSet.add(0, title);
        if (mTitlesSet.size() > 30){
            for (int i = 30; i < mTitlesSet.size(); i++){
                mTitlesSet.remove(i);
            }
            tinyDB.putListString("istorijaTitles", mTitlesSet);
        }

        empty.setVisibility(View.GONE);
    }

    public void addData(String data){
        mDataSet.add(0, data);
        if (mDataSet.size() > 30){
            for (int i = 30; i < mDataSet.size(); i++){
                mDataSet.remove(i);
            }
            tinyDB.putListString("istorijaData", mDataSet);
        }
//        tinyDB.putListString("istorijaData", mDataSet);
    }

    public void addUrl(String url){
        mUrlsSet.add(0, url);
        if (mUrlsSet.size() > 30){
            for (int i = 30; i < mUrlsSet.size(); i++){
                mUrlsSet.remove(i);
            }
            tinyDB.putListString("istorijaUrls", mUrlsSet);

        }
//        tinyDB.putListString("istorijaUrls", mUrlsSet);
    }

    public void removeAt(int position){

        mTitlesSet.remove(position);
        mDataSet.remove(position);
        mUrlsSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTitlesSet.size());
        tinyDB.putListString("istorijaTitles", mTitlesSet);
        tinyDB.putListString("istorijaData", mDataSet);
        tinyDB.putListString("istorijaUrls", mUrlsSet);
        if (mTitlesSet.size() == 0){

            empty.setVisibility(View.VISIBLE);
        }

    }

    public void clearAll(){
        int size = mTitlesSet.size();
        clearTitles();
        clearData();
        clearUrls();
        notifyItemRangeRemoved(0, size);
        if (mTitlesSet.size() == 0){

            empty.setVisibility(View.VISIBLE);
        }
    }

    public void clearTitles(){
        mTitlesSet.clear();
        tinyDB.putListString("istorijaTitles", mTitlesSet);
    }

    public void clearData(){
        mDataSet.clear();
        tinyDB.putListString("istorijaData", mDataSet);
    }
    public void clearUrls(){
        mUrlsSet.clear();
        tinyDB.putListString("istorijaUrls", mUrlsSet);
    }

}
