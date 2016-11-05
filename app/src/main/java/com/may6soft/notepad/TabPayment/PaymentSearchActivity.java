package com.may6soft.notepad.TabPayment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.may6soft.notepad.CheckPasswordActivity;
import com.may6soft.notepad.DB.GlobalDataStructure;
import com.may6soft.notepad.R;
import com.may6soft.notepad.Views.SearchView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PaymentSearchActivity extends AppCompatActivity {
    private PaymentPageData mPaymentPageData;
    private Intent startIntent;
    private SearchView searchString;
    private ListView listView;
    private MyAdapter adapter;
    static final int CHECK_PASSWORD_REQUEST = 1;
    private class SearchMatch {
        int pagePosition; // page number in database, generated when a page is added into database
        int pageListPosition; // position of tab in UI, generated when a page is readout from database
        String title;
        String value;
    }
    private List<SearchMatch> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_search);

        searchResults = new ArrayList<SearchMatch>();
        mPaymentPageData = PaymentPageData.getInstance();
        searchString = (SearchView) findViewById(R.id.search_payment_searchString);
        searchString.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchResults.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    searchResults.clear();
                } else {
                    search(s.toString());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listView = (ListView) findViewById(R.id.search_payment_listview);


        if (savedInstanceState == null) {
            CheckPasswordActivity.set_password_verified(true);
        } else {
            CheckPasswordActivity.set_password_verified(false);
        }

        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CHECK_PASSWORD_REQUEST) && (resultCode == RESULT_OK)) {
            CheckPasswordActivity.set_password_verified(true);
        }
    }

    private void search (String searchStr) {
        Log.d("Password_Search", searchStr);
        int pagePosition = 0;
        Iterator <GlobalDataStructure.OnePage> iteratorPage = mPaymentPageData.mPagelist.iterator();
        while (iteratorPage.hasNext()) {
            GlobalDataStructure.OnePage onePage = iteratorPage.next();
            if (onePage.title.toLowerCase().contains(searchStr.toLowerCase())) {
                SearchMatch searchMatch  = new SearchMatch();
                searchMatch.title = onePage.title;
                searchMatch.value = onePage.rowList.get(0).name + ": " + onePage.rowList.get(0).text;
                searchMatch.pageListPosition = getPageListPosition(onePage.position);
                searchResults.add(searchMatch);
                continue;
            } else {
                Iterator<GlobalDataStructure.OneRow> iteratorRow = onePage.rowList.iterator();
                while (iteratorRow.hasNext()) {
                    GlobalDataStructure.OneRow oneRow = iteratorRow.next();
                    if (oneRow.name.toLowerCase().contains(searchStr.toLowerCase())) {
                        SearchMatch searchMatch  = new SearchMatch();
                        searchMatch.title = onePage.title;
                        searchMatch.value = oneRow.name;
                        searchMatch.pageListPosition = getPageListPosition(onePage.position);
                        searchResults.add(searchMatch);
                        break;
                    }
                    if (oneRow.text.toLowerCase().contains(searchStr.toLowerCase())) {
                        SearchMatch searchMatch  = new SearchMatch();
                        searchMatch.title = onePage.title;
                        searchMatch.value = oneRow.text;
                        searchMatch.pageListPosition = getPageListPosition(onePage.position);
                        searchResults.add(searchMatch);
                        break;
                    }
                }
            }
        }
    }

    private int getPageListPosition (int position) {
        int pageListPosition = 0;
        for (GlobalDataStructure.OnePage p : mPaymentPageData.mPagelist){
            if(position == p.position) {
                pageListPosition = mPaymentPageData.mPagelist.indexOf(p);
                break;
            }
        }
        return pageListPosition;
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

    private final class ViewHolder{
        public TextView title;
        public TextView value;
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return searchResults.size();
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
                convertView = mInflater.inflate(R.layout.row_txt_txt_vertical, null);

                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.title.setTag(position);
                holder.title.setOnClickListener(new MyOnClickListener(holder) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        int pos = (Integer) holder.title.getTag();
                        PaymentTabFragment.setSelectedPageNumber(searchResults.get(pos).pageListPosition);
                        PaymentTabFragment.topTabIndicator.notifyDataSetChanged();
                        PaymentTabFragment.topTabIndicator.setCurrentItem(searchResults.get(pos).pageListPosition);
                        finish();
                    }
                });

                holder.value = (TextView)convertView.findViewById(R.id.value);
                holder.value.setTag(position);
                holder.value.setOnClickListener(new MyOnClickListener(holder) {
                    @Override
                    public void onClick(View v, ViewHolder holder) {
                        int pos = (Integer) holder.value.getTag();
                        PaymentTabFragment.setSelectedPageNumber(searchResults.get(pos).pageListPosition);
                        PaymentTabFragment.topTabIndicator.notifyDataSetChanged();
                        PaymentTabFragment.topTabIndicator.setCurrentItem(searchResults.get(pos).pageListPosition);
                        finish();
                    }
                });

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
                holder.title.setTag(position);
                holder.value.setTag(position);
            }

            holder.title.setText(((searchResults.get(position)).title).toString());
            holder.value.setText(((searchResults.get(position)).value).toString());

            return convertView;
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
