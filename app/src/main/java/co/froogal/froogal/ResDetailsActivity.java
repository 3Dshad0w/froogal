package co.froogal.froogal;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.widget.ImageView;

import co.froogal.froogal.fragment.AboutScrollViewFragment;
import co.froogal.froogal.fragment.MenuListViewFragment;
import co.froogal.froogal.fragment.OffersViewFragment;
import co.froogal.froogal.slidingTab.SlidingTabLayout;
import co.froogal.froogal.view.ParallaxFragmentPagerAdapter;
import co.froogal.froogal.view.ParallaxViewPagerBaseActivity;
import co.froogal.froogal.fragment.DemoRecyclerViewFragment;


public class ResDetailsActivity extends ParallaxViewPagerBaseActivity {

    private ImageView mTopImage;
    private SlidingTabLayout mNavigBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resdetails);

        initValues();

        mTopImage = (ImageView) findViewById(R.id.image);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNavigBar = (SlidingTabLayout) findViewById(R.id.navig_tab);
        mHeader = findViewById(R.id.header);
        SpannableString s = new SpannableString("Restaurant Name");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       /* ImageView call = (ImageView) findViewById(R.id.call);
        ImageView directions = (ImageView) findViewById(R.id.directions);
        ImageView rating = (ImageView) findViewById(R.id.rating);
        ImageView favourite = (ImageView) findViewById(R.id.favourite);

        Uri calluri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getResources().getResourcePackageName(android.R.drawable.sym_action_call) + '/' +
                getResources().getResourceTypeName(android.R.drawable.sym_action_call) + '/' +
                getResources().getResourceEntryName(android.R.drawable.sym_action_call) );

        Uri directionsuri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getResources().getResourcePackageName(android.R.drawable.ic_menu_directions) + '/' +
                getResources().getResourceTypeName(android.R.drawable.ic_menu_directions) + '/' +
                getResources().getResourceEntryName(android.R.drawable.ic_menu_directions) );
        Uri rateuri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getResources().getResourcePackageName(android.R.drawable.star_on) + '/' +
                getResources().getResourceTypeName(android.R.drawable.star_on) + '/' +
                getResources().getResourceEntryName(android.R.drawable.star_on) );
        Uri favuri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getResources().getResourcePackageName(android.R.drawable.presence_away) + '/' +
                getResources().getResourceTypeName(android.R.drawable.presence_away) + '/' +
                getResources().getResourceEntryName(android.R.drawable.presence_away) );

        ImageUtil.displayRoundImage(call, String.valueOf(calluri), null);
        ImageUtil.displayRoundImage(directions, String.valueOf(directionsuri), null);
        ImageUtil.displayRoundImage(rating, String.valueOf(rateuri), null);
        ImageUtil.displayRoundImage(favourite, String.valueOf(favuri), null);
        */
        if (savedInstanceState != null) {
            mTopImage.setTranslationY(savedInstanceState.getFloat(IMAGE_TRANSLATION_Y));
            mHeader.setTranslationY(savedInstanceState.getFloat(HEADER_TRANSLATION_Y));
        }

        setupAdapter();




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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initValues() {
        int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        mMinHeaderHeight = getResources().getDimensionPixelSize(R.dimen.min_header_height);
        mHeaderHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
        mMinHeaderTranslation = -mMinHeaderHeight + tabHeight + tabHeight + tabHeight;

        mNumFragments = 4;
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
    protected void setupAdapter() {
        if (mAdapter == null) {
            mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mNumFragments);
        }

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mNumFragments);
        mNavigBar.setOnPageChangeListener(getViewPagerChangeListener());
        mNavigBar.setViewPager(mViewPager);
    }

    @Override
    protected void scrollHeader(int scrollY) {
        float translationY = Math.max(-scrollY, mMinHeaderTranslation);
        mHeader.setTranslationY(translationY);
        mTopImage.setTranslationY(-translationY / 3);
    }

//    private int getActionBarHeight() {
//        if (mActionBarHeight != 0) {
//            return mActionBarHeight;
//        }
//
//        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
//            getTheme().resolveAttribute(android.R.attr.actionBarSize, mTypedValue, true);
//        } else {
//            getTheme().resolveAttribute(R.attr.actionBarSize, mTypedValue, true);
//        }
//
//        mActionBarHeight = TypedValue.complexToDimensionPixelSize(
//                mTypedValue.data, getResources().getDisplayMetrics());
//
//        return mActionBarHeight;
//    }

    private static class ViewPagerAdapter extends ParallaxFragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm, int numFragments) {
            super(fm, numFragments);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = OffersViewFragment.newInstance(0);
                    break;

                case 1:
                    fragment = AboutScrollViewFragment.newInstance(1);
                    break;

                case 2:
                    fragment = DemoRecyclerViewFragment.newInstance(2);
                    break;

                case 3:
                    fragment = MenuListViewFragment.newInstance(3);
                    break;

                default:
                    throw new IllegalArgumentException("Wrong page given " + position);
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Offers";

                case 1:
                    return "About";

                case 2:
                    return "Reviews";

                case 3:
                    return "Menu";

                default:
                    throw new IllegalArgumentException("wrong position for the fragment in vehicle page");
            }
        }
    }
}
