package com.isure.servicebestpractice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationChannelUtil {
    public static final String DEFAULT_CHANNEL_NAME = "MY_CHANNEL";

    public static void createNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_NAME, "channel name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("channel description");
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

}
