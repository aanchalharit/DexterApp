package com.ccec.dexterapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
   private FloatingActionButton productFab;
    public ProductsFragment() {}

    /*public static ProductsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ProductsFragment fragment = new ProductsFragment();
        fragment.setArguments(args);
        return fragment;
    } */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_products, container, false);

        //TextView productfragmenttext = (TextView)view.findViewById(R.id.firstfragment_tv);
        //productfragmenttext.setText("Products will be listed here");
        final BottomSheetDialogFragment productbottomsheet = ProductAddBottomsheetFragment.newInstance("Add Products");
        productFab = (FloatingActionButton) view.findViewById(R.id.productAddFab);
        productFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productbottomsheet.show(getFragmentManager(),productbottomsheet.getTag());

                //Intent intent = new Intent(getActivity(),ProductAddActivity_Vehicle.class);
                //startActivity(intent);
            }
        });
        return view;
    }

}
