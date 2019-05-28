package com.isure.simpleaidldemo.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.app.IntentService;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int WHAT_DOWNLOAD = 0;

    private Button mBtn;

    private Looper mWorkLooper;
    private Handler mWorkHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(this);

        HandlerThread thread = new HandlerThread("handler thread");
        thread.start();

        mWorkLooper = thread.getLooper();
        mWorkHandler = new WorkHandler(mWorkLooper);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mWorkLooper.quit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                    Message message = mWorkHandler.obtainMessage();
                    message.what = WHAT_DOWNLOAD;
                    mWorkHandler.sendMessage(message);
                break;
        }
    }

    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DOWNLOAD:
                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL("http://127.0.0.1/get_data.xml");
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(false);
                        urlConnection.setConnectTimeout(5000);
                        urlConnection.setReadTimeout(5000);
                        urlConnection.connect();

                        InputStream in = urlConnection.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                    break;
            }
        }
    }


}

