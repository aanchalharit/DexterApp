package com.ccec.dexterapp.maps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.HomePage;
import com.ccec.dexterapp.R;
import com.ccec.dexterapp.entities.Notif;
import com.ccec.dexterapp.entities.Requests;
import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.recyclers.AttachmentsViewAdapter;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.ui.IconGenerator;
import com.mancj.slideup.SlideUp;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.ccec.dexterapp.R.id.map;

public class ShowCentresNearMe extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    UserSessionManager session;
    private LocationManager mLocationManager;
    private Marker myMarker;
    private Location location;
    private ImageView img;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 20;
    private String result = null, id;
    private FloatingActionButton fab;
    private byte[] imageData;
    private Bitmap thumbnail;
    private String source = "normal", make;
    private Place place;
    private String picturePath;
    DatabaseReference mFirebaseDatabase;
    private String name, path, makes;
    private Vehicle veh;
    private Location myLOC;
    private float dis;
    private String selectedCenter;
    private ProgressBar progressBar;
    private CircularImageView circularImageView2, circularImageView;
    private ProgressDialog pDialog;
    private Integer serviceNumber;
    private SlideUp slideUp;
    private TextView txt1, txt2, txt3, txt4, txt11;
    private MediaRecorder recorder = null;
    private String AudioSavePathInDevice = null;
    private String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private Random random;
    private ImageButton im1, im2, im3;
    private android.app.AlertDialog dialogRecordAudio;
    private ArrayList<String> attachmentsKeys, attachments;
    private ListView listView;
    private AttachmentsViewAdapter adapter;
    private static int REQUEST_CAMERA = 0;
    private static int SELECT_FILE = 1;
    private static int RESULT_LOAD_IMAGE = 1;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centres_near_me);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(FontsManager.actionBarTypeface(getApplicationContext(), "Select Service Center"));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        session = new UserSessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(UserSessionManager.TAG_id);

        View slideView = findViewById(R.id.slideView);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        TranslateAnimation slide = new TranslateAnimation(0, 0, 100, 0);
        slide.setDuration(1000);
        slide.setFillAfter(true);
        fab.startAnimation(slide);

        slide = new TranslateAnimation(0, 0, -100, 0);
        slide.setDuration(1000);
        slide.setFillAfter(true);
        fab.startAnimation(slide);

        random = new Random();

        txt1 = (TextView) findViewById(R.id.textView);
        txt11 = (TextView) findViewById(R.id.textView2);
        txt11.setVisibility(View.GONE);
        txt2 = (TextView) findViewById(R.id.txt2);
        txt3 = (TextView) findViewById(R.id.txt3);
        txt4 = (TextView) findViewById(R.id.txt4);

        txt1.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        txt11.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        txt2.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        txt3.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));
        txt4.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        listView = (ListView) findViewById(R.id.mobile_list);

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + id);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    count = 0;
                    txt11.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);

                    attachments = new ArrayList<String>();
                    attachmentsKeys = new ArrayList<String>();

                    Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                        attachmentsKeys.add(count, entry.getKey());
                        attachments.add(count, (String) entry.getValue());
                        count++;
                    }

                    adapter = new AttachmentsViewAdapter(ShowCentresNearMe.this, R.layout.layout_listview, attachments, attachmentsKeys);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    txt11.setVisibility(View.GONE);
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference2.keepSynced(true);

        SlideUp.Listener slideUpListener = new SlideUp.Listener() {
            @Override
            public void onSlide(float percent) {
                txt1.setAlpha(1 - (percent / 100));
            }

            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == View.GONE) {
                    fab.show();
                }
            }
        };

        fab.hide();
        slideUp = SlideUp.Builder.forView(slideView)
                .withListeners(slideUpListener)
                .withDownToUpVector(true)
                .withLoggingEnabled(true)
                .withStartState(SlideUp.State.SHOWED)
                .build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp.show();
                fab.hide();
            }
        });

        veh = AppData.currentVehi;
        make = veh.getMake();
        path = AppData.currentImagePath;

        if (!isNetwork()) {
            Toast.makeText(ShowCentresNearMe.this, "Please connect to internet", Toast.LENGTH_LONG).show();
            ShowCentresNearMe.this.finish();
        }

        img = (ImageView) findViewById(R.id.imageView);
        im1 = (ImageButton) findViewById(R.id.im1);
        im2 = (ImageButton) findViewById(R.id.im2);
        im3 = (ImageButton) findViewById(R.id.im3);

        im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                myMarker = null;
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(ShowCentresNearMe.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    slideUp.hide();
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < 5)
                    selectAudio();
                else
                    Toast.makeText(ShowCentresNearMe.this, "A maximum of 5 attachments can be added", Toast.LENGTH_SHORT).show();
            }
        });

        im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < 5)
                    selectImage();
                else
                    Toast.makeText(ShowCentresNearMe.this, "A maximum of 5 attachments can be added", Toast.LENGTH_SHORT).show();
            }
        });

        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLace", "Placeeeeeeeeeee: " + place.getName());
            }

            @Override
            public void onError(Status status) {
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowCentresNearMe.this);
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

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);

                final LatLng latLong;
                latLong = place.getLatLng();
                Location loc = new Location("Sample");
                loc.setLatitude(latLong.latitude);
                loc.setLongitude(latLong.longitude);
                updateMyLocation(mMap, loc);
            }
        } else if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String result = null;
                if (uri.getScheme().equals("content")) {
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                }
                if (result == null) {
                    result = uri.getPath();
                    int cut = result.lastIndexOf('/');
                    if (cut != -1) {
                        result = result.substring(cut + 1);
                    }
                }

                if (!result.contains("3gp")) {
                    if (!result.contains("amr"))
                        Toast.makeText(this, "Only 3gp and amr format allowed", Toast.LENGTH_LONG).show();
                } else {
                    pDialog = new ProgressDialog(ShowCentresNearMe.this);
                    pDialog.setMessage("Attaching..");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                    StorageReference riversRef = storageRef.child("attachments/" + result);
                    UploadTask uploadTask = riversRef.putFile(uri);

                    final String finalFileName = result;
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ShowCentresNearMe.this, "File attached", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();

                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + id);
                            databaseReference2.push().setValue(finalFileName);
                        }
                    });
                }
            }
        } else if (resultCode == Activity.RESULT_OK) {
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
                        Toast.makeText(getApplicationContext(), "Out Of Memory", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_SHORT).show();
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
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowCentresNearMe.this);
        builder.setCancelable(true);
        builder.setView(dialoglayout);
        final android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        circularImageView2 = (CircularImageView) dialoglayout.findViewById(R.id.circularImage);
        if (source.equals("camera"))
            circularImageView2.setImageBitmap(thumbnail);
        else if (source.equals("gallery"))
            circularImageView2.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        progressBar = (ProgressBar) dialoglayout.findViewById(R.id.progressBar);

        final TextView text3 = (TextView) dialoglayout.findViewById(R.id.headerText3);
        text3.setTypeface(FontsManager.getRegularTypeface(getApplicationContext()));

        final Button ok = (Button) dialoglayout.findViewById(R.id.yes);
        ok.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
        final Button cancel = (Button) dialoglayout.findViewById(R.id.no);
        cancel.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text3.setText("Attaching..");
                final String finalFileName = CreateRandomAudioFileName(5) + "Image.jpg";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                StorageReference imageRef = storageRef.child("attachments/" + finalFileName);
                UploadTask uploadTask = imageRef.putBytes(imageData);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + id);
                        databaseReference2.push().setValue(finalFileName);
                        Toast.makeText(getApplicationContext(), "Image attached", Toast.LENGTH_SHORT).show();
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

    private void selectAudio() {
        final CharSequence[] items = {"Record Audio", "Choose from device",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowCentresNearMe.this);
        builder.setTitle("Add Audio");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Record Audio")) {
                    dialog.dismiss();

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    final File file = new File(AudioSavePathInDevice);
                    try {
                        file.createNewFile();

                        LayoutInflater inflater = getLayoutInflater();
                        final View dialoglayout = inflater.inflate(R.layout.custom_dialog_record, null);
                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowCentresNearMe.this);
                        builder.setCancelable(true);
                        builder.setView(dialoglayout);
                        dialogRecordAudio = builder.create();
                        dialogRecordAudio.setCancelable(false);
                        dialogRecordAudio.show();

                        final TextView recordText = (TextView) dialoglayout.findViewById(R.id.recordTitle);
                        recordText.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));

                        ImageView mageView = (ImageView) dialoglayout.findViewById(R.id.crossButton);
                        mageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogRecordAudio.dismiss();
                            }
                        });

                        final Button attach = (Button) dialoglayout.findViewById(R.id.attachButton);
                        attach.setTypeface(FontsManager.getBoldTypeface(getApplicationContext()));
                        attach.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogRecordAudio.dismiss();
                                pDialog = new ProgressDialog(ShowCentresNearMe.this);
                                pDialog.setMessage("Attaching..");
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(false);
                                pDialog.show();

                                Uri afile = Uri.fromFile(file);
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                                StorageReference riversRef = storageRef.child("attachments/" + file.getName());
                                UploadTask uploadTask = riversRef.putFile(afile);

                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(ShowCentresNearMe.this, "File attached", Toast.LENGTH_SHORT).show();
                                        pDialog.dismiss();

                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + id);
                                        databaseReference2.push().setValue(file.getName());
                                    }
                                });
                            }
                        });

                        final ImageButton record = (ImageButton) dialoglayout.findViewById(R.id.recordButton);
                        final ImageButton play = (ImageButton) dialoglayout.findViewById(R.id.playButton);
                        play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                play.setAlpha(128);
                                MediaPlayer mMediaPlayer = new MediaPlayer();
                                try {
                                    mMediaPlayer.setDataSource(AudioSavePathInDevice);
                                    mMediaPlayer.prepare();
                                    mMediaPlayer.start();
                                    recordText.setText("Playing...");

                                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            play.setAlpha(255);
                                            recordText.setText("Tap to play");
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        record.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        Log.e("FILEEEEEEE", file.getAbsolutePath());
                                        recordText.setText("Recording...");
                                        startRecording(file);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        stopRecording();
                                        recordText.setText("Tap to play");
                                        record.setVisibility(View.INVISIBLE);
                                        play.setVisibility(View.VISIBLE);
                                        attach.setVisibility(View.VISIBLE);
                                        break;
                                }
                                return false;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (items[item].equals("Choose from device")) {
                    dialog.dismiss();
                    Intent intent_upload = new Intent();
                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    intent_upload.setType("audio/3gp|audio/AMR");
                    startActivityForResult(intent_upload, 10);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void startRecording(File file) {
        if (recorder != null) {
            recorder.release();
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile(file.getAbsolutePath());
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            Log.e("giftlist", "io problems while preparing [" +
                    file.getAbsolutePath() + "]: " + e.getMessage());
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    protected void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
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

            if (location == null) {
                location = mLocationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    location = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        updateMyLocation(googleMap, location);
                    }
                } else {
                    updateMyLocation(googleMap, location);
                }
            } else {
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

                if (location == null) {
                    location = mLocationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null) {
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            updateMyLocation(googleMap, location);
                        }
                    } else {
                        updateMyLocation(googleMap, location);
                    }
                } else {
                    updateMyLocation(googleMap, location);
                }
            }
        });

