package com.ccec.dexterapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddBottomsheetFragment extends BottomSheetDialogFragment {


    private ImageButton iv_car;
    private ImageButton iv_mobile;

    public ProductAddBottomsheetFragment() {
        // Required empty public constructor
    }

    static ProductAddBottomsheetFragment newInstance(String string)
    {
        ProductAddBottomsheetFragment f = new ProductAddBottomsheetFragment();
        Bundle myBundle = new Bundle();
        myBundle.putString("string",string);
        f.setArguments(myBundle);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_productadd,container,false);

        iv_car = (ImageButton) view.findViewById(R.id.product_car);

        iv_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ProductAddActivity_Vehicle.class);
                startActivity(intent);
            }
        });

        return view;
    }



}
