package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccec.dexterapp.entities.Requests;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.JSONArrayParser;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.recyclers.PastServicesViewAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PastServices extends AppCompatActivity {
    private UserSessionManager session;
    private String id;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout errorSec;
    private TextView erTxt;
    private List<Requests> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_services);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Completed Services"));

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);

        recyclerView = (RecyclerView) findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        errorSec = (RelativeLayout) findViewById(R.id.errorSec);

        erTxt = (TextView) findViewById(R.id.errorHeader);
        erTxt.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        ImageView erImgTxt = (ImageView) findViewById(R.id.errorImage);

        items = new ArrayList<>();

        if (isNetwork())
            new GetData().execute();
        else {
            erImgTxt.setImageDrawable(getResources().getDrawable(R.drawable.icon_no_connection));
            errorSec.setVisibility(View.VISIBLE);
            erTxt.setText("Please connect to internet");
        }
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    class GetData extends AsyncTask<String, String, String> {
        private static final String url = "http://188.166.245.67/html/phpscript/getCompletedList.php";
        private static final String TAG_DATA = "data";
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PastServices.this);
            pDialog.setMessage("Updating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            JSONArrayParser jsonObjectParser = new JSONArrayParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));

            JSONArray response = jsonObjectParser.makeHttpRequest(url, "GET", params);

            try {
                for (int i = 0; i < response.length(); i++) {
                    Requests item = new Requests();
                    JSONObject noti = response.getJSONObject(i);

                    if (noti.getString("item").toString().equals(AppData.currentImagePath)) {
                        item.setEstPrice(noti.getString("estPrice").toString());
                        item.setIssuedBy(noti.getString("issuedBy").toString());
                        item.setIssuedTo(noti.getString("issuedTo").toString());
                        item.setItem(noti.getString("item").toString());
                        item.setKey(noti.getString("keyCar").toString());
                        item.setOpenTime(noti.getString("openTime").toString());
                        item.setQueries(noti.getString("queries").toString());
                        item.setScheduleTime(noti.getString("scheduleTime").toString());
                        item.setStatus(noti.getString("status").toString());

                        items.add(item);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                pDialog.dismiss();
                if (items.size() > 0) {
                    Collections.sort(items, new Comparator<Requests>() {
                        public int compare(Requests emp1, Requests emp2) {
                            return emp2.getKey().compareToIgnoreCase(emp1.getKey());
                        }
                    });

                    PastServicesViewAdapter recyclerViewAdapter = new PastServicesViewAdapter(PastServices.this, items);
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    errorSec.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
