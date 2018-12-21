package co.circe.respos.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import co.circe.respos.R;
import co.circe.respos.library.NotifyingScrollView;
import co.circe.respos.loyaltyActivity;
import co.circe.respos.view.ScrollViewFragment;


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
                startActivity(i);
            }
        });
        setScrollViewOnScrollListener();




        return view;
    }



}

