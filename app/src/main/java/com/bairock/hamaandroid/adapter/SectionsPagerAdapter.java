package com.bairock.hamaandroid.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.hamaandroid.app.ElectricalCtrlFragment;
import com.bairock.hamaandroid.app.VirtualFragment;

/**
 * Created by Administrator on 2016/5/29.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        //position = 1;
        switch (position){
            case 0 :
                fragment = ElectricalCtrlFragment.newInstance(position);
                break;
            case 1 :
                fragment = ClimateFragment.newInstance(position);
                break;
            case 2 :
                fragment = VirtualFragment.newInstance(position);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "电器";
            case 1:
                return "仪表";
            case 2:
                return "虚拟";
        }
        return null;
    }
}