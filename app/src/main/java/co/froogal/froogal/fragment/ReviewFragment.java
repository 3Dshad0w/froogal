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
import co.froogal.froogal.view.RecyclerViewFragment;

import java.util.ArrayList;
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

        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
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

            //JSONObject json = null;//
            JSONObject json = userFunction.getReviews("1");

            Log.d("WOWOWOWOW", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {





            List<ReviewInfo> result = new ArrayList<ReviewInfo>();

            JSONObject reviewsJson = null;

            try {
                reviewsJson = json.getJSONObject("reviews");

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
                    ci.date = reviewJson.getString("date");
                    ci.description = reviewJson.getString("description");
                    ci.title = reviewJson.getString("title");
                    result.add(ci);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            } catch (JSONException e) {
                e.printStackTrace();
            }





            mLayoutMgr = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutMgr);
            ReviewAdapter reviewAdapter = new ReviewAdapter(result);
            mRecyclerView.setAdapter(reviewAdapter);

            setRecyclerViewOnScrollListener();
            pDialog.dismiss();

        }

    }


}
