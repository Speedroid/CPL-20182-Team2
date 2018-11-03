package cspmbilelab.com.newsnailmail.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import cspmbilelab.com.newsnailmail.R;

@RequiresApi(api = Build.VERSION_CODES.O)
class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";

    public NotificationHelper(Context ctx) {
        super(ctx);
        NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL, getString(R.string.notification_channel_id), NotificationManager.IMPORTANCE_DEFAULT);
        chan1.setLightColor(Color.GREEN);
        chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(chan1);
    }

    public Notification.Builder getNotification1(String title, String body,int smallIcon,Bitmap largeIcon) {
        return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true);
    }


    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}