package com.vies.notifikacijevesti;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String novaVest;
    public static String newTitle;
    public static String newUrl;

    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    public static final String UPDATE_ISTORIJA = "com.notifikacijevesti.updateistorija";
    public static final String UPDATE_VESTI = "com.notifikacijevesti.updatevesti";

    private TinyDB tinyDB;

    /**
     * Called when message is received.
     * <p>
     * param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    public void onCreate() {
        tinyDB = new TinyDB(this.getApplicationContext());
        // Get the Firebase token and save it in local storage
        tinyDB.putString("token", FirebaseInstanceId.getInstance().getToken());

    }

    /* Called when a Firebase message is received */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Load saved notification preferences (sound, vibration, etc.)
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationPref = sharedPref.getBoolean("notifications_new_message", true);

        // Get message data
        Map<String, String> data = remoteMessage.getData();

        // Separate data from the received JSON string
        novaVest = data.get("body");
        newTitle = data.get("title");
        newUrl = data.get("url");

        // Get the lists with data from local storage
        ArrayList<String> listData = tinyDB.getListString("vesti");
        ArrayList<String> listTitles = tinyDB.getListString("titles");
        ArrayList<String> listUrls = tinyDB.getListString("urls");

        // Add new data
        listData.add(0, novaVest);
        listTitles.add(0, newTitle);
        listUrls.add(0, newUrl);

        // Save modified lists to local storage
        tinyDB.putListString("vesti", listData);
        tinyDB.putListString("titles", listTitles);
        tinyDB.putListString("urls", listUrls);

        // Repeat for history data
        ArrayList<String> titleSet = tinyDB.getListString("istorijaTitles");
        ArrayList<String> dataSet = tinyDB.getListString("istorijaData");
        ArrayList<String> urlSet = tinyDB.getListString("istorijaUrls");

        titleSet.add(0, newTitle);
        dataSet.add(0, novaVest);
        urlSet.add(0, newUrl);

        tinyDB.putListString("istorijaTitles", titleSet);
        tinyDB.putListString("istorijaData", dataSet);
        tinyDB.putListString("istorijaUrls", urlSet);

        broadcastIntent();

        if (notificationPref) {

            sendNotification(data);
        }

    }

    /**
     * Broadcast intents to update data in fragments
     */
    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction(UPDATE_VESTI);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        Intent intent1 = new Intent();
        intent1.setAction(UPDATE_ISTORIJA);
        intent1.putExtra("extraString", "firebase");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

    }

    // Send the notification to the user
    private void sendNotification(Map<String, String> notification) {

        // A unique ID is needed for every notification, curent system time can be used
        int requestID = (int) System.currentTimeMillis();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String ringtonePreference = sharedPrefs.getString("notifications_new_message_ringtone", "DEFAULT_SOUND");
        Uri ringtoneuri = Uri.parse(ringtonePreference);
        boolean vibrate = sharedPrefs.getBoolean("notifications_new_message_vibrate", true);
        Intent intent = new Intent(this, NotificationClickReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("vestExtra", novaVest);
        intent.putExtra("urlExtra", newUrl);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Default channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(vibrate);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder =

                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo_new))
                        .setSmallIcon(R.drawable.ic_home_dark)
                        .setContentTitle(newTitle)
                        .setContentText(novaVest)
                        .setAutoCancel(true)
                        .setSound(ringtoneuri)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(novaVest));

        if (vibrate) {
            notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        notificationBuilder.setLights(Color.BLUE, 1000, 300);

        notificationManager.notify(requestID, notificationBuilder.build());


    }

    /* If the token changes for some reason
     * send the new token to server */
    public void onTokenRefresh() {

        Context context = getApplicationContext();

        final TinyDB tinyDB = new TinyDB(context);
        String url = "http://165.227.154.9/api/onrefresh";

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("oldtoken", tinyDB.getString("token"));
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        Response.ErrorListener errorListen = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.v("responseJson: ", error);
            }
        };

        Response.Listener<JSONObject> responseListen = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("message").contains("Success")) {
                        tinyDB.putString("token", FirebaseInstanceId.getInstance().getToken());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, responseListen, errorListen);

        requestQueue.add(jsObjRequest);

    }
}
