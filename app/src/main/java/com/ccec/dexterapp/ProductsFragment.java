package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private String id;
    public List<String> carkeysarray;
    private ProductsViewAdapter adapter;
    private String rpm, spee, air, thr, tem, deviceAddress;
    private ProgressDialog pDialog;
    BluetoothSocket socket = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);
        carkeysarray = new ArrayList<>();
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
            pDialog.setMessage("Logging In...");
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

        final RPMCommand engineRpmCommand = new RPMCommand();
        final SpeedCommand speedCommand = new SpeedCommand();
        final MassAirFlowCommand mafCommand = new MassAirFlowCommand();
        final ThrottlePositionCommand thCommand = new ThrottlePositionCommand();
        final EngineCoolantTemperatureCommand enCommand = new EngineCoolantTemperatureCommand();
        final ObdCommand cmd = new ObdRawCommand("0100");

        int i = 0;
        while (i < 2) {
            try {
                engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                cmd.run(socket.getInputStream(), socket.getOutputStream());
                mafCommand.run(socket.getInputStream(), socket.getOutputStream());
                thCommand.run(socket.getInputStream(), socket.getOutputStream());
                enCommand.run(socket.getInputStream(), socket.getOutputStream());
            } catch (Exception e) {
                pDialog.dismiss();
                e.printStackTrace();
            }

            Log.d("RPMMMMMMM", "RPM: " + engineRpmCommand.getFormattedResult());

            if (i == 1) {
                rpm = engineRpmCommand.getFormattedResult();
                spee = cmd.getFormattedResult();
                air = mafCommand.getFormattedResult();
                thr = thCommand.getFormattedResult();
                tem = enCommand.getFormattedResult();
            }

            i++;
        }
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
        speed.setText(spee);
        airflow.setText(air);
        throttle.setText(thr);
        temp.setText(tem);

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





