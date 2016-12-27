package com.ccec.dexterapp.maps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.HomePage;
import com.ccec.dexterapp.R;
import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowCentresNearMe extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    UserSessionManager session;
    private LocationManager mLocationManager;
    private Marker myMarker;
    private Location location;
    private ImageView img;
    private TextView searchLoc;
    private EditText enterLoc;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String result = null, id;
    private FloatingActionButton fab;
    private String source = "normal", make;
    private Place place;
    DatabaseReference mFirebaseDatabase;
    private String name, path, makes;
    private Vehicle veh;
    private CircularImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centres_near_me);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Select Service Center"));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        veh = AppData.currentVeh;
        make = veh.getMake();
        path = AppData.currentImagePath;

        if (!isNetwork()) {
            Toast.makeText(ShowCentresNearMe.this, "Please connect to internet", Toast.LENGTH_LONG).show();
            ShowCentresNearMe.this.finish();
        }

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);

        img = (ImageView) findViewById(R.id.imageView);
//        enterLoc = (EditText) findViewById(R.id.input_location);
//        searchLoc = (TextView) findViewById(R.id.textSearch);
//        enterLoc.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
//        searchLoc.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        mapFragment.getMapAsync(this);

//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AppData.selectedLoc = enterLoc.getText().toString();
//                AppData.selectedCordLoc = location;
//                ShowCentresNearMe.this.finish();
//            }
//        });
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ShowCentresNearMe.this);
            dialog.setMessage("Location not enabled");
            dialog.setPositiveButton("Enable location", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    ShowCentresNearMe.this.finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } else {
            Toast.makeText(ShowCentresNearMe.this, "Getting location..", Toast.LENGTH_SHORT).show();
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(ShowCentresNearMe.this, "Make sure location is on.", Toast.LENGTH_LONG).show();

            return;
        }

        String provider = mLocationManager.getBestProvider(new Criteria(), true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

        if (gps_enabled != false) {
            location = mLocationManager
                    .getLastKnownLocation(provider);
            if (location != null) {
                updateMyLocation(googleMap, location);
            }
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                source = "normal";
                if (ActivityCompat.checkSelfPermission(ShowCentresNearMe.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ShowCentresNearMe.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String provider = mLocationManager.getBestProvider(new Criteria(), true);
                location = mLocationManager
                        .getLastKnownLocation(provider);

                updateMyLocation(googleMap, location);
            }
        });
    }

    private void updateMyLocation(final GoogleMap googleMap, Location location) {
        this.location = location;

        LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());

        if (myMarker == null)
            myMarker = mMap.addMarker(new MarkerOptions().position(myLoc).
                    title("My location")
//                    .snippet("address:588 , sector 45 , gurgaon")
                    .icon(getMarkerIcon("#8b3e58")));
        else {
            myMarker.setPosition(myLoc);
            myMarker.showInfoWindow();
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("geofire");
        GeoFire geoFire = new GeoFire(mFirebaseDatabase);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 5.0);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users/ServiceCenter/" + key);
                mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            name = (String) itemMap.get("name");
                            makes = (String) itemMap.get("makes");

                            if (makes.toLowerCase().contains(make.toLowerCase())) {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).
                                        title(name));
                                marker.setTag((String) key);
                                marker.showInfoWindow();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mFirebaseDatabase.keepSynced(true);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 14));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (!marker.getTitle().equals("My location")) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialoglayout = inflater.inflate(R.layout.custom_show_detail, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ShowCentresNearMe.this);
                    builder.setCancelable(true);
                    builder.setView(dialoglayout);
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    final TextView rpmm = (TextView) dialoglayout.findViewById(R.id.headerTextCloudDetail);
                    rpmm.setTypeface(FontsManager.getBoldTypeface(ShowCentresNearMe.this));

                    final TextView throttle = (TextView) dialoglayout.findViewById(R.id.headerTextCloudOS);
                    throttle.setTypeface(FontsManager.getRegularTypeface(ShowCentresNearMe.this));

                    final TextView temp = (TextView) dialoglayout.findViewById(R.id.headerTextCloudRam);
                    temp.setTypeface(FontsManager.getRegularTypeface(ShowCentresNearMe.this));

                    ImageView mageView = (ImageView) dialoglayout.findViewById(R.id.crossButton);
                    mageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    circularImageView = (CircularImageView) dialoglayout.findViewById(R.id.circularImageCloudDetail);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                    storageRef.child("profilePics/" + marker.getTag() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.with(getApplicationContext()).load(uri).noPlaceholder().into(circularImageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            storageRef.child("profilePics/" + marker.getTag() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(getApplicationContext()).load(uri).noPlaceholder().into(circularImageView);
                                }
                            });
                        }
                    });

                    mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users/ServiceCenter/" + marker.getTag());
                    mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                rpmm.setText((String) itemMap.get("name"));
                                throttle.setText("Website: " + (String) itemMap.get("website"));
                                temp.setText("Location: " + (String) itemMap.get("location"));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mFirebaseDatabase.keepSynced(true);

                    final Button submit = (Button) dialoglayout.findViewById(R.id.submitButton);
                    submit.setTypeface(FontsManager.getBoldTypeface(ShowCentresNearMe.this));
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

                return false;
            }
        });
    }

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}