package co.circe.respos.fragment;


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

import java.util.ArrayList;
import java.util.List;

import co.circe.respos.R;
import co.circe.respos.ResDetailsActivity;
import co.circe.respos.adapter.OfferInfo;
import co.circe.respos.adapter.OffersAdapter;
import co.circe.respos.library.UserFunctions;
import co.circe.respos.util.basic_utils;
import co.circe.respos.view.RecyclerViewFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class OffersFragment extends RecyclerViewFragment {

    public static final String TAG = OffersFragment.class.getSimpleName();

    private LinearLayoutManager mLayoutMgr;

    public static Fragment newInstance(int position) {
        OffersFragment fragment = new OffersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public OffersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPosition = getArguments().getInt(ARG_POSITION);

        View view = inflater.inflate(R.layout.fragment_offers_view, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        new ProcessOffers().execute();
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

    private class ProcessOffers extends AsyncTask<String, String, JSONObject> {


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
            JSONObject json = userFunction.getOffers(ResDetailsActivity.resID);

            Log.d("WOWOWOWOW", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {





            List<OfferInfo> result = new ArrayList<>();
            JSONObject offersJson = null;

            try {
                offersJson = json.getJSONObject("offers");

            Log.d("offersJson", offersJson.toString());
            int noOfOffers = offersJson.length();
            int i = 0;
            for (i = 0 ; i < noOfOffers; i++) {
                JSONObject offerJson = null;
                try {
                    offerJson = offersJson.getJSONObject("'" + i + "'");

                    OfferInfo ci = new OfferInfo();
                    ci.type = offerJson.getString("type");
                    if(ci.type.equals("bogo")){
                        ci.buy = offerJson.getString("buy");
                        ci.get = offerJson.getString("get");
                        ci.discount = null;
                        ci.productID = null;
                        ci.rewardPerecentage = null;
                        ci.loyaltyCardID = null;
                        ci.resID = ResDetailsActivity.resID;

                    }
                    else if(ci.type.equals("discount")){
                        ci.discount = offerJson.getString("discount");
                        ci.productID = offerJson.getString("productID");
                        ci.buy = null;
                        ci.get = null;
                        ci.rewardPerecentage = null;
                        ci.loyaltyCardID = null;
                        ci.resID = ResDetailsActivity.resID;

                    }
                    else if(ci.type.equals("rewards")){
                        ci.discount = null;
                        ci.productID = null;
                        ci.buy = null;
                        ci.get = null;
                        ci.rewardPerecentage = offerJson.getString("rewardsPercentage");
                        ci.loyaltyCardID = null;
                        ci.resID = ResDetailsActivity.resID;

                    }
                    else if(ci.type.equals("loyaltyCard")){
                        ci.discount = null;
                        ci.productID = null;
                        ci.buy = null;
                        ci.get = null;
                        ci.rewardPerecentage = null;
                        ci.loyaltyCardID = offerJson.getString("loyaltyCardID");
                        ci.resID = ResDetailsActivity.resID;

                    }



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
            OffersAdapter offersAdapter = null;
            basic_utils bu = new basic_utils(getActivity());
            offersAdapter = new OffersAdapter(result);
            mRecyclerView.setAdapter(offersAdapter);

            setRecyclerViewOnScrollListener();
            pDialog.dismiss();

        }




    }


}
