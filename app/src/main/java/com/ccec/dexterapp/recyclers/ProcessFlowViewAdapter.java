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
import com.google.android.gms.drive.realtime.internal.event.ObjectChangedDetails;
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

public class ProcessFlowViewAdapter extends RecyclerView.Adapter<ProcessFlowViewHolder> {
    private Context mContext;
    private CircularImageView userImg;
    private int pos;
    private ServicesFragment up;
    private List<String> keys;
    private Map<String, Object> itemMap;

    public ProcessFlowViewAdapter(Context context, Map<String, Object> itemMap, List<String> keys) {
        this.mContext = context;
        this.itemMap = itemMap;
        this.keys = keys;
    }

    @Override
    public ProcessFlowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_order_process_flow, parent, false);
        ProcessFlowViewHolder productsvh = new ProcessFlowViewHolder(layoutview);
        return productsvh;
    }

    @Override
    public void onBindViewHolder(final ProcessFlowViewHolder holder, final int position) {
        final String key = keys.get(position);
        Object obj = itemMap.get(key);

//        holder.RVtitle.setText((String) ((HashMap) obj).get("key"));
        holder.RVtitle.setText(key);
    }

    @Override
    public int getItemCount() {
        return itemMap.size();
    }
}