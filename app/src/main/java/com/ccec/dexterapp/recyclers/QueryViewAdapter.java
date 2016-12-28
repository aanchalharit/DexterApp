package com.ccec.dexterapp.recyclers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ccec.dexterapp.ProductsFragment;
import com.ccec.dexterapp.R;
import com.ccec.dexterapp.managers.AppData;

import java.util.ArrayList;
import java.util.List;

public class QueryViewAdapter extends RecyclerView.Adapter<QueryViewHolder> {
    private List<String> allproductsva;
    private Context mContext;
    private ProductsFragment fragm;

    public QueryViewAdapter(Context context, List<String> productslist, ProductsFragment frag) {
        this.mContext = context;
        this.allproductsva = productslist;
        this.fragm = frag;

        AppData.queries = new ArrayList<>();
        for (int i = 0; i < allproductsva.size(); i++) {
            AppData.queries.add(false);
        }
    }

    @Override
    public QueryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_query_row, parent, false);
        QueryViewHolder productsvh = new QueryViewHolder(layoutview);
        return productsvh;
    }

    @Override
    public void onBindViewHolder(final QueryViewHolder holder, final int position) {
        holder.QueryTitle.setText(allproductsva.get(position).toString());

        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppData.queries.set(position, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allproductsva.size();
    }
}
