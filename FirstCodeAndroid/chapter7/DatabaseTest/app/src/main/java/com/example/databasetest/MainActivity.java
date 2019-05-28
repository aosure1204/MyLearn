package com.example.databasetest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    private Button mBtnAdd;
    private Button mBtnDelete;
    private Button mBtnUpdate;
    private Button mBtnSelect;

    private MyDatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnAdd = (Button) findViewById(R.id.add);
        mBtnDelete = (Button) findViewById(R.id.delete);
        mBtnUpdate = (Button) findViewById(R.id.update);
        mBtnSelect = (Button) findViewById(R.id.select);
        mBtnAdd.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnUpdate.setOnClickListener(this);
        mBtnSelect.setOnClickListener(this);

        mDbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 1);
    }

    @Override
    public void onClick(View v) {
        SQLiteDatabase db;

        switch (v.getId()) {
            case R.id.add:{
                db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("author", "章三");
                values.put("price", 65.4);
                values.put("pages", 456);
                values.put("name", "Android开发入门书");
                db.insert("book", null, values);
                values.clear();
                values.put("author", "李四");
                values.put("price", 83.9);
                values.put("pages", 893);
                values.put("name", "Android进阶书");
                db.insert("book", null, values);
            }
                break;
            case R.id.delete:{
                db = mDbHelper.getWritableDatabase();
                db.delete("book", "author = ? and name = ?", new String[] {"章三", "Android开发入门书"});
            }
                break;
            case R.id.update:{
                db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("price", 56);
                values.put("pages", 423);
                db.update("book", values, "author = ?", new String[] {"章三"});
            }
                break;
            case R.id.select:{
                db = mDbHelper.getReadableDatabase();
                Cursor cursor = db.query("book", null, null, null, null, null, null);
                while(cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String author = cursor.getString(cursor.getColumnIndex("author"));
                    double price = cursor.getDouble(cursor.getColumnIndex("price"));
                    int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.d(TAG, "select: id " + id + ", author " + author + ", price " + price + ", pages " + pages + ", name " + name);
                }
            }
                break;
        }
    }
}
