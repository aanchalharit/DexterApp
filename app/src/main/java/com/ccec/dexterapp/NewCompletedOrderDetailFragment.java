package com.ccec.dexterapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccec.dexterapp.entities.FlowRecord;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.AttachmentHoriViewAdapter;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.recyclers.ProcessFlowViewAdapter;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCompletedOrderDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, contact, name, company;
    private TextView locationD, contactD, nameD, companyD, locMoreD;
    private Map<String, Object> itemMap;
    private LatLng sydney;
    private LinearLayout scheduledLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String currentStatus = "";
    private RecyclerView recyclerView2;
    private LinearLayout lrecyclerView2;
    private CardView cardAttachList, cardProcessList, cardPrice;
    private View view1;
    private TextView esPriceF, esPrice;
    private String estPrice;

    public NewCompletedOrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(AppData.currentReq.getKey());
        }

        estPrice = AppData.currentReq.getEstPrice();
        currentStatus = AppData.currentReq.getStatus();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("geofire");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.getLocation(AppData.currentReq.getIssuedTo(), new LocationCallback() {
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

        name = (TextView) view.findViewById(R.id.fullNameTitle);
        location = (TextView) view.findViewById(R.id.skypeNameTitle);
        company = (TextView) view.findViewById(R.id.companyNameTitle);
        contact = (TextView) view.findViewById(R.id.companyNameTitle1);

        nameD = (TextView) view.findViewById(R.id.fullNameDetail);
        locationD = (TextView) view.findViewById(R.id.skypeNameDetail);
        companyD = (TextView) view.findViewById(R.id.companyNameDetail);
        contactD = (TextView) view.findViewById(R.id.companyNameDetail1);

        TextView aFlowtv = (TextView) view.findViewById(R.id.aFlow);
        aFlowtv.setTypeface(FontsManager.getBoldTypeface(getActivity()));

        name.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        location.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        company.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        contact.setTypeface(FontsManager.getBoldTypeface(getActivity()));

        nameD.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        locationD.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        companyD.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        contactD.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        locMoreD = (TextView) view.findViewById(R.id.fullNameMore);
        SpannableString content = new SpannableString(locMoreD.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        locMoreD.setText(content);
        locMoreD.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        locMoreD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + sydney.latitude + ", " + sydney.longitude));
                startActivity(intent);
            }
        });

        scheduledLayout = (LinearLayout) view.findViewById(R.id.linProf11111);
        if (currentStatus.equals("Open"))
            scheduledLayout.setVisibility(View.GONE);
        else if (currentStatus.equals("Accepted") ||
                currentStatus.equals("Completed")) {
            scheduledLayout.setVisibility(View.VISIBLE);
            if (currentStatus.equals("Completed"))
                contact.setText("Processed on :");
            else
                contact.setText("Scheduled on :");

            view1 = view.findViewById(R.id.viewne);
            view1.setVisibility(View.VISIBLE);

            String datee = AppData.currentReq.getScheduleTime();
            contactD.setText(datee);
        }

        if (currentStatus.equals("Completed")) {
            CardView cardView = (CardView) view.findViewById(R.id.card_view5);
            cardView.setVisibility(View.GONE);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/ServiceCenter/" + AppData.currentReq.getIssuedTo());
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

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("items/Car/" + AppData.currentReq.getItem());
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

        companyD.setText(AppData.currentReq.getOpenTime());

        if (!currentStatus.equals("Open")) {
            recyclerView = (RecyclerView) view.findViewById(R.id.processList);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);

            databaseReference = FirebaseDatabase.getInstance().getReference("/processFlow/" + AppData.currentReq.getKey());
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
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            databaseReference.keepSynced(true);
        } else {
            cardProcessList = (CardView) view.findViewById(R.id.card_view4);
            cardProcessList.setVisibility(View.GONE);
        }

        if (!currentStatus.equals("Completed")) {
            recyclerView2 = (RecyclerView) view.findViewById(R.id.attachList);
            lrecyclerView2 = (LinearLayout) view.findViewById(R.id.attach_steps);
            cardAttachList = (CardView) view.findViewById(R.id.card_view5);
            showAttachmentsCard();
        }

        cardPrice = (CardView) view.findViewById(R.id.card_view6);
        esPrice = (TextView) view.findViewById(R.id.estPrice);
        esPriceF = (TextView) view.findViewById(R.id.estPriceFlow);
        esPriceF.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        esPrice.setTypeface(FontsManager.getRegularTypeface(getActivity()));
        if (!estPrice.equals("")) {
            showPriceCard();
        } else
            cardPrice.setVisibility(View.GONE);

        return view;
    }

    private void showPriceCard() {
        cardPrice.setVisibility(View.VISIBLE);
        esPrice.setText("\u20B9 " + estPrice);
    }

    private void showAttachmentsCard() {
        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + AppData.currentReq.getKey());
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    try {
                        Map<String, String> itemMap = (HashMap<String, String>) dataSnapshot.getValue();
                        ArrayList<String> itemMap2 = new ArrayList<String>(itemMap.values());

                        if (itemMap2.size() > 0) {
                            AttachmentHoriViewAdapter adapter = new AttachmentHoriViewAdapter(itemMap2, getActivity());
                            recyclerView2.setAdapter(adapter);
                        } else {
                            lrecyclerView2.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        lrecyclerView2.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else
                    cardAttachList.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference2.keepSynced(true);
    }
}
