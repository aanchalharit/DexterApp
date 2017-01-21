package com.ccec.dexterapp.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccec.dexterapp.R;
import com.ccec.dexterapp.managers.FontsManager;

import java.util.ArrayList;

public class AttachViewAdapter extends RecyclerView.Adapter<AttachViewAdapter.ViewHolder> {
    private ArrayList<String> countries;
    private Context ctx;

    public AttachViewAdapter(ArrayList<String> countries, Context ctx) {
        this.countries = countries;
        this.ctx = ctx;
    }

    @Override
    public AttachViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttachViewAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tv_country.setText(i + 1 + ". " + countries.get(i));
        viewHolder.tv_country.setTypeface(FontsManager.getRegularTypeface(ctx));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_country;

        public ViewHolder(View view) {
            super(view);

            tv_country = (TextView) view.findViewById(R.id.queryTitle);
        }
    }
}
