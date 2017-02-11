package com.ccec.dexterapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccec.dexterapp.entities.Requests;
import com.ccec.dexterapp.managers.JSONArrayParser;
import com.ccec.dexterapp.managers.UserSessionManager;
import com.ccec.dexterapp.recyclers.PastServicesViewAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CompletedServicesFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private UserSessionManager session;
    private String id;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout errorSec;
    private ImageView erImg;
    private TextView erTxt;
    private ProgressDialog pDialog;
    private List<Requests> items;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        session = new UserSessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(UserSessionManager.TAG_id);

        recyclerView = (RecyclerView) view.findViewById(R.id.task_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        errorSec = (RelativeLayout) view.findViewById(R.id.errorSec);

        erTxt = (TextView) view.findViewById(R.id.errorHeader);
        erImg = (ImageView) view.findViewById(R.id.errorImage);
        ImageView erImgTxt = (ImageView) view.findViewById(R.id.errorImage);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Updating...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        items = new ArrayList<>();

        if (isNetwork())
            new GetData().execute();
        else {
            erImgTxt.setImageDrawable(getResources().getDrawable(R.drawable.icon_no_connection));
            errorSec.setVisibility(View.VISIBLE);
            erTxt.setText("Please connect to internet");
        }

        return view;
    }

    public boolean isNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    class GetData extends AsyncTask<String, String, String> {
        private static final String url = "http://188.166.245.67/html/phpscript/getCompletedList.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            JSONArrayParser jsonObjectParser = new JSONArrayParser();

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));

            JSONArray response = jsonObjectParser.makeHttpRequest(url, "GET", params);

            try {
                for (int i = 0; i < response.length(); i++) {
                    Requests item = new Requests();
                    JSONObject noti = response.getJSONObject(i);

                    item.setEstPrice(noti.getString("estPrice").toString());
                    item.setIssuedBy(noti.getString("issuedBy").toString());
                    item.setIssuedTo(noti.getString("issuedTo").toString());
                    item.setItem(noti.getString("item").toString());
                    item.setKey(noti.getString("keyCar").toString());
                    item.setOpenTime(noti.getString("openTime").toString());
                    item.setQueries(noti.getString("queries").toString());
                    item.setScheduleTime(noti.getString("scheduleTime").toString());
                    item.setStatus(noti.getString("status").toString());

                    items.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            try {
                pDialog.dismiss();
                if (items.size() > 0) {
                    Collections.sort(items, new Comparator<Requests>() {
                        public int compare(Requests emp1, Requests emp2) {
                            return emp2.getKey().compareToIgnoreCase(emp1.getKey());
                        }
                    });

                    PastServicesViewAdapter recyclerViewAdapter = new PastServicesViewAdapter(getActivity(), items);
                    recyclerView.setAdapter(recyclerViewAdapter);
                } else {
                    errorSec.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
