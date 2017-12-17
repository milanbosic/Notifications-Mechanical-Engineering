package com.vies.notifikacijevesti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by milan on 26-Nov-17.
 */

public class NotificationClickReceiver extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        String vest = intent.getStringExtra("vestExtra");
        String url = intent.getStringExtra("urlExtra");
//        Log.d("test123", "LOG 2: Primljena vest NotificationClickReceiver " + vest);

        TinyDB tinyDB = new TinyDB(getApplicationContext());
        int index = tinyDB.getListString("vesti").indexOf(vest);

        Intent intent1 = new Intent();
        intent1.putExtra("shouldDelete", true);
        intent1.putExtra("index", index);
        intent1.setAction("com.notifikacijevesti.refresh");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

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
