package com.l.eyescure.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.l.eyescure.activity.fragement.PrintfViewFragemrnt;

import java.util.List;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<PrintfViewFragemrnt> mIndicators;

    public MyFragmentPagerAdapter(FragmentManager fm, List<PrintfViewFragemrnt> indicators) {
        super(fm);
        this.mIndicators = indicators;
    }

    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (mIndicators != null && mIndicators.size() > 0) {
             return mIndicators.get(position);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mIndicators.size();
    }
}
