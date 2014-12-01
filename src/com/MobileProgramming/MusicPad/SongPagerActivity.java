package com.MobileProgramming.MusicPad;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class SongPagerActivity extends FragmentActivity {
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("keyHere", "pager created");
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        final ArrayList<Song> songs = SongLab.get(this).getSongs();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return songs.size();
            }
            @Override
            public Fragment getItem(int pos) {
                UUID crimeId =  songs.get(pos).getId();
                return SongFragment.newInstance(crimeId);
            }
        }); 

        UUID crimeId = (UUID)getIntent().getSerializableExtra(SongFragment.EXTRA_SONG_ID);
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            } 
        }
    }
}
