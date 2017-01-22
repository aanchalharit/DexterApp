package com.ccec.dexterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VehicleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, contact, name, skype, company, avgkilo, kilo, poll, nxtPoll, insu, nxtIns;
    private TextView locationD, contactD, nameD, companyD, skypeD, avgkiloD, kiloD, pollD, nxtPollD, insuD, nxtInsD;
    private Vehicle veh;
    private ImageView im1, im2, im3, im4, im5, im6, im7, im8, im9;

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
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete product");
                builder.setMessage("Are you sure you want to delete this?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog pDialog = new ProgressDialog(getActivity());
                        pDialog.setMessage("Deleting...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();

                        pDialog.dismiss();
                        dialog.dismiss();

                        getActivity().finish();
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
        });

        im3 = (ImageView) view.findViewById(R.id.edit3);
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
