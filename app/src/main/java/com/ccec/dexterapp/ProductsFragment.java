package com.ccec.dexterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.UserSessionManager;
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
    private Vehicle VehicleDetails;
    private UserSessionManager session;
    private String id;
    public List<String> carkeysarray;
    private ProductsViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
        carkeysarray = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ProductsRV = (RecyclerView) view.findViewById(R.id.allproducts);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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
        return view;
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
}





