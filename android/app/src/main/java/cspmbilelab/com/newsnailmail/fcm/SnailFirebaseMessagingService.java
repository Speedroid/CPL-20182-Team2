package cspmbilelab.com.newsnailmail.fcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import cspmbilelab.com.newsnailmail.R;
import cspmbilelab.com.newsnailmail.fcm.NotificationHelper;

public class SnailFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        Log.e("newToken", "FirebaseToken: "+ s);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        getSharedPreferences("fcm",MODE_PRIVATE).edit().putString("FCMToken",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("fcm", MODE_PRIVATE).getString("FCMToken", "empty");
    }

    private void sendNotification(RemoteMessage remoteMessage){

        String body = remoteMessage.getNotification().getBody();
        String title = remoteMessage.getNotification().getTitle();
        int smallIcon = R.mipmap.icon02;
        Bitmap largeIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.mipmap.icon02);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
            Notification.Builder nb =(notificationHelper.getNotification1(title,body,smallIcon,largeIcon));
            notificationHelper.notify(0,nb);
        }else
        {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(largeIcon);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}