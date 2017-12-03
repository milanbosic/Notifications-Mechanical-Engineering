package com.vies.notifikacijevesti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by milan on 26-Nov-17.
 */

public class NotificationClickReceiver extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String vest = intent.getStringExtra("vestExtra");

        Log.d("test123", "LOG 2: Primljena vest NotificationClickReceiver " + vest);

        TinyDB tinyDB = new TinyDB(getApplicationContext());

        tinyDB.putString("closedString", "asdf");

        Intent intent1 = new Intent();
        intent1.putExtra("shouldDelete", true);
        intent1.putExtra("vest", vest);
        intent1.setAction("com.notifikacijevesti.refresh");
        // We should use LocalBroadcastManager when we want INTRA app
        // communication
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

        finish();
    }
}
