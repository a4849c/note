package com.may6soft.notepad;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.DB.GlobalDataStructure;

public class InitializeActivity extends AppCompatActivity {
    private Intent startIntent;
    private static RecordDBHelper mDbHelper;
    EditText myPassword;
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);

        mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
        startIntent = getIntent();
        myPassword = (EditText) findViewById(R.id.initialize_password);
        confirmBtn = (Button) findViewById(R.id.initialize_confirm_btn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*SQLiteDatabase dbRead = mDbHelper.getReadableDatabase();
                String select = "SELECT * FROM " + OthersContract.RecordEntry.TABLE_NAME;
                Cursor cursor = dbRead.rawQuery(select, null);*/

                if (myPassword.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(InitializeActivity.this, R.style.My_Theme_AlertDialog));
                    builder.setMessage("Please set a password");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                } else {
                    SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase(GlobalDataStructure.DBKey);
                    ContentValues values = new ContentValues();
                    values.put(RecordContract.RecordEntry.COLUMN_NAME_CATEGORY, GlobalDataStructure.DB_PASSWORD);
                    values.put(RecordContract.RecordEntry.COLUMN_NAME_ROW_TYPE, GlobalDataStructure.ROW_TEXT);
                    values.put(RecordContract.RecordEntry.COLUMN_NAME_TITLE, myPassword.getText().toString());
                    dbWrite.insert(RecordContract.RecordEntry.TABLE_NAME, null, values);

                    setResult(RESULT_OK, startIntent);
                    finish();
                }
            }
        });
    }
}
