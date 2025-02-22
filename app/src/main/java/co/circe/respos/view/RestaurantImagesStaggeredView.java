package co.circe.respos.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.AbsListView;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;

import co.circe.respos.R;
import co.circe.respos.adapter.StaggeredAdapter;

/**
 * Created by akhil on 17/6/15.
 */
public class RestaurantImagesStaggeredView extends ActionBarActivity implements AbsListView.OnScrollListener {
    private static final String TAG = "StaggeredGridActivity";
    public static final String SAVED_DATA_KEY = "SAVED_DATA";
    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private StaggeredAdapter mAdapter;
    private ArrayList<String> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpannableString s = new SpannableString("Froogal");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_sgv);
        setTitle("TechnoTalkative - SGV Demo");
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        mAdapter = new StaggeredAdapter(this,android.R.layout.simple_list_item_1, generateData());
// do we have saved data?
        if (savedInstanceState != null) {
            mData = savedInstanceState.getStringArrayList(SAVED_DATA_KEY);
        }
        if (mData == null) {
            mData = generateData();
        }
        for (String data : mData) {
            mAdapter.add(data);
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(this);

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(SAVED_DATA_KEY, mData);
    }
    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:" + scrollState);
    }
    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        Log.d(TAG, "onScroll firstVisibleItem:" + firstVisibleItem +
                " visibleItemCount:" + visibleItemCount +
                " totalItemCount:" + totalItemCount);
// our handling
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                Log.d(TAG, "onScroll lastInScreen - so load more");
                mHasRequestedMore = true;
                onLoadMoreItems();
            }
        }
    }
    private void onLoadMoreItems() {
        final ArrayList<String> sampleData = generateData();
        for (String data : sampleData) {
            mAdapter.add(data);
        }
// stash all the data in our backing store
        mData.addAll(sampleData);
// notify the adapter that we can update now
        mAdapter.notifyDataSetChanged();
        mHasRequestedMore = false;
    }
    private ArrayList<String> generateData() {
        ArrayList<String> listData = new ArrayList<String>();
        listData.add("http://i62.tinypic.com/2iitkhx.jpg");
        listData.add("http://i61.tinypic.com/w0omeb.jpg");
        listData.add("http://i60.tinypic.com/w9iu1d.jpg");
        listData.add("http://i60.tinypic.com/iw6kh2.jpg");
        listData.add("http://i57.tinypic.com/ru08c8.jpg");
        listData.add("http://i60.tinypic.com/k12r10.jpg");
        listData.add("http://i58.tinypic.com/2e3daug.jpg");
        listData.add("http://i59.tinypic.com/2igznfr.jpg");
        return listData;
    }

}
