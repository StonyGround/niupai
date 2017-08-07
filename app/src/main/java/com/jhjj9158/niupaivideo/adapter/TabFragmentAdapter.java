package com.jhjj9158.niupaivideo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.jhjj9158.niupaivideo.fragment.BaseDynamicFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 17-4-14.
 */

public class TabFragmentAdapter extends FragmentStatePagerAdapter {
    private List<BaseDynamicFragment> fragmentList = new ArrayList<>();
    private List<String> titles=new ArrayList<>();

    public TabFragmentAdapter(FragmentManager fm, List<BaseDynamicFragment> fragmentList, List<String> titles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titles = titles;
    }

    public void addData(BaseDynamicFragment fragment,String title){
        fragmentList.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}
