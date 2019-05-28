package com.example.httpconnectiontest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button mBtnHttpConClient;
    private Button mBtnOkHttp;
    private TextView mTextResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnHttpConClient = (Button) findViewById(R.id.http_con_client);
        mBtnOkHttp = (Button) findViewById(R.id.ok_http);
        mTextResponse = (TextView) findViewById(R.id.show_response);

        mBtnHttpConClient.setOnClickListener(this);
        mBtnOkHttp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.http_con_client:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpsURLConnection urlConnection = null;
                                try {
                                    URL url = new URL("https://m.baidu.com");
                                    urlConnection = (HttpsURLConnection) url.openConnection();
                                    urlConnection.setRequestMethod("GET");
                                    urlConnection.setDoInput(true);
                                    urlConnection.setConnectTimeout(8000);
                                    urlConnection.setReadTimeout(8000);
                                    urlConnection.connect();

                                    InputStream inputStream = urlConnection.getInputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    StringBuffer buffer = new StringBuffer();
                                    String line;
                                    while((line = reader.readLine()) != null ) {
                                        buffer.append(line);
                                    }
                                    showResponse(buffer.toString());

                                    reader.close();
                                    inputStream.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (urlConnection != null) {
                                        urlConnection.disconnect();
                                    }
                                }
                            }
                        }).start();
                    }
                }).start();
                break;
            case R.id.ok_http:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://10.0.2.2/get_data.xml")
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            parseXMLWithPull(responseData);
//                            showResponse(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }

                    }
                }).start();
            break;
        }
    }

    private void showResponse(final String response) {
        Log.d(TAG, "showResponse: response = " + response);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextResponse.setText(response);
            }
        });
    }

    private void parseXMLWithPull(String data) {
        XmlPullParser xmlPullParser;
    }
}
