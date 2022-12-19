package com.rezzza.calculatorapp.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rezzza.calculatorapp.fragment.DatabaseFragment;
import com.rezzza.calculatorapp.fragment.FileFragment;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStateAdapter {

    ArrayList<String> listTab;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<String> listTab) {
        super(fragmentActivity);
        this.listTab = listTab;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = DatabaseFragment.newInstance();
        if (position == 0){
            fragment = DatabaseFragment.newInstance();
        }
        else if (position == 1){
            fragment = FileFragment.newInstance();
        }
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return listTab.size();
    }
}

