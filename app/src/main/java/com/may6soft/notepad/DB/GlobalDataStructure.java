package com.may6soft.notepad.DB;

import android.graphics.Bitmap;

import java.io.File;
import java.util.List;

/**
 * Created by a4849c on 6/3/2016.
 */
public class GlobalDataStructure {
    //
    public static int TAB_WEBSITE = 0;
    public static int TAB_PAYMENT = 1;
    public static int TAB_IDS = 2;
    public static int TAB_NOTES = 3;
    public static int DB_PASSWORD = 4;

    public static int mControlDefault = 0;
    public static int mControlNew = 1;
    public static int mControlUpdated = 2;
    public static int mControlDelete = 3;

    public static int ROW_USER_NAME = 0;
    public static int ROW_PASSWORD = 1;
    public static int ROW_TEXT = 2;
    public static int ROW_IMAGE = 3;
    public static int ROW_DATE = 4;
    public static int ROW_PHONE = 5;
    public static int ROW_EMAIL = 6;

    public static int imageSize = 4;
    public static int imageCompress = 80;
    public static int imageIconSize = 8;
    public static int imageIconCompress = 70;
    public static  String emptyString = "";
    public static String DBKey = "&89JhMokp@";

    public static class OneDisplayRow {
        public String rowName;
        public String text;
        public Bitmap image;
        public File imageFile;
        public int rowType;
    }
    public static class OneRow {
        public int dbPrimaryKey;
        public int controlWord;
        public String name;
        public String text;
        public Bitmap image;
        public int rowType;
    }
    public static class OnePage {
        public int position;
        public String title;
        public List<OneRow> rowList;
    }
}
