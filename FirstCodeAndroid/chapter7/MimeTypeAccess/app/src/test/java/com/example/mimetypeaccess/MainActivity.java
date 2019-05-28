package com.example.mimetypeaccess;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHORITY = "com.harvic.provider.PeopleContentProvider";
    public static final Uri CONTENT_URI_FIRST = Uri.parse("content://" + AUTHORITY + "/first");
    public static Uri mCurrentURI = CONTENT_URI_FIRST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setAction("harvic.test.qijian");
        intent.setData(mCurrentURI);
        intent.putExtra("transportText", "start Activity from outside.");
        startActivity(intent);
    }
}
