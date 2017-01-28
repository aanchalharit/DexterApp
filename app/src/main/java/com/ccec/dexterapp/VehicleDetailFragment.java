package com.ccec.dexterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class VehicleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, contact, name, skype, company, avgkilo, kilo, poll, nxtPoll, insu, nxtIns;
    private TextView locationD, contactD, nameD, companyD, skypeD, avgkiloD, kiloD, pollD, nxtPollD, insuD, nxtInsD;
    private Vehicle veh;
    private ImageView im1, im2, im3, im4, im5, im6, im7, im8, im9;
    private UserSessionManager session;
    private String id;

    public VehicleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            veh = AppData.currentVehi;

            appBarLayout.setTitle(FontsManager.actionBarTypeface(activity, veh.getMake() + " " + veh.getModel()));
        }

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(UserSessionManager.TAG_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail, container, false);

        name = (TextView) view.findViewById(R.id.fullNameTitle);
        company = (TextView) view.findViewById(R.id.companyNameTitle);
        location = (TextView) view.findViewById(R.id.locationNameTitle);
        contact = (TextView) view.findViewById(R.id.contactNameTitle);
        skype = (TextView) view.findViewById(R.id.skypeNameTitle);
        kilo = (TextView) view.findViewById(R.id.KilometerNameTitle);
        avgkilo = (TextView) view.findViewById(R.id.AvgKilometerNameTitle);
        poll = (TextView) view.findViewById(R.id.PollutionNameTitle);
//        nxtPoll = (TextView) view.findViewById(R.id.nxtPollutionNameTitle);
        insu = (TextView) view.findViewById(R.id.InsuranceNameTitle);
//        nxtIns = (TextView) view.findViewById(R.id.dueInsuranceNameTitle);

        nameD = (TextView) view.findViewById(R.id.fullNameDetail);
        companyD = (TextView) view.findViewById(R.id.companyNameDetail);
        locationD = (TextView) view.findViewById(R.id.locationNameDetail);
        contactD = (TextView) view.findViewById(R.id.contactNameDetail);
        skypeD = (TextView) view.findViewById(R.id.skypeNameDetail);
        kiloD = (TextView) view.findViewById(R.id.KilometerNameDetail);
        avgkiloD = (TextView) view.findViewById(R.id.AvgKilometerNameDetail);
        pollD = (TextView) view.findViewById(R.id.PollutionNameDetail);
//        nxtPollD = (TextView) view.findViewById(R.id.nxtPollutionNameDetail);
        insuD = (TextView) view.findViewById(R.id.InsuranceNameDetail);
//        nxtInsD = (TextView) view.findViewById(R.id.dueInsuranceNameDetail);

        name.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contact.setTypeface(FontsManager.getRegularTypeface(getContext()));
        location.setTypeface(FontsManager.getRegularTypeface(getContext()));
        company.setTypeface(FontsManager.getRegularTypeface(getContext()));
        skype.setTypeface(FontsManager.getRegularTypeface(getContext()));
        kilo.setTypeface(FontsManager.getRegularTypeface(getContext()));
        avgkilo.setTypeface(FontsManager.getRegularTypeface(getContext()));
        poll.setTypeface(FontsManager.getRegularTypeface(getContext()));
//        nxtPoll.setTypeface(FontsManager.getRegularTypeface(getContext()));
        insu.setTypeface(FontsManager.getRegularTypeface(getContext()));
//        nxtIns.setTypeface(FontsManager.getRegularTypeface(getContext()));

        nameD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contactD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        locationD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        companyD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        skypeD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        kiloD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        avgkiloD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        pollD.setTypeface(FontsManager.getRegularTypeface(getContext()));
//        nxtPollD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        insuD.setTypeface(FontsManager.getRegularTypeface(getContext()));
//        nxtInsD.setTypeface(FontsManager.getRegularTypeface(getContext()));

        nameD.setText(veh.getMake());
        skypeD.setText(veh.getModel());
        companyD.setText(veh.getManufacturedin());
        contactD.setText(veh.getRegistrationnumber());
        locationD.setText(veh.getChessisnumber());
        kiloD.setText(veh.getKilometer());
        avgkiloD.setText(veh.getAvgrunning());
        pollD.setText(veh.getPolluctionchkdate());
