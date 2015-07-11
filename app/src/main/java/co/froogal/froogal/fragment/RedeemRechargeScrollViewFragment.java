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
public class RedeemRechargeScrollViewFragment extends ScrollViewFragment {

    TableLayout table_layout;
    basic_utils bu;

    public static final String TAG = RedeemRechargeScrollViewFragment.class.getSimpleName();

    public static Fragment newInstance(int position) {
        RedeemRechargeScrollViewFragment fragment = new RedeemRechargeScrollViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public RedeemRechargeScrollViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        bu = new basic_utils(getActivity());
        View view = inflater.inflate(R.layout.fragment_rewards_scroll_view, container, false);
        mScrollView = (NotifyingScrollView) view.findViewById(R.id.scrollview);
        setScrollViewOnScrollListener();

        table_layout = (TableLayout) view.findViewById(R.id.tableLayout);

        new ProcessRewards().execute();


        setScrollViewOnScrollListener();

        return view;
    }


    private void BuildTable(int rows, int cols, List<RewardInfo> result) {

        // outer for loop

        TableRow row = new TableRow(getActivity());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        row.setBackgroundResource(R.drawable.abc_list_pressed_holo_dark);
        // inner for loop
        for (int j = 1; j <= cols; j++) {

            TextView tv = new TextView(getActivity());
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            tv.setMinHeight((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 40, this.getResources()
                            .getDisplayMetrics()));
            tv.setMinWidth((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 100, this.getResources()
                            .getDisplayMetrics()));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            Typeface myfont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
            tv.setTypeface(myfont);
            tv.setPadding(2, 5, 2, 5);
            if(j == 1){
                tv.setText("order-ID");
            }
            else if(j == 2){
                tv.setText("Description");
            }
            else{
                tv.setText("Amount");
            }

            row.addView(tv);

        }

        table_layout.addView(row);


        for (int i = 0; i < rows; i++) {

            row = new TableRow(getActivity());
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            row.setBackgroundResource(R.drawable.abc_item_background_holo_light);
            // inner for loop
            for (int j = 1; j <= cols; j++) {

                TextView tv = new TextView(getActivity());
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setMinHeight((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 40, this.getResources()
                                .getDisplayMetrics()));
                tv.setMinWidth((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 100, this.getResources()
                                .getDisplayMetrics()));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                Typeface myfont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
                tv.setTypeface(myfont);

                tv.setPadding(2, 5, 2, 5);
                if(j == 1){
                    tv.setText(result.get(i).orderID);
                }
                else if(j == 2){
                    tv.setText(result.get(i).description);
                }
                else{
                    tv.setText(result.get(i).amount);
                }

                row.addView(tv);

            }

            table_layout.addView(row);

        }
    }


    private class ProcessRewards extends AsyncTask<String, String, JSONObject> {

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

            BuildTable(noOfRewards, 3, result);


        }

    }


}
