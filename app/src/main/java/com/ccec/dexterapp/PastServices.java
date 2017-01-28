package com.ccec.dexterapp;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.recyclers.PastServicesViewAdapter;
import com.ccec.dexterapp.recyclers.ServicesViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastServices extends AppCompatActivity {
    private UserSessionManager session;
    private String id;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;

    private RelativeLayout errorSec;
    private ImageView erImg;
    private TextView erTxt;
    private ProgressDialog pDialog;
    private PastServicesViewAdapter recyclerViewAdapter;

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

        pDialog = new ProgressDialog(PastServices.this);
        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("/requests/Car");
        Query query = databaseReference.orderByChild("issuedBy").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    errorSec.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    Map<String, Object> copyMap = new HashMap<String, Object>(itemMap);
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < itemMap.keySet().size(); i++) {
                        String temp = (String) itemMap.keySet().toArray()[i];
                        Map<String, Object> requestMap = (HashMap<String, Object>) itemMap.get(temp);
                        if (((String) requestMap.get("status")).equals("Completed"))
                            if (((String) requestMap.get("item")).equals(AppData.currentImagePath))
                                list.add(temp);
                            else
                                copyMap.remove(temp);
                        else
                            copyMap.remove(temp);
                    }

                    if (list.size() > 0) {
                        recyclerViewAdapter = new PastServicesViewAdapter(PastServices.this, copyMap, list);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    } else
                        errorSec.setVisibility(View.VISIBLE);
                } else {
                    errorSec.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                pDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pDialog.dismiss();
            }
        });
        databaseReference.keepSynced(true);
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
