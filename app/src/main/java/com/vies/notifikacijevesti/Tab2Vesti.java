package com.vies.notifikacijevesti;

import android.app.NotificationManager;
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
 * Tab that shows all "unread" elements. If a notification is opened, or the card
 * is clicked on, the coresponding card is removed.
 */

public class Tab2Vesti extends Fragment implements CustomTabActivityHelper.ConnectionCallback {

    private static final String TAG = "RecyclerViewFragment";

    RecyclerView mRecyclerView;
    private VestiListAdapter mAdapter;
    private ArrayList<String> mDataset;
    private ArrayList<String> mTitlesSet;
    private ArrayList<String> mUrlsSet;
    private TextView emptyText;
    private CustomTabsIntent customTabsIntent;
    private NotificationManager notificationManager;
    TinyDB tinyDB;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Declare an intent filter and register a receiver for it
         * in order to refresh or clear the list when a notification is received */
        IntentFilter intentFilter = new IntentFilter(MyFirebaseMessagingService.UPDATE_VESTI);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onNotice, intentFilter);
        customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .setStartAnimations(getActivity(), R.anim.left_to_right_start, R.anim.right_to_left_start)
                .setExitAnimations(getActivity(), R.anim.right_to_left_exit, R.anim.left_to_right_exit)
                .build();
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2vesti, container, false);
        rootView.setTag(TAG);

        // text object to show when the list is empty
        emptyText = rootView.findViewById(R.id.emptyVesti);

        initDataset();

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // initialize the adapter that regulates data
        mAdapter = new VestiListAdapter(mDataset, mTitlesSet, mUrlsSet, getContext(), rootView, customTabsIntent, notificationManager);

        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    // Intialize data from local storage if it is available
    private void initDataset() {

        tinyDB = new TinyDB(this.getContext());
        ArrayList<String> listData = tinyDB.getListString("vesti");
        ArrayList<String> listTitles = tinyDB.getListString("titles");
        ArrayList<String> listUrls = tinyDB.getListString("urls");

        if (!listData.isEmpty()) {
            mDataset = listData;
            mTitlesSet = listTitles;
            mUrlsSet = listUrls;

        } else {
            mDataset = new ArrayList<>();
            mTitlesSet = new ArrayList<>();
            mUrlsSet = new ArrayList<>();
            emptyText.setVisibility(View.VISIBLE);

        }
    }

    // Intent receiver that diferentiates between calls to delete data or update the adapter
    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            /* If SHOULD_DELETE extra string is true, the notification, or a relevant item from
            history, has been clicked on, so delete the item from the list */
            if (intent.getBooleanExtra(NotificationClickReceiver.SHOULD_DELETE, false)) {
                int index = intent.getIntExtra(NotificationClickReceiver.INDEX, -1);
                // No need to update local storage since that has been done in the activity that sent this intent
                if (index != -1) mAdapter.removeWithoutUpdatingDatabaseAt(index);
            }
            // If fromMain string is true, this is a call from the FAB from MainActivity to delete all data
            else if (intent.getBooleanExtra("fromMain", false)) {
                mAdapter.clearAll();
            }
            // If none is true, the intent comes from Firebase Service, simply add the new item
            else {
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