//        nxtPollD.setText(veh.getNextpolluctionchkdate());
        insuD.setText(veh.getInsurancepurchasedate());
//        nxtInsD.setText(veh.getInsuranceduedate());

        Button delBtn = (Button) view.findViewById(R.id.delButton);
        delBtn.setTypeface(FontsManager.getBoldTypeface(getContext()));
        if (AppData.isProductDeleted)
            delBtn.setText("Revive Product");
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppData.isProductDeleted) {
                    final ProgressDialog pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage("Reviving...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();

                    final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users/Customer/" + id + "/items/deletedCars/");
                    databaseReference2.orderByValue().equalTo(AppData.currentImagePath).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();

                                final DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("users/Customer/" + id + "/items/Car/");
                                databaseReference3.push().setValue((itemMap.values().toArray())[0]);
                                databaseReference2.child((String) (itemMap.keySet().toArray())[0]).removeValue();
                                pDialog.dismiss();

                                Toast.makeText(getActivity(), "Product Revived", Toast.LENGTH_SHORT).show();

                                getActivity().finish();

                                AppData.isProductDeleted = false;
                                Intent i = new Intent(getActivity(), HomePage.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Delete product");
                    builder.setMessage("Are you sure you want to delete this?");
                    builder.setCancelable(false);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            final ProgressDialog pDialog = new ProgressDialog(getActivity());
                            pDialog.setMessage("Deleting...");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(false);
                            pDialog.show();

                            final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users/Customer/" + id + "/items/Car/");
                            databaseReference2.orderByValue().equalTo(AppData.currentImagePath).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();

                                        final DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("users/Customer/" + id + "/items/deletedCars/");
                                        databaseReference3.push().setValue((itemMap.values().toArray())[0]);
                                        databaseReference2.child((String) (itemMap.keySet().toArray())[0]).removeValue();
                                        pDialog.dismiss();
                                        dialog.dismiss();

                                        Toast.makeText(getActivity(), "Product Deleted", Toast.LENGTH_SHORT).show();

                                        getActivity().finish();

                                        AppData.isProductDeleted = true;
                                        Intent i = new Intent(getActivity(), HomePage.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        getActivity().startActivity(i);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        im3 = (ImageView) view.findViewById(R.id.edit3);
        im4 = (ImageView) view.findViewById(R.id.edit4);
        im5 = (ImageView) view.findViewById(R.id.edit5);
        im6 = (ImageView) view.findViewById(R.id.edit6);
        im7 = (ImageView) view.findViewById(R.id.edit7);
        im8 = (ImageView) view.findViewById(R.id.edit8);
        im9 = (ImageView) view.findViewById(R.id.edit9);

        if (AppData.isProductDeleted) {
            im3.setVisibility(View.INVISIBLE);
            im4.setVisibility(View.INVISIBLE);
            im5.setVisibility(View.INVISIBLE);
            im6.setVisibility(View.INVISIBLE);
            im7.setVisibility(View.INVISIBLE);
            im8.setVisibility(View.INVISIBLE);
            im9.setVisibility(View.INVISIBLE);
        }

        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.custom_edit_item, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setView(dialoglayout);
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText textView = (EditText) dialoglayout.findViewById(R.id.newDetail);
                Button btnView = (Button) dialoglayout.findViewById(R.id.submitButton);
                btnView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (textView.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Please provide a valid value", Toast.LENGTH_SHORT).show();
                        else if (isNetwork()) {
                            dialog.dismiss();
                            final ProgressDialog pDialog = new ProgressDialog(getActivity());
                            pDialog.setMessage("Updating..");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(false);
                            pDialog.show();
                            final DatabaseReference firebasedbref = FirebaseDatabase.getInstance().getReference("items/" + AppData.serviceType + "/" + AppData.currentImagePath);
                            firebasedbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        Vehicle v = dataSnapshot.getValue(Vehicle.class);
                                        v.setManufacturedin(textView.getText().toString());
                                        final DatabaseReference firebasedbref = FirebaseDatabase.getInstance().getReference();
                                        firebasedbref.child("items/" + AppData.serviceType + "/" + AppData.currentImagePath).setValue(v);
                                        pDialog.dismiss();
                                        Toast.makeText(getActivity(), "Value updated", Toast.LENGTH_SHORT).show();
                                        companyD.setText(textView.getText().toString());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
