package com.ccec.dexterapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by aanchalharit on 22/09/16.
 */

public class personrvViewHolder extends RecyclerView.ViewHolder
{

         public TextView RVSinglerowName;
         public TextView RVSinglerowAddress;

    public personrvViewHolder(View itemView)
    {
        super(itemView);

        RVSinglerowName = (TextView) itemView.findViewById(R.id.rv_singlerowName);
        RVSinglerowAddress = (TextView)itemView.findViewById(R.id.rv_singlerowAddress);
    }

}
