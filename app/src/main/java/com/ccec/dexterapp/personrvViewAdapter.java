package com.ccec.dexterapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by aanchalharit on 22/09/16.
 */

public class personrvViewAdapter extends RecyclerView.Adapter<personrvViewHolder>
{
    private List<person> allpersonrva;
    private Context mContext;

    public personrvViewAdapter(Context context , List<person> personlist )
    {
        this.mContext= context;
        this.allpersonrva = personlist;
    }

    @Override
    public personrvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_rvsinglerow,parent,false);
        personrvViewHolder personvh = new personrvViewHolder(layoutview);
        return personvh;
    }

    @Override
    public void onBindViewHolder(personrvViewHolder holder, int position)
    {
        holder.RVSinglerowName.setText(allpersonrva.get(position).getName().toString());
        holder.RVSinglerowAddress.setText(allpersonrva.get(position).getAddress().toString());

    }

    @Override
    public int getItemCount() {
        return allpersonrva.size();
    }
}
