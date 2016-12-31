package com.ccec.dexterapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccec.dexterapp.entities.Vehicle;
import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;

public class VehicleDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private TextView location, contact, name, skype, company, kilo, poll, nxtPoll, insu, nxtIns;
    private TextView locationD, contactD, nameD, companyD, skypeD, kiloD, pollD, nxtPollD, insuD, nxtInsD;
    private Vehicle veh;

    public VehicleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            veh = AppData.currentVehi;

            appBarLayout.setTitle(FontsManager.actionBarTypeface(activity, veh.getMake() + " " + veh.getModel()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_detail, container, false);

        name = (TextView) view.findViewById(R.id.fullNameTitle);
        company = (TextView) view.findViewById(R.id.companyNameTitle);
        location = (TextView) view.findViewById(R.id.locationNameTitle);
        contact = (TextView) view.findViewById(R.id.contactNameTitle);
        skype = (TextView) view.findViewById(R.id.skypeNameTitle);
        kilo = (TextView) view.findViewById(R.id.KilometerNameTitle);
        poll = (TextView) view.findViewById(R.id.PollutionNameTitle);
        nxtPoll = (TextView) view.findViewById(R.id.nxtPollutionNameTitle);
        insu = (TextView) view.findViewById(R.id.InsuranceNameTitle);
        nxtIns = (TextView) view.findViewById(R.id.dueInsuranceNameTitle);

        nameD = (TextView) view.findViewById(R.id.fullNameDetail);
        companyD = (TextView) view.findViewById(R.id.companyNameDetail);
        locationD = (TextView) view.findViewById(R.id.locationNameDetail);
        contactD = (TextView) view.findViewById(R.id.contactNameDetail);
        skypeD = (TextView) view.findViewById(R.id.skypeNameDetail);
        kiloD = (TextView) view.findViewById(R.id.KilometerNameDetail);
        pollD = (TextView) view.findViewById(R.id.PollutionNameDetail);
        nxtPollD = (TextView) view.findViewById(R.id.nxtPollutionNameDetail);
        insuD = (TextView) view.findViewById(R.id.InsuranceNameDetail);
        nxtInsD = (TextView) view.findViewById(R.id.dueInsuranceNameDetail);

        name.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contact.setTypeface(FontsManager.getRegularTypeface(getContext()));
        location.setTypeface(FontsManager.getRegularTypeface(getContext()));
        company.setTypeface(FontsManager.getRegularTypeface(getContext()));
        skype.setTypeface(FontsManager.getRegularTypeface(getContext()));
        kilo.setTypeface(FontsManager.getRegularTypeface(getContext()));
        poll.setTypeface(FontsManager.getRegularTypeface(getContext()));
        nxtPoll.setTypeface(FontsManager.getRegularTypeface(getContext()));
        insu.setTypeface(FontsManager.getRegularTypeface(getContext()));
        nxtIns.setTypeface(FontsManager.getRegularTypeface(getContext()));

        nameD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        contactD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        locationD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        companyD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        skypeD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        kiloD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        pollD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        nxtPollD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        insuD.setTypeface(FontsManager.getRegularTypeface(getContext()));
        nxtInsD.setTypeface(FontsManager.getRegularTypeface(getContext()));

        nameD.setText(veh.getMake());
        skypeD.setText(veh.getModel());
        companyD.setText(veh.getManufacturedin());
        contactD.setText(veh.getRegistrationnumber());
        locationD.setText(veh.getChessisnumber());
        kiloD.setText(veh.getKilometer());
        pollD.setText(veh.getPolluctionchkdate());
        nxtPollD.setText(veh.getNextpolluctionchkdate());
        insuD.setText(veh.getInsurancepurchasedate());
        nxtInsD.setText(veh.getInsuranceduedate());

        return view;
    }
}
