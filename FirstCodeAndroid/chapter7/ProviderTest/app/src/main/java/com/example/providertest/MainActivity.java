package com.example.providertest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button mBtnAdd;
    private Button mBtnDelete;
    private Button mBtnUpdate;
    private Button mBtnSelect;

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
    }
    @Override
    public void onClick(View v) {
        ContentResolver contentResolver = getContentResolver();

        switch (v.getId()) {
            case R.id.add:
                Uri insertUri = Uri.parse("content://com.example.databasetest.provider/book");
                ContentValues values = new ContentValues();
                values.put("author", "小明");
                values.put("price", 98.7);
                values.put("pages", 854);
                values.put("name", "数据结构书");
                Uri resultUri = contentResolver.insert(insertUri, values);
                Log.d(TAG, "insert data: resultUri is " + resultUri.toString());

                values.clear();
                values.put("author", "晓红");
                values.put("price", 34);
                values.put("pages", 453);
                values.put("name", "英语书");
                resultUri = contentResolver.insert(insertUri, values);
                Log.d(TAG, "insert data: resultUri is " + resultUri.toString());

                insertUri = Uri.parse("content://com.example.databasetest.provider/book/3");
                values.clear();
                values.put("author", "晓红");
                values.put("price", 56.3);
                values.put("pages", 790);
                values.put("name", "Java基础书");
                resultUri = contentResolver.insert(insertUri, values);
                Log.d(TAG, "insert data: resultUri is " + resultUri.toString());

                values.clear();
                values.put("author", "小芳");
                values.put("price", 92.4);
                values.put("pages", 1003);
                values.put("name", "linux入门书");
                resultUri = contentResolver.insert(insertUri, values);
                Log.d(TAG, "insert data: resultUri is " + resultUri.toString());
            break;
            case R.id.delete:
                String where = "author = ? and name = ?";
                String[] selectArgs = new String[]{"晓红", "Java基础书"};

                Uri deleteUri = Uri.parse("content://com.example.databasetest.provider/book/1");
                long deleteRow = contentResolver.delete(deleteUri, where, selectArgs);
                Log.d(TAG, "delete data: deleteRow is " + deleteRow);

                deleteUri = Uri.parse("content://com.example.databasetest.provider/book");
                deleteRow = contentResolver.delete(deleteUri, where, selectArgs);
                Log.d(TAG, "delete data: deleteRow is " + deleteRow);
            break;
            case R.id.update:
                String updateWhere = "author = ?";
                String[] updateSelectArgs = new String[] {"晓红"};

                Uri updateUri = Uri.parse("content://com.example.databasetest.provider/book/2");
                ContentValues updateValues = new ContentValues();
                updateValues.put("price", 67);
                updateValues.put("name", "编程宝典");
                long updateRow = contentResolver.update(updateUri, updateValues, updateWhere, updateSelectArgs);
                Log.d(TAG, "update data: updateRow is " + updateRow);

                updateUri = Uri.parse("content://com.example.databasetest.provider/book");
                updateValues = new ContentValues();
                updateValues.put("price", 52);
                updateValues.put("name", "算法书");
                updateRow = contentResolver.update(updateUri, updateValues, updateWhere, updateSelectArgs);
                Log.d(TAG, "update data: updateRow is " + updateRow);
            break;
            case R.id.select:
                Uri queryUri = Uri.parse("content://com.example.databasetest.provider/book");
                Cursor cursor = contentResolver.query(queryUri, null, null, null, null);
                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        Log.d(TAG, "select: id " + id + ", author " + author + ", price " + price + ", pages " + pages + ", name " + name);
                    }
                    cursor.close();
                }
            break;
        }
    }
}
