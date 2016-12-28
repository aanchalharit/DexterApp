package com.ccec.dexterapp.recyclers;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ccec.dexterapp.R;
import com.pkmmte.view.CircularImageView;

public class ProductsViewHolder extends RecyclerView.ViewHolder {

    public CardView RVSinglerowCard;
    public CircularImageView RVCircle;
    public TextView RVSinglerowMake;
    public TextView RVSinglerowModel;
    public TextView RVSinglerowRegNumber;

    public ProductsViewHolder(View itemView) {
        super(itemView);

        RVSinglerowCard = (CardView) itemView.findViewById(R.id.singleitemCardView);
        RVCircle = (CircularImageView) itemView.findViewById(R.id.product_circle);

        RVSinglerowMake = (TextView) itemView.findViewById(R.id.product_cardviewMake);
        RVSinglerowModel = (TextView) itemView.findViewById(R.id.product_cardviewModel);
        RVSinglerowRegNumber = (TextView) itemView.findViewById(R.id.product_cardviewRegNumber);
    }
}
