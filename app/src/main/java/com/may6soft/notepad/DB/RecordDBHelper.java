package com.may6soft.notepad.DB;

import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import com.may6soft.notepad.DB.RecordContract.RecordEntry;

/**
 * Created by a4849c on 2016/4/16.
 */
public class RecordDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notepad.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordEntry.TABLE_NAME + " (" +
                    RecordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RecordEntry.COLUMN_NAME_CATEGORY + INTEGER_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_ROW_TYPE + INTEGER_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_PAGE_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_ROW_NAME + TEXT_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_VALUE_TEXT + TEXT_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_VALUE_IMAGE + BLOB_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_VALUE_IMAGE_ICON + BLOB_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_BACKUP_TEXT_1 + TEXT_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_BACKUP_TEXT_2 + TEXT_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_BACKUP_BLOB_1 + BLOB_TYPE + COMMA_SEP +
                    RecordEntry.COLUMN_NAME_BACKUP_BLOB_2 + BLOB_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecordContract.RecordEntry.TABLE_NAME;

    private static RecordDBHelper mInstance = null;

    public RecordDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /*public RecordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    //使用单例模式，如果MyDatabaseHelper存在则返回，不存在则创建
    static public synchronized RecordDBHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        if (mInstance == null){
            //DatabaseContext dbContext = new DatabaseContext(null);
            mInstance = new RecordDBHelper(context,name,factory,version);
        }
        return mInstance;
    }
}
