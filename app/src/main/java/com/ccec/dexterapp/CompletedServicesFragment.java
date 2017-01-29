package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.entities.FlowRecord;
import com.ccec.dexterapp.entities.Requests;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.recyclers.PastServicesViewAdapter;
import com.ccec.dexterapp.recyclers.ServicesViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompletedServicesFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);

        recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        errorSec = (RelativeLayout) view.findViewById(R.id.errorSec);

        erTxt = (TextView) view.findViewById(R.id.errorHeader);
        erImg = (ImageView) view.findViewById(R.id.errorImage);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("/requests/CompletedCar");
        Query query = databaseReference.orderByChild("issuedBy").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    errorSec.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);

                    Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < itemMap.keySet().size(); i++) {
                        String temp = (String) itemMap.keySet().toArray()[i];
                        list.add(temp);
                    }

                    Collections.sort(list, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });

                    Collections.reverse(list);

                    if (list.size() > 0) {
                        recyclerViewAdapter = new PastServicesViewAdapter(getActivity(), itemMap, list);
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

        return view;
    }
}
