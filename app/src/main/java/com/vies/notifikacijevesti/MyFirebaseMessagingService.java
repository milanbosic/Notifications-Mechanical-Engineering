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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.media.RingtoneManager.getDefaultUri;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    public static String novaVest;
    public static String newTitle;
    public static String newUrl;

    private TinyDB tinyDB;

    /**
     * Called when message is received.
     *
     * param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    public void onCreate(){
        tinyDB = new TinyDB(this.getApplicationContext());
        tinyDB.putString("token", FirebaseInstanceId.getInstance().getToken());
        Log.d("tokenfirebase", FirebaseInstanceId.getInstance().getToken());
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map<String, String> data = remoteMessage.getData();

        novaVest = data.get("body");
        newTitle = data.get("title");
        newUrl = data.get("url");

        ArrayList<String> listData = tinyDB.getListString("vesti");
        ArrayList<String> listTitles = tinyDB.getListString("titles");
        ArrayList<String> listUrls = tinyDB.getListString("urls");

        listData.add(0, novaVest);
        listTitles.add(0, newTitle);
        listUrls.add(0, newUrl);

        tinyDB.putListString("vesti", listData);
        tinyDB.putListString("titles", listTitles);
        tinyDB.putListString("urls", listUrls);

        ArrayList<String> titleSet = tinyDB.getListString("istorijaTitles");
        ArrayList<String> dataSet = tinyDB.getListString("istorijaData");
        ArrayList<String> urlSet = tinyDB.getListString("istorijaUrls");

        titleSet.add(0, newTitle);
        dataSet.add(0, novaVest);
        urlSet.add(0, newUrl);

        tinyDB.putListString("istorijaTitles", titleSet);
        tinyDB.putListString("istorijaData", dataSet);
        tinyDB.putListString("istorijaUrls", urlSet);

        sendNotification(data);

        broadcastIntent();

    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.notifikacijevesti.refresh");
        // We should use LocalBroadcastManager when we want INTRA app
        // communication
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        Intent intent1 = new Intent();
        intent1.setAction("com.notifikacijevesti.refreshhistory");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);

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

    public void onTokenRefresh(){

        Context context = getApplicationContext();

        final TinyDB tinyDB = new TinyDB(context);
        String url ="http://91.187.151.172:3000/api/onrefresh/";

        HashMap<String, String> params = new HashMap<String, String>();

        params.put ("oldtoken", tinyDB.getString("token"));
        params.put("token", FirebaseInstanceId.getInstance().getToken());

        Response.ErrorListener errorListen = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.v("responseJson: ", error);
                //Toast.makeText(getApplicationContext(), "Дошло је до грешке.", Toast.LENGTH_SHORT).show();
            }
        };

        Response.Listener<JSONObject> responseListen = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    Log.d("Response: ", response.getString("message"));
                    if (response.getString("message").contains("Success")){
                        tinyDB.putString("token", FirebaseInstanceId.getInstance().getToken());
                    } else{

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, responseListen, errorListen);

        requestQueue.add(jsObjRequest);

    }
}
