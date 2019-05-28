package com.isure.servicebestpractice;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private static final String TAG = "DownloadTask";

    private static final int NOTIFICATION_ID = 1;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public DownloadTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        startNotification();//在下载任务前，发送通知到状态栏
    }

    private void startNotification() {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannelUtil.createNotificationChannel(mContext);

        Notification notification = initNotification("Downloading......", 0, "");
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void updateNotification(int progress) {
        Notification notification = initNotification("Downloading......", progress, progress + "%");
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private Notification initNotification(String contentTitle, int progress, String contentText) {
        Notification notification = new NotificationCompat.Builder(mContext, NotificationChannelUtil.DEFAULT_CHANNEL_NAME)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(contentTitle)
                .setProgress(100, progress, false)
                .setContentText(contentText)
                .build();
        return notification;
    }

    private void stopDownloadService() {
        Intent intent = new Intent(mContext, DownloadService.class);
        mContext.stopService(intent);
    }

    @Override
    protected Integer doInBackground(String... strings) {
       /* Downloader.downloadFile(urls[i]); //下载文件
        publishProgress(); // 发布进度
        if (isCancelled()) break; //如果取消任务，则立马退出*/

       HttpsURLConnection connection = null;
        try {
            URL url = new URL("https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            connection.connect();

            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File targetFile = new File(downloadDir, "eclipse-inst-win64.exe");
            RandomAccessFile savedFile = new RandomAccessFile(targetFile, "rw");

            int fileLength = connection.getContentLength();
            int downloadLength = 0;
            InputStream is = connection.getInputStream();
            byte[] b = new byte[1024];
            int length;
            while ((length = is.read(b)) != -1) {
                savedFile.write(b, 0, length);
                downloadLength += length;
                int progress = downloadLength * 100 / fileLength;
                publishProgress(progress);
                Log.d(TAG, "doInBackground: progress = " + progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        cancelNotification();
        stopDownloadService();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        updateNotification((int)values[0]);
    }

    @Override
    protected void onCancelled(Integer integer) {
        super.onCancelled(integer);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
