package co.froogal.froogal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

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

        RestaurantAdapter ca = new RestaurantAdapter(createList(30));
        /*
        * private class ProcessRestaurants extends AsyncTask<String, String, JSONObject> {


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

            JSONObject json = userFunction.getRestaurants("1");

            Log.d("RestaurantFlashing", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {


            List<RestaurantInfo> result = new ArrayList<RestaurantInfo>();

            JSONObject restaurantsJson = null;

            restaurantsJson = json.getJSONObject("restaurants");
                Log.d("restaurantsJson", restaurantsJson.toString());
                int noOfRestaurants = restaurantsJson.length();
                int i = 0;
            for (i = 0 ; i < noOfRestaurants ; i++) {
                JSONObject restaurantJson = null;
                restaurantJson = restaurantsJson.getJSONObject("'"+ i +"'");

            RestaurantInfo ci = new RestaurantInfo();
            ci.resName = restaurantJson.getString("name");
            ci.resAddress = restaurantJson.getString("address");

            result.add(ci);

        }

        return result;
        * */
        recList.setAdapter(ca);
        /**/

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


}