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

import java.util.ArrayList;
import java.util.List;

import co.froogal.froogal.ForgetPasswordActivity;
import co.froogal.froogal.R;
import co.froogal.froogal.adapter.RestaurantAdapter;
import co.froogal.froogal.adapter.RestaurantInfo;

/**
 * Created by akhil on 24/5/15.
 */
public class locationListViewFragment extends Fragment {

    public static locationListViewFragment newInstance() {
        return new locationListViewFragment();
    }

    private RecyclerView recList;
    List<RestaurantInfo> list;
    RestaurantAdapter ca;

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

        list = createList(10);
        ca = new RestaurantAdapter(list);

        recList.setAdapter(ca);

        return rootView;
    }



    private List<RestaurantInfo> createList(int size) {

        List<RestaurantInfo> result = new ArrayList<RestaurantInfo>();
        for (int i=1; i <= size; i++) {
            RestaurantInfo ci = new RestaurantInfo();
            ci.resName = RestaurantInfo.RES_NAME + i;
            ci.resAddress = RestaurantInfo.ADDRESS + i;

            result.add(ci);

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
            result.add(ci);

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        ca.updateList(result);
    }

}