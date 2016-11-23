package com.ccec.dexterapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WhatsNewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whats_new, container, false);

        TextView whatsnewfragmenttext = (TextView) view.findViewById(R.id.thirdfragment_tv);
        whatsnewfragmenttext.setText("whats new will be listed here");

        return view;
    }

}
