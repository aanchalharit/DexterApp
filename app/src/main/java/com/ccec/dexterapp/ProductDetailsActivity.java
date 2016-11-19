package com.ccec.dexterapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button btnServiceRequest;
    private DatabaseReference firebasedbrefproducts;
    public String kilometer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Bundle bundle = getIntent().getExtras();
        final String regNo = bundle.getString("REG_NO");



       Toast.makeText(this, regNo, Toast.LENGTH_SHORT).show();

        btnServiceRequest = (Button)findViewById(R.id.btn_carServiceRequest);
        btnServiceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailsActivity.this,ServiceCenterAroundMapsActivity.class);
                startActivity(intent);
            }
        });

        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("items/Car");

        firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    Vehicle vehiclesers  = snapshot.getValue(Vehicle.class);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = firebasedbrefproducts.orderByChild("registrationnumber").equalTo(regNo);

        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);
                kilometer = vehicle.getKilometer();
                Toast.makeText(getApplicationContext(), kilometer, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

   //     Toast.makeText(this, kilometer, Toast.LENGTH_SHORT).show();

    }


}
