package co.froogal.froogal.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import co.froogal.froogal.R;
import co.froogal.froogal.adapter.RestaurantInfo;
import co.froogal.froogal.adapter.ReviewAdapter;
import co.froogal.froogal.adapter.ReviewInfo;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.RecyclerViewFragment;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends RecyclerViewFragment {

    public static final String TAG = ReviewFragment.class.getSimpleName();

    private LinearLayoutManager mLayoutMgr;

    public static Fragment newInstance(int position) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        View view = inflater.inflate(R.layout.fragment_review_view, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        new ProcessReviews().execute();
        return view;
    }

    @Override
    protected void setScrollOnLayoutManager(int scrollY) {
        mLayoutMgr.scrollToPositionWithOffset(0, -scrollY);
    }



    private List<String> createItemList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("Item " + i);
        }
        return list;
    }

    private class ProcessReviews extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Resources ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            basic_utils bu = new basic_utils(getActivity());

            //JSONObject json = null;//
            JSONObject json = userFunction.getReviews("1", bu.get_defaults("uid"));

            Log.d("WOWOWOWOW", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {





            List<ReviewInfo> result = new ArrayList<ReviewInfo>();
            String isReviewPresent = "0";
            JSONObject reviewsJson = null;

            try {
                reviewsJson = json.getJSONObject("reviews");

                isReviewPresent = reviewsJson.getString("isReviewPresent");
            Log.d("reviewsJson", reviewsJson.toString());
            int noOfReviews = reviewsJson.length();
            int i = 0;
            for (i = 0 ; i < noOfReviews ; i++) {
                JSONObject reviewJson = null;
                try {
                    reviewJson = reviewsJson.getJSONObject("'" + i + "'");

                    ReviewInfo ci = new ReviewInfo();
                    ci.userName = reviewJson.getString("firstname");
                    ci.image_url = reviewJson.getString("image_url");
                    ci.date = reviewJson.getString("date(reviews.date)");
                    ci.datetime = reviewJson.getString("date");
                    ci.description = reviewJson.getString("description");
                    ci.title = reviewJson.getString("title");
                    ci.rating = reviewJson.getString("rating");
                    ci.uid = reviewJson.getString("uid");
                    result.add(ci);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Collections.sort(result, Comparator);


            mLayoutMgr = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutMgr);
            ReviewAdapter reviewAdapter = null;
            basic_utils bu = new basic_utils(getActivity());
            reviewAdapter = new ReviewAdapter(result, isReviewPresent, bu.get_defaults("uid"), bu.get_defaults("fname"), bu.get_defaults("image_url"));
            mRecyclerView.setAdapter(reviewAdapter);

            setRecyclerViewOnScrollListener();
            pDialog.dismiss();

        }

        public java.util.Comparator<ReviewInfo> Comparator = new Comparator<ReviewInfo>() {

            @Override
            public int compare(ReviewInfo lhs, ReviewInfo rhs) {
            java.sql.Timestamp date1 = Timestamp.valueOf(lhs.datetime);
            java.sql.Timestamp date2 = Timestamp.valueOf(rhs.datetime);

                if(date1.compareTo(date2) > 0){
                return 1;
            }
                return 0;
            }



        };


    }


}
