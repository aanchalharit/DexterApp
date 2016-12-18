package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ccec.dexterapp.entities.Files;
import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdRawCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.google.android.gms.wearable.DataMap.TAG;

public class ProductsFragment extends Fragment {
    private FloatingActionButton productFab, viewF, edit, raise, delete;
    private RecyclerView ProductsRV;
    private DatabaseReference firebasedbrefproducts;
    private List<Vehicle> allproducts;
    private Vehicle VehicleDetails;
    private UserSessionManager session;
    private String id, name;
    public List<String> carkeysarray;
    private ProductsViewAdapter adapter;
    private String rpm, spee, air, thr, tem, deviceAddress;
    private ProgressDialog pDialog;
    private BluetoothSocket socket = null;
    private HashMap<String, String> carSets;
    private String set1, set2, set3, set4, set5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
        name = user.get(UserSessionManager.TAG_fullname);
        carkeysarray = new ArrayList<>();

        carSets = session.getSetDetails();
        set1 = carSets.get(UserSessionManager.TAG_set1);
        set2 = carSets.get(UserSessionManager.TAG_set2);
        set3 = carSets.get(UserSessionManager.TAG_set3);
        set4 = carSets.get(UserSessionManager.TAG_set4);
        set5 = carSets.get(UserSessionManager.TAG_set5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        ProductsRV = (RecyclerView) view.findViewById(R.id.allproducts);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        getProducts();

        viewF = (FloatingActionButton) view.findViewById(R.id.productView);
        delete = (FloatingActionButton) view.findViewById(R.id.productDelete);
        edit = (FloatingActionButton) view.findViewById(R.id.productEdit);
        raise = (FloatingActionButton) view.findViewById(R.id.productRaise);
        hideLinFab();

        productFab = (FloatingActionButton) view.findViewById(R.id.productAddFab);
        productFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddVehicle.class);
                startActivity(intent);
            }
        });

        viewF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getProductDetails();
                hideLinFab();
                showAddFab();
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOBDDevice();
                hideLinFab();
                showAddFab();
            }
        });

        return view;
    }

    class PostData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);

            pDialog.show();
        }

        protected String doInBackground(String... args) {
            getOBDData(socket);

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            showOBDData();
        }
    }

    private void getOBDDevice() {
        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Toast.makeText(getActivity(), "Please turn on the bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        Set pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (Object device : pairedDevices) {
                BluetoothDevice bdDevice = (BluetoothDevice) device;
                deviceStrs.add(bdDevice.getName());
                devices.add(bdDevice.getAddress());
            }
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Connecting.. Please wait.", Toast.LENGTH_LONG).show();

                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                deviceAddress = devices.get(position) + "";

                connectSocket();
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private void connectSocket() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter.isEnabled()) {
            if (deviceAddress != "?") {
                BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

                UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


                try {
                    socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
                } catch (Exception e) {
                    Log.e("", "Error creating socket");
                }

                try {
                    socket.connect();
                    Log.e("", "Connected");
                    getOBDData(socket);
                } catch (IOException e) {
                    Log.e("", e.getMessage());
                    try {
                        Log.e("", "trying fallback...");

                        socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
                        socket.connect();

                        new PostData().execute();

//                        getOBDData(socket);
                        Log.e("", "Connected");
                    } catch (Exception e2) {
                        Log.e("", "Couldn't establish Bluetooth connection!");
                        Toast.makeText(getActivity(), "Couldn't establish Bluetooth connection!", Toast.LENGTH_SHORT).show();
//                        pDialog.dismiss();
                    }
                }
            } else {
                Log.e("", "BT device not selected");
                Toast.makeText(getActivity(), "Bluetooth device not selected", Toast.LENGTH_SHORT).show();
//                pDialog.dismiss();
            }
        }
    }

    private void getOBDData(final BluetoothSocket socket) {
        try {
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
            new ThrottlePositionCommand().run(socket.getInputStream(), socket.getOutputStream());
            new MassAirFlowCommand().run(socket.getInputStream(), socket.getOutputStream());
            new EngineCoolantTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());
        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();
        }

        if (set1 == null) {
//        final RPMCommand engineRpmCommand = new RPMCommand();
//        final SpeedCommand speedCommand = new SpeedCommand();
//        final MassAirFlowCommand mafCommand = new MassAirFlowCommand();
//        final ThrottlePositionCommand thCommand = new ThrottlePositionCommand();
//        final EngineCoolantTemperatureCommand enCommand = new EngineCoolantTemperatureCommand();
            final ObdCommand cmd = new ObdRawCommand("0100");
            final ObdCommand cmd2 = new ObdRawCommand("0120");
            final ObdCommand cmd3 = new ObdRawCommand("0140");
            final ObdCommand cmd4 = new ObdRawCommand("0160");
            final ObdCommand cmd5 = new ObdRawCommand("0180");

            int i = 0;
            while (i < 2) {
                try {
                    cmd.run(socket.getInputStream(), socket.getOutputStream());
                    cmd2.run(socket.getInputStream(), socket.getOutputStream());
                    cmd3.run(socket.getInputStream(), socket.getOutputStream());
                    cmd4.run(socket.getInputStream(), socket.getOutputStream());
                    cmd5.run(socket.getInputStream(), socket.getOutputStream());
                } catch (Exception e) {
                    pDialog.dismiss();
                    e.printStackTrace();
                }

                Log.d("cmd1", "cmd1: " + cmd.getFormattedResult());
                Log.d("cmd2", "cmd2: " + cmd2.getFormattedResult());
                Log.d("cmd3", "cmd3: " + cmd3.getFormattedResult());
                Log.d("cmd4", "cmd4: " + cmd4.getFormattedResult());
                Log.d("cmd5", "cmd5: " + cmd5.getFormattedResult());

                set1 = cmd.getFormattedResult();
                set2 = cmd2.getFormattedResult();
                set3 = cmd3.getFormattedResult();
                set4 = cmd4.getFormattedResult();
                set5 = cmd5.getFormattedResult();

                if (set1 != null)
                    if (set1.length() > 8)
                        rpm = getStrin(getVal(set1.substring(set1.length() - 8)));
                    else
                        rpm = "Not Supported";

                if (set2 != null)
                    if (set2.length() > 8)
                        thr = getStrin2(getVal(set2.substring(set2.length() - 8)));
                    else
                        thr = "Not Supported";

                if (set3 != null)
                    if (set3.length() > 8)
                        tem = getStrin3(getVal(set3.substring(set3.length() - 8)));
                    else
                        tem = "Not Supported";

                if (set4 != null)
                    if (set4.length() > 8)
                        air = getStrin4(getVal(set4.substring(set4.length() - 8)));
                    else
                        air = "Not Supported";

                if (set5 != null)
                    if (set5.length() > 8)
                        spee = getStrin5(getVal(set5.substring(set5.length() - 8)));
                    else
                        spee = "Not Supported";

                session.createUserLoginSession(rpm, thr, tem, air, spee);

                if (i == 1) {
                    rpm = cmd.getFormattedResult();
                    thr = cmd2.getFormattedResult();
                    tem = cmd3.getFormattedResult();
                    air = cmd4.getFormattedResult();
                    spee = cmd5.getFormattedResult();
                }

                i++;
            }
        } else {
            ObdCommand cmd;
            StringBuilder output = new StringBuilder();
            output.append("PID's (00 - 20)\n");
            List<String> list1 = Arrays.asList(set1.split(","));
            for (int i = 0; i < list1.size(); i++) {
                try {
                    cmd = new ObdRawCommand("01" + list1.get(i));
                    cmd.run(socket.getInputStream(), socket.getOutputStream());
                    Log.d("cmd" + i, output.toString());
//                    output.append("01" + list1.get(i) + "\n");
                    output.append(getPIDDesc(list1.get(i)) + "\n");
                    output.append(cmd.getFormattedResult() + " ");
                    output.append("\n");
                    rpm = "Saved";
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder output2 = new StringBuilder();
            output2.append("\nPID's (20 - 40)\n");
            if (!set2.equals("") && !set2.equals("Not Supported")) {
                List<String> list2 = Arrays.asList(set2.split(","));
                for (int i = 0; i < list2.size(); i++) {
                    try {
                        cmd = new ObdRawCommand("01" + list2.get(i));
                        cmd.run(socket.getInputStream(), socket.getOutputStream());
                        Log.d("cmd" + i, output.toString());
//                        output2.append("01" + list2.get(i) + "\n");
                        output2.append(getPIDDesc(list2.get(i)) + "\n");
                        output2.append(cmd.getFormattedResult() + " ");
                        output2.append("\n");
                        thr = "Saved";
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                thr = "Not supported";
            }

            StringBuilder output3 = new StringBuilder();
            output3.append("\nPID's (40 - 60)\n");
            if (!set3.equals("") && !set3.equals("Not Supported")) {
                List<String> list3 = Arrays.asList(set3.split(","));
                for (int i = 0; i < list3.size(); i++) {
                    try {
                        cmd = new ObdRawCommand("01" + list3.get(i));
                        cmd.run(socket.getInputStream(), socket.getOutputStream());
                        Log.d("cmd" + i, output.toString());
                        output3.append(getPIDDesc(list3.get(i)) + "\n");
//                        output3.append("01" + list3.get(i) + "\n");
                        output3.append(cmd.getFormattedResult() + " ");
                        output3.append("\n");
                        tem = "Saved";
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                tem = "Not supported";
            }

            StringBuilder output4 = new StringBuilder();
            output4.append("\nPID's (60 - 80)\n");
            if (!set4.equals("") && !set4.equals("Not Supported")) {
                List<String> list4 = Arrays.asList(set4.split(","));
                for (int i = 0; i < list4.size(); i++) {
                    try {
                        cmd = new ObdRawCommand("01" + list4.get(i));
                        cmd.run(socket.getInputStream(), socket.getOutputStream());
                        Log.d("cmd" + i, output.toString());
//                        output4.append("01" + list4.get(i) + "\n");
                        output4.append(getPIDDesc(list4.get(i)) + "\n");
                        output4.append(cmd.getFormattedResult() + " ");
                        output4.append("\n");
                        air = "Saved";
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                air = "Not supported";
            }

            StringBuilder output5 = new StringBuilder();
            output5.append("\nPID's (80 - 87)\n");
            try {
                if (!set5.equals("") && !set5.equals("Not Supported")) {
                    List<String> list5 = Arrays.asList(set5.split(","));
                    for (int i = 0; i < list5.size(); i++) {
                        try {
                            cmd = new ObdRawCommand("01" + list5.get(i));
                            cmd.run(socket.getInputStream(), socket.getOutputStream());
                            Log.d("cmd" + i, output.toString());
//                            output5.append("01" + list5.get(i) + "\n");
                            output5.append(getPIDDesc(list5.get(i)) + "\n");
                            output5.append(cmd.getFormattedResult() + " ");
                            output5.append("\n");
                            spee = "Saved";
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    spee = "Not supported";
                }
            } catch (NullPointerException e) {
                spee = "Not supported";
            }

            File file = new File(Environment.getExternalStorageDirectory(), "dexter_obd_output.txt");
            try {
                FileOutputStream stream = new FileOutputStream(file);
                try {
                    stream.write(output.toString().getBytes());
                    stream.write(output2.toString().getBytes());
                    stream.write(output3.toString().getBytes());
                    stream.write(output4.toString().getBytes());
                    stream.write(output5.toString().getBytes());
                } finally {
                    stream.close();
                    uploadFile(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(File file2) {
        Uri file = Uri.fromFile(file2);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
        StorageReference riversRef = storageRef.child("obdFiles/" + id + ".txt");
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("obdFiles");
                databaseReference.push().setValue(id);
            }
        });
    }

    private static String getPIDDesc(String code) {
        switch (code) {
            case "00":
                return "PIDs supported [01 - 20]";
            case "01":
                return "Monitor status since DTCs cleared. (Includes malfunction indicator lamp (MIL) status and number of DTCs.)";
            case "87":
                return "Intake manifold absolute pressure";
            default:
                return "got it";
        }
    }

    private static String getVal(String s) {
        String m = "";
        for (int i = 0; i < s.length(); i++) {
            m += getBin(s.charAt(i));
        }
        return m;
    }

    private static String getBin(char a) {
        String s = "";
        switch (a) {
            case '0':
                s = "0000";
                break;
            case '1':
                s = "0001";
                break;
            case '2':
                s = "0010";
                break;
            case '3':
                s = "0011";
                break;
            case '4':
                s = "0100";
                break;
            case '5':
                s = "0101";
                break;
            case '6':
                s = "0110";
                break;
            case '7':
                s = "0111";
                break;
            case '8':
                s = "1000";
                break;
            case '9':
                s = "1001";
                break;
            case 'A':
                s = "1010";
                break;
            case 'B':
                s = "1011";
                break;
            case 'C':
                s = "1100";
                break;
            case 'D':
                s = "1101";
                break;
            case 'E':
                s = "1110";
                break;
            case 'F':
                s = "1111";
                break;
        }

        return s;
    }

    private static String getStrin(String s) {
        String[] ar = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F",
                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F"};

        String m = "";
        for (int i = 0; i < ar.length; i++) {
            if (s.charAt(i) == '1') {
                m += ar[i];
                if (i != ar.length - 1)
                    m += ",";
            }
        }
        return m;
    }

    private static String getStrin2(String s) {
        String[] ar = new String[]{"21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
                "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F"};

        String m = "";
        for (int i = 0; i < ar.length; i++) {
            if (s.charAt(i) == '1') {
                m += ar[i];
                if (i != ar.length - 1)
                    m += ",";
            }
        }
        return m;
    }

    private static String getStrin3(String s) {
        String[] ar = new String[]{"41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F",
                "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F"};

        String m = "";
        for (int i = 0; i < ar.length; i++) {
            if (s.charAt(i) == '1') {
                m += ar[i];
                if (i != ar.length - 1)
                    m += ",";
            }
        }
        return m;
    }

    private static String getStrin4(String s) {
        String[] ar = new String[]{"61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F",
                "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F"};

        String m = "";
        for (int i = 0; i < ar.length; i++) {
            if (s.charAt(i) == '1') {
                m += ar[i];
                if (i != ar.length - 1)
                    m += ",";
            }
        }
        return m;
    }

    private static String getStrin5(String s) {
        String[] ar = new String[]{"81", "82", "83", "84", "85", "86", "87"};

        String m = "";
        for (int i = 0; i < ar.length; i++) {
            if (s.charAt(i) == '1') {
                m += ar[i];
                if (i != ar.length - 1)
                    m += ",";
            }
        }
        return m;
    }

    private void showOBDData() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_dialog_obd, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(dialoglayout);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final TextView rpmm = (TextView) dialoglayout.findViewById(R.id.fullNameDetail);
        rpmm.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView throttle = (TextView) dialoglayout.findViewById(R.id.skypeNameDetail);
        throttle.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView temp = (TextView) dialoglayout.findViewById(R.id.companyNameDetail);
        temp.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView airflow = (TextView) dialoglayout.findViewById(R.id.contactNameDetail);
        airflow.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        final TextView speed = (TextView) dialoglayout.findViewById(R.id.locationNameDetail);
        speed.setTypeface(FontsManager.getRegularTypeface(getActivity()));

        rpmm.setText(rpm);
        throttle.setText(thr);
        temp.setText(tem);
        airflow.setText(air);
        speed.setText(spee);

        final Button cancel = (Button) dialoglayout.findViewById(R.id.cancelButton);
        cancel.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                HomeFragment homeFragment = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment, "homeFragment").commit();
            }
        });

        final Button submit = (Button) dialoglayout.findViewById(R.id.submitButton);
        submit.setTypeface(FontsManager.getBoldTypeface(getActivity()));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Data will be saved to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getProducts() {
        allproducts = new ArrayList<Vehicle>();

        firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("users/Customer/" + id + "/items/Car");
        firebasedbrefproducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                for (Object value : itemMap.values()) {
                    carkeysarray.add(value.toString());
                }

                for (int carkey = 0; carkey < carkeysarray.size(); carkey++) {
                    String dbcarkey = carkeysarray.get(carkey);
                    firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("items/Car/" + dbcarkey);
                    firebasedbrefproducts.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            Vehicle vehicle = dataSnapshot1.getValue(Vehicle.class);
                            allproducts.add(vehicle);
                            adapter = new ProductsViewAdapter(getActivity(), allproducts, carkeysarray, ProductsFragment.this);
                            ProductsRV.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                return;
            }
        });
        firebasedbrefproducts.keepSynced(true);
    }

    public void showAddFab() {
        productFab.show();
    }

    public void hideAddFab() {
        productFab.hide();
    }

    public void showLinFab() {
        AppData.fabVisible = true;
        viewF.show();
        edit.show();
        delete.show();
        raise.show();
    }

    public void hideLinFab() {
        AppData.fabVisible = false;
        viewF.hide();
        edit.hide();
        delete.hide();
        raise.hide();
    }
}





