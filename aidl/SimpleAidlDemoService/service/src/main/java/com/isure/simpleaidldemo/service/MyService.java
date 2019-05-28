package com.isure.simpleaidldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class MyService extends Service {

    private IMyAidlInterface.Stub stub = new IMyAidlInterface.Stub() {
        @Override
        public int add(int a, int b) throws RemoteException {
            return a + b;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }
}
