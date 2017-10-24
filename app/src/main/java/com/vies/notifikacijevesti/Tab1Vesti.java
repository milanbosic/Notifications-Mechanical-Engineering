package com.vies.notifikacijevesti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

/**
 * Created by milan on 10.10.2017..
 */

public class Tab1Vesti extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private Handler mHandler;

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    public RecyclerView mRecyclerView;
    public CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<String> mDataset;
    protected ArrayList<String> mTitlesSet;
    protected ArrayList<String> mUrlsSet;
    protected Button button;
    protected int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter("com.notifikacijevesti.refresh");
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onNotice, intentFilter);

//        TinyDB tinyDB = new TinyDB(getContext());
//
//        tinyDB.clear();
//        ArrayList<String> test = new ArrayList<>();
//        test.add(0, "PREDMET 1");
//
//        ArrayList<String> test1 = new ArrayList<>();
//        test1.add(0, "VEST 1");
//
//        ArrayList<String> test2 = new ArrayList<>();
//        test2.add(0, "http://www.youtube.com");
//
//        tinyDB.putListString("titles", test);
//        tinyDB.putListString("vesti", test1);
//        tinyDB.putListString("urls", test2);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1vesti, container, false);
        rootView.setTag(TAG);

        initDataset(rootView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new CustomAdapter(mDataset, mTitlesSet, mUrlsSet, getContext());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        runLayoutAnimation(mRecyclerView);

        Log.d("token", FirebaseInstanceId.getInstance().getToken());

        button = rootView.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyDB tinyDB = new TinyDB(getContext());
                ArrayList<String> testData = tinyDB.getListString("vesti");
                testData.add(0, "Термин поправног (писаног) дела испита + Усмени фебруарски рок");
                tinyDB.putListString("vesti", testData);

                mAdapter.addData("Термин поправног(писаног) дела испита + Усмени фебруарски рок");

                ArrayList<String> testTitles = tinyDB.getListString("titles");
                testTitles.add(0, "" + counter);
                tinyDB.putListString("titles", testTitles);

//                mAdapter.addTitle("Компјутерска симулација и вештачка интелигенција (0404)");
                mAdapter.addTitle("" + counter);
                counter++;

                ArrayList<String> testUrls = tinyDB.getListString("urls");
                testUrls.add(0, "http://nastava.mas.bg.ac.rs/nastava/viewtopic.php?f=16&t=3062");
                tinyDB.putListString("urls", testUrls);

                mAdapter.addUrl("http://nastava.mas.bg.ac.rs/nastava/viewtopic.php?f=16&t=3062");

                mAdapter.notifyItemInserted(0);
                mRecyclerView.scrollToPosition(0);
            }
        });

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Sve vesti su obrisane.", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                mAdapter.clearAll();
                Toast.makeText(getContext(),  "Све вести су обрисане.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
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

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initDataset(View view) {
        final TinyDB tinyDB = new TinyDB(this.getContext());
        ArrayList<String> listData = tinyDB.getListString("vesti");
        if (listData.size() != 0){
            mDataset = listData;
        } else{
            TextView textView = view.findViewById(R.id.emptyVesti);
            textView.setText("Nema novih vesti.");
        }

        ArrayList<String> listTitles = tinyDB.getListString("titles");
        if (listTitles.size() != 0){
            mTitlesSet = listTitles;
        } else{

        }

        ArrayList<String> listUrls = tinyDB.getListString("urls");
        if (listUrls.size() != 0){
            mUrlsSet = listUrls;
        } else{

        }

    }

}

