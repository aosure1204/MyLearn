package com.isure.simpleaidldemo.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.isure.simpleaidldemo.service.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private IMyAidlInterface mStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        //由于是隐式启动Service 所以要添加对应的action，A和之前服务端的一样。
        intent.setAction("com.isure.simpleaidldemo.service.MyService");
        //android 5.0以后直设置action不能启动相应的服务，需要设置packageName或者Component。
        intent.setPackage("com.isure.simpleaidldemo.service"); //packageName 需要和服务端的一致.
        bindService(intent, serviceConn, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStub = IMyAidlInterface.Stub.asInterface(service);
            if(mStub == null) {
                Log.d(TAG, "onServiceConnected: mStub is null.");
            } else {
                try {
                    int value = mStub.add(1, 8);
                    Log.d(TAG, "onServiceConnected: value = " + value);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConn);
    }
}
