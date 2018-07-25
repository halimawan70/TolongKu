package edu.bluejack17_2.tolongku;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabAdapter extends FragmentStatePagerAdapter {

    String tabHeaders[] = new String[]{"Emergency", "Map", "Friends"};
    int tabNumber = 3;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabHeaders[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                EmergencyFragment fragment1 = new EmergencyFragment();
                return fragment1;
            case 1:
                MapFragment fragment2 = new MapFragment();
                return fragment2;
            case 2:
                FriendsFragment fragment3 = new FriendsFragment();
                return fragment3;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
