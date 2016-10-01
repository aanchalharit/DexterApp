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

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";

    private int Page;
    private String Title;
    private FloatingActionButton productFab;
    private RecyclerView ProductsRV;
    private DatabaseReference firebasedbrefperson;
    private List<person> allperson;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ProductsRV = (RecyclerView) view.findViewById(R.id.allproducts);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        allperson = new ArrayList<person>();
        firebasedbrefperson = FirebaseDatabase.getInstance().getReference().child("Person");

        firebasedbrefperson.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllperson(dataSnapshot);
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

    private void getAllperson(DataSnapshot dataSnapshot) {
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            person Person  = singleSnapshot.getValue(person.class);
            allperson.add(Person);
        }
        personrvViewAdapter adapter = new personrvViewAdapter(getActivity(), allperson);
        ProductsRV.setAdapter(adapter);
   }


    }





