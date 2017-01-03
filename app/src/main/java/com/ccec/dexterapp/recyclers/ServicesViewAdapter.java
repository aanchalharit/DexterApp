package com.ccec.dexterapp.recyclers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ccec.dexterapp.NewOrderDetail;
import com.ccec.dexterapp.R;
import com.ccec.dexterapp.ServicesFragment;
import com.ccec.dexterapp.managers.AppData;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesViewAdapter extends RecyclerView.Adapter<ServicesViewHolder> {
    private Context mContext;
    private Map<String, Object> itemMap;
    private CircularImageView userImg;
    private int pos;
    private ServicesFragment up;
    private List<String> keys;

    public ServicesViewAdapter(Context context, Map<String, Object> itemMap, List<String> keys, ServicesFragment up) {
        this.mContext = context;
        this.itemMap = itemMap;
        this.up = up;
        this.keys = keys;
    }

    @Override
    public ServicesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_new_row, parent, false);
        ServicesViewHolder productsvh = new ServicesViewHolder(layoutview);
        return productsvh;
    }

    @Override
    public void onBindViewHolder(final ServicesViewHolder holder, final int position) {
        final String key = keys.get(position);
        final Object obj = itemMap.get(key);

        holder.RVtitle.setText("Service ID: " + (String) ((HashMap) obj).get("key"));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items/Car/" + (String) ((HashMap) obj).get("item"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemMap = (HashMap<String, Object>) dataSnapshot.getValue();
                holder.RVCar.setText((String) ((HashMap) itemMap).get("make") + " " + (String) ((HashMap) itemMap).get("model"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.keepSynced(true);

        if (((String) ((HashMap) obj).get("status")).equals("Open")) {
            holder.buttons.setVisibility(View.GONE);
            String dateText = "Placed On: " + (String) ((HashMap) obj).get("openTime");
            Spannable spannable3 = new SpannableString(dateText);
            spannable3.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorBlack)), 0, ("Placed On:").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable3.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, ("Placed On:").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.RVDate.setText(spannable3, TextView.BufferType.SPANNABLE);
        } else if (((String) ((HashMap) obj).get("status")).equals("Accepted") ||
                ((String) ((HashMap) obj).get("status")).equals("Completed")) {

            DatabaseReference firebasedbrefproducts = FirebaseDatabase.getInstance().getReference().child("processFlow/" + (String) ((HashMap) obj).get("key") + "/status");
            firebasedbrefproducts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (!dataSnapshot.getValue(String.class).equals("")) {
                            holder.imgAcc.setVisibility(View.INVISIBLE);
                            holder.imgRej.setVisibility(View.INVISIBLE);
                        } else {
                            holder.imgAcc.setVisibility(View.VISIBLE);
                            holder.imgRej.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            String datee = (String) ((HashMap) obj).get("scheduleTime");
            String[] splitStr = datee.split("\\s+");

            Date d = new Date();
            d.setDate(Integer.parseInt(splitStr[0]));
            int mon = 0;
            switch (splitStr[1]) {
                case "Jan":
                    mon = 0;
                    break;
                case "Feb":
                    mon = 1;
                    break;
                case "Mar":
                    mon = 2;
                    break;
                case "Apr":
                    mon = 3;
                    break;
                case "May":
                    mon = 4;
                    break;
                case "Jun":
                    mon = 5;
                    break;
                case "Jul":
                    mon = 6;
                    break;
                case "Aug":
                    mon = 7;
                    break;
                case "Sep":
                    mon = 8;
                    break;
                case "Oct":
                    mon = 9;
                    break;
                case "Nov":
                    mon = 10;
                    break;
                case "Dec":
                    mon = 11;
                    break;
            }
            d.setMonth(mon);

            SimpleDateFormat format = new SimpleDateFormat("d");
            String date = format.format(d);
            if (date.endsWith("1") && !date.endsWith("11"))
                format = new SimpleDateFormat("EE MMM d'st', yyyy");
            else if (date.endsWith("2") && !date.endsWith("12"))
                format = new SimpleDateFormat("EE MMM d'nd', yyyy");
            else if (date.endsWith("3") && !date.endsWith("13"))
                format = new SimpleDateFormat("EE MMM d'rd', yyyy");
            else
                format = new SimpleDateFormat("EE MMM d'th', yyyy");
            String yourDate = format.format(d);

            String dateText = "";
            if (((String) ((HashMap) obj).get("status")).equals("Completed"))
                dateText = "Processed On: " + yourDate;
            else
                dateText = "Scheduled On: " + yourDate;
            Spannable spannable3 = new SpannableString(dateText);
            spannable3.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorBlack)), 0, ("Scheduled On:").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable3.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, ("Scheduled On:").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.RVDate.setText(spannable3, TextView.BufferType.SPANNABLE);
        }

        String desText = "Status: " + (String) ((HashMap) obj).get("status");
        Spannable spannable = new SpannableString(desText);
        spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorBlack)), 0, ("Status:").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, ("Status:").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.RVStatus.setText(spannable, TextView.BufferType.SPANNABLE);

        holder.imgAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.currentMap = obj;
                up.showInfo();
            }
        });

        holder.imgRej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.currentMap = obj;
                up.rejectOrder();
            }
        });

        userImg = holder.user;

        setPic((String) ((HashMap) obj).get("issuedTo"), userImg);

        holder.RVSinglerowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                getProductDetails();
            }
        });
    }

    private void setPic(String path, final CircularImageView im) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        storageRef.child("profilePics/" + path + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
//                Picasso.with(mContext).load(uri).noPlaceholder().into(im);

                try {
                    Glide.with(mContext).load(uri).override(200, 100).fitCenter().placeholder(R.drawable.icon_user).into(im);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void getProductDetails() {
        Intent intent = new Intent(mContext, NewOrderDetail.class);

        AppData.currentImagePath = keys.get(pos);
        AppData.currentVeh = itemMap.get(AppData.currentImagePath);

        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return itemMap.size();
    }
}
