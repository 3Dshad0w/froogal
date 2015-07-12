package co.froogal.froogal.fragment;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.froogal.froogal.R;
import co.froogal.froogal.adapter.RewardInfo;
import co.froogal.froogal.library.NotifyingScrollView;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.ScrollViewFragment;

/**
 * Created by akhil on 17/6/15.
 */
public class RewardsRecievedScrollViewFragment extends ScrollViewFragment {

    TableLayout table_layout;
    basic_utils bu;

    public static final String TAG = RewardsRecievedScrollViewFragment.class.getSimpleName();

    public static Fragment newInstance(int position) {
        RewardsRecievedScrollViewFragment fragment = new RewardsRecievedScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RewardsRecievedScrollViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        bu = new basic_utils(getActivity());
        View view = inflater.inflate(R.layout.fragment_rewards_scroll_view, container, false);
        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        setScrollViewOnScrollListener();

        table_layout = (TableLayout) view.findViewById(R.id.tableLayout);

        new ProcessRewards(inflater).execute();


        setScrollViewOnScrollListener();

        return view;
    }


    private void BuildTable(int rows, List<RewardInfo> result, LayoutInflater inflater) {

        // outer for loop
        View tr = inflater.inflate(R.layout.row_item_recieved_header, null,false);


        table_layout.addView(tr);


        for (int i = 0; i < rows; i++) {

            View trin = inflater.inflate(R.layout.row_item_recieved_child, null,false);;
            // inner for loop
            TextView id = (TextView) trin.findViewById(R.id.orderID);
            TextView description = (TextView) trin.findViewById(R.id.description);
            TextView amount = (TextView) trin.findViewById(R.id.amount);
            id.setText(result.get(i).orderID);
            description.setText(result.get(i).description);
            amount.setText(result.get(i).amount);

            table_layout.addView(trin);

        }



    }


    private class ProcessRewards extends AsyncTask<String, String, JSONObject> {
        LayoutInflater inflater;
        public ProcessRewards(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.getRewardsList(bu.get_defaults("uid"));
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {

            List<RewardInfo> result = new ArrayList<RewardInfo>();
            int noOfRewards = 0;
            try {

                JSONObject rewardsJson = json.getJSONObject("rewards");
                noOfRewards = rewardsJson.length();
                int i = 0;
                for (i = 0 ; i < noOfRewards ; i++) {
                    JSONObject rewardJson = null;
                    try {
                        rewardJson = rewardsJson.getJSONObject("'" + i + "'");
                        RewardInfo ci = new RewardInfo();
                        ci.orderID = rewardJson.getString("order_id");
                        ci.description = rewardJson.getString("description");
                        ci.amount = rewardJson.getString("reward");
                        ci.type = rewardJson.getString("rewardType");
                        ci.fromID = rewardJson.getString("from_id");
                        result.add(ci);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            BuildTable(noOfRewards, result, inflater);


        }

    }


}
