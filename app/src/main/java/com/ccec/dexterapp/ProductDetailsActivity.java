package com.ccec.dexterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ProductDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Bundle bundle = getIntent().getExtras();
        String regNo = bundle.getString("REG_NO");
        Toast.makeText(this, regNo, Toast.LENGTH_SHORT).show();
    }
}
