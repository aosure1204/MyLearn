package com.example.mimetypeaccess;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        browserUri();

        sendFile();

        startPendingIntent();
    }

    private void browserUri(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.baidu.com"));
        startActivity(intent);
    }

    private void sendFile() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        Intent intentChooser = Intent.createChooser(intent, "共享方式");

        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intentChooser);
        }
    }

    private void startPendingIntent(){
        Notification.Builder mBuilder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();
    }

}
