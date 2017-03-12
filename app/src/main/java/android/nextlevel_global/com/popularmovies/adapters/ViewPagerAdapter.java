package android.nextlevel_global.com.popularmovies.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * View pager adapter for movie details tabs.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    // List of tab titles.
    private final List<String> mTitlesList = new ArrayList<>();

    // List of fragments.
    private final List<Fragment> mFragmentsList = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param fm for FragmentPagerAdapter
     */
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitlesList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Add fragment and tab title to the adapter.
     *
     * @param fragment which we want to add
     * @param tabName  for this fragment
     */
    public void addFragment(Fragment fragment, Bundle args, String tabName) {
        if (args != null) {
            fragment.setArguments(args);
        }

        mTitlesList.add(tabName);
        mFragmentsList.add(fragment);
    }
}
