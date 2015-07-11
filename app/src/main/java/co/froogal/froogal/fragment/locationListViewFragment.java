package co.froogal.froogal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.froogal.froogal.ForgetPasswordActivity;
import co.froogal.froogal.R;
import co.froogal.froogal.adapter.RestaurantAdapter;
import co.froogal.froogal.adapter.RestaurantInfo;

/**
 * Created by akhil on 24/5/15.
 */
public class locationListViewFragment extends Fragment {

    private RecyclerView recList;
    List<RestaurantInfo> list;
    RestaurantAdapter ca;

    public static locationListViewFragment newInstance(JSONObject restaurants) {
        locationListViewFragment fragment = new locationListViewFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        if(restaurants != null)
            args.putString("values", restaurants.toString());
        else{
            args.putString("values", "");
        }
        fragment.setArguments(args);


        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        recList.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        Bundle args = getArguments();
        JSONObject resJson = null;
        if(!args.getString("values").equals("")) {
            try {
                resJson = new JSONObject(args.getString("values"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            resJson = null;
        }

        list = createList(resJson);
        //Collections.sort(list, Comparator);
        ca = new RestaurantAdapter(list);


        recList.setAdapter(ca);

        return rootView;
    }

    public static Comparator<RestaurantInfo> Comparator = new Comparator<RestaurantInfo>() {

        @Override
        public int compare(RestaurantInfo lhs, RestaurantInfo rhs) {
            if(lhs.checkedIN){
                return 1;
            }
            return 0;
        }

    };


    private List<RestaurantInfo> createList(JSONObject json) {


        List<RestaurantInfo> result = new ArrayList<RestaurantInfo>();

        JSONObject restaurantsJson = null;

        //restaurantsJson = json.getJSONObject("restaurants");
        restaurantsJson = json;
        if(restaurantsJson != null) {
            Log.d("restaurantsJson", restaurantsJson.toString());
            int noOfRestaurants = restaurantsJson.length();
            int i = 0;
            for (i = 0; i < noOfRestaurants; i++) {
                JSONObject restaurantJson = null;
                try {
                    restaurantJson = restaurantsJson.getJSONObject("'" + i + "'");

                    RestaurantInfo ci = new RestaurantInfo();
                    ci.resName = restaurantJson.getString("name");
                    ci.resAddress = restaurantJson.getString("address");
                    ci.phoneNumber = restaurantJson.getString("phone_number");
                    ci.reward = restaurantJson.getString("reward");
                    ci.coupon = restaurantJson.getString("coupon");
                    ci.distance = restaurantJson.getString("latitude") + restaurantJson.getString("longitude");
                    ci.resID = restaurantJson.getString("restaurant_id");
                    ci.latitude = restaurantJson.getString("latitude");
                    ci.longitude = restaurantJson.getString("longitude");
                    ci.isFav = restaurantJson.getString("isFav");
                    result.add(ci);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }

        return result;
    }

    public void updateList(JSONObject json){

        List<RestaurantInfo> result = new ArrayList<RestaurantInfo>();

        JSONObject restaurantsJson = null;

        //restaurantsJson = json.getJSONObject("restaurants");
        restaurantsJson = json;

        Log.d("restaurantsJson", restaurantsJson.toString());
        int noOfRestaurants = restaurantsJson.length();
        int i = 0;
        for (i = 0 ; i < noOfRestaurants ; i++) {
            JSONObject restaurantJson = null;
            try {
                restaurantJson = restaurantsJson.getJSONObject("'" + i + "'");

                RestaurantInfo ci = new RestaurantInfo();
                ci.resName = restaurantJson.getString("name");
                ci.resAddress = restaurantJson.getString("address");
                ci.phoneNumber = restaurantJson.getString("phone_number");
                ci.reward = restaurantJson.getString("reward");
                ci.coupon = restaurantJson.getString("coupon");
                ci.distance = restaurantJson.getString("latitude") + restaurantJson.getString("longitude");
                ci.resID = restaurantJson.getString("restaurant_id");
                ci.latitude = restaurantJson.getString("latitude");
                ci.longitude = restaurantJson.getString("longitude");
                ci.isFav = restaurantJson.getString("isFav");

                result.add(ci);

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

       list = result;
       ca.updateData(result);
    }

}