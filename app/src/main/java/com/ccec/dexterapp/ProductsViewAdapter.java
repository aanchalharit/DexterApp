package com.ccec.dexterapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by aanchalharit on 22/09/16.
 */

public class ProductsViewAdapter extends RecyclerView.Adapter<ProductsViewHolder> {
    private List<Vehicle> allproductsva;
    private Context mContext;
    private ProductsFragment fragm;
    private Context context;
    private int pos;
    private CircularImageView img;
    public List<String> carkeys;

    public ProductsViewAdapter(Context context, List<Vehicle> productslist, List<String> carkeys, ProductsFragment frag) {
        this.mContext = context;
        this.allproductsva = productslist;
        this.fragm = frag;
        this.carkeys = carkeys;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutview = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_row, parent, false);
        ProductsViewHolder productsvh = new ProductsViewHolder(layoutview);
        return productsvh;
    }

    @Override
    public void onBindViewHolder(ProductsViewHolder holder, final int position) {
        // holder.RVSinglerowProductImage.setImageResource(R.drawable.ic_directions_car_black_24dp);
        holder.RVSinglerowMake.setText(allproductsva.get(position).getMake());
        holder.RVSinglerowModel.setText(allproductsva.get(position).getModel());
        holder.RVSinglerowRegNumber.setText(allproductsva.get(position).getRegistrationnumber());
        img = holder.RVCircle;
        String temp = carkeys.get(position);
        setPic(temp, img);

        holder.RVSinglerowMake.setTypeface(FontsManager.getRegularTypeface(mContext));
        holder.RVSinglerowModel.setTypeface(FontsManager.getRegularTypeface(mContext));
        holder.RVSinglerowRegNumber.setTypeface(FontsManager.getRegularTypeface(mContext));

        holder.RVSinglerowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragm.hideAddFab();
                fragm.showLinFab();

                context = view.getContext();
                pos = position;
            }
        });
    }

    private void setPic(String path, final CircularImageView im) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://dexterapp-bb161.appspot.com");

        storageRef.child("items/cars/" + path + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mContext).load(uri).noPlaceholder().into(im);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void getProductDetails() {
        Intent intent = new Intent(context, VehicleDetail.class);

        AppData.currentVeh = allproductsva.get(pos);
        AppData.currentImagePath = carkeys.get(pos);

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return allproductsva.size();
    }
}