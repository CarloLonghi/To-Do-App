package com.carlolonghi.todo.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.carlolonghi.todo.fragments.MenuActivity;
import com.carlolonghi.todo.fragments.TodaysActivity;
import com.carlolonghi.todo.R;

public class ScreenSlidePagerActivity extends AppCompatActivity {


    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mPager);

        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.actionbar_background));

    }

    @Override
    public void onBackPressed() {
        if(mPager.getCurrentItem()==0)
            super.onBackPressed();
        else
            mPager.setCurrentItem(0);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return new TodaysActivity();
            else
                return new MenuActivity();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position){
            if(position==0)
                return "Today";
            else
                return "Lists";
        }
    }

    public void onPause(){
        //model.updateItemsOnFile(this.getBaseContext());

        super.onPause();
    }

}
