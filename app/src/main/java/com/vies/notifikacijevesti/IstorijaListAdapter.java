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
            // Inicijalizacija dva view-a za naslov i sadržaj vesti za svaku karticu u RecyclerView-u
            mTitleText = v.findViewById(R.id.textViewIstorijaTitle);
            mDataText = v.findViewById(R.id.textViewIstorijaData);
            // Podešavanja listener-a za klik za view-e da bi se otvorio URL
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    // Prevencija duplih klikova
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (adapterPosition != -1) {
                        // Otvaranje URL-a iz liste jer pozicija elementa u adapteru odgovara setu podataka
                        CustomTabActivityHelper.openCustomTab((Activity) mContext, customTabsIntent, Uri.parse(mUrlsSet.get(adapterPosition)), new CustomTabActivityHelper.CustomTabFallback() {
                            @Override
                            public void openUri(Activity activity, Uri uri) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                activity.startActivity(intent);
                            }
                        });

                        notificationManager.cancel(mDataSet.get(adapterPosition).hashCode());

                        int index = tinyDB.getListString("vesti").indexOf(mDataSet.get(adapterPosition));
                        /* Ako selektovan element postoji u bazi neporčitanih vesti, obrisati vest iz baze i
                         * pokrenuti intent koji se očekuje u Tab2Vesti i koji refrešuje adapter */
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

    // Dodavanje naslova u adapter i bazu
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

    // Dodavanje sadržaja vesti u adapter i bazu
    public void addData(String data) {
        mDataSet.add(0, data);
        if (mDataSet.size() > 30) {
            for (int i = 30; i < mDataSet.size(); i++) {
                mDataSet.remove(i);
            }
            tinyDB.putListString("istorijaData", mDataSet);
        }
    }

    // Dodavanje URL-a u adapter i bazu
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
     * Brisanje podataka iz adaptera i baze i podešavanje
     * vidljivosti teksta koji se prikazuje kada nema vesti
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
