package com.ccec.dexterapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WhatsNewFragment extends Fragment {


    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    /*public static WhatsNewFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        WhatsNewFragment fragment = new WhatsNewFragment();
        fragment.setArguments(args);
        return fragment;
    } */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whats_new, container, false);

        TextView whatsnewfragmenttext = (TextView)view.findViewById(R.id.thirdfragment_tv);
        whatsnewfragmenttext.setText("whats new will be listed here");

        return view;
    }

}
