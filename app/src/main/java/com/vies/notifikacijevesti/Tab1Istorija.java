package com.vies.notifikacijevesti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Tab that shows a history of all received data
 */

public class Tab1Istorija extends Fragment implements CustomTabActivityHelper.ConnectionCallback {

    RecyclerView mRecyclerView;
    private ArrayList<String> titleSet;
    private ArrayList<String> dataSet;
    private ArrayList<String> urlSet;
    private IstorijaListAdapter mAdapter;
    private TextView emptyText;
    private CustomTabsIntent customTabsIntent;
    TinyDB tinyDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Declare an intent filter in order to refresh the list when a notification is received
         * or clear it if the intent comes from Settings Activity
         */
        IntentFilter intentFilter = new IntentFilter(MyFirebaseMessagingService.UPDATE_ISTORIJA);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onNotice, intentFilter);
        customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .setShowTitle(true)
                .setStartAnimations(getActivity(), R.anim.left_to_right_start, R.anim.right_to_left_start)
                .setExitAnimations(getActivity(), R.anim.right_to_left_exit, R.anim.left_to_right_exit)
                .build();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1istorija, container, false);

        mRecyclerView = rootView.findViewById(R.id.istorijaRecyclerView);
        // text object to show when the list is empty
        emptyText = rootView.findViewById(R.id.emptyVesti1);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initDataSet();
        // initialize the adapter that regulates data
        mAdapter = new IstorijaListAdapter(titleSet, dataSet, urlSet, getContext(), rootView, customTabsIntent);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    // Intialize data from local storage if it is available
    void initDataSet() {
        tinyDB = new TinyDB(getContext());
        if (tinyDB.getListString("istorijaTitles").size() != 0) {
            titleSet = tinyDB.getListString("istorijaTitles");
            dataSet = tinyDB.getListString("istorijaData");
            urlSet = tinyDB.getListString("istorijaUrls");
        } else {

            titleSet = new ArrayList<>();
            dataSet = new ArrayList<>();
            urlSet = new ArrayList<>();
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    // Intent receiver that diferentiates between calls to delete data (from Settings) or update the adapter
    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getStringExtra("extraString");
            if (string.equals("deleteActivity")) {
                mAdapter.clearAll();
                mAdapter.notifyDataSetChanged();

            } else {
                mAdapter.addData(MyFirebaseMessagingService.novaVest);
                mAdapter.addTitle(MyFirebaseMessagingService.newTitle);
                mAdapter.addUrl(MyFirebaseMessagingService.newUrl);

                mAdapter.notifyItemInserted(0);
                mRecyclerView.scrollToPosition(0);
            }

        }
    };

    @Override
    public void onCustomTabsConnected() {

    }

    @Override
    public void onCustomTabsDisconnected() {

    }
}
