package com.example.isurehorizontalscrollview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ViewGroup mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = (ViewGroup)findViewById(R.id.container);

        initContentView();
    }

    private void initContentView() {
        View item = LayoutInflater.from(this).inflate(R.layout.horizontal_scroll_item, mContainer, false);
        TextView title = item.findViewById(R.id.title);
        ListView listView = item.findViewById(R.id.list);
        for(int i = 0; i < 3; i++) {
            title.setText("page" + (i+1));
            initList(listView);
            mContainer.addView(item);
        }
    }

    private void initList(ListView listView) {

        listView.setAdapter();
    }

}
