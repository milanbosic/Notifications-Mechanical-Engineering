package com.vies.notifikacijevesti;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

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
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .setStartAnimations(this, R.anim.left_to_right_start, R.anim.right_to_left_start)
                .setExitAnimations(this, R.anim.right_to_left_exit, R.anim.left_to_right_exit)
                .build();

        // Intent koji je pokrenuo ovu aktivnost
        Intent intent = getIntent();

        // Izvuci ekstra stringove iz intenta
        String vest = intent.getStringExtra("vestExtra");
        String url = intent.getStringExtra("urlExtra");

        // Otvoriti custom tab sa zeljenim urlom
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, Uri.parse(url), new CustomTabActivityHelper.CustomTabFallback() {
            @Override
            public void openUri(Activity activity, Uri uri) {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });

        // Inicijalizacija baze
        TinyDB tinyDB = new TinyDB(getApplicationContext());
        int index = tinyDB.getListString("vesti").indexOf(vest);

        // Generisanje novog intenta sa ekstra stringovima da bi se ažurirala lista u fragmentima
        Intent intent1 = new Intent();
        intent1.putExtra(SHOULD_DELETE, true);
        intent1.putExtra(INDEX, index);
        intent1.setAction(MyFirebaseMessagingService.UPDATE_VESTI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

        // Ako je index - 1 obrisati iz lokala
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
        }

        finish();

    }
}
