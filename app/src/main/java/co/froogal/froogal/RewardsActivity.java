package co.froogal.froogal;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.ImageView;

import org.json.JSONException;

import co.froogal.froogal.fragment.AboutScrollViewFragmentTemp;
import co.froogal.froogal.fragment.MenuFinalOrderViewFragment;
import co.froogal.froogal.fragment.MenuOrderViewFragment;
import co.froogal.froogal.fragment.ReviewFragment;
import co.froogal.froogal.fragment.RewardsScrollViewFragment;
import co.froogal.froogal.slidingTab.SlidingTabLayout;
import co.froogal.froogal.view.ParallaxFragmentPagerAdapter;
import co.froogal.froogal.view.ParallaxViewPagerBaseActivity;

/**
 * Created by akhil on 8/7/15.
 */
public class RewardsActivity extends ParallaxViewPagerBaseActivity {

    private ImageView mTopImage;
    private SlidingTabLayout mNavigBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rewards);

        mTopImage = (ImageView) findViewById(R.id.image);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNavigBar = (SlidingTabLayout) findViewById(R.id.navig_tab);
        mHeader = findViewById(R.id.header);

        SpannableString s = new SpannableString("Rewards");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mNumFragments = 2;

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initValues();
        setupAdapter();

        if (savedInstanceState != null) {
            mTopImage.setTranslationY(savedInstanceState.getFloat(IMAGE_TRANSLATION_Y));
            mHeader.setTranslationY(savedInstanceState.getFloat(HEADER_TRANSLATION_Y));
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putFloat(IMAGE_TRANSLATION_Y, mTopImage.getTranslationY());
        outState.putFloat(HEADER_TRANSLATION_Y, mHeader.getTranslationY());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {

        super.onStop();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initValues() {
        int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height_order);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height_order);
        mMinHeaderTranslation = -mMinHeaderHeight + tabHeight ;
    }

    @Override
    protected void scrollHeader(int scrollY) {
        float translationY = Math.max(-scrollY, mMinHeaderTranslation);
        mHeader.setTranslationY(translationY);
        mTopImage.setTranslationY(-translationY / 3);
    }

    @Override
    protected void setupAdapter() {
        if (mAdapter == null) {
            mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mNumFragments);
        }

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mNumFragments);
        mNavigBar.setOnPageChangeListener(getViewPagerChangeListener());
        mNavigBar.setViewPager(mViewPager);
    }

    private static class ViewPagerAdapter extends ParallaxFragmentPagerAdapter {

        int numFragments;
        public ViewPagerAdapter(FragmentManager fm, int numFragments) {
            super(fm, numFragments);
            this.numFragments = numFragments;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            if(position ==  numFragments){
                return RewardsScrollViewFragment.newInstance(position);
            }
            fragment = RewardsScrollViewFragment.newInstance(position);
            return fragment;
        }
        @Override
        public CharSequence getPageTitle(int position) {

            String title = "";

            if(position == 0){
                title = "Recieved";
            }
            else{
                title = "spent";
            }

            return title;

        }
    }


}
