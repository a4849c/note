package com.may6soft.notepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.DB.GlobalDataStructure;
import com.may6soft.notepad.TabIDs.IDsAddActivity;
import com.may6soft.notepad.TabIDs.IDsEditActivity;
import com.may6soft.notepad.TabIDs.IDsPageData;
import com.may6soft.notepad.TabIDs.IDsSearchActivity;
import com.may6soft.notepad.TabIDs.IDsTabFragment;
import com.may6soft.notepad.TabWebsite.WebsiteAddActivity;
import com.may6soft.notepad.TabWebsite.WebsiteEditActivity;
import com.may6soft.notepad.TabWebsite.WebsitePageData;
import com.may6soft.notepad.TabWebsite.WebsiteSearchActivity;
import com.may6soft.notepad.TabWebsite.WebsiteTabFragment;
import com.may6soft.notepad.TabPayment.PaymentAddActivity;
import com.may6soft.notepad.TabPayment.PaymentEditActivity;
import com.may6soft.notepad.TabPayment.PaymentPageData;
import com.may6soft.notepad.TabPayment.PaymentSearchActivity;
import com.may6soft.notepad.TabPayment.PaymentTabFragment;
import com.may6soft.notepad.TabNote.NoteAddActivity;
import com.may6soft.notepad.TabNote.NoteEditActivity;
import com.may6soft.notepad.TabNote.NoteSearchActivity;
import com.may6soft.notepad.TabNote.NoteTabFragment;
import com.may6soft.notepad.TabNote.NotePageData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    //
    private LayoutInflater layoutInflater;
    // Default value of dataInitialized should read from DB
    private static boolean dataInitialized = false;

    // Four bottom navigation Tabs: Sequency of fragment, image, text need be match
    private FragmentTabHost mTabHost;
    private String mTextviewArray[] = {"Website", "Payment", "ID", "Note"};
    private int mImageViewArray[] = {R.drawable.tab_password_btn,R.drawable.tab_payment_btn,R.drawable.tab_ids_btn, R.drawable.tab_notes_btn};
    private Class fragmentArray[] = {WebsiteTabFragment.class,PaymentTabFragment.class,IDsTabFragment.class,NoteTabFragment.class};

    // The request code
    static final int ADD_PASSWORD_ITEM_REQUEST = 1;
    static final int ADD_PAYMENT_ITEM_REQUEST = 2;
    static final int ADD_IDS_ITEM_REQUEST = 3;
    static final int ADD_NOTE_ITEM_REQUEST = 4;
    static final int EDIT_PASSWORD_ITEM_REQUEST = 5;
    static final int EDIT_PAYMENT_ITEM_REQUEST = 6;
    static final int EDIT_IDS_ITEM_REQUEST = 7;
    static final int EDIT_NOTE_ITEM_REQUEST = 8;
    static final int SEARCH_PASSWORD_ITEM_REQUEST = 9;
    static final int SEARCH_PAYMENT_ITEM_REQUEST = 10;
    static final int SEARCH_IDS_ITEM_REQUEST = 11;
    static final int SEARCH_NOTE_ITEM_REQUEST = 12;

    static final int INITIALIZE_REQUEST = 30;
    static final int CHECK_PASSWORD_REQUEST = 31;
    static final int SETTING_REQUEST = 32;
    public final static String EXTRA_MESSAGE_MAIN = "com.may6soft.notepad.main";
    public final static String EXTRA_MESSAGE_MAIN_PAGE_NUMBER = "com.may6soft.notepad.main.page.number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SQLiteDatabase.loadLibs(this);
        dataInitialized = firstRunCheck();
        // Todo: executeOnExecutor
        //DBAsyncTask myBackgroundTask = new DBAsyncTask();
        //myBackgroundTask.execute();
        //myBackgroundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        NotePageData testData = new NotePageData();
        initView();

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_ATTACH_DATA.equals(action) && type != null) {
            if ("data/import".equals(type)) {
                handleExportData(intent); // Handle export data
            }
        }

        if (!dataInitialized) {
            Intent InitializeIntent = new Intent(this, InitializeActivity.class);
            startActivityForResult(InitializeIntent, INITIALIZE_REQUEST);
        } else if (!CheckPasswordActivity.get_password_verified()) {
            Intent checkPasswordIntent = new Intent(this, CheckPasswordActivity.class);
            startActivityForResult(checkPasswordIntent, CHECK_PASSWORD_REQUEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckPasswordActivity.set_password_verified(false);
    }

    private void initView(){
        layoutInflater = LayoutInflater.from(this);
        //
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for(int i = 0; i < fragmentArray.length; i++){
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));

            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    /**
     *
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        if (textView.isSelected()) {
            textView.setTextColor(getResources().getColor(R.color.seagreen));
        } else {
            textView.setTextColor(getResources().getColor(R.color.black));
        }

        return view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_WEBSITE]) {
                    // Todo: Check better way, not use WebsitePageData
                    if (WebsitePageData.getWebsiteTabNumber() == 0) {
                        Intent addPasswordIntent = new Intent(this, WebsiteAddActivity.class);
                        addPasswordIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_WEBSITE);
                        startActivityForResult(addPasswordIntent, ADD_PASSWORD_ITEM_REQUEST);
                    }
                    else {
                        Intent editPasswordIntent = new Intent(this, WebsiteEditActivity.class);
                        editPasswordIntent.putExtra(EXTRA_MESSAGE_MAIN_PAGE_NUMBER, WebsiteTabFragment.getSelectedPageNumber());
                        startActivityForResult(editPasswordIntent, EDIT_PASSWORD_ITEM_REQUEST);
                    }
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_PAYMENT]) {
                    if (PaymentPageData.getPaymentTabNumber() == 0) {
                        Intent addPaymentIntent = new Intent(this, PaymentAddActivity.class);
                        addPaymentIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_PAYMENT);
                        startActivityForResult(addPaymentIntent, ADD_PAYMENT_ITEM_REQUEST);
                    }
                    else {
                        Intent editPaymentIntent = new Intent(this, PaymentEditActivity.class);
                        editPaymentIntent.putExtra(EXTRA_MESSAGE_MAIN_PAGE_NUMBER, PaymentTabFragment.getSelectedPageNumber());
                        startActivityForResult(editPaymentIntent, EDIT_PAYMENT_ITEM_REQUEST);
                    }
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_IDS]) {
                    if (IDsPageData.getIDsTabNumber() == 0) {
                        Intent addIDsIntent = new Intent(this, IDsAddActivity.class);
                        addIDsIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_IDS);
                        startActivityForResult(addIDsIntent, ADD_IDS_ITEM_REQUEST);
                    }
                    else {
                        Intent editIDsIntent = new Intent(this, IDsEditActivity.class);
                        editIDsIntent.putExtra(EXTRA_MESSAGE_MAIN_PAGE_NUMBER, IDsTabFragment.getSelectedPageNumber());
                        startActivityForResult(editIDsIntent, EDIT_IDS_ITEM_REQUEST);
                    }
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_NOTES]) {
                    if (NotePageData.getNoteTabNumber() == 0) {
                        Intent addNoteIntent = new Intent(this, NoteAddActivity.class);
                        addNoteIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_NOTES);
                        startActivityForResult(addNoteIntent, ADD_NOTE_ITEM_REQUEST);
                    }
                    else {
                        Intent editNoteIntent = new Intent(this, NoteEditActivity.class);
                        editNoteIntent.putExtra(EXTRA_MESSAGE_MAIN_PAGE_NUMBER, NoteTabFragment.getSelectedPageNumber());
                        startActivityForResult(editNoteIntent, EDIT_NOTE_ITEM_REQUEST);
                    }
                }
                return true;

            case R.id.action_search:
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_WEBSITE]) {
                    Intent searchPasswordIntent = new Intent(this, WebsiteSearchActivity.class);
                    searchPasswordIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_WEBSITE);
                    startActivityForResult(searchPasswordIntent, SEARCH_PASSWORD_ITEM_REQUEST);
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_PAYMENT]) {
                    Intent searchPaymentIntent = new Intent(this, PaymentSearchActivity.class);
                    searchPaymentIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_PAYMENT);
                    startActivityForResult(searchPaymentIntent, SEARCH_PAYMENT_ITEM_REQUEST);
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_IDS]) {
                    Intent searchIDsIntent = new Intent(this, IDsSearchActivity.class);
                    searchIDsIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_IDS);
                    startActivityForResult(searchIDsIntent, SEARCH_IDS_ITEM_REQUEST);
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_NOTES]) {
                    Intent searchNoteIntent = new Intent(this, NoteSearchActivity.class);
                    searchNoteIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_NOTES);
                    startActivityForResult(searchNoteIntent, SEARCH_NOTE_ITEM_REQUEST);
                }
                return true;

            case R.id.action_add:
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_WEBSITE]) {
                    Intent addPasswordIntent = new Intent(this, WebsiteAddActivity.class);
                    addPasswordIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_WEBSITE);
                    startActivityForResult(addPasswordIntent, ADD_PASSWORD_ITEM_REQUEST);
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_PAYMENT]) {
                    Intent addPaymentIntent = new Intent(this, PaymentAddActivity.class);
                    addPaymentIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_PAYMENT);
                    startActivityForResult(addPaymentIntent, ADD_PAYMENT_ITEM_REQUEST);
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_IDS]) {
                    Intent addIDsIntent = new Intent(this, IDsAddActivity.class);
                    addIDsIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_IDS);
                    startActivityForResult(addIDsIntent, ADD_IDS_ITEM_REQUEST);
                }
                if (mTabHost.getCurrentTabTag() == mTextviewArray[GlobalDataStructure.TAB_NOTES]) {
                    Intent addNoteIntent = new Intent(this, NoteAddActivity.class);
                    addNoteIntent.putExtra(EXTRA_MESSAGE_MAIN, GlobalDataStructure.TAB_NOTES);
                    startActivityForResult(addNoteIntent, ADD_NOTE_ITEM_REQUEST);
                }
                return true;

            case R.id.action_settings:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivityForResult(settingIntent, SETTING_REQUEST);
                return true;

            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
                builder.setMessage("Notepad Version " + getVersion());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_PASSWORD_REQUEST) {
            if (resultCode == RESULT_OK) {
                CheckPasswordActivity.set_password_verified(true);
            }else {
                // password verification failed
                finish();
            }
        }

        if (requestCode == INITIALIZE_REQUEST) {
            if (resultCode == RESULT_OK) {
                dataInitialized = true;
                CheckPasswordActivity.set_password_verified(true);
            }else {
                finish();
            }
        }

        if ((requestCode == ADD_PASSWORD_ITEM_REQUEST) ||
                (requestCode == EDIT_PASSWORD_ITEM_REQUEST) ||
                (requestCode == SEARCH_PASSWORD_ITEM_REQUEST) ||
                (requestCode == ADD_PAYMENT_ITEM_REQUEST) ||
                (requestCode == EDIT_PAYMENT_ITEM_REQUEST) ||
                (requestCode == SEARCH_PAYMENT_ITEM_REQUEST) ||
                (requestCode == ADD_IDS_ITEM_REQUEST) ||
                (requestCode == EDIT_IDS_ITEM_REQUEST) ||
                (requestCode == SEARCH_IDS_ITEM_REQUEST) ||
                (requestCode == ADD_NOTE_ITEM_REQUEST) ||
                (requestCode == EDIT_NOTE_ITEM_REQUEST) ||
                (requestCode == SEARCH_NOTE_ITEM_REQUEST) ||
                (requestCode == SETTING_REQUEST)) {
            CheckPasswordActivity.set_password_verified(true);
        }
    }

    void handleExportData(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
        builder.setMessage("Export Data to Notepad Pro?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    exportData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.show();
    }

    private void exportData() throws IOException {
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if(sdExist){
            String sourceDir = getFilesDir().getParent().toString();
            sourceDir += "/databases";
            File sourceFile = new File(sourceDir + "/" + RecordDBHelper.DATABASE_NAME); // source file

            String destDir = android.os.Environment.getExternalStorageDirectory().toString();
            destDir += "/may6soft";
            File destFile = new File(destDir + "/" + "export.db"); // export file, no extension
            File dirDirFile = new File(destDir);
            if (!dirDirFile.exists()) {
                dirDirFile.mkdirs();
            }

            try {
                if (copyFileTo(sourceFile, destFile)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
                    builder.setMessage("Data file export.db exported to " + destDir + ".");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
                    builder.setMessage("Data file export fail.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
            builder.setMessage("Please insert SD card.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    private boolean copyFileTo(File srcFile, File destFile) throws IOException {
        if (srcFile.isDirectory() || destFile.isDirectory()) {
            return false;
        }
        if (!srcFile.exists()) {
            return false;
        }
        if (destFile.exists()) {
            destFile.delete();
        }
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fis.close();
        return true;
    }

    private boolean firstRunCheck() {
        RecordDBHelper mDbHelper;
        boolean initialized = false;
        mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
        SQLiteDatabase dbRead = mDbHelper.getReadableDatabase(GlobalDataStructure.DBKey);
        String select = "SELECT * FROM " + RecordContract.RecordEntry.TABLE_NAME;
        Cursor cursor = dbRead.rawQuery(select, null);
        if (cursor.moveToFirst()) {
            initialized = true;
        } else {
            initialized = false;
        }
        dbRead.close();
        return initialized;
    }

    private class DBAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("Nico", "initView start");
            initView();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d("Nico", "initView end");
            super.onPostExecute(o);
        }
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "version_unknown";
        }
    }
}
