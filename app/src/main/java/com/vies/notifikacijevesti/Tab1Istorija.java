package com.vies.notifikacijevesti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by milan on 26-Oct-17.
 */

public class Tab1Istorija extends Fragment {

    RecyclerView mRecyclerView;
    private ArrayList<String> titleSet;
    private ArrayList<String> dataSet;
    private ArrayList<String> urlSet;
    private Button mButton;
    private Button mButtonDelete;
    private IstorijaListAdapter mAdapter;
    private int counter;
    private TextView emptyText;
    TinyDB tinyDB;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter("com.notifikacijevesti.refreshhistory");
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onNotice, intentFilter);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tab1istorija, container, false);

        mRecyclerView = rootView.findViewById(R.id.istorijaRecyclerView);
        emptyText = rootView.findViewById(R.id.emptyVesti1);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        initDataSet();
        mAdapter = new IstorijaListAdapter(titleSet, dataSet, urlSet, getContext(), rootView);
        mRecyclerView.setAdapter(mAdapter);

        mButton = rootView.findViewById(R.id.buttonTest);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mAdapter.addTitle(" " + counter);
//                mAdapter.addData("test");
//                mAdapter.addUrl("http://www.google.com");
//                mAdapter.notifyItemInserted(0);
//                mRecyclerView.scrollToPosition(0);
//                counter++;
            }
        });

//        mButtonDelete = rootView.findViewById(R.id.buttonTestDelete);
//        mButtonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAdapter.clearAll();
//            }
//        });

        return rootView;
    }

    void initDataSet(){
        tinyDB = new TinyDB(getContext());
        if (tinyDB.getListString("istorijaTitles").size() != 0){

            titleSet = tinyDB.getListString("istorijaTitles");
            dataSet = tinyDB.getListString("istorijaData");
            urlSet = tinyDB.getListString("istorijaUrls");
        } else{

            titleSet = new ArrayList<>();
            dataSet = new ArrayList<>();
            urlSet = new ArrayList<>();
            emptyText.setVisibility(View.VISIBLE);
        }
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mAdapter.addData(MyFirebaseMessagingService.novaVest);
            mAdapter.addTitle(MyFirebaseMessagingService.newTitle);
            mAdapter.addUrl(MyFirebaseMessagingService.newUrl);

            mAdapter.notifyItemInserted(0);
            mRecyclerView.scrollToPosition(0);
        }
    };

}
