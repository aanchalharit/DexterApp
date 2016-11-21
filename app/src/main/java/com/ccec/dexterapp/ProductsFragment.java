package com.ccec.dexterapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterapp.managers.UserSessionManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";

    private int Page;
    private String Title;
    private FloatingActionButton productFab;
    private RecyclerView ProductsRV;
    private DatabaseReference firebasedbrefproducts;
    private List<Vehicle> allproducts;
    private Vehicle VehicleDetails;
    private UserSessionManager session;
    private String id;
    public List<String> carkeysarray;


    public static ProductsFragment newInstance(int page,String title )
    {
        ProductsFragment productsfragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putInt("mypageint1", page);
        args.putString("mypagetitle1", title);
        productsfragment.setArguments(args);
        return productsfragment;
}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Page = getArguments().getInt("mypageint1",0);
        Title = getArguments().getString("mypagetitle1");
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
        carkeysarray = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ProductsRV = (RecyclerView) view.findViewById(R.id.allproducts);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

//        ProductsRV.OnItemTouchListener(new productsrvClickListener(getActivity(),new productsrvClickListener.OnItemClickListener(){
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
//                intent.putExtra(ProductDetailsActivity.ID, Contact.CONTACTS[position].getId());
//
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        // the context of the activity
//                        MainActivity.this,
//
//                        // For each shared element, add to this method a new Pair item,
//                        // which contains the reference of the view we are transitioning *from*,
//                        // and the value of the transitionName attribute
//                        new Pair<View, String>(view.findViewById(R.id.CONTACT_circle),
//                                getString(R.string.transition_name_circle)),
//                        new Pair<View, String>(view.findViewById(R.id.CONTACT_name),
//                                getString(R.string.transition_name_name)),
//                        new Pair<View, String>(view.findViewById(R.id.CONTACT_phone),
//                                getString(R.string.transition_name_phone))
//                );
//                ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
//            }
//        }));

        allproducts = new ArrayList<Vehicle>();

        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("users/Customer/" +id +"/items/Car");

//        firebasedbrefproducts.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
////                    Vehicle vehicle = singleSnapshot.getValue(Vehicle.class);
//
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
              for (Object value : itemMap.values()) {
                    carkeysarray.add(value.toString());
                }

                for (int carkey = 0 ; carkey <carkeysarray.size() ; carkey++)
                {
                    String dbcarkey = carkeysarray.get(carkey);
                    firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("items/Car/" +dbcarkey);
                    firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            Vehicle vehicle  = dataSnapshot1.getValue(Vehicle.class);
                            allproducts.add(vehicle);
                            productsrvViewAdapter adapter = new productsrvViewAdapter(getActivity(), allproducts);
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



//        firebasedbrefperson.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                getAllperson(dataSnapshot);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                getAllperson(dataSnapshot);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


        final BottomSheetDialogFragment productbottomsheet = ProductAddBottomsheetFragment.newInstance("Add Products");

        productFab = (FloatingActionButton) view.findViewById(R.id.productAddFab);
        productFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productbottomsheet.show(getFragmentManager(),productbottomsheet.getTag());

                //Intent intent = new Intent(getActivity(),ProductAddActivity_Vehicle.class);
                //startActivity(intent);
            }
        });
        return view;
    }

    private void getAllProducts(DataSnapshot dataSnapshot) {
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Vehicle vehicle  = singleSnapshot.getValue(Vehicle.class);
            allproducts.add(vehicle);
        }
        productsrvViewAdapter adapter = new productsrvViewAdapter(getActivity(), allproducts);
        ProductsRV.setAdapter(adapter);
   }


    }





