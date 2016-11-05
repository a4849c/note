package com.may6soft.notepad.Views;

import android.content.Intent;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.may6soft.notepad.CheckPasswordActivity;
import com.may6soft.notepad.DB.GlobalDataStructure;
import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.TabWebsite.WebsitePageData;
import com.may6soft.notepad.R;

public class ShowImageActivity extends AppCompatActivity {
    private Intent startIntent;
    private int imageNumber;
    private RecordDBHelper mDbHelper;
    SQLiteDatabase dbRead;
    static final int CHECK_PASSWORD_REQUEST = 1;
    private WebsitePageData mWebsitePageData;
    public final static String EXTRA_MESSAGE_IMAGE_NUMBER = "com.may6soft.notepad.showimage.image.number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        startIntent = getIntent();
        imageNumber = startIntent.getIntExtra(EXTRA_MESSAGE_IMAGE_NUMBER, 0);

        mDbHelper = RecordDBHelper.getInstance(null, RecordDBHelper.DATABASE_NAME, null, RecordDBHelper.DATABASE_VERSION);
        dbRead = mDbHelper.getWritableDatabase(GlobalDataStructure.DBKey);
        String mySelection = "SELECT * FROM " + RecordContract.RecordEntry.TABLE_NAME + " WHERE " + RecordContract.RecordEntry._ID + " =" + imageNumber;
        Cursor cursor = dbRead.rawQuery(mySelection, null);
        if(cursor.moveToFirst()) {
            byte[] in = cursor.getBlob(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_VALUE_IMAGE));
            if (in != null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap image = BitmapFactory.decodeByteArray(in, 0, in.length);
                ImageView imageView = (ImageView) findViewById(R.id.show_image);
                imageView.setImageBitmap(image);
            }
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CHECK_PASSWORD_REQUEST) && (resultCode == RESULT_OK)) {
            CheckPasswordActivity.set_password_verified(true);
        }
    }
}
