package com.ccec.dexterapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;
import java.util.Vector;

/**
 * Created by aanchalharit on 28/08/16.
 */
public class fragmentPagerAdapter extends FragmentPagerAdapter
{

   // public static List<String> fragments = new Vector<String>();
    private static int PAGE_COUNT = 3;

    private String tabTitles[] = new String[] { "Products", "Service", "Whats New" };
    //private Context _context;

    public fragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        //fragments.add(ProductsFragment.class.getName());
        //fragments.add(ServicesFragment.class.getName());
        //fragments.add(WhatsNewFragment.class.getName());
        //_context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ProductsFragment.newInstance(0,"Products");
            case 1:
                return ServicesFragment.newInstance(1,"Services");
            case 2:
                return WhatsNewFragment.newInstance(2,"WhatsNew");
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
  @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    }
