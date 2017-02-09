package com.ccec.dexterapp.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccec.dexterapp.R;
import com.ccec.dexterapp.entities.ModelNoti;
import com.ccec.dexterapp.managers.FontsManager;

import java.util.ArrayList;

public class NotiViewAdapter extends RecyclerView.Adapter<NotiViewAdapter.ViewHolder> {
    private ArrayList<ModelNoti> notis;
    private Context ctx;

    public NotiViewAdapter(ArrayList<ModelNoti> countries, Context ctx) {
        this.notis = countries;
        this.ctx = ctx;
    }

    @Override
    public NotiViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_noti, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotiViewAdapter.ViewHolder viewHolder, int i) {
        ModelNoti noti = notis.get(i);

        viewHolder.tv_date.setText(noti.getMessage());
        viewHolder.tv_date.setTypeface(FontsManager.getRegularTypeface(ctx));

        viewHolder.tv_title.setText(noti.getDate());
        viewHolder.tv_title.setTypeface(FontsManager.getBoldTypeface(ctx));
    }

    @Override
    public int getItemCount() {
        return notis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_date;

        public ViewHolder(View view) {
            super(view);

            tv_title = (TextView) view.findViewById(R.id.queryTitle);
            tv_date = (TextView) view.findViewById(R.id.queryTitle2);
        }
    }
}
