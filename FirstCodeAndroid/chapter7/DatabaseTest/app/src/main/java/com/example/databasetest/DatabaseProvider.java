package com.example.databasetest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {

    private static final String AUTHORITIES = "com.example.databasetest.provider";
    private static final int BOOK_DIR = 0;
    private static final int BOOK_ITEM = 1;
    private static final int CATEGORY_DIR = 2;
    private static final int CATEGORY_ITEM = 3;

    private SQLiteOpenHelper dbOpenHelper;

    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITIES, "book", BOOK_DIR);
        uriMatcher.addURI(AUTHORITIES, "book/#", BOOK_ITEM);
        uriMatcher.addURI(AUTHORITIES, "category", CATEGORY_DIR);
        uriMatcher.addURI(AUTHORITIES, "category/#", CATEGORY_ITEM);
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        dbOpenHelper = new MyDatabaseHelper(getContext(), "BookStore.db", null, 1);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
                cursor = db.query("book",projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ITEM:
                cursor = db.query("book", projection, "id = ?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
                break;
            case CATEGORY_DIR:
                cursor = db.query("category",projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ITEM:
                cursor = db.query("category", projection, "id = ?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (uriMatcher.match(uri)){
            case BOOK_DIR:
            case BOOK_ITEM:
                long newBookId = db.insert("book", null, values);
                returnUri = Uri.parse("content://" + AUTHORITIES + "/table/" + newBookId);
                break;
            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long newCategoryId = db.insert("category", null, values);
                returnUri = Uri.parse("content://" + AUTHORITIES + "/category/" + newCategoryId);
                break;

        }
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int updateRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                updateRows = db.update("book", values, selection, selectionArgs);
                break;
            case BOOK_ITEM:
                updateRows = db.update("book", values, "id = ?", new String[]{uri.getLastPathSegment()});
                break;
            case CATEGORY_DIR:
                updateRows = db.update("category", values, selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                updateRows = db.update("category", values, "id = ?", new String[]{uri.getLastPathSegment()});
                break;
        }
        return updateRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int deleteRows = 0;
        switch (uriMatcher.match(uri)) {
            case BOOK_DIR:
                deleteRows = db.delete("book", selection, selectionArgs);
                break;
            case BOOK_ITEM:
                deleteRows = db.delete("book", "deleteRows = ?", new String[]{uri.getLastPathSegment()});
                break;
            case CATEGORY_DIR:
                deleteRows = db.delete("category", selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                deleteRows = db.delete("category", "deleteRows = ?", new String[]{uri.getLastPathSegment()});
                break;
            default:
                break;
        }
        return deleteRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
