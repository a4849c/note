package com.may6soft.notepad.DB;

import android.provider.BaseColumns;

/**
 * Created by a4849c on 2016/4/15.
 */


public class RecordContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public RecordContract() {}

    /* Inner class that defines the table contents */
    public static abstract class RecordEntry implements BaseColumns {
        public static final String TABLE_NAME = "notepad";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_ROW_TYPE = "text_type";
        public static final String COLUMN_NAME_PAGE_NUMBER = "page_number";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ROW_NAME = "row_name";
        public static final String COLUMN_NAME_VALUE_TEXT = "value_text";
        public static final String COLUMN_NAME_VALUE_IMAGE = "value_image";
        public static final String COLUMN_NAME_VALUE_IMAGE_ICON = "value_image_icon";
        public static final String COLUMN_NAME_BACKUP_TEXT_1 = "backup_text_1";
        public static final String COLUMN_NAME_BACKUP_TEXT_2 = "backup_text_2";
        public static final String COLUMN_NAME_BACKUP_BLOB_1 = "backup_blob_1";
        public static final String COLUMN_NAME_BACKUP_BLOB_2 = "backup_blob_2";
    }

}

