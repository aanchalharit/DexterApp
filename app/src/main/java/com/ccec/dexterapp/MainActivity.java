package com.ccec.dexterapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends BaseActivity {


    fragmentPagerAdapter mfragmentPagerAdapter;

    private RecyclerView ProductsRV;
    private DatabaseReference firebasedbrefperson;
    private List<person> allperson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.MainPager);
        mfragmentPagerAdapter  = new fragmentPagerAdapter(getSupportFragmentManager());
        // Get the ViewPager and set it's PagerAdapter so that it can display items

        viewPager.setAdapter(mfragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);
        // Give the PagerSlidingTabStrip the ViewPager
       //PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.toolbartabs);
        // Attach the view pager to the tab strip
        //tabsStrip.setViewPager(viewPager);
    //    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
      //  tabLayout.addTab(tabLayout.newTab().setText("Products"));
        //tabLayout.addTab(tabLayout.newTab().setText("Services"));
        //tabLayout.addTab(tabLayout.newTab().setText("What's New"));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


//        ProductsRV = (RecyclerView) findViewById(R.id.allproducts);
//        ProductsRV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//        allperson = new ArrayList<person>();
//        firebasedbrefperson = FirebaseDatabase.getInstance().getReference().child("Person");
//
//        firebasedbrefperson.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                getAllperson(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                return;
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_main:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void getAllperson(DataSnapshot dataSnapshot) {
//        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//            person Person  = singleSnapshot.getValue(person.class);
//            allperson.add(Person);
//            personrvViewAdapter adapter = new personrvViewAdapter(this, allperson);
//            ProductsRV.setAdapter(adapter);
//        }
//    }
}
