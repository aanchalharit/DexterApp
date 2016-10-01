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

    private int Page;
    private String Title;

    public static WhatsNewFragment newInstance(int page,String title )
    {
        WhatsNewFragment whatsNewFragment = new WhatsNewFragment();
        Bundle args = new Bundle();
        args.putInt("mypageint3", page);
        args.putString("mypagetitle3", title);
        whatsNewFragment.setArguments(args);
        return whatsNewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Page = getArguments().getInt("mypageint3",2);
        Title = getArguments().getString("mypagetitle3");
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
