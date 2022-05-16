package com.cleantec.benfalexadmin.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.cleantec.benfalexadmin.Fragments.Delivered;
import com.cleantec.benfalexadmin.Fragments.Pending;
import com.cleantec.benfalexadmin.Fragments.PickedUp;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int fragmentNo;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int fragmentNo) {
        super(fm);
        this.fragmentNo = fragmentNo;
    }

    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Pending pending = new Pending();
                return pending;
            case 1:
                PickedUp pickedUp=new PickedUp();
                return pickedUp;
            case 2:
                Delivered delivered=new Delivered();
                return delivered;


            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return fragmentNo;
    }
}
