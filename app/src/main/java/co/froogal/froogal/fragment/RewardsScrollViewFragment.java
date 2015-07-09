package co.froogal.froogal.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import co.froogal.froogal.R;
import co.froogal.froogal.adapter.ExpandableHeightGridView;
import co.froogal.froogal.adapter.ImageAdapter;
import co.froogal.froogal.library.NotifyingScrollView;
import co.froogal.froogal.view.ScrollViewFragment;

/**
 * Created by akhil on 17/6/15.
 */
public class RewardsScrollViewFragment extends ScrollViewFragment {

    TableLayout table_layout;

    public static final String TAG = RewardsScrollViewFragment.class.getSimpleName();

    public static Fragment newInstance(int position) {
        RewardsScrollViewFragment fragment = new RewardsScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RewardsScrollViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        View view = inflater.inflate(R.layout.fragment_rewards_scroll_view, container, false);
        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        setScrollViewOnScrollListener();

        table_layout = (TableLayout) view.findViewById(R.id.tableLayout);

        BuildTable(15, 3);

        setScrollViewOnScrollListener();

        return view;
    }


    private void BuildTable(int rows, int cols) {

        // outer for loop
        for (int i = 1; i <= rows; i++) {

            TableRow row = new TableRow(getActivity());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // inner for loop
            for (int j = 1; j <= cols; j++) {

                TextView tv = new TextView(getActivity());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setBackgroundResource(R.drawable.abc_item_background_holo_light);
                tv.setPadding(5, 5, 5, 5);
                tv.setText("R " + i + ", C" + j);

                row.addView(tv);

            }

            table_layout.addView(row);

        }
    }



}
