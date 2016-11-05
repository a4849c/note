package com.may6soft.notepad;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by a4849c on 5/13/2016.
 */
public class DBBackgroudTask extends AsyncTask {
    private final WeakReference dataInitReference;

    public DBBackgroudTask() {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        dataInitReference = new WeakReference(null);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }
}
