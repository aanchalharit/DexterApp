package com.ccec.dexterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pkmmte.view.CircularImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddVehicle extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText etmake, etmodel, etmanufacturedin, etregnumber, etchessisnumber, etkilometer, etAvgkilometer;
    private TextInputLayout tilmake, tilmodel, tilmanufacturedin, tilregnumber, tilchessisnumber, tilkilometer, tilAvgkilometer;
    private Button btnpollutionchkdate, btnnextpollutionchkdate, btninsurancepurchasedate, btninsuranceduedate, btnaddvehicle;
    private DatabaseReference firebasedbref;
    private int year, month, day;
    public DatePickerDialog mDatePickerDialog;
    private int clickedbuttonid;
    private UserSessionManager session;
    private String id;
    private RelativeLayout r1, r2;
    private String carMake, carModel, carManufacturedIn, carchessisnumber, carRegnumber, carKilometer, carAvgKilometer, carPollutionchkdate, carInsurancePurchasedate;
    private CircularImageView circularImageView, circularImageView2;

    private static int REQUEST_CAMERA = 0;
    private static int SELECT_FILE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    private String picturePath, source = "";
    private byte[] imageData;
    private AlertDialog dialog;
    private Bitmap thumbnail;
    private boolean gotReference = false;
    private String key;

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

        firebasedbref = FirebaseDatabase.getInstance().getReference().child("items/Car");

//        r1 = (RelativeLayout) findViewById(R.id.rel1);
//        r2 = (RelativeLayout) findViewById(R.id.rel2);

        etmake = (EditText) findViewById(R.id.editcarMake);
        etmodel = (EditText) findViewById(R.id.editTextcarModel);
        etmanufacturedin = (EditText) findViewById(R.id.editTextcarManufacturedIn);
        etregnumber = (EditText) findViewById(R.id.editTextcarRegNo);
        etchessisnumber = (EditText) findViewById(R.id.editTextcarChessisNo);
        etkilometer = (EditText) findViewById(R.id.editTextcarKilometer);
        etAvgkilometer = (EditText) findViewById(R.id.editTextcarAvgKilometer);

        etmake.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etmodel.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etmanufacturedin.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etregnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etchessisnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etkilometer.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        etAvgkilometer.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        tilmake = (TextInputLayout) findViewById(R.id.TILcarmake);
        tilmodel = (TextInputLayout) findViewById(R.id.TILcarmodel);
        tilmanufacturedin = (TextInputLayout) findViewById(R.id.TILcarmanufacturedin);
        tilregnumber = (TextInputLayout) findViewById(R.id.TILcarRegNo);
        tilchessisnumber = (TextInputLayout) findViewById(R.id.TILcarChessisNo);
        tilkilometer = (TextInputLayout) findViewById(R.id.TILcarKilometer);
        tilAvgkilometer = (TextInputLayout) findViewById(R.id.TILcarAvgKilometer);

        tilmake.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilmodel.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilmanufacturedin.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilregnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilchessisnumber.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilkilometer.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        tilAvgkilometer.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        btnpollutionchkdate = (Button) findViewById(R.id.btn_pollutioncheckdate);
//        btnnextpollutionchkdate = (Button) findViewById(R.id.btn_nextpollutioncheckdate);
        btninsurancepurchasedate = (Button) findViewById(R.id.btn_insurancepurchasedate);
//        btninsuranceduedate = (Button) findViewById(R.id.btn_insuranceduedate);
        btnaddvehicle = (Button) findViewById(R.id.btn_carSave);

        btnpollutionchkdate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
//        btnnextpollutionchkdate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        btninsurancepurchasedate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
//        btninsuranceduedate.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        btnaddvehicle.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));

        circularImageView = (CircularImageView) findViewById(R.id.circularProduct);
        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnpollutionchkdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedbuttonid = v.getId();
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd;
                dpd = DatePickerDialog.newInstance(
                        AddVehicle.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                Calendar calendar = Calendar.getInstance();
                dpd.setMaxDate(calendar);
                dpd.show(getFragmentManager(), "PollutionCheckDatepickerdialog");
            }
        });

        btninsurancepurchasedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedbuttonid = v.getId();
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd1 = DatePickerDialog.newInstance(
                        AddVehicle.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                Calendar calendar = Calendar.getInstance();
                dpd1.setMaxDate(calendar);
                dpd1.show(getFragmentManager(), "InsurancePurchasepickerdialog");
            }
        });

        btnaddvehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carMake = etmake.getText().toString().trim();
                carModel = etmodel.getText().toString().trim();
                carManufacturedIn = etmanufacturedin.getText().toString().trim();
                carRegnumber = etregnumber.getText().toString().trim();
                carchessisnumber = etchessisnumber.getText().toString().trim();
                carKilometer = etkilometer.getText().toString().trim();
                carAvgKilometer = etAvgkilometer.getText().toString().trim();

                carPollutionchkdate = btnpollutionchkdate.getText().toString().trim();
//                String carNextPollutionchkdate = btnnextpollutionchkdate.getText().toString().trim();
                carInsurancePurchasedate = btninsurancepurchasedate.getText().toString().trim();
