package com.ccec.dexterapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ccec.dexterapp.managers.AppData;
import com.ccec.dexterapp.managers.FontsManager;
import com.ccec.dexterapp.managers.UserSessionManager;

import java.util.HashMap;

public class HomeFragment extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    UserSessionManager session;
    private TabLayout tabLayout;
    private ProductsFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        session = new UserSessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
//        uid = user.get(UserSessionManager.TAG_id);
//        profilePic = user.get(UserSessionManager.TAG_profilepic);
//        email = user.get(UserSessionManager.TAG_email);

        mViewPager = (ViewPager) view.findViewById(R.id.containerHomeTabs);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (AppData.fabVisible = true) {
                    processFab();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.setupWithViewPager(mViewPager);

        if (AppData.selectedTab != 0) {
            setTab(AppData.selectedTab);
            AppData.selectedTab = 0;
        }

        changeTabsFont();

        return view;
    }

    private void setTab(int pos) {
        TabLayout.Tab tab = tabLayout.getTabAt(pos);
        tab.select();
    }

    public void processFab() {
        fragment.hideLinFab();
        fragment.showAddFab();
    }

    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(FontsManager.getRegularTypeface(getContext()));
                }
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    fragment = new ProductsFragment();
                    return fragment;
                case 1:
                    ServicesFragment fragment2 = new ServicesFragment();
                    return fragment2;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Products";
                case 1:
                    return "Services";
            }
            return null;
        }
    }
}