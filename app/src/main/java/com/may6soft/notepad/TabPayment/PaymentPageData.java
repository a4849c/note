package com.may6soft.notepad.TabPayment;

import android.app.Application;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.graphics.BitmapFactory;

import com.may6soft.notepad.DB.GlobalDataStructure;
import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a4849c on 2016/4/23.
 */
public class PaymentPageData extends Application {
    private static PaymentPageData mInstance = null;
    private static RecordDBHelper mDbHelper;
    private static int totalPaymentTabNumber = 0;
    private static int maxPaymentPageNumber = 0;
    static boolean dataInit = false;
    static public List<GlobalDataStructure.OnePage> mPagelist;

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
        mPagelist = new ArrayList<GlobalDataStructure.OnePage>();
    }

    static public synchronized PaymentPageData getInstance(){
        if (mInstance == null){
            mInstance = new PaymentPageData();
        }
        return mInstance;
    }

    static public void getPaymentData() {
        GlobalDataStructure.OneRow oneRow;
        List<GlobalDataStructure.OneRow> mRowList = null;
        GlobalDataStructure.OnePage onePage = null;

        assert (mInstance != null);

        if (!dataInit) {
            dataInit = true;

            // Todo: where is WebsitePageData:onCreate called ????
            if (mDbHelper == null) {
                mDbHelper = RecordDBHelper.getInstance(null, RecordDBHelper.DATABASE_NAME, null, RecordDBHelper.DATABASE_VERSION);
                mPagelist = new ArrayList<GlobalDataStructure.OnePage>();
            }
            SQLiteDatabase dbRead = mDbHelper.getReadableDatabase(GlobalDataStructure.DBKey);
            String select = "SELECT * FROM " + RecordContract.RecordEntry.TABLE_NAME + " WHERE " + RecordContract.RecordEntry.COLUMN_NAME_CATEGORY + " = " + GlobalDataStructure.TAB_PAYMENT
                    + " ORDER BY " + RecordContract.RecordEntry.COLUMN_NAME_TITLE + " COLLATE NOCASE ASC" + " ," + RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER + " ASC";
            Cursor cursor = dbRead.rawQuery(select, null);

            if(cursor.moveToFirst()) {
                do {
                    totalPaymentTabNumber++;
                    int pageNumber = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER));
                    if(pageNumber > maxPaymentPageNumber) {
                        maxPaymentPageNumber = pageNumber;
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

    public static int getPaymentTabNumber() {
        return totalPaymentTabNumber;
    }

    public static void increasePaymentTabNumber() {
        totalPaymentTabNumber++;
    }

    public static void decreasePaymentTabNumber() {
        totalPaymentTabNumber--;
    }

    public static int getMaxPaymentPageNumber() {
        return maxPaymentPageNumber;
    }

    public static void setMaxPaymentPageNumber(int num) {
        if (maxPaymentPageNumber < num) {
            maxPaymentPageNumber = num;
        }
    }
}