//                String carInsuranceDuedate = btninsuranceduedate.getText().toString().trim();

                if (isNetwork() && validate()) {
                    Vehicle vehicle = new Vehicle();

                    vehicle.setMake(carMake);
                    vehicle.setModel(carModel);
                    vehicle.setManufacturedin(carManufacturedIn);
                    vehicle.setRegistrationnumber(carRegnumber);
                    vehicle.setChessisnumber(carchessisnumber);
                    vehicle.setKilometer(carKilometer);
                    vehicle.setAvgrunning(carAvgKilometer);

                    vehicle.setPolluctionchkdate(carPollutionchkdate);
                    vehicle.setNextpolluctionchkdate("");
                    vehicle.setInsurancepurchasedate(carInsurancePurchasedate);
                    vehicle.setInsuranceduedate("");

                    if (gotReference == false)
                        key = firebasedbref.push().getKey();

                    firebasedbref.child(key).setValue(vehicle);

                    firebasedbref = FirebaseDatabase.getInstance().getReference().child("users/Customer/" + id + "/items/Car");
                    firebasedbref.push().setValue(key);

                    Intent intent = new Intent(AddVehicle.this, HomePage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AddVehicle.this);
        builder.setTitle("Update Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");

                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        Bitmap bm = BitmapFactory.decodeFile(picturePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        imageData = baos.toByteArray();

                        source = "gallery";
                        confirmUpload();
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(AddVehicle.this, "Out Of Memory", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(AddVehicle.this, "Some Error", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        try {
            imageData = bytes.toByteArray();

            source = "camera";
            confirmUpload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmUpload() {
        LayoutInflater inflater = getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.dialog_pic, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddVehicle.this);
        builder.setCancelable(true);
        builder.setView(dialoglayout);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        circularImageView2 = (CircularImageView) dialoglayout.findViewById(R.id.circularImage);
        if (source.equals("camera"))
            circularImageView2.setImageBitmap(thumbnail);
        else if (source.equals("gallery"))
            circularImageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        final ProgressBar progressBar = (ProgressBar) dialoglayout.findViewById(R.id.progressBar);

        final TextView text3 = (TextView) dialoglayout.findViewById(R.id.headerText3);
        text3.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        final LinearLayout linearLayout = (LinearLayout) dialoglayout.findViewById(R.id.lin);

        final Button ok = (Button) dialoglayout.findViewById(R.id.yes);
        ok.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        final Button cancel = (Button) dialoglayout.findViewById(R.id.no);
        cancel.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text3.setText("Updating..");

                key = firebasedbref.push().getKey();
                gotReference = true;

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                StorageReference imageRef = storageRef.child("items/cars/" + key + ".jpg");
                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AddVehicle.this, "Image updated", Toast.LENGTH_SHORT).show();

                        if (source.equals("camera"))
                            circularImageView.setImageBitmap(thumbnail);
                        else if (source.equals("gallery"))
                            circularImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                    }
                });

                ok.setEnabled(false);
                cancel.setEnabled(false);

                ok.setAlpha((float) 0.7);
                cancel.setAlpha((float) 0.7);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean validate() {
        boolean valid = true;

        if (carMake.isEmpty()) {
            etmake.setError("Enter this field");
            valid = false;
        } else {
            etmake.setError(null);
        }

        if (carModel.isEmpty()) {
            etmodel.setError("Enter this field");
            valid = false;
        } else {
            etmodel.setError(null);
        }

        if (carManufacturedIn.isEmpty()) {
            etmanufacturedin.setError("Enter this field");
            valid = false;
        } else {
            etmanufacturedin.setError(null);
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int temp = 0;
        try {
            temp = Integer.parseInt(carManufacturedIn);
            if (temp < 1960 || temp > year) {
                etmanufacturedin.setError("Invalid year");
                valid = false;
            } else {
                etmanufacturedin.setError(null);
            }
        } catch (NumberFormatException e) {

        }

        if (carRegnumber.isEmpty()) {
            etregnumber.setError("Enter this field");
            valid = false;
        } else {
            etregnumber.setError(null);
        }

        if (carchessisnumber.isEmpty()) {
            etchessisnumber.setError("Enter this field");
            valid = false;
        } else {
            etchessisnumber.setError(null);
        }

        if (carKilometer.isEmpty()) {
            etkilometer.setError("Enter this field");
            valid = false;
        } else {
            etkilometer.setError(null);
        }

        if (carAvgKilometer.isEmpty()) {
            etAvgkilometer.setError("Enter this field");
            valid = false;
        } else {
            etAvgkilometer.setError(null);
        }

        if (carPollutionchkdate.equals("Pollution Check Date")) {
            Toast.makeText(this, "Please select pollution check date", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (carInsurancePurchasedate.equals("Insurance Purchase Date")) {
            Toast.makeText(this, "Please select insurance purchase date", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
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
//            r1.setVisibility(View.VISIBLE);
//            btnnextpollutionchkdate.setText(shownextdate);
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
//            r2.setVisibility(View.VISIBLE);
//            btninsuranceduedate.setText(shownextduedate);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddVehicle.this);
        builder.setTitle("Cancel");
        builder.setMessage("Are you sure you want to cancel adding product?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                NavUtils.navigateUpFromSameTask(AddVehicle.this);
                AddVehicle.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

