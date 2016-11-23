package com.ccec.dexterapp;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ProductAddActivity_Vehicle extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText etmake, etmodel, etmanufacturedin, etregnumber, etchessisnumber, etkilometer;
    private TextInputLayout tilmake, tilmodel, tilmanufacturedin, tilregnumber, tilchessisnumber, tilkilometer;
    private Button btnpollutionchkdate, btnnextpollutionchkdate, btninsurancepurchasedate, btninsuranceduedate, btnaddvehicle;
    private DatabaseReference firebasedbref;
    private int year, month, day;
    public DatePickerDialog mDatePickerDialog;
    private int clickedbuttonid;
    private UserSessionManager session;
    private String id;
    private RelativeLayout r1, r2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_vehicleadd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Add Vehicle"));

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);

        r1 = (RelativeLayout) findViewById(R.id.rel1);
        r2 = (RelativeLayout) findViewById(R.id.rel2);

        etmake = (EditText) findViewById(R.id.editcarMake);
        etmodel = (EditText) findViewById(R.id.editTextcarModel);
        etmanufacturedin = (EditText) findViewById(R.id.editTextcarManufacturedIn);
        etregnumber = (EditText) findViewById(R.id.editTextcarRegNo);
        etchessisnumber = (EditText) findViewById(R.id.editTextcarChessisNo);
        etkilometer = (EditText) findViewById(R.id.editTextcarKilometer);

        etmake.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etmodel.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etmanufacturedin.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etregnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etchessisnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etkilometer.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        tilmake = (TextInputLayout) findViewById(R.id.TILcarmake);
        tilmodel = (TextInputLayout) findViewById(R.id.TILcarmodel);
        tilmanufacturedin = (TextInputLayout) findViewById(R.id.TILcarmanufacturedin);
        tilregnumber = (TextInputLayout) findViewById(R.id.TILcarRegNo);
        tilchessisnumber = (TextInputLayout) findViewById(R.id.TILcarChessisNo);
        tilkilometer = (TextInputLayout) findViewById(R.id.TILcarKilometer);

        tilmake.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilmodel.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilmanufacturedin.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilregnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilchessisnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilkilometer.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        btnpollutionchkdate = (Button) findViewById(R.id.btn_pollutioncheckdate);
        btnnextpollutionchkdate = (Button) findViewById(R.id.btn_nextpollutioncheckdate);
        btninsurancepurchasedate = (Button) findViewById(R.id.btn_insurancepurchasedate);
        btninsuranceduedate = (Button) findViewById(R.id.btn_insuranceduedate);
        btnaddvehicle = (Button) findViewById(R.id.btn_carSave);

        btnpollutionchkdate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        btnnextpollutionchkdate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        btninsurancepurchasedate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        btninsuranceduedate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        btnaddvehicle.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));

        btnpollutionchkdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedbuttonid = v.getId();
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd;
                dpd = DatePickerDialog.newInstance(
                        ProductAddActivity_Vehicle.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "PollutionCheckDatepickerdialog");
            }
        });

        btninsurancepurchasedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedbuttonid = v.getId();
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd1 = DatePickerDialog.newInstance(
                        ProductAddActivity_Vehicle.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd1.show(getFragmentManager(), "InsurancePurchasepickerdialog");
            }
        });

        btnaddvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating firebase object
                firebasedbref = FirebaseDatabase.getInstance().getReference().child("items/Car");
                //Getting values to store
                String carMake = etmake.getText().toString().trim();
                String carModel = etmodel.getText().toString().trim();
                String carManufacturedIn = etmanufacturedin.getText().toString().trim();
                String carRegnumber = etregnumber.getText().toString().trim();
                String carchessisnumber = etchessisnumber.getText().toString().trim();
                String carKilometer = etkilometer.getText().toString().trim();

                String carPollutionchkdate = btnpollutionchkdate.getText().toString().trim();
                String carNextPollutionchkdate = btnnextpollutionchkdate.getText().toString().trim();
                String carInsurancePurchasedate = btninsurancepurchasedate.getText().toString().trim();
                String carInsuranceDuedate = btninsuranceduedate.getText().toString().trim();

                //Creating vehicle object
                Vehicle vehicle = new Vehicle();

                //Adding values
                vehicle.setMake(carMake);
                vehicle.setModel(carModel);
                vehicle.setManufacturedin(carManufacturedIn);
                vehicle.setRegistrationnumber(carRegnumber);
                vehicle.setChessisnumber(carchessisnumber);
                vehicle.setKilometer(carKilometer);

                vehicle.setPolluctionchkdate(carPollutionchkdate);
                vehicle.setNextpolluctionchkdate(carNextPollutionchkdate);
                vehicle.setInsurancepurchasedate(carInsurancePurchasedate);
                vehicle.setInsuranceduedate(carInsuranceDuedate);

                //Storing values to firebase
                String key = firebasedbref.push().getKey();
                firebasedbref.child(key).setValue(vehicle);

                firebasedbref = FirebaseDatabase.getInstance().getReference().child("users/Customer/" + id + "/items/Car");
                firebasedbref.push().setValue(key);

                Intent intent = new Intent(ProductAddActivity_Vehicle.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd =
                (DatePickerDialog) getFragmentManager().findFragmentByTag("PollutionCheckDatepickerdialog");

        DatePickerDialog dpd1 =
                (DatePickerDialog) getFragmentManager().findFragmentByTag("InsurancePurchasepickerdialog");

        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet
            (DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (clickedbuttonid == R.id.btn_pollutioncheckdate) {
            String date1 = dayOfMonth + "-" + (++monthOfYear) + "-" + year;
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
            Date selecteddate = null;
            try {
                selecteddate = dateformat.parse(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calender = Calendar.getInstance();
            calender.setTime(selecteddate);
            calender.add(Calendar.MONTH, 3);
            calender.add(Calendar.DATE, -1);
            Date nextdate = calender.getTime();
            String shownextdate = dateformat.format(nextdate);

            btnpollutionchkdate.setText(date1);
            r1.setVisibility(View.VISIBLE);
            btnnextpollutionchkdate.setText(shownextdate);
        }

        if (clickedbuttonid == R.id.btn_insurancepurchasedate) {
            String date2 = dayOfMonth + "-" + (++monthOfYear) + "-" + year;
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
            Date selecteddate = null;
            try {
                selecteddate = dateformat.parse(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calender = Calendar.getInstance();
            calender.setTime(selecteddate);
            calender.add(Calendar.YEAR, 1);
            calender.add(Calendar.DATE, -1);
            Date insurancenextdate = calender.getTime();
            String shownextduedate = dateformat.format(insurancenextdate);
            btninsurancepurchasedate.setText(date2);
            r2.setVisibility(View.VISIBLE);
            btninsuranceduedate.setText(shownextduedate);
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

