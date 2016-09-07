package com.ccec.dexterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ProductAddActivity_Vehicle extends BaseActivity {

    private EditText editTextName;
    private EditText editTextAddress;
    private TextView textViewPersons;
    private Button buttonSave;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);

        textViewPersons = (TextView) findViewById(R.id.textViewPersons);

        Firebase.setAndroidContext(this);

        Firebase ref = new Firebase(config.FIREBASE_URL);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creating firebase object
                Firebase ref = new Firebase(config.FIREBASE_URL);
                //Getting values to store
                String name = editTextName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();

                //Creating Person object
                person person = new person();

                //Adding values
                person.setName(name);
                person.setAddress(address);

                //Storing values to firebase
                ref.child("Person").setValue(person);
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //Getting the data from snapshot
                    person person = postSnapshot.getValue(com.ccec.dexterapp.person.class);

                    //Adding it to a string
                    String string = "Name: "+person.getName()+"\nAddress: "+person.getAddress()+"\n\n";

                    //Displaying it on textview
                    textViewPersons.setText(string);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
