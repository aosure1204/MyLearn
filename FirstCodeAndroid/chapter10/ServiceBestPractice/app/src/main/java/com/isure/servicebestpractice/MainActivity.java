package com.isure.servicebestpractice;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 0;

    private DownloadService.DownloadBinder mDownloadBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartDownload = (Button) findViewById(R.id.btn_start_download);
        Button btnPauseDownload = (Button) findViewById(R.id.btn_pause_download);
        Button btnCancelDownload = (Button) findViewById(R.id.btn_cancel_download);
        btnStartDownload.setOnClickListener(this);
        btnPauseDownload.setOnClickListener(this);
        btnCancelDownload.setOnClickListener(this);

        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(permissions != null && permissions.length > 0) {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service != null) {
                mDownloadBinder = (DownloadService.DownloadBinder)service;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View v) {
        if(mDownloadBinder == null) {
            return;
        }
        switch (v.getId()){
            case R.id.btn_start_download:
                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                mDownloadBinder.startDownload(url);
//                startDownload();
                break;
            case R.id.btn_pause_download:
                //mDownloadBinder.pauseDownload();
                break;
            case R.id.btn_cancel_download:
                //mDownloadBinder.cancelDownload();
                //sendNotification();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

/*    private void sendNotification() {

        NotificationChannelUtil.createNotificationChannel(this);
        Notification notification = new NotificationCompat.Builder(this, NotificationChannelUtil.DEFAULT_CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Downloading......")
                .setProgress(100, 1, false)
                .setContentText("1%")
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }*/

    /*
    private void startDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;
                try {
                    URL url = new URL("https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe");
                    connection = (HttpsURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(false);
                    connection.setDoInput(true);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    connection.connect();

                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File targetFile = new File(downloadDir, "eclipse-inst-win64.exe");
                    RandomAccessFile savedFile = new RandomAccessFile(targetFile, "rw");

                    InputStream is = connection.getInputStream();
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = is.read(b)) != -1) {
                        savedFile.write(b, 0, len);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }*/
}
