package co.froogal.froogal.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import co.froogal.froogal.R;
import co.froogal.froogal.adapter.ExpandableHeightGridView;
import co.froogal.froogal.adapter.ImageAdapter;
import co.froogal.froogal.library.NotifyingScrollView;
import co.froogal.froogal.loyaltyActivity;
import co.froogal.froogal.otp_activity;
import co.froogal.froogal.services.registration_intent_service;
import co.froogal.froogal.view.RecyclerViewFragment;
import co.froogal.froogal.view.ScrollViewFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class OffersViewFragment extends ScrollViewFragment {


    public static final String TAG = OffersViewFragment.class.getSimpleName();

    public static Fragment newInstance(int position) {
        OffersViewFragment fragment = new OffersViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public OffersViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        View view = inflater.inflate(R.layout.fragment_offers_scroll_view, container, false);
        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.addressCardLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), loyaltyActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        setScrollViewOnScrollListener();




        return view;
    }



}

