package com.ccec.dexterapp;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProductAddActivity_Vehicle extends BaseActivity {

    private EditText editTextName;
    private EditText editTextAddress;
    private TextInputLayout tilname;
    private TextInputLayout tiladdress;

    private Button buttonSave;

    private DatabaseReference firebasedbrefper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        tilname = (TextInputLayout) findViewById(R.id.TILname);
        tiladdress = (TextInputLayout) findViewById(R.id.TILaddress);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creating firebase object
                firebasedbrefper = FirebaseDatabase.getInstance().getReference().child("Car");
                //Getting values to store
                String name = editTextName.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();

                //Creating Person object
                person person = new person();

                //Adding values
                person.setName(name);
                person.setAddress(address);

                //Storing values to firebase
                firebasedbrefper.push().setValue(person);

                editTextName.setText("");
                editTextAddress.setText("");
                Intent intent = new Intent(ProductAddActivity_Vehicle.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
