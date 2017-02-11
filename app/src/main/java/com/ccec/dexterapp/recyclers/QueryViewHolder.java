package com.ccec.dexterapp.recyclers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.ccec.dexterapp.R;

public class QueryViewHolder extends RecyclerView.ViewHolder {
    public TextView QueryTitle;
    public SwitchCompat switchCompat;

    public QueryViewHolder(View itemView) {
        super(itemView);

        switchCompat = (SwitchCompat) itemView.findViewById(R.id.switchButton);
        QueryTitle = (TextView) itemView.findViewById(R.id.queryTitle);
    }
}
