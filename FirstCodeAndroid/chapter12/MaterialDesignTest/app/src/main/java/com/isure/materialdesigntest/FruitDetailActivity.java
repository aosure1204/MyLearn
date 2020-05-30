package com.isure.materialdesigntest;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FruitDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);

        Fruit fruit = (Fruit) getIntent().getParcelableExtra("fruit");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(fruit.getName());
        }

        TextView fruitContent = (TextView) findViewById(R.id.fruit_content);
        fruitContent.setText(generateFruitContent(fruit.getName()));;

        ImageView fruitImage = (ImageView) findViewById(R.id.fruit_image);
//        fruitImage.setImageResource(fruit.getImageId());
        Glide.with(this).load(fruit.getImageId()).into(fruitImage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_comment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FruitDetailActivity.this, "you click FloatingActionButton", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateFruitContent(String fruitName) {
        StringBuilder strBuilder = new StringBuilder();
        for(int i=0; i<500; i++) {
            strBuilder.append(fruitName);
        }
        return strBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
