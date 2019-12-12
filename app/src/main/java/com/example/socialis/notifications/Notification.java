package com.example.socialis.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import static android.app.Notification.VISIBILITY_PRIVATE;

public class Notification extends ContextWrapper {

    private static final String ID = "some_id";
    private static final String Name = "Socialis";

    private NotificationManager notificationManager;

    public Notification(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.O)
        {
            createChanel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChanel() {

        NotificationChannel notificationChannel = new NotificationChannel(ID , Name , notificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager()
    {
        if(notificationManager==null)
        {
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return  notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder getNotifications(String title ,
                                                             String body ,
                                                             PendingIntent pendingIntent
            , Uri sounduri , String icon)
    {
        return  new android.app.Notification.Builder(getApplicationContext() , ID )
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sounduri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
    }
}
