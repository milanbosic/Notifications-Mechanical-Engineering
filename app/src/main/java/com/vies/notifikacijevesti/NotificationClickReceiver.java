package com.vies.notifikacijevesti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Called on notification click
 * Open browser with the matching URL and delete that item from local storage
 */

public class NotificationClickReceiver extends AppCompatActivity{
    public static final String SHOULD_DELETE = "Delete data if true";
    public static final String INDEX = "index";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Get the intent that started this activity
        Intent intent = getIntent();

        // Get the extra strings from the intent
        String vest = intent.getStringExtra("vestExtra");
        String url = intent.getStringExtra("urlExtra");

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        /* Get the new item from the local storage, since it has been added
         * in MyFirebaseMessagingService when the Firebase message arrived
         * returns -1 if no such item exists
         */
        int index = tinyDB.getListString("vesti").indexOf(vest);

        // Generate a new Intent with extra strings to update the lists in fragments
        Intent intent1 = new Intent();
        intent1.putExtra(SHOULD_DELETE, true);
        intent1.putExtra(INDEX, index);
        intent1.setAction(MyFirebaseMessagingService.UPDATE_VESTI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

        // If index != -1 remove the corresponding item from local storage
        ArrayList<String> listData = tinyDB.getListString("vesti");
        ArrayList<String> listTitles = tinyDB.getListString("titles");
        ArrayList<String> listUrls = tinyDB.getListString("urls");

        if (index != -1) {
            listData.remove(index);
            listTitles.remove(index);
            listUrls.remove(index);
        }

        tinyDB.putListString("vesti", listData);
        tinyDB.putListString("titles", listTitles);
        tinyDB.putListString("urls", listUrls);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(browserIntent);

        finish();
    }
}
