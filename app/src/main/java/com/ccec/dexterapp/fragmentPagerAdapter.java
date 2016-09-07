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

    public static List<String> fragments = new Vector<String>();
    final int PAGE_COUNT = 3;

    private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3" };
    private Context _context;

    public fragmentPagerAdapter(Context context,FragmentManager fm) {
        super(fm);

        fragments.add(ProductsFragment.class.getName());
        fragments.add(ServicesFragment.class.getName());
        fragments.add(WhatsNewFragment.class.getName());
        _context = context;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

      /*  switch (position) {
            case 0:
                ProductsFragment tab1 =  Fragment.instantiate(context,ProductsFragment.class.getName());
                return tab1;
            case 1:
                ServicesFragment tab2 = new ServicesFragment();
                return tab2;
            case 2:
                WhatsNewFragment tab3 = new WhatsNewFragment();
                return tab3;
            default:
                return null;

        } */

        return Fragment.instantiate(_context,fragments.get(position));
    }

    }

