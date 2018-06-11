package com.vies.notifikacijevesti;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Provides views for the RecyclerView in Tab1Istorija
 */

public class IstorijaListAdapter extends RecyclerView.Adapter<IstorijaListAdapter.ViewHolder> {

    private ArrayList<String> mDataSet;
    private ArrayList<String> mTitlesSet;
    private ArrayList<String> mUrlsSet;
    private Context mContext;
    private TinyDB tinyDB;
    private TextView empty;
    private CustomTabsIntent customTabsIntent;
    private NotificationManager notificationManager;
    // variable to track event time
    private long mLastClickTime;

    /**
     * Provide a reference to the type of views that are used (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleText;
        TextView mDataText;

        ViewHolder(View v) {
            super(v);
            // initialize two views for title and data for each card in the Recycler View
            mTitleText = v.findViewById(R.id.textViewIstorijaTitle);
            mDataText = v.findViewById(R.id.textViewIstorijaData);
            // Set on click listener for the views in order to open a URL on click
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    // Preventing multiple clicks, using threshold of .5 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (adapterPosition != -1) {

//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrlsSet.get(getAdapterPosition())));
//                        mContext.startActivity(browserIntent);

//                        Intent intent = new Intent(mContext, NotificationClickReceiver.class);
//                        intent.putExtra("URLfromList", mUrlsSet.get(getAdapterPosition()));
//                        mContext.startActivity(intent);

                        /* open the url from the list since adapter position coresponds to the data set */
                        CustomTabActivityHelper.openCustomTab((Activity) mContext, customTabsIntent, Uri.parse(mUrlsSet.get(adapterPosition)), new CustomTabActivityHelper.CustomTabFallback() {
                            @Override
                            public void openUri(Activity activity, Uri uri) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                activity.startActivity(intent);
                            }
                        });

                        notificationManager.cancel(mDataSet.get(adapterPosition).hashCode());

                        int index = tinyDB.getListString("vesti").indexOf(mDataSet.get(adapterPosition));
                        /* If the selected item exists in the database of unread items (its index is not -1), delete it from storage
                         * and start the intent which is received by Tab2Vesti to refresh the adapter */
                        if (index != -1) {

                            ArrayList<String> listData = tinyDB.getListString("vesti");
                            ArrayList<String> listTitles = tinyDB.getListString("titles");
                            ArrayList<String> listUrls = tinyDB.getListString("urls");

                            listData.remove(index);
                            listTitles.remove(index);
                            listUrls.remove(index);

                            tinyDB.putListString("vesti", listData);
                            tinyDB.putListString("titles", listTitles);
                            tinyDB.putListString("urls", listUrls);

                            Intent intent = new Intent();
                            intent.putExtra(NotificationClickReceiver.SHOULD_DELETE, true);
                            intent.putExtra(NotificationClickReceiver.INDEX, index);
                            intent.setAction(MyFirebaseMessagingService.UPDATE_VESTI);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

                        }

                    }
                }
            });
        }

        public TextView getTextViewTitle() {
            return mTitleText;
        }

        public TextView getTextViewData() {
            return mDataText;
        }
    }

    IstorijaListAdapter(ArrayList<String> myTitlesSet, ArrayList<String> myDataSet, ArrayList<String> myUrlsSet, Context context, View myRootView, CustomTabsIntent customTabsIntent, NotificationManager notificationManager) {
        mTitlesSet = myTitlesSet;
        mDataSet = myDataSet;
        mUrlsSet = myUrlsSet;
        mContext = context;
        tinyDB = new TinyDB(mContext);
        empty = myRootView.findViewById(R.id.emptyVesti1);
        this.customTabsIntent = customTabsIntent;
        this.notificationManager = notificationManager;
    }

    @Override
    public IstorijaListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.istorija_list_item, parent, false);

        return new ViewHolder(v);
    }

    //Sets title and data text, if the dataset is empty, set the empty text view as visible
    @Override
    public void onBindViewHolder(IstorijaListAdapter.ViewHolder holder, int position) {
        holder.getTextViewData().setText(mDataSet.get(position));
        holder.getTextViewTitle().setText(mTitlesSet.get(position));
        if (mTitlesSet.size() == 0) {

            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * Adds Titles to the adapter and local storage
     */
    public void addTitle(String title) {
        mTitlesSet.add(0, title);
        if (mTitlesSet.size() > 30) {
            for (int i = 30; i < mTitlesSet.size(); i++) {
                mTitlesSet.remove(i);
            }
            tinyDB.putListString("istorijaTitles", mTitlesSet);
        }

        empty.setVisibility(View.GONE);
    }

    /**
     * Adds Data to the adapter and local storage
     */
    public void addData(String data) {
        mDataSet.add(0, data);
        if (mDataSet.size() > 30) {
            for (int i = 30; i < mDataSet.size(); i++) {
                mDataSet.remove(i);
            }
            tinyDB.putListString("istorijaData", mDataSet);
        }
    }

    /**
     * Adds URLs to the adapter and local storage
     */
    public void addUrl(String url) {
        mUrlsSet.add(0, url);
        if (mUrlsSet.size() > 30) {
            for (int i = 30; i < mUrlsSet.size(); i++) {
                mUrlsSet.remove(i);
            }
            tinyDB.putListString("istorijaUrls", mUrlsSet);

        }
    }

    /**
     * Clears data from the adapter and local storage
     * and sets empty text view object to be visible
     */
    public void clearAll() {
        int size = mTitlesSet.size();
        clearTitles();
        clearData();
        clearUrls();
        notifyItemRangeRemoved(0, size);
        if (mTitlesSet.size() == 0) {

            empty.setVisibility(View.VISIBLE);
        }
    }

    private void clearTitles() {
        mTitlesSet.clear();
        tinyDB.putListString("istorijaTitles", mTitlesSet);
    }

    private void clearData() {
        mDataSet.clear();
        tinyDB.putListString("istorijaData", mDataSet);
    }

    private void clearUrls() {
        mUrlsSet.clear();
        tinyDB.putListString("istorijaUrls", mUrlsSet);
    }

}
