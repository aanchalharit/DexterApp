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

import com.ccec.dexterapp.entities.Notif;
import com.ccec.dexterapp.entities.Requests;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
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

public class ServicesFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private UserSessionManager session;
    private String id, loc;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference databaseReference;

    private RelativeLayout errorSec;
    private ImageView erImg;
    private TextView erTxt;
    private ProgressDialog pDialog;
    private ServicesViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        if (!isNetwork()) {
            ((HomePage) getActivity()).showHelperNoConnection();
        } else {
            session = new UserSessionManager(getActivity());
            HashMap<String, String> user = session.getUserDetails();
            loc = user.get(UserSessionManager.TAG_location);
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

            databaseReference = FirebaseDatabase.getInstance().getReference("/requests/Car");
            Query query = databaseReference.orderByChild("issuedBy").equalTo(id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        errorSec.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);

                        Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        List<String> list = new ArrayList<>(itemMap.keySet());

                        recyclerViewAdapter = new ServicesViewAdapter(getActivity(), itemMap, list, ServicesFragment.this);
                        recyclerView.setAdapter(recyclerViewAdapter);
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

        return view;
    }

    public void showInfo() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Accept scheduled date?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final ProgressDialog pDialog2 = new ProgressDialog(getActivity());
                pDialog2.setMessage("Accepting..");
                pDialog2.setIndeterminate(false);
                pDialog2.setCancelable(false);
                pDialog2.show();

                DatabaseReference firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("processFlow/" + (String) ((HashMap) AppData.currentMap).get("key"));
                firebasedbrefproducts.push().setValue("Schedule date accepted by customer", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        DatabaseReference firebasedbref2 = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + (String) ((HashMap) AppData.currentMap).get("key"));
                        firebasedbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Requests requests = dataSnapshot.getValue(Requests.class);
                                requests.setScheduleTime("");

                                DatabaseReference firebasedbref2 = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + (String) ((HashMap) AppData.currentMap).get("key"));
                                firebasedbref2.setValue(requests, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        pDialog2.dismiss();
                                        dialog.dismiss();

                                        Toast.makeText(getActivity(), "Date Accepted", Toast.LENGTH_SHORT).show();
                                        HomeFragment profileFragment = new HomeFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, profileFragment).commit();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void rejectOrder() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to reject the scheduled date?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final ProgressDialog pDialog2 = new ProgressDialog(getActivity());
                pDialog2.setMessage("Rejecting..");
                pDialog2.setIndeterminate(false);
                pDialog2.setCancelable(false);
                pDialog2.show();

                DatabaseReference firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("processFlow/" + (String) ((HashMap) AppData.currentMap).get("key"));
                firebasedbrefproducts.push().setValue("Schedule date rejected by customer", new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        DatabaseReference firebasedbref2 = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + (String) ((HashMap) AppData.currentMap).get("key"));
                        firebasedbref2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Requests requests = dataSnapshot.getValue(Requests.class);
                                requests.setStatus("Open");
                                requests.setScheduleTime("");

                                DatabaseReference firebasedbref2 = FirebaseDatabase.getInstance().getReference().child("requests/" + AppData.serviceType + "/" + (String) ((HashMap) AppData.currentMap).get("key"));
                                firebasedbref2.setValue(requests, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        pDialog2.dismiss();
                                        dialog.dismiss();

                                        Toast.makeText(getActivity(), "Date Rejected", Toast.LENGTH_SHORT).show();
                                        HomeFragment profileFragment = new HomeFragment();
                                        getActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.fragment_container, profileFragment).commit();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
