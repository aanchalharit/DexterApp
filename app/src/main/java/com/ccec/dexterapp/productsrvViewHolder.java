package com.ccec.dexterapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by aanchalharit on 22/09/16.
 */

public class productsrvViewHolder extends RecyclerView.ViewHolder
{

         public CardView RVSinglerowCard;
         //public ImageView RVSinglerowProductImage;
         public View RVCircle;
         public TextView RVSinglerowMake;
         public TextView RVSinglerowModel;
         public TextView RVSinglerowRegNumber;

    public productsrvViewHolder(View itemView)
    {
        super(itemView);

        RVSinglerowCard = (CardView)itemView.findViewById(R.id.singleitemCardView);
        RVCircle = (View)itemView.findViewById(R.id.product_circle);
        //RVSinglerowProductImage = (ImageView)itemView.findViewById(R.id.product_cardviewphoto);
        RVSinglerowMake = (TextView)itemView.findViewById(R.id.product_cardviewMake);
        RVSinglerowModel = (TextView)itemView.findViewById(R.id.product_cardviewModel);
        RVSinglerowRegNumber = (TextView)itemView.findViewById(R.id.product_cardviewRegNumber);
    }

}
