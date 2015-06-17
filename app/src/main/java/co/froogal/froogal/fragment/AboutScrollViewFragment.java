package co.froogal.froogal.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import co.froogal.froogal.R;
import co.froogal.froogal.adapter.ExpandableHeightGridView;
import co.froogal.froogal.adapter.ImageAdapter;
import co.froogal.froogal.library.NotifyingScrollView;
import co.froogal.froogal.view.ScrollViewFragment;

/**
 * Created by akhil on 17/6/15.
 */
public class AboutScrollViewFragment extends ScrollViewFragment {


    public static final String TAG = AboutScrollViewFragment.class.getSimpleName();
    ExpandableHeightGridView mAppsGrid;
    public static Fragment newInstance(int position) {
        AboutScrollViewFragment fragment = new AboutScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public AboutScrollViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        View view = inflater.inflate(R.layout.fragment_about_scroll_view, container, false);
        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        setScrollViewOnScrollListener();

        mAppsGrid = (ExpandableHeightGridView) view.findViewById(R.id.myId);
        mAppsGrid.setExpanded(true);
        mAppsGrid.setAdapter(new ImageAdapter(getActivity()));



        return view;
    }



}
