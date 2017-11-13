package com.vies.notifikacijevesti;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

public class Tab2Vesti extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

    RecyclerView mRecyclerView;
    private VestiListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mDataset;
    private ArrayList<String> mTitlesSet;
    private ArrayList<String> mUrlsSet;
    private Button button;
    private int counter;

    private TextView emptyText;
    TinyDB tinyDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter("com.notifikacijevesti.refresh");
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(onNotice, intentFilter);

//        TinyDB tinyDB = new TinyDB(getContext());

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
        View rootView = inflater.inflate(R.layout.tab2vesti, container, false);
        rootView.setTag(TAG);

        emptyText = rootView.findViewById(R.id.emptyVesti);

        initDataset(rootView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter = new VestiListAdapter(mDataset, mTitlesSet, mUrlsSet, getContext(), rootView);

        mRecyclerView.setAdapter(mAdapter);

        button = rootView.findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TinyDB tinyDB = new TinyDB(getContext());
                mAdapter.addData("Термин поправног(писаног) дела испита + Усмени фебруарски рок");

                mAdapter.addTitle("" + counter);
                counter++;

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
                Toast.makeText(getContext(),  "Sve vesti su obrisane.", Toast.LENGTH_SHORT).show();
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

    private void initDataset(View view) {

        tinyDB = new TinyDB(this.getContext());
        ArrayList<String> listData = tinyDB.getListString("vesti");
        ArrayList<String> listTitles = tinyDB.getListString("titles");
        ArrayList<String> listUrls = tinyDB.getListString("urls");

        if (!listData.isEmpty()){
            mDataset = listData;
            mTitlesSet = listTitles;
            mUrlsSet = listUrls;

        } else{
            mDataset = new ArrayList<>();
            mTitlesSet = new ArrayList<>();
            mUrlsSet = new ArrayList<>();
            emptyText.setVisibility(View.VISIBLE);

        }
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            mAdapter.removeAt(viewHolder.getAdapterPosition());
        }

    };
}

