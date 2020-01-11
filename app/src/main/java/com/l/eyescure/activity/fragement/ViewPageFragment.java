package com.l.eyescure.activity.fragement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.l.eyescure.R;
import com.l.eyescure.activity.adapter.MyFragmentPagerAdapter;

import java.util.List;


/**
 * @author lly
 */
@SuppressLint("ValidFragment")
public class ViewPageFragment extends Fragment {
    private List<PrintfViewFragemrnt> mIndicators;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter mPagerAdapter;
    private Context mContext;

    public ViewPageFragment(List<PrintfViewFragemrnt> tabsIndicator,Context mContext) {
        this.mIndicators = tabsIndicator;
        this.mContext = mContext;
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //getActivity().getWindow().getDecorView().setSystemUiVisibility(4108);

        View view = inflater.inflate(R.layout.viewpage_fragment, null);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(),
                mIndicators);
        mViewPager.setAdapter(mPagerAdapter);
        if (mIndicators.size() != 0) {
            mViewPager.setOffscreenPageLimit(0);
        }

        return view;
    }
}
