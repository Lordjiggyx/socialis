package com.example.socialis.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.socialis.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FrebaseMessaging extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //get current user from shared preeence
        SharedPreferences sp = getSharedPreferences("SP_USER" , MODE_PRIVATE);
        String savedCurrentUser = sp.getString("CURRENT_USERID" , "None");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser()
;
        if(firebaseUser != null && sent.equals(firebaseUser.getUid()))
        {
            if(!savedCurrentUser.equals(user))
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    SendVersionNotification(remoteMessage);
                }
                else
                {
                    sendNoramlNotification(remoteMessage);
                }
            }
        }
    }

    private void SendVersionNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]" , ""));
        Intent intent = new Intent(this , Chat.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid" , user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pintent = PendingIntent.getActivity(this , i,intent , PendingIntent.FLAG_ONE_SHOT);


        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pintent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0 ;
        if(i> 0)
        {
            j=i;
        }
        notificationManager.notify(j , builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNoramlNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]" , ""));
        Intent intent = new Intent(this , Chat.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid" , user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pintent = PendingIntent.getActivity(this , i,intent , PendingIntent.FLAG_ONE_SHOT);


        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification1 = new Notification(this);
        android.app.Notification.Builder builder = notification1.getNotifications(title , body ,pintent, defSoundUri , icon);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0 ;
        if(i> 0)
        {
            j=i;
        }
        notification1.getManager().notify(j , builder.build());
    }
}
