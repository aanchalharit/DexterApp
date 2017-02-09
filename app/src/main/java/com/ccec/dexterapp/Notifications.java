package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.ccec.dexterapp.entities.ModelNoti;
import com.ccec.dexterapp.managers.JSONArrayParser;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.recyclers.NotiViewAdapter;
import com.ccec.dexterapp.recyclers.ServicesViewAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Notifications extends AppCompatActivity {
    UserSessionManager session;
    private String id;
    private ArrayList<ModelNoti> items;
    private RelativeLayout errorSec;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(UserSessionManager.TAG_id);
        items = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(Notifications.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        errorSec = (RelativeLayout) findViewById(R.id.errorSec);

        new GetData().execute();
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

    class GetData extends AsyncTask<String, String, String> {
        private static final String url = "http://188.166.245.67/html/phpscript/getdata.php";
        private static final String TAG_DATA = "data";
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Notifications.this);
            pDialog.setMessage("Updating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            JSONArrayParser jsonObjectParser = new JSONArrayParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));

            JSONArray response = jsonObjectParser.makeHttpRequest(url, "GET", params);

            try {
                for (int i = 0; i < response.length(); i++) {
                    ModelNoti item = new ModelNoti();
                    JSONObject noti = response.getJSONObject(i);

                    item.setDate(noti.getString("title").toString());
                    item.setMessage(noti.getString("date").toString());

                    items.add(item);
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
                    NotiViewAdapter recyclerViewAdapter = new NotiViewAdapter(items, Notifications.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else
                    errorSec.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
