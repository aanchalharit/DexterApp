package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class ProductsFragment extends Fragment {
    private FloatingActionButton productFab, viewF, raise, history;
    private RecyclerView ProductsRV, QueriesRV;
    private DatabaseReference firebasedbrefproducts;
    private List<Vehicle> allproducts;
    private UserSessionManager session;
    private String id, name;
    public List<String> carkeysarray;
    public List<String> queriesArr;
    private ProductsViewAdapter adapter;
    private QueryViewAdapter adapterQ;
    private ProgressBar prBar;
    private RelativeLayout rel;
    private ProgressDialog pDialog;
    private HashMap<String, Object> itemMap;
    private boolean swiper = false;
    private SwipeRefreshLayout mySwipeRefreshLayout;

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

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        getProducts();

        viewF = (FloatingActionButton) view.findViewById(R.id.productView);
        history = (FloatingActionButton) view.findViewById(R.id.productHistory);
        raise = (FloatingActionButton) view.findViewById(R.id.productRaise);
        hideLinFab();

        rel = (RelativeLayout) view.findViewById(R.id.noOrders);

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

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PastServices.class);
                startActivity(intent);
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

        if (AppData.raiseRequest == true) {
            AppData.raiseRequest = false;
            showRequestData();

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

        return view;
    }

    public void getProducts() {
        carkeysarray = new ArrayList<>();
        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("users/Customer/" + id + "/items/Car");
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
                    if (swiper == false) {
                        pDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        firebasedbrefproducts.keepSynced(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshClouds);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swiper = true;
                        getProducts();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mySwipeRefreshLayout.setRefreshing(false);
                            }
                        }, 2000);
                    }
                }
        );
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
                    if (swiper == false) {
                        pDialog.dismiss();
                    }
                    return;
                }
            });
        }
    }

    private void setRecycler() {
        adapter = new ProductsViewAdapter(getActivity(), allproducts, carkeysarray, ProductsFragment.this);
        ProductsRV.setAdapter(adapter);

        if (swiper == false) {
            pDialog.dismiss();
        }
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
        history.show();
        raise.show();
    }

    public void hideLinFab() {
        AppData.fabVisible = false;
        viewF.hide();
        history.hide();
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

        prBar = (ProgressBar) dialoglayout.findViewById(R.id.progBar);

        QueriesRV = (RecyclerView) dialoglayout.findViewById(R.id.allQueries);
        QueriesRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

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
                dialog.dismiss();
            }
        });

        DatabaseReference firebasedbrefproducts3 = FirebaseDatabase.getInstance().getReference().child("queries");
        firebasedbrefproducts3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    queriesArr = (ArrayList) dataSnapshot.getValue();
                    prBar.setVisibility(View.GONE);
                    QueriesRV.setVisibility(View.VISIBLE);

                    adapterQ = new QueryViewAdapter(getActivity(), queriesArr, ProductsFragment.this);
                    QueriesRV.setAdapter(adapterQ);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        firebasedbrefproducts3.keepSynced(true);
    }
}