package com.isure.servicebestpractice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class DownloadService extends Service {
    private DownloadBinder mBinder = new DownloadBinder();

    private DownloadTask mTask;

    public DownloadService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class DownloadBinder extends Binder{
        public void startDownload(String url){
            mTask = new DownloadTask(DownloadService.this);
            mTask.execute(url);
        }

        public void pauseDownload(){

        }

        public void cancelDownload(){
        }
    }

}
