package com.example.james.autocomplete;

/**
 * Created by James on 15/11/21.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import java.util.ArrayList;


import java.sql.SQLException;

/**
 * Created by James on 15/11/8.
 */
public class SampleDBManager
{
    public static final String KEY_ROWID = "_id_";
    public static final String KEY_FIRSTNAME = "first_name";
    private static final int DATABASE_VERSION  = 1;
    private static final String DATABASE_CREATE =   "CREATE TABLE RECORDS (" +
            "ID INTERAGE PRIMARAY KEY," +
            "CONTENT VARCHAR2(50) NOT NULL UNIQUE," +
            "STAR BOOLEAN DEFAULT FALSE" +
            ");";

    private final Context context;
    private MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public SampleDBManager(Context ctx) {
        this.context = ctx;
        DBHelper = new MyDatabaseHelper(context);
    }

    private static class MyDatabaseHelper extends SQLiteOpenHelper {
        public MyDatabaseHelper(Context context) {
            super(context, "RECORD", null, DATABASE_VERSION);
        }

        //Create Table in Database.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            ;
        }
    }


    public SampleDBManager open() throws SQLException {
        db = DBHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        DBHelper.close();
    }

    //Insert Item with a String.
    public long insertSomething(String content) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("CONTENT", content);
        return db.insert("RECORDS", null, initialValues);
    }

    //Insert Item with a String and Star
    public long insertSomething(String content, boolean star) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("CONTENT", content);
        initialValues.put("STAR", star);
        return db.insert("RECORDS", null, initialValues);
    }

    //Change the value of Star column
    public long updateSomething(Item item) {
        if(item.getStar() == true) {
            ContentValues initialValues = new ContentValues();
            initialValues.put("STAR", false);
            item.setStar(false);
            return db.update("RECORDS", initialValues, "CONTENT = '" + item.getContent() + "'", null);
        } else {
            ContentValues initialValues = new ContentValues();
            initialValues.put("STAR", true);
            item.setStar(true);
            return db.update("RECORDS", initialValues, "CONTENT = '" + item.getContent() + "'", null);
        }
    }


    //Select the items what matches to content
    public Cursor selectSomeThing(String content) throws SQLException {
        Cursor mCursor = db.query(true, "RECORDS", new String[] {"ID", "CONTENT", "STAR"}, "CONTENT like '%" + content + "%'", null, null, null, null, null);

        return mCursor;
    }

    //List all items that are starred.
    public Cursor ListStar () throws SQLException {
        Cursor mCursor = db.query(true, "RECORDS", new String[] {"ID", "CONTENT", "STAR"}, "STAR = 1", null, null, null, null, null);

        return mCursor;
    }

    //List all items in table
    public Cursor ListData() throws SQLException {
        Cursor mCursor = db.query(true, "RECORDS", new String[] {"ID", "CONTENT", "STAR"}, null, null, null, null, null, null);

        return mCursor;
    }

    //Process the cursor and make an array of Item including all data in database.
    public ArrayList<Item> getItems(Cursor cursor) {
        ArrayList<Item> items = new ArrayList<Item>();

        if (cursor != null ) {
            if  (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("ID"));
                    String content = cursor.getString(cursor.getColumnIndex("CONTENT"));
                    boolean star = cursor.getInt(cursor.getColumnIndex("STAR")) > 0;
                    Item item = new Item(content, 1, star);
                    items.add(item);
                }while (cursor.moveToNext());
            }
        }

        cursor.close();

        return items;
    }

    //Delete item according to content in database
    public long deleteSomething(String content) {
        return db.delete("RECORDS", "CONTENT = '" + content + "'", null);
    }
}
