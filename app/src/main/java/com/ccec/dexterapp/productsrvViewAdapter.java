package com.ccec.dexterapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.FontsManager;

import java.util.List;

/**
 * Created by aanchalharit on 22/09/16.
 */

public class productsrvViewAdapter extends RecyclerView.Adapter<productsrvViewHolder> {
    private List<Vehicle> allproductsva;
    private Context mContext;

    public productsrvViewAdapter(Context context, List<Vehicle> productslist) {
        this.mContext = context;
        this.allproductsva = productslist;
    }

    @Override
    public productsrvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_row, parent, false);
        productsrvViewHolder productsvh = new productsrvViewHolder(layoutview);
        return productsvh;
    }

    @Override
    public void onBindViewHolder(productsrvViewHolder holder, final int position) {
        // holder.RVSinglerowProductImage.setImageResource(R.drawable.ic_directions_car_black_24dp);
        holder.RVSinglerowMake.setText(allproductsva.get(position).getMake());
        holder.RVSinglerowModel.setText(allproductsva.get(position).getModel());
        holder.RVSinglerowRegNumber.setText(allproductsva.get(position).getRegistrationnumber());

        holder.RVSinglerowMake.setTypeface(FontsManager.getRegularTypeface(mContext));
        holder.RVSinglerowModel.setTypeface(FontsManager.getRegularTypeface(mContext));
        holder.RVSinglerowRegNumber.setTypeface(FontsManager.getRegularTypeface(mContext));

        holder.RVSinglerowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("REG_NO", allproductsva.get(position).getRegistrationnumber());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allproductsva.size();
    }
}
