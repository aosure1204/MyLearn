package com.example.mimetypetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        String str = getIntent().getStringExtra("transportText");

        TextView textView = findViewById(R.id.show_text);
        textView.setText(str);
    }
}
