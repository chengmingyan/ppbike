package com.ppbike.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by chengmingyan on 16/6/30.
 */
public class StatePagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragments;
    private String[] titles = null ;
    public StatePagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        mFragments = new ArrayList<Fragment>();
        this.titles  = titles;
    }
    public void addFragment(Fragment fragment){
        mFragments.add(fragment);
    }
    public void addFragment(int position,Fragment fragment){
        mFragments.add(position,fragment);
    }
    @Override
    public int getCount() {
        if (mFragments == null)
            return 0;
        return mFragments.size();
    }
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && position < titles.length)
            return titles[position];
        return super.getPageTitle(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
