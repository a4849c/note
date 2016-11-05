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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.DB.GlobalDataStructure;

public class SettingActivity extends AppCompatActivity {
    private Intent startIntent;
    static final int CHECK_PASSWORD_REQUEST = 1;
    EditText password;
    EditText passwordRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        startIntent = getIntent();
        password = (EditText) findViewById(R.id.setting_password);
        passwordRepeat = (EditText) findViewById(R.id.setting_password_repeat);

        if (savedInstanceState == null) {
            CheckPasswordActivity.set_password_verified(true);
        } else {
            CheckPasswordActivity.set_password_verified(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CheckPasswordActivity.get_password_verified()) {
            Intent checkPasswordIntent = new Intent(this, CheckPasswordActivity.class);
            startActivityForResult(checkPasswordIntent, CHECK_PASSWORD_REQUEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckPasswordActivity.set_password_verified(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Discard change, back to parent
                setResult(RESULT_CANCELED, startIntent);
                finish();
                return true;

            case R.id.action_save:
                if (password.getText().toString().isEmpty() && passwordRepeat.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
                    builder.setMessage("The input is empty");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    return false;
                } else if (password.getText().toString().equals(passwordRepeat.getText().toString())) {
                    String newPassword = password.getText().toString();
                    RecordDBHelper mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
                    SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase(GlobalDataStructure.DBKey);
                    String mySelection = RecordContract.RecordEntry.COLUMN_NAME_CATEGORY + " = ?";
                    String[] selectionArgs = {String.valueOf(GlobalDataStructure.DB_PASSWORD)};
                    ContentValues newValues = new ContentValues();
                    newValues.put(RecordContract.RecordEntry.COLUMN_NAME_TITLE, newPassword);
                    dbWrite.update(RecordContract.RecordEntry.TABLE_NAME, newValues, mySelection, selectionArgs);

                    setResult(RESULT_OK, startIntent);
                    finish();
                    return true;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
                    builder.setMessage("Passwords do not match");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    password.setText("");
                    passwordRepeat.setText("");
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void exportData(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
        builder.setMessage("Export Data");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public void importData(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
        builder.setMessage("Import Data");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
