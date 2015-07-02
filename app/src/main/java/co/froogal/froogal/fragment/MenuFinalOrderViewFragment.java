package co.froogal.froogal.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.froogal.froogal.MenuOrder;
import co.froogal.froogal.R;
import co.froogal.froogal.ResDetailsActivity;
import co.froogal.froogal.adapter.ProductInfo;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.ImageUtil;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.ListViewFragment;

/**
 * Created by akhil on 26/6/15.
 */
public class MenuFinalOrderViewFragment extends ListViewFragment {

    public static final String TAG = MenuFinalOrderViewFragment.class.getSimpleName();

    static  JSONObject productJson;
    ArrayList<ProductInfo> products;

    MenuAdapter adapter;

    public static Fragment newInstance(int position) {
        MenuFinalOrderViewFragment fragment = new MenuFinalOrderViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuFinalOrderViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mPosition = getArguments().getInt(ARG_POSITION);
        productJson = null;
            try {
                productJson = MenuOrder.menuJson.getJSONObject("'" + mPosition + "'").getJSONObject("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        basic_utils bu = new basic_utils(getActivity());
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        View placeHolderView = inflater.inflate(R.layout.header_placeholder_temp, mListView, false);
        mListView.addHeaderView(placeHolderView);


        products = new ArrayList<ProductInfo>();

        new ProcessOrder(bu.get_defaults("uid"), ResDetailsActivity.resID).execute();


        return view;
    }



    private void setAdapter(int mPosition) {
        if (getActivity() == null) return;


        adapter = new MenuAdapter(getActivity(), this.products);
        mListView.setAdapter(adapter);
        /*
        int size = 20;
        String[] stringArray = new String[size];
        for (int i = 0; i < size; ++i) {
            stringArray[i] = "" + i;
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, stringArray);

        mListView.setAdapter(adapter);
        */

    }

    public  void update(ProductInfo product){
        this.products.add(product);
        adapter.notifyDataSetChanged();
    }


    private class MenuAdapter extends ArrayAdapter<ProductInfo> {

        LayoutInflater mInflater;

        public MenuAdapter(Context context, List<ProductInfo> objects) {

            super(context, R.layout.menu_order_list_item , objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProductHolder holder = null;
            final int pos = position;
            ProductInfo product = getItem(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.menu_order_list_item, null);

                holder = new ProductHolder();

                holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
                holder.cost = (TextView) convertView.findViewById(R.id.costTextView);
                holder.size = (TextView) convertView.findViewById(R.id.sizeTextView);
                holder.type = (TextView) convertView.findViewById(R.id.typeTextView);
                holder.description = (TextView) convertView.findViewById(R.id.description);
                holder.count = (TextView) convertView.findViewById(R.id.countTextView);
                holder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingIndicator);
                holder.plus = (ImageView) convertView.findViewById(R.id.plusImageView);
                holder.minus = (ImageView) convertView.findViewById(R.id.minusImageView);
                convertView.setTag(holder);
            } else {
                holder = (ProductHolder) convertView.getTag();
            }

            holder.itemName.setText(product.itemName);
            holder.cost.setText(product.cost);
            holder.size.setText(product.size);
            holder.type.setText(product.type);
            holder.description.setText(product.description);
            holder.ratingBar.setRating(Float.parseFloat(product.rating));
            holder.count.setText("count: " + product.count);
            ImageUtil.displayImage(holder.itemImage, product.itemImage, null);
            holder.minus.setVisibility(View.INVISIBLE);
            holder.plus.setVisibility(View.INVISIBLE);

            return convertView;
        }


        public class ProductHolder {
            TextView itemName;
            ImageView itemImage;
            TextView size;
            TextView type;
            TextView description;
            TextView cost;
            TextView count;
            ImageView plus;
            ImageView minus;
            RatingBar ratingBar;

        }

    }


    private class ProcessOrder extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String userID,resID;

        public ProcessOrder(String userId, String resID) {
            this.userID = userId;
            this.resID = resID;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Processing Order List ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            JSONObject json = userFunction.getProcessOrder(userID, resID);

            Log.d("keysuccess", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            int noOfitems = 0;
            JSONObject productsJson = null;
            try {
                productsJson = json.getJSONObject("order");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(productsJson != null) {
                noOfitems = productsJson.length();
                Log.d("productJson", productsJson.toString());
            }

            products = new ArrayList<ProductInfo>();
            for (int i = 0; i < noOfitems; ++i) {
                ProductInfo object = new ProductInfo();
                JSONObject productJson = null;
                try {
                    productJson = productsJson.getJSONObject("'" + i + "'");
                    try {
                        object.itemName = productJson.getString("name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        object.cost = productJson.getString("price");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        object.description = productJson.getString("description");
                    } catch (JSONException e) {
                        object.description = "";
                    }
                    try {
                        object.itemID = productJson.getString("product_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        object.itemImage = productJson.getString("image_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        object.size = "size: " + productJson.getString("size");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   /* try {
                        object.type = productJson.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        object.rating = productJson.getString("rating");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        object.count = productJson.getString("quantity");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                object.type = "veg";


                products.add(object);
            }


            Log.d("productsinlist", products.toString());

            setAdapter(mPosition);
            setListViewOnScrollListener();
            pDialog.dismiss();


        }
    }

}

/*
* private class MenuAdapter extends ListAdapter {
        LayoutInflater mInflater;
        String[] nameArray;
        int selectedPosition = 0;

        public MenuAdapter(Context context) {
            super(context, R.layout.menu_order_list_item);
            //nameArray = values;
            mInflater = LayoutInflater.from(context);

        }
        // Override getView which is responsible for creating the rows for our list
        // position represents the index we are in for the array.

        // convertView is a reference to the previous view that is available for reuse. As
        // the user scrolls the information is populated as needed to conserve memory.

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public String getItem(int position) {
            return super.getItem(position);
        }

        // A ViewGroup are invisible containers that hold a bunch of views and
        // define their layout properties.
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            // The LayoutInflator puts a layout into the right View


            ProductHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.menu_order_list_item, null);

                holder = new ProductHolder();

                holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
                holder.cost = (TextView) convertView.findViewById(R.id.costTextView);
                holder.size = (TextView) convertView.findViewById(R.id.sizeTextView);
                holder.type = (TextView) convertView.findViewById(R.id.typeTextView);
                holder.description = (TextView) convertView.findViewById(R.id.description);
                holder.count = (TextView) convertView.findViewById(R.id.countTextView);
                holder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);

                convertView.setTag(holder);
            } else {
                holder = (ProductHolder) convertView.getTag();
            }


            return convertView;

        }


        public class ProductHolder {
            TextView itemName;
            ImageView itemImage;
            TextView size;
            TextView type;
            TextView description;
            TextView cost;
            TextView count;

        }
    }*/