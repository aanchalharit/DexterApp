package com.ccec.dexterapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccec.dexterapp.entities.FlowRecord;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.recyclers.ProcessFlowViewAdapter;
import com.ccec.dexterapp.recyclers.ServicesViewAdapter;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewOrderDetailFragment extends Fragment implements OnMapReadyCallback {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, contact, name, company;
    private TextView locationD, contactD, nameD, companyD;
    private Object obj;
    private LinearLayout lin;
    private Map<String, Object> itemMap;
    private GoogleMap mMap;
    private LatLng sydney;
    private ImageView navImg;
    private LinearLayout scheduledLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    public NewOrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            obj = AppData.currentVeh;

            appBarLayout.setTitle((String) ((HashMap) obj).get("key"));
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.getLocation(((String) ((HashMap) obj).get("issuedTo")), new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {
                    sydney = new LatLng(location.latitude, location.longitude);
                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("There was an error getting the GeoFire location: " + databaseError);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.neworder_detail, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        navImg = (ImageView) view.findViewById(R.id.navigate);
        navImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + sydney.latitude + ", " + sydney.longitude));
                startActivity(intent);
            }
        });

        name = (TextView) view.findViewById(R.id.fullNameTitle);
        location = (TextView) view.findViewById(R.id.skypeNameTitle);
        company = (TextView) view.findViewById(R.id.companyNameTitle);
        contact = (TextView) view.findViewById(R.id.contactNameTitle);

        nameD = (TextView) view.findViewById(R.id.fullNameDetail);
        locationD = (TextView) view.findViewById(R.id.skypeNameDetail);
        companyD = (TextView) view.findViewById(R.id.companyNameDetail);
        contactD = (TextView) view.findViewById(R.id.contactNameDetail);

        scheduledLayout = (LinearLayout) view.findViewById(R.id.linProf111);
        if (((String) ((HashMap) obj).get("status")).equals("Open"))
            scheduledLayout.setVisibility(View.GONE);
        else if (((String) ((HashMap) obj).get("status")).equals("Accepted") ||
                ((String) ((HashMap) obj).get("status")).equals("Completed")) {
            scheduledLayout.setVisibility(View.VISIBLE);
            if (((String) ((HashMap) obj).get("status")).equals("Completed"))
                contact.setText("Processed on :");
            else
                contact.setText("Scheduled on :");

            String datee = (String) ((HashMap) obj).get("scheduleTime");
            contactD.setText(datee);
        }

        lin = (LinearLayout) view.findViewById(R.id.linProf11);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/ServiceCenter/" + (String) ((HashMap) AppData.currentVeh).get("issuedTo"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                nameD.setText(((String) itemMap.get("name")) + "\n"
                        + ((String) itemMap.get("contact")) + "\n"
                        + ((String) itemMap.get("location")));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("items/Car/" + (String) ((HashMap) obj).get("item"));
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                locationD.setText(((String) ((HashMap) itemMap).get("make") + " " + (String) ((HashMap) itemMap).get("model")));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference2.keepSynced(true);

        companyD.setText((String) ((HashMap) obj).get("openTime"));

        recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference("/processFlow/" + (String) ((HashMap) obj).get("key"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    List<FlowRecord> list = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        try {
                            Map<String, Object> itemMap = (HashMap<String, Object>) postSnapshot.getValue();
                            FlowRecord f = new FlowRecord();
                            f.setTitle((String) itemMap.get("title"));
                            f.setTimestamp((String) itemMap.get("timestamp"));
                            list.add(f);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ProcessFlowViewAdapter recyclerViewAdapter = new ProcessFlowViewAdapter(getActivity(), list);
                    recyclerView.setAdapter(recyclerViewAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        databaseReference.keepSynced(true);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (sydney != null) {
            mMap.addMarker(new MarkerOptions().position(sydney).title(("Service Center's location"))).showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14));
        }
    }
}
