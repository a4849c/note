package com.may6soft.notepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.DB.GlobalDataStructure;

public class CheckPasswordActivity extends AppCompatActivity {
    private Intent startIntent;
    private static RecordDBHelper mDbHelper;
    private static String myPassword = "";
    // Default value of password_verified should be false
    private static boolean password_verified = false;
    private EditText inputPassword;
    private Button enterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable Up Navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_check_password);

        mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
        SQLiteDatabase dbRead = mDbHelper.getReadableDatabase(GlobalDataStructure.DBKey);
        String select = "SELECT * FROM " + RecordContract.RecordEntry.TABLE_NAME + " WHERE " + RecordContract.RecordEntry.COLUMN_NAME_CATEGORY + " = " + GlobalDataStructure.DB_PASSWORD;
        Cursor cursor = dbRead.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            // COLUMN_NAME_TITLE of 1st line is database password
            if (cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_TITLE)) != null) {
                myPassword = cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_TITLE));
            }
        }

        inputPassword = (EditText) findViewById(R.id.check_password_str);
        enterButton = (Button) findViewById(R.id.check_password_btn);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPassword(inputPassword.getText().toString())) {
                    setResult(RESULT_OK, startIntent);
                    finish();
                } else {
                    inputPassword.setText("");
                    alertInformation();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED, startIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static boolean checkPassword (String input) {
        if (myPassword.equals(input)) {
            password_verified = true;

        } else {
            password_verified = false;
        }
        return password_verified;
    }

    public static void set_password_verified (boolean verified) {
        password_verified = verified;
    }

    public static boolean get_password_verified () {
        return password_verified;
    }

    private void alertInformation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
        builder.setMessage("Password is wrong");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
