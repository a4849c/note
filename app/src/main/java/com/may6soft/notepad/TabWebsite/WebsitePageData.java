package com.may6soft.notepad.TabWebsite;

import android.app.Application;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.graphics.BitmapFactory;

import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.DB.GlobalDataStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a4849c on 2016/4/23.
 */
public class WebsitePageData extends Application {
    private static WebsitePageData mInstance = null;
    private static RecordDBHelper mDbHelper;
    private static int totalWebsiteTabNumber = 0;
    private static int maxWebsitePageNumber = 0;
    static boolean dataInit = false;
    static public List<GlobalDataStructure.OnePage> mPagelist;

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
        mPagelist = new ArrayList<GlobalDataStructure.OnePage>();
    }

    static public synchronized WebsitePageData getInstance(){
        if (mInstance == null){
            mInstance = new WebsitePageData();
        }
        return mInstance;
    }

    static public void getWebsiteData() {
        GlobalDataStructure.OneRow oneRow;
        List<GlobalDataStructure.OneRow> mRowList = null;
        GlobalDataStructure.OnePage onePage = null;

        assert (mInstance != null);

        if (!dataInit) {
            dataInit = true;

            SQLiteDatabase dbRead = mDbHelper.getReadableDatabase(GlobalDataStructure.DBKey);
            String select = "SELECT * FROM " + RecordContract.RecordEntry.TABLE_NAME + " WHERE " + RecordContract.RecordEntry.COLUMN_NAME_CATEGORY + " = " + GlobalDataStructure.TAB_WEBSITE
                    + " ORDER BY " + RecordContract.RecordEntry.COLUMN_NAME_TITLE + " COLLATE NOCASE ASC" + " ," + RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER + " ASC";
            Cursor cursor = dbRead.rawQuery(select, null);

            if(cursor.moveToFirst()) {
                do {
                    totalWebsiteTabNumber++;
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER));
                    if(pageNumber > maxWebsitePageNumber) {
                        maxWebsitePageNumber = pageNumber;
                    }
                    onePage = new GlobalDataStructure.OnePage();
                    onePage.position = pageNumber;
                    onePage.title = cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_TITLE)).toString();
                    mRowList = new ArrayList<GlobalDataStructure.OneRow>();
                    do {
                        if (pageNumber == cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER))) {
                            oneRow = new GlobalDataStructure.OneRow();
                            oneRow.dbPrimaryKey = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry._ID));
                            oneRow.controlWord = GlobalDataStructure.mControlDefault;
                            oneRow.name = cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_ROW_NAME)).toString();
                            oneRow.rowType = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_ROW_TYPE));
                            if (cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_ROW_TYPE)) == GlobalDataStructure.ROW_IMAGE ) {
                                oneRow.text = GlobalDataStructure.emptyString;
                                byte[] in = cursor.getBlob(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_VALUE_IMAGE_ICON));
                                if (in != null) {
                                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                    bmOptions.inJustDecodeBounds = false;
                                    oneRow.image = BitmapFactory.decodeByteArray(in, 0, in.length, bmOptions);
                                }
                            }
                            else {
                                oneRow.text = cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_VALUE_TEXT)).toString();
                                oneRow.image = null;
                            }
                            mRowList.add(oneRow);
                        } else {
                            break;
                        }
                    } while (cursor.moveToNext());

                    onePage.rowList = mRowList;
                    mInstance.mPagelist.add(onePage);
                    cursor.moveToPrevious();
                } while (cursor.moveToNext());
            }
            dbRead.close();
        }
    }

    public static int getWebsiteTabNumber() {
        return totalWebsiteTabNumber;
    }

    public static void increaseWebsiteTabNumber() {
        totalWebsiteTabNumber++;
    }

    public static void decreaseWebsiteTabNumber() {
        totalWebsiteTabNumber--;
    }

    public static int getMaxWebsitePageNumber() {
        return maxWebsitePageNumber;
    }

    public static void setMaxWebsitePageNumber(int num) {
        if (maxWebsitePageNumber < num) {
            maxWebsitePageNumber = num;
        }
    }
}