//        img.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                img.performClick();
//            }
//        }, 10000);
    }

    private void updateMyLocation(final GoogleMap googleMap, Location location) {
        this.location = location;

        LatLng myLoc = null;
        if (location != null) {
            myLoc = new LatLng(location.getLatitude(), location.getLongitude());

            if (myMarker == null) {
                IconGenerator bubbleIconFactory = new IconGenerator(getApplicationContext());
                bubbleIconFactory.setStyle(IconGenerator.STYLE_BLUE);
                Bitmap bit = bubbleIconFactory.makeIcon("My location");
                myMarker = mMap.addMarker(new MarkerOptions().position(myLoc).
                        icon(BitmapDescriptorFactory.fromBitmap(bit)).title("My location"));
                myLOC = location;
            } else {
                myMarker.setPosition(myLoc);
                myLOC = location;
            }
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
                            Location targetLocation = new Location("");
                            targetLocation.setLatitude(location.latitude);
                            targetLocation.setLongitude(location.longitude);
                            dis = myLOC.distanceTo(targetLocation);
                            dis /= 1000;
                            dis = (float) Math.round(dis * 100f) / 100f;

                            if (makes.toLowerCase().contains(make.toLowerCase())) {
                                IconGenerator bubbleIconFactory = new IconGenerator(getApplicationContext());
                                bubbleIconFactory.setStyle(IconGenerator.STYLE_GREEN);
                                Bitmap bit = bubbleIconFactory.makeIcon(name + "\n" + dis + " KM");
                                Marker marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bit)).position(new LatLng(location.latitude, location.longitude)).title(""));
                                marker.setTag(key);
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
                            selectedCenter = (String) marker.getTag();
                            if (isNetwork()) {
                                raiseRequest();
                            }
                        }
                    });
                }

                return false;
            }
        });
    }

    private void raiseRequest() {
        pDialog = new ProgressDialog(ShowCentresNearMe.this);
        pDialog.setMessage("Processing..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        String queryArr = "";
        for (int i = 0; i < AppData.queries.size(); i++) {
            if (AppData.queries.get(i) == false)
                queryArr += 0;
            else
                queryArr += 1;
        }

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(new Date());
        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("EE, MMM d'st'");
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("EE, MMM d'nd'");
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("EE, MMM d'rd'");
        else
            format = new SimpleDateFormat("EE, MMM d'th'");
        final String yourDate = format.format(new Date());

        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference().child("variables/serviceNumber");
        final String finalQueryArr = queryArr;
        countRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                serviceNumber = mutableData.getValue(Integer.class);
                if (serviceNumber == null) {
                    return Transaction.success(mutableData);
                }

                final Requests requests = new Requests();
                requests.setIssuedBy(id);
                requests.setIssuedTo(selectedCenter);
                requests.setItem(path);
                requests.setKey("DexterSR" + serviceNumber);
                requests.setOpenTime(yourDate);
                requests.setScheduleTime("");
                requests.setEstPrice("");
                requests.setStatus("Open");
                requests.setQueries(finalQueryArr);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("requests/Car");
                final Integer finalServiceNumber = serviceNumber;
                databaseReference.child("DexterSR" + serviceNumber).setValue(requests, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("users/Customer/" + id + "/requests/Car");
                        databaseReference2.push().setValue("DexterSR" + finalServiceNumber, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                sendNotification(finalServiceNumber);
                                pDialog.dismiss();

                                final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + id);
                                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getChildrenCount() > 0){
                                            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("attachments/");
                                            databaseReference3.child("DexterSR" + finalServiceNumber).setValue(dataSnapshot.getValue());
                                            databaseReference2.removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowCentresNearMe.this);
                                builder.setTitle("Service request raised.");
                                builder.setMessage("Your service request ID is DexterSR " + finalServiceNumber);
                                builder.setCancelable(true);

                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        AppData.selectedTab = 1;
                                        Intent in = new Intent(ShowCentresNearMe.this, HomePage.class);
                                        startActivity(in);
                                        finish();
                                        dialog.dismiss();
                                    }
                                });

                                android.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }
                });

                serviceNumber += 1;
                mutableData.setValue(serviceNumber);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    private void sendNotification(final int num) {
        DatabaseReference firebasedbrefproduct = FirebaseDatabase.getInstance().getReference("users/ServiceCenter/" + selectedCenter);
        firebasedbrefproduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference firebasedbrefproduc = FirebaseDatabase.getInstance().getReference();
                Notif notif = new Notif();
                notif.setTitle("New service request received");
                notif.setUsername((String) ((HashMap) dataSnapshot.getValue()).get("fcm"));
                notif.setMessage("DexterSR" + num);
                firebasedbrefproduc.child("notifs").push().setValue(notif);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ShowCentresNearMe.this);
        builder.setTitle("Cancel service request");
        builder.setMessage("Are you sure you want to cancel service request?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog pDialog = new ProgressDialog(ShowCentresNearMe.this);
                pDialog.setMessage("Cancelling...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

                final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("attachments/" + id);
                databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            attachments = new ArrayList<String>();
                            attachmentsKeys = new ArrayList<String>();

                            Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                            for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                                StorageReference desertRef = storageRef.child("attachments/" + (String) entry.getValue());
                                desertRef.delete();
                            }

                            databaseReference2.removeValue();
                        }
                        pDialog.dismiss();
                        ShowCentresNearMe.this.finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}