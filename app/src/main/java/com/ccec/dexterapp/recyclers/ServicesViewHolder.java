package com.ccec.dexterapp.recyclers;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccec.dexterapp.R;
import com.pkmmte.view.CircularImageView;

public class ServicesViewHolder extends RecyclerView.ViewHolder {
    public CardView RVSinglerowCard;
    public TextView RVtitle;
    public TextView RVDate;
    public TextView RVCar, RVStatus;
    public ImageView imgAcc, imgRej;
    public CircularImageView user;
    public RelativeLayout buttons;

    public ServicesViewHolder(View itemView) {
        super(itemView);

        RVSinglerowCard = (CardView) itemView.findViewById(R.id.singleitemCardView);

        RVtitle = (TextView) itemView.findViewById(R.id.product_cardviewMake);
        RVCar = (TextView) itemView.findViewById(R.id.product_cardviewModel);
        RVDate = (TextView) itemView.findViewById(R.id.product_cardviewRegNumber);
        RVStatus = (TextView) itemView.findViewById(R.id.product_cardviewReg1);

        buttons = (RelativeLayout) itemView.findViewById(R.id.buttons);

        imgAcc = (ImageView) itemView.findViewById(R.id.btnChat);
        imgRej = (ImageView) itemView.findViewById(R.id.btnAccept);
        user = (CircularImageView) itemView.findViewById(R.id.product_circle);
    }
}
