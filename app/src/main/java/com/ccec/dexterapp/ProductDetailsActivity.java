package com.ccec.dexterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ccec.dexterapp.entities.Requests;
import com.ccec.dexterapp.entities.Vehicle;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button btnServiceRequest;
    private DatabaseReference firebasedbrefproducts;
    public String kilometer;
    private DatabaseReference firebasedbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Bundle bundle = getIntent().getExtras();
        final String regNo = bundle.getString("REG_NO");

        Toast.makeText(this, regNo, Toast.LENGTH_SHORT).show();

        btnServiceRequest = (Button) findViewById(R.id.btn_carServiceRequest);
        btnServiceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //temporary send request
//                firebasedbref = FirebaseDatabase.getInstance().getReference("/variables/serviceNumber");
//                firebasedbref.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        int serviceNumber = dataSnapshot.getValue(Integer.class);
//
//                        Requests add = new Requests();
//
//                        add.setIssuedTo("M9dsAfxziQQuQVsZXD0j0Qc4hDX2");
//                        add.setIssuedBy("euishcijfdjfbkldv83d");
//                        add.setStatus("open");
//                        add.setOpenTime("23/11/2016");
//                        add.setScheduleTime("23/11/2016");
//                        add.setKey("DexterSR" + serviceNumber);
//                        add.setItem("-KWw6GTDfMvKtOIN_H7l");
//
//                        firebasedbref.setValue(serviceNumber + 1);
//
//                        firebasedbref = FirebaseDatabase.getInstance().getReference("/requests/Car");
//                        firebasedbref.child("DexterSR" + serviceNumber).setValue(add);
//
//                        firebasedbref = FirebaseDatabase.getInstance().getReference("/notifs");
//                        Map notification = new HashMap<>();
//                        notification.put("username", "dMJTDYsHUBg:APA91bEGWjGWvSRaLR7NUjHk3C2Y44HJGEU6WeWfCVjFV_4jVQtXUOzkeGiwUPmj7YyeYMnfpa1YjG6NUnOD3OHSM5a2kSPwlVZOt1W72iJMSiIAxdcQGeR");
//                        notification.put("message", "helloo");
//                        firebasedbref.push().setValue(notification);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                });

                Intent intent = new Intent(ProductDetailsActivity.this, ServiceCenterAroundMapsActivity.class);
                startActivity(intent);
            }
        });

        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("items/Car");

        firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Vehicle vehiclesers = snapshot.getValue(Vehicle.class);

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
    }
}
