package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.maps.ShowCentresNearMe;
import com.ccec.dexterapp.recyclers.DeletedProductsViewAdapter;
import com.ccec.dexterapp.recyclers.ProductsViewAdapter;
import com.ccec.dexterapp.recyclers.QueryViewAdapter;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeletedProductsFragment extends Fragment {
    private RecyclerView ProductsRV, QueriesRV;
    private DatabaseReference firebasedbrefproducts;
    private List<Vehicle> allproducts;
    private UserSessionManager session;
    private String id, name;
    public List<String> carkeysarray;
    public List<String> queriesArr;
    private DeletedProductsViewAdapter adapter;
    private RelativeLayout rel;
    private ProgressDialog pDialog;
    private HashMap<String, Object> itemMap;
    private FloatingActionButton viewF, raise, history;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
        name = user.get(UserSessionManager.TAG_fullname);
        carkeysarray = new ArrayList<>();
        queriesArr = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ProductsRV = (RecyclerView) view.findViewById(R.id.allproducts);
        ProductsRV.setHasFixedSize(true);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getProducts();

        viewF = (FloatingActionButton) view.findViewById(R.id.productView);
        history = (FloatingActionButton) view.findViewById(R.id.productHistory);
        raise = (FloatingActionButton) view.findViewById(R.id.productRaise);

        viewF.hide();
        history.hide();
        raise.hide();

        rel = (RelativeLayout) view.findViewById(R.id.noOrders);

        return view;
    }

    public void getProducts() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("users/Customer/" + id + "/items/deletedCars");
        firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    ProductsRV.setVisibility(View.VISIBLE);
                    rel.setVisibility(View.GONE);

                    itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for (Object value : itemMap.values()) {
                        carkeysarray.add(value.toString());
                    }

                    getProdDetails();
                } else {
                    ProductsRV.setVisibility(View.GONE);
                    rel.setVisibility(View.VISIBLE);
                    pDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        firebasedbrefproducts.keepSynced(true);
    }

    private void getProdDetails() {
        allproducts = new ArrayList<Vehicle>();

        for (int carkey = 0; carkey <= carkeysarray.size(); carkey++) {
            String dbcarkey = "";
            if (carkey != carkeysarray.size())
                dbcarkey = carkeysarray.get(carkey);

            firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("items/Car/" + dbcarkey);
            final int finalCarkey = carkey;
            firebasedbrefproducts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot1) {
                    if (finalCarkey == carkeysarray.size())
                        setRecycler();

                    if (finalCarkey != carkeysarray.size()) {
                        Vehicle vehicle = dataSnapshot1.getValue(Vehicle.class);
                        allproducts.add(vehicle);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    pDialog.dismiss();
                    return;
                }
            });
        }
    }

    private void setRecycler() {
        adapter = new DeletedProductsViewAdapter(getActivity(), allproducts, carkeysarray);
        ProductsRV.setAdapter(adapter);

        pDialog.dismiss();
    }
}