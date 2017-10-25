package com.vies.notifikacijevesti;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static android.media.RingtoneManager.getDefaultUri;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private  ArrayList<String> vestiDatabase;
    private  ArrayList<String> titlesDatabase;
    private ArrayList<String> urlsDatabase;

    public static String novaVest;
    public static String newTitle;
    public static String newUrl;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        TinyDB tinyDB = new TinyDB(getApplicationContext());

        Log.d("wtf", "notifikacija primljena");

        novaVest = remoteMessage.getData().get("body");
        newTitle = remoteMessage.getData().get("title");
        newUrl = remoteMessage.getData().get("url");

//        novaVest = "test2020";
//        newTitle = "asdff";
//        newUrl = "http://www.goole.com";

        sendNotification(remoteMessage.getData());

        broadcastIntent();

    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.notifikacijevesti.refresh");
        // We should use LocalBroadcastManager when we want INTRA app
        // communication
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendNotification(Map<String, String> notification) {
        int requestID = (int) System.currentTimeMillis();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(notification.get("url")));

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID , intent, PendingIntent.FLAG_UPDATE_CURRENT);
//      Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.solemn);
        Uri defaultSoundUri= getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =

            new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_logo_new))
                    .setSmallIcon(R.drawable.ic_home)
                    .setContentTitle(notification.get("title"))
                    .setContentText(notification.get("body"))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.get("body")));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.BLUE, 1000, 300);

        notificationManager.notify(requestID, notificationBuilder.build());
    }
}
