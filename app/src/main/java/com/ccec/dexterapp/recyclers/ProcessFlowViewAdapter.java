package com.ccec.dexterapp.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterapp.R;
import com.ccec.dexterapp.ServicesFragment;
import com.ccec.dexterapp.entities.FlowRecord;
import com.ccec.dexterapp.managers.FontsManager;
import com.pkmmte.view.CircularImageView;

import java.util.List;

public class ProcessFlowViewAdapter extends RecyclerView.Adapter<ProcessFlowViewHolder> {
    private Context mContext;
    private CircularImageView userImg;
    private int pos;
    private ServicesFragment up;
    private List<FlowRecord> keys;

    public ProcessFlowViewAdapter(Context context, List<FlowRecord> keys) {
        this.mContext = context;
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
        final FlowRecord key = keys.get(position);

        holder.RVtitle.setText(key.getTitle());
        holder.RVDate.setText(key.getTimestamp());

        holder.RVtitle.setTypeface(FontsManager.getBoldTypeface(mContext));
        holder.RVDate.setTypeface(FontsManager.getRegularTypeface(mContext));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }
}
