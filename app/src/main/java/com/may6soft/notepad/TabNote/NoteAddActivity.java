

package com.may6soft.notepad.TabNote;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.may6soft.notepad.CheckPasswordActivity;
import com.may6soft.notepad.DB.RecordContract;
import com.may6soft.notepad.DB.RecordDBHelper;
import com.may6soft.notepad.DBBackgroudTask;
import com.may6soft.notepad.DB.GlobalDataStructure;
import com.may6soft.notepad.MainActivity;
import com.may6soft.notepad.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteAddActivity extends AppCompatActivity {
    private Intent startIntent;
    private static Intent takePictureIntent;
    private MyAdapter adapter;
    private RecordDBHelper mDbHelper;
    String str_note = "Note";
    private NotePageData mNotePageData;
    private List<GlobalDataStructure.OneDisplayRow> mData;
    static final int ADD_NOTE_REQUEST_IMAGE_CAPTURE = 1;
    static final int CHECK_PASSWORD_REQUEST = 2;
    public final static String ADD_NOTE_EXTRA_MESSAGE_POSITION = "NoteAddActivity.POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = RecordDBHelper.getInstance(this,RecordDBHelper.DATABASE_NAME,null,RecordDBHelper.DATABASE_VERSION);
        mNotePageData = NotePageData.getInstance();

        setContentView(R.layout.note_add);
        ListView listView = (ListView) findViewById(R.id.add_note_listview);

        // Get the message from the startIntent
        startIntent = getIntent();
        int tab = startIntent.getIntExtra(MainActivity.EXTRA_MESSAGE_MAIN, -1);
        if (tab == GlobalDataStructure.TAB_NOTES) {
            mData = addDefaultRow();
        }

        if (savedInstanceState == null) {
            CheckPasswordActivity.set_password_verified(true);
        } else {
            CheckPasswordActivity.set_password_verified(false);
        }

        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                AlertDialog.Builder builder = new AlertDialog.Builder(NoteAddActivity.this);
                builder.setTitle("Add a row");
                final String[] rowType = {"Image", "Text Note", "Date"};
                builder.setItems(rowType, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which) {
                            case 0:
                                addImageRow();
                                break;
                            case 1:
                                addTextRow();
                                break;
                            case 2:
                                addDateRow();
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

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
                for (GlobalDataStructure.OneDisplayRow row : mData) {
                    if (row.imageFile != null) {
                        row.imageFile.delete();
                    }
                }
                setResult(RESULT_CANCELED, startIntent);
                finish();
                return true;

            case R.id.action_save:
                final EditText title = (EditText) findViewById(R.id.add_note_title);
                final String titleStr = title.getText().toString();
                final int pageNumber = NotePageData.getMaxNotePageNumber() + 1;
                if (TextUtils.isEmpty(titleStr)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.My_Theme_AlertDialog));
                    builder.setMessage("Please Input Title");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    /*new AlertDialog.Builder(this)
                            .setTitle("Title is empty")
                            .setMessage("Input title")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();*/
                } else {
                    DBBackgroudTask task = new DBBackgroudTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            SQLiteDatabase dbWrite = mDbHelper.getWritableDatabase(GlobalDataStructure.DBKey);
                            // Save user input data to DB
                            // Write new page (mData) to database
                            for (GlobalDataStructure.OneDisplayRow row : mData) {
                                ContentValues values = new ContentValues();
                                values.put(RecordContract.RecordEntry.COLUMN_NAME_CATEGORY,GlobalDataStructure.TAB_NOTES);
                                values.put(RecordContract.RecordEntry.COLUMN_NAME_ROW_TYPE, row.rowType);
                                values.put(RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER, pageNumber);
                                values.put(RecordContract.RecordEntry.COLUMN_NAME_TITLE, titleStr);
                                values.put(RecordContract.RecordEntry.COLUMN_NAME_ROW_NAME, row.rowName.toString());
                                values.put(RecordContract.RecordEntry.COLUMN_NAME_VALUE_TEXT, row.text.toString());
                                if (row.imageFile != null) {
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                    bmOptions.inJustDecodeBounds = false;
                                    // Todo: why crash if inSampleSize is 1???
                                    bmOptions.inSampleSize = GlobalDataStructure.imageSize;
                                    Bitmap bitmap = BitmapFactory.decodeFile(row.imageFile.getAbsolutePath(), bmOptions);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, GlobalDataStructure.imageCompress, os);
                                    values.put(RecordContract.RecordEntry.COLUMN_NAME_VALUE_IMAGE, os.toByteArray());

                                    // Icon is 1/8 size of original image
                                    ByteArrayOutputStream osIcon = new ByteArrayOutputStream();
                                    bmOptions.inSampleSize = GlobalDataStructure.imageIconSize;
                                    bitmap = BitmapFactory.decodeFile(row.imageFile.getAbsolutePath(), bmOptions);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, GlobalDataStructure.imageIconCompress, osIcon);
                                    values.put(RecordContract.RecordEntry.COLUMN_NAME_VALUE_IMAGE_ICON, osIcon.toByteArray());

                                    row.imageFile.delete();
                                    row.imageFile = null;
                                }
                                dbWrite.insert(RecordContract.RecordEntry.TABLE_NAME, null, values);
                            }
                            dbWrite.close();
                            return super.doInBackground(params);
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);

                            // Read out the last added page from database, keep mNotePageData.mPagelist consistent with database
                            SQLiteDatabase dbRead = mDbHelper.getWritableDatabase(GlobalDataStructure.DBKey);
                            GlobalDataStructure.OnePage onePage = new GlobalDataStructure.OnePage();
                            List<GlobalDataStructure.OneRow> mRowList = new ArrayList<GlobalDataStructure.OneRow>();
                            String select = "SELECT * FROM " + RecordContract.RecordEntry.TABLE_NAME + " WHERE " + RecordContract.RecordEntry.COLUMN_NAME_CATEGORY + " = " + GlobalDataStructure.TAB_NOTES
                                    + " AND " + RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER + " = " + pageNumber;
                            Cursor cursor = dbRead.rawQuery(select, null);
                            if(cursor.moveToFirst()) {
                                do {
                                    onePage.position = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_PAGE_NUMBER));
                                    onePage.title = cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_TITLE)).toString();

                                    GlobalDataStructure.OneRow oneRow = new GlobalDataStructure.OneRow();
                                    oneRow.dbPrimaryKey = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry._ID));
                                    oneRow.controlWord = GlobalDataStructure.mControlDefault;
                                    oneRow.name = cursor.getString(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_ROW_NAME)).toString();
                                    oneRow.rowType = cursor.getInt(cursor.getColumnIndexOrThrow(RecordContract.RecordEntry.COLUMN_NAME_ROW_TYPE));
                                    if (oneRow.rowType == GlobalDataStructure.ROW_IMAGE ) {
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

                                } while (cursor.moveToNext());

                                onePage.rowList = mRowList;
                                mNotePageData.mPagelist.add(onePage);

                                NotePageData.increaseNoteTabNumber();
                                NotePageData.setMaxNotePageNumber(pageNumber);
                                NoteTabFragment.mSectionsPagerAdapter.notifyDataSetChanged();
                                // Call notifyDataSetChanged ahead of setCurrentItem
                                NoteTabFragment.topTabIndicator.notifyDataSetChanged();
                                NoteTabFragment.topTabIndicator.setCurrentItem(NotePageData.getNoteTabNumber()-1);
                            }
                            dbRead.close();
                        }
                    };
                    // Todo: executeOnExecutor
                    //task.execute();
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    this.setResult(RESULT_OK, startIntent);
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<GlobalDataStructure.OneDisplayRow> addDefaultRow() {
        List<GlobalDataStructure.OneDisplayRow> list = new ArrayList<GlobalDataStructure.OneDisplayRow>();

        GlobalDataStructure.OneDisplayRow rowNote = new GlobalDataStructure.OneDisplayRow();
        rowNote.rowName = str_note;
        rowNote.text = GlobalDataStructure.emptyString;
        rowNote.image = null;
        rowNote.rowType = GlobalDataStructure.ROW_USER_NAME;
        list.add(rowNote);

        return list;
    }

    private void addDateRow () {
        // Add new line
        GlobalDataStructure.OneDisplayRow row = new GlobalDataStructure.OneDisplayRow();
        row.rowName = "Date";
        row.text = GlobalDataStructure.emptyString;
        row.image = null;
        row.rowType = GlobalDataStructure.ROW_DATE;
        mData.add(row);

        adapter.notifyDataSetChanged();
    }

    private void addTextRow () {
        // Add a new line
        GlobalDataStructure.OneDisplayRow row = new GlobalDataStructure.OneDisplayRow();
        row.rowName = GlobalDataStructure.emptyString;
        row.text = GlobalDataStructure.emptyString;
        row.image = null;
        row.rowType = GlobalDataStructure.ROW_TEXT;

        mData.add(row);
        adapter.notifyDataSetChanged();
    }

    private void addImageRow () {
        // Add a new line
        GlobalDataStructure.OneDisplayRow row = new GlobalDataStructure.OneDisplayRow();
        row.rowName = GlobalDataStructure.emptyString;
        row.text = GlobalDataStructure.emptyString;
        row.image = null;
        row.rowType = GlobalDataStructure.ROW_IMAGE;

        mData.add(row);
        adapter.notifyDataSetChanged();
    }

    /* Delete user defined test notes */
    private void deleteOneRow(int position) {
        // Delete a line
        mData.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void dispatchTakePictureIntent(int position) {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(ADD_NOTE_EXTRA_MESSAGE_POSITION, position);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                mData.get(position).imageFile = createImageFile(position);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (mData.get(position).imageFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mData.get(position).imageFile));
                startActivityForResult(takePictureIntent, ADD_NOTE_REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile(int position) throws IOException {
        // Create an image file name
        String imageFileName = "AddNote_" + position + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    /* Hangdle result of dispatchTakePictureIntent */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == ADD_NOTE_REQUEST_IMAGE_CAPTURE) && (resultCode == RESULT_OK)) {
            int position = takePictureIntent.getIntExtra(ADD_NOTE_EXTRA_MESSAGE_POSITION, 0);
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = GlobalDataStructure.imageIconSize;
            Bitmap bitmap = BitmapFactory.decodeFile(mData.get(position).imageFile.getAbsolutePath(), bmOptions);
            mData.get(position).image = bitmap;
            adapter.notifyDataSetChanged();

            CheckPasswordActivity.set_password_verified(true);
        }

        if ((requestCode == CHECK_PASSWORD_REQUEST) && (resultCode == RESULT_OK)) {
            CheckPasswordActivity.set_password_verified(true);
        }
    }

    private void pickDate(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.date_dialog, null);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
        builder.setView(view);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

        builder.setTitle("Set Date");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer date = new StringBuffer();
                date.append(String.format("%d-%02d-%02d",
                        datePicker.getYear(),
                        datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth()));
                mData.get(position).text = date.toString();
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private final class ViewHolder{
        public EditText row_name;
        public EditText row_text;
        public ImageView row_image;
        public ImageView actionIcon;
        public ImageView deleteIcon;
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.row_img_edit_edit_img_img, null);

                holder.row_name = (EditText)convertView.findViewById(R.id.row_name);
                holder.row_name.setTag(position);
                holder.row_name.addTextChangedListener(new MyTextWatcher(holder) {
                    @Override
                    public void afterTextChanged(Editable s, ViewHolder holder) {
                        int pos = (Integer) holder.row_name.getTag();
                        GlobalDataStructure.OneDisplayRow row = new GlobalDataStructure.OneDisplayRow();
                        row = mData.get(pos);
                        row.rowName = s.toString();
                        mData.set(pos, row);
                    }
                });

                holder.row_text = (EditText)convertView.findViewById(R.id.row_text);
                holder.row_text.setTag(position);
                holder.row_text.addTextChangedListener(new MyTextWatcher(holder) {
                    @Override
                    public void afterTextChanged(Editable s, ViewHolder holder) {
                        int pos = (Integer) holder.row_text.getTag();
                        GlobalDataStructure.OneDisplayRow row = new GlobalDataStructure.OneDisplayRow();
                        row = mData.get(pos);
                        row.text = s.toString();
                        mData.set(pos, row);
                    }
                });

                holder.row_image = (ImageView)convertView.findViewById(R.id.row_image);
                holder.row_image.setTag(position);

                holder.actionIcon = (ImageView) convertView.findViewById(R.id.action_icon);
                holder.actionIcon.setTag(position);
                holder.actionIcon.setOnClickListener(new MyOnClickListener(holder) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        int pos = (Integer) holder.actionIcon.getTag();
                        if (mData.get(pos).rowType == GlobalDataStructure.ROW_IMAGE) {
                            dispatchTakePictureIntent(pos);
                        } else if (mData.get(pos).rowType == GlobalDataStructure.ROW_DATE) {
                            pickDate(pos);
                        }
                    }
                });

                holder.deleteIcon = (ImageView)convertView.findViewById(R.id.delete_icon);
                holder.deleteIcon.setTag(position);
                holder.deleteIcon.setOnClickListener(new MyOnClickListener(holder) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        int pos = (Integer) holder.deleteIcon.getTag();
                        deleteOneRow(pos);
                    }
                });

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
                holder.row_name.setTag(position);
                holder.row_text.setTag(position);
                holder.row_image.setTag(position);
                holder.actionIcon.setTag(position);
                holder.deleteIcon.setTag(position);
            }


            if (mData.get(position).rowType == GlobalDataStructure.ROW_IMAGE){
                holder.row_name.setText(((mData.get(position)).rowName).toString());
                holder.row_text.setText(((mData.get(position)).text).toString());
                holder.row_text.setVisibility(View.GONE);
                holder.row_image.setImageBitmap(mData.get(position).image);
                holder.row_image.setVisibility(View.VISIBLE);
                holder.actionIcon.setImageResource(R.drawable.icon_camera_bmp);
                holder.actionIcon.setVisibility(View.VISIBLE);
                holder.deleteIcon.setVisibility(View.VISIBLE);
            }
            else if (mData.get(position).rowType == GlobalDataStructure.ROW_USER_NAME) {
                holder.row_name.setText(((mData.get(position)).rowName).toString());
                holder.row_text.setText(((mData.get(position)).text).toString());
                holder.row_text.setVisibility(View.VISIBLE);
                holder.row_image.setVisibility(View.GONE);
                holder.actionIcon.setVisibility(View.GONE);
                holder.deleteIcon.setVisibility(View.GONE);
            }
            else if (mData.get(position).rowType == GlobalDataStructure.ROW_PASSWORD) {
                holder.row_name.setText(((mData.get(position)).rowName).toString());
                holder.row_text.setText(((mData.get(position)).text).toString());
                holder.row_text.setVisibility(View.VISIBLE);
                holder.row_image.setVisibility(View.GONE);
                holder.actionIcon.setVisibility(View.GONE);
                holder.deleteIcon.setVisibility(View.GONE);
            }
            else if (mData.get(position).rowType == GlobalDataStructure.ROW_DATE) {
                holder.row_name.setText(((mData.get(position)).rowName).toString());
                holder.row_text.setText(((mData.get(position)).text).toString());
                holder.row_text.setVisibility(View.VISIBLE);
                holder.row_text.setInputType(InputType.TYPE_CLASS_DATETIME);
                holder.row_image.setVisibility(View.GONE);
                holder.actionIcon.setImageResource(R.drawable.icon_calendar_bmp);
                holder.actionIcon.setVisibility(View.VISIBLE);
                holder.deleteIcon.setVisibility(View.VISIBLE);
            }
            else if (mData.get(position).rowType == GlobalDataStructure.ROW_PHONE) {
                holder.row_name.setText(((mData.get(position)).rowName).toString());
                holder.row_text.setText(((mData.get(position)).text).toString());
                holder.row_text.setVisibility(View.VISIBLE);
                holder.row_text.setInputType(InputType.TYPE_CLASS_PHONE);
                holder.row_image.setVisibility(View.GONE);
                holder.actionIcon.setVisibility(View.GONE);
                holder.deleteIcon.setVisibility(View.VISIBLE);
            }
            // Default
            else {
                holder.row_name.setText(((mData.get(position)).rowName).toString());
                holder.row_text.setText(((mData.get(position)).text).toString());
                holder.row_text.setVisibility(View.VISIBLE);
                holder.row_image.setVisibility(View.GONE);
                holder.actionIcon.setVisibility(View.GONE);
                holder.deleteIcon.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private abstract class MyTextWatcher implements TextWatcher {
            private ViewHolder mHolder;

            public MyTextWatcher(ViewHolder holder) {
                this.mHolder=holder;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                afterTextChanged(s, mHolder);
            }
            public abstract void afterTextChanged(Editable s,ViewHolder holder);
        }

        private abstract class MyOnClickListener implements View.OnClickListener {
            private ViewHolder mHolder;

            public MyOnClickListener(ViewHolder holder){
                this.mHolder = holder;
            }

            @Override
            public void onClick(View v) {
                onClick(v, mHolder);
            }

            public abstract void onClick(View v, ViewHolder holder);
        }
    }
}
