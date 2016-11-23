package com.ccec.dexterapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServicesFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        TextView servicefragmenttext = (TextView) view.findViewById(R.id.secondfragment_tv);
        servicefragmenttext.setText("services will be listed here");

        return view;
    }
}
