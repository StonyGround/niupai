package com.xiuxiu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.xiuxiu.fragment.PagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 17-4-14.
 */

public class TabFragmentAdapter extends FragmentStatePagerAdapter {
    private List<PagerFragment> fragmentList = new ArrayList<>();
    private List<String> titles=new ArrayList<>();

    public TabFragmentAdapter(FragmentManager fm, List<PagerFragment> fragmentList, List<String> titles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titles = titles;
    }

    public void addData(PagerFragment fragment,String title){
        fragmentList.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position % fragmentList.size());
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("instantiateItem",position+"----");
        return super.instantiateItem(container, position);
    }
}
