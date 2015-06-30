package co.froogal.froogal.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.view.AnimatedExpandableListView;
import co.froogal.froogal.view.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import co.froogal.froogal.view.ExpandableListViewFragment;

import co.froogal.froogal.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuListViewFragment extends ExpandableListViewFragment {

    public static final String TAG = MenuListViewFragment.class.getSimpleName();
    private MenuAdapter adapter;


    public static Fragment newInstance(int position) {
        MenuListViewFragment fragment = new MenuListViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuListViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPosition = getArguments().getInt(ARG_POSITION);

        View view = inflater.inflate(R.layout.activity_expandable_list_view, container, false);
        mListView = (AnimatedExpandableListView) view.findViewById(R.id.list_view);
        View placeHolderView = inflater.inflate(R.layout.header_placeholder, mListView, false);
        mListView.addHeaderView(placeHolderView);


        new ProcessMenu().execute();


        return view;
    }

    private static class GroupItem {
        String title;
        String categoryID;
        List<ChildItem> items = new ArrayList<ChildItem>();

    }

    private static class ChildItem {
        String title;
        String itemID;
        String price;
        String rating;
        String description;
        //String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView price;
        TextView rating;
        TextView description;
        //TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }


    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class MenuAdapter extends AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public MenuAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition,
                                     boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.list_item, parent,
                        false);
                holder.title = (TextView) convertView
                        .findViewById(R.id.textTitle);
                holder.price = (TextView) convertView
                        .findViewById(R.id.textPrice);
                holder.rating = (TextView) convertView
                        .findViewById(R.id.textRating);
                holder.description = (TextView) convertView
                        .findViewById(R.id.textDescription);

				/*holder.hint = (TextView) convertView
						.findViewById(R.id.textHint);*/
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);
            holder.price.setText(item.price);
            holder.rating.setText(item.rating);
            holder.description.setText(item.description);

            //holder.hint.setText(item.hint);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group_item, parent,
                        false);
                holder.title = (TextView) convertView
                        .findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }


    }

    private class ProcessMenu extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String email, password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Getting Menu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();
            Log.d("redmenu", "true");
            JSONObject json = userFunction.getMenu("1");

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            List<GroupItem> menu = new ArrayList<GroupItem>();
            int i = 0, j = 0;
            JSONObject menuJson = null;
            try {
                menuJson = json.getJSONObject("menu");
                Log.d("menuJson", menuJson.toString());
                int noOfCategories = menuJson.length();

                for (i = 0 ; i< noOfCategories; i++){
                    JSONObject categoryJson = null;
                    GroupItem item = new GroupItem();
                    try {
                        categoryJson = menuJson.getJSONObject("'"+ i +"'");
                        item.title = categoryJson.getString("name") +" status: " +categoryJson.getString("status");
                        Log.d("categoryJson", categoryJson.toString());
                        JSONObject itemsJson = null;
                        try {
                            itemsJson = categoryJson.getJSONObject("items");
                            Log.d("itemSSJson", itemsJson.toString());
                            int noOfitems = itemsJson.length();

                            for (j = 0; j < noOfitems; j++) {

                                JSONObject itemJson = null;
                                itemJson = itemsJson.getJSONObject("'" + j + "'");
                                Log.d("itemJson", itemJson.toString());
                                ChildItem child = new ChildItem();
                                child.title = itemJson.getString("name") + "(size : " + itemJson.getString("size") + ")";
                                child.price = itemJson.getString("price");
                                child.rating = itemJson.getString("rating");
                                child.description = "Description: " + itemJson.getString("description");
                                item.items.add(child);


                            }
                        }
                        catch (JSONException e1){
                            e1.printStackTrace();
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    menu.add(item);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }



            adapter = new MenuAdapter(getActivity());
            adapter.setData(menu);


            mListView.setAdapter(adapter);

            // In order to show animations, we need to use a custom click handler
            // for our ExpandableListView.
            mListView.setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    // We call collapseGroupWithAnimation(int) and
                    // expandGroupWithAnimation(int) to animate group
                    // expansion/collapse.
                    if (mListView.isGroupExpanded(groupPosition)) {
                        mListView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        mListView.expandGroupWithAnimation(groupPosition);
                    }
                    return true;
                }

            });


            // Set indicator (arrow) to the right
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            //Log.v("width", width + "");
            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    50, r.getDisplayMetrics());
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mListView.setIndicatorBounds(width - px, width);
            } else {
                mListView.setIndicatorBoundsRelative(width - px, width);
            }


            setListViewOnScrollListener();
            pDialog.dismiss();
        }

    }
}
