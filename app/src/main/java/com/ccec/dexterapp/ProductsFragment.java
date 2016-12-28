package com.ccec.dexterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.maps.ShowCentresNearMe;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsFragment extends Fragment {
    private FloatingActionButton productFab, viewF, edit, raise, delete;
    private RecyclerView ProductsRV;
    private DatabaseReference firebasedbrefproducts;
    private List<Vehicle> allproducts;
    private UserSessionManager session;
    private String id, name;
    public List<String> carkeysarray;
    private ProductsViewAdapter adapter;
    private SwitchCompat switchCompat, switchCompat2, switchCompat3, switchCompat4;
    private boolean sw1 = false, sw2 = false, sw3 = false, sw4 = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
        name = user.get(UserSessionManager.TAG_fullname);
        carkeysarray = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ProductsRV = (RecyclerView) view.findViewById(R.id.allproducts);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getProducts();

        viewF = (FloatingActionButton) view.findViewById(R.id.productView);
        delete = (FloatingActionButton) view.findViewById(R.id.productDelete);
        edit = (FloatingActionButton) view.findViewById(R.id.productEdit);
        raise = (FloatingActionButton) view.findViewById(R.id.productRaise);
        hideLinFab();

        productFab = (FloatingActionButton) view.findViewById(R.id.productAddFab);
        productFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddVehicle.class);
                startActivity(intent);
            }
        });

        viewF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getProductDetails();
                hideLinFab();
                showAddFab();
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRequestData();
                hideLinFab();
                showAddFab();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MapView mv = new MapView(getActivity());
                            mv.onCreate(null);
                            mv.onPause();
                            mv.onDestroy();
                        } catch (Exception ignored) {

                        }
                    }
                }).start();
            }
        });

        return view;
    }

    public void getProducts() {
        allproducts = new ArrayList<Vehicle>();

        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("users/Customer/" + id + "/items/Car");
        firebasedbrefproducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                for (Object value : itemMap.values()) {
                    carkeysarray.add(value.toString());
                }

                for (int carkey = 0; carkey < carkeysarray.size(); carkey++) {
                    String dbcarkey = carkeysarray.get(carkey);
                    firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("items/Car/" + dbcarkey);
                    firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            Vehicle vehicle = dataSnapshot1.getValue(Vehicle.class);
                            allproducts.add(vehicle);
                            adapter = new ProductsViewAdapter(getActivity(), allproducts, carkeysarray, ProductsFragment.this);
                            ProductsRV.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        firebasedbrefproducts.keepSynced(true);
    }

    public void showAddFab() {
        productFab.show();
    }

    public void hideAddFab() {
        productFab.hide();
    }

    public void showLinFab() {
        AppData.fabVisible = true;
        viewF.show();
        edit.show();
        delete.show();
        raise.show();
    }

    public void hideLinFab() {
        AppData.fabVisible = false;
        viewF.hide();
        edit.hide();
        delete.hide();
        raise.hide();
    }

    private void showRequestData() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_dialog_reqeuest, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(dialoglayout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final TextView rpmm = (TextView) dialoglayout.findViewById(R.id.fullNameTitle);
        rpmm.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView throttle = (TextView) dialoglayout.findViewById(R.id.skypeNameTitle);
        throttle.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView temp = (TextView) dialoglayout.findViewById(R.id.companyNameTitle);
        temp.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView airflow = (TextView) dialoglayout.findViewById(R.id.contactNameTitle);
        airflow.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        switchCompat = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton1);
        switchCompat2 = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton2);
        switchCompat3 = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton3);
        switchCompat4 = (SwitchCompat) dialoglayout.findViewById(R.id.switchButton4);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw1 = isChecked;
            }
        });

        switchCompat2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw2 = isChecked;
            }
        });

        switchCompat3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw3 = isChecked;
            }
        });

        switchCompat4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sw4 = isChecked;
            }
        });

        final Button cancel = (Button) dialoglayout.findViewById(R.id.cancelButton);
        cancel.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Button submit = (Button) dialoglayout.findViewById(R.id.submitButton);
        submit.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent in = new Intent(getActivity(), ShowCentresNearMe.class);
                getActivity().startActivity(in);
            }
        });
    }
}