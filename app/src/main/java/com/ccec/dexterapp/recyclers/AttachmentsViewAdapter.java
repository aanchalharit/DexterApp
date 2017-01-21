package com.ccec.dexterapp.recyclers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccec.dexterapp.R;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class AttachmentsViewAdapter extends ArrayAdapter<String> {
    private final UserSessionManager session;
    private String id;
    private LayoutInflater inflater;
    private Activity mContext;
    private ArrayList<String> modelss, modelKeys;
    private int layoutResId;

    public AttachmentsViewAdapter(Activity context, int resource, ArrayList<String> modelss, ArrayList<String> modelsKeys) {
        super(context, resource, modelss);
        layoutResId = resource;
        mContext = context;
        this.modelss = modelss;
        this.modelKeys = modelsKeys;

        session = new UserSessionManager(mContext);
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(UserSessionManager.TAG_id);
    }

    @Override
    public int getCount() {
        return modelss.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public String getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(String item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            inflater = LayoutInflater.from(mContext);
            row = inflater.inflate(layoutResId, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) row.findViewById(R.id.queryTitle);
            holder.cross = (ImageView) row.findViewById(R.id.crossButton);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.title.setText(position + 1 + ". " + modelss.get(position));
        holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete it?");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog pDialog = new ProgressDialog(mContext);
                        pDialog.setMessage("Deleting...");
                        pDialog.setIndeterminate(false);
                        pDialog.setCancelable(false);
                        pDialog.show();

                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference(id);
                        databaseReference2.child(modelKeys.get(position)).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");
                                StorageReference desertRef = storageRef.child("attachments/" + modelss.get(position));
                                desertRef.delete();

                                pDialog.dismiss();
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

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return row;
    }

    public class ViewHolder {
        TextView title;
        ImageView cross;
    }
}