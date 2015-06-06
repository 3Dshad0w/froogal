package co.froogal.froogal;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import co.froogal.froogal.adapter.PlaceAutocompleteAdapter;
import co.froogal.froogal.fragment.locationListViewFragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.froogal.froogal.adapter.DrawerAdapter;
import co.froogal.froogal.fragment.locationMapViewFragment;
import co.froogal.froogal.library.GMapV2Direction;
import co.froogal.froogal.model.DrawerItem;
import co.froogal.froogal.util.ImageUtil;

import static co.froogal.froogal.LoginActivity.MyPREFERENCES;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, LocationListener {

    //tag
    public static String TAG = "Maps Activity";

    // Map variables
    private GoogleMap map;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(8.06890, 68.03215), new LatLng(35.674520, 97.40238));
    Marker currentmarker;
    public boolean updateposition = false;

    //.Location variables
    protected GoogleApiClient googleapiclientlocation;
    protected Location currentLocation;
    private String latitude;
    private String longitude;
    protected LocationRequest locationrequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //Fragment variables
    protected boolean fragmentback = false;

    //Action Bar variables
    private boolean searchexpanded = false;

    // UI variables
    private static View view;
    private AutoCompleteTextView autocompletetextview;

    //Adapter variables
    private PlaceAutocompleteAdapter adapter;

    //Places Autocomplete variables
    protected GoogleApiClient googleapiclientplaces;


    public static final String LEFT_MENU_OPTION = "co.froogal.froogal.MainActivity";
    public static final String LEFT_MENU_OPTION_1 = "Left Menu Option 1";
    public static final String LEFT_MENU_OPTION_2 = "Left Menu Option 2";

    private ListView mDrawerList;
    private List<DrawerItem> mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    SharedPreferences sharedpreferences;
    String username = "user";

    private Handler mHandler;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Handler for testing purposes - to be removed later
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, gcm_activity.class);
                startActivity(i);
                finish();
            }
        },3000 );

        // Updating values from shared preferences
        if(ConnectionDetector.locationCheck(getApplicationContext())) {

            latitude = ConnectionDetector.getDefaults("latitude", getApplicationContext());
            longitude = ConnectionDetector.getDefaults("longitude", getApplicationContext());
            Log.d(TAG,"Location Updated from shared preference");

        }

        // Update position won't work for 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateposition = true;
            }
        }, 3000);

        // Card flip animation
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, new CardFrontFragment())
                    .commit();
        }

        // location
        buildGoogleApiClient();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Changing action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM |  ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_USE_LOGO);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.actionbar_maps, null);
        actionBar.setCustomView(v);

        // Places AutoComplete
        googleapiclientplaces = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .build();
        googleapiclientplaces.connect();
        adapter = new PlaceAutocompleteAdapter(this, R.layout.listview,googleapiclientplaces, BOUNDS_INDIA, null);

        //UI
        autocompletetextview = (AutoCompleteTextView) findViewById(R.id.autocomplete_places);
        autocompletetextview.setAdapter(adapter);
        autocompletetextview.setOnItemClickListener(autocompleteclicklistener);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        prepareNavigationDrawerItems();
        setAdapter();
        //mDrawerList.setAdapter(new DrawerAdapter(this, mDrawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);



    }

    private void setAdapter() {
        String option = LEFT_MENU_OPTION_2;


        boolean isFirstType = true;

        View headerView = null;

            headerView = prepareHeaderView(R.layout.header_navigation_drawer_2,
                    "http://pengaja.com/uiapptemplate/avatars/0.jpg");


        BaseAdapter adapter = new DrawerAdapter(this, mDrawerItems, isFirstType);

        mDrawerList.addHeaderView(headerView);//Add header before adapter (for pre-KitKat)
        mDrawerList.setAdapter(adapter);
    }

    private View prepareHeaderView(int layoutRes, String url) {
        View headerView = getLayoutInflater().inflate(layoutRes, mDrawerList, false);
        ImageView iv = (ImageView) headerView.findViewById(R.id.image);
        TextView tv = (TextView) headerView.findViewById(R.id.email);
        ImageUtil.displayRoundImage(iv, url, null);
        sharedpreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains("email")) {
            if (sharedpreferences.contains("password")) {
                username = sharedpreferences.getString("fname", "User");
                Log.d("preferencehere", sharedpreferences.getString("fname", "user"));
            }
        }
        tv.setText(username);

        return headerView;
    }

    private void prepareNavigationDrawerItems() {
        mDrawerItems = new ArrayList<>();
        mDrawerItems.add(
                new DrawerItem(
                        R.string.drawer_icon_linked_in,
                        R.string.drawer_title_linked_in,
                        DrawerItem.DRAWER_ITEM_TAG_LINKED_IN));
        mDrawerItems.add(
                new DrawerItem(
                        R.string.drawer_icon_blog,
                        R.string.drawer_title_blog,
                        DrawerItem.DRAWER_ITEM_TAG_BLOG));
        mDrawerItems.add(
                new DrawerItem(
                        R.string.drawer_icon_git_hub,
                        R.string.drawer_title_git_hub,
                        DrawerItem.DRAWER_ITEM_TAG_GIT_HUB));
        mDrawerItems.add(
                new DrawerItem(
                        R.string.drawer_icon_instagram,
                        R.string.drawer_title_instagram,
                        DrawerItem.DRAWER_ITEM_TAG_INSTAGRAM));
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;

		}
		// Handle action buttons
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Intent login = new Intent(getApplicationContext(), LoginRegisterActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            finish();
        }
		if (id == R.id.action_changePassword){
            Intent chgpass = new Intent(getApplicationContext(), ChangePasswordActivity.class);

            startActivity(chgpass);
        }


        if (id == R.id.search) {
            if (!searchexpanded) {

                searchexpanded = true;
                item.setIcon(R.drawable.ic_action_delete);

                // Action Bar
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                findViewById(R.id.autocomplete_places).setVisibility(View.VISIBLE);
                autocompletetextview.requestFocus();

                //Showing keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(autocompletetextview, InputMethodManager.SHOW_IMPLICIT);

                return true;

            }
            else
            {
                searchexpanded = false;
                item.setIcon(R.drawable.ic_action_search);

                //Hiding keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autocompletetextview.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);

                //Action Bar
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                findViewById(R.id.autocomplete_places).setVisibility(View.GONE);

                return true;

            }
        }
        if(id == R.id.maplist)
        {
            if(fragmentback)
            {
                //Animation
                flipCard();

                item.setIcon(R.drawable.ic_action_view_as_list);
                fragmentback = false;

            }
            else {

                //Animation
                flipCard();

                item.setIcon(R.drawable.ic_action_locate);
                fragmentback = true;

            }
        }

			return super.onOptionsItemSelected(item);

	}


    protected synchronized void buildGoogleApiClient() {

        googleapiclientlocation = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();

    }

    protected void createLocationRequest() {

        locationrequest = new LocationRequest();
        locationrequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(googleapiclientlocation, locationrequest, this);

    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(googleapiclientlocation, this);

    }


    @Override
    protected void onStart() {

        super.onStart();
        googleapiclientlocation.connect();
        googleapiclientplaces.connect();

    }

    @Override
    protected void onResume() {

        super.onResume();
        setUpMapIfNeeded();
        googleapiclientlocation.connect();
        googleapiclientplaces.connect();
        if (googleapiclientlocation.isConnected()) {
            startLocationUpdates();
        }

    }

    @Override
    protected void onStop() {

        super.onStop();
        if (googleapiclientlocation.isConnected()) {
            googleapiclientlocation.disconnect();
        }
        if(googleapiclientplaces.isConnected()) {
            googleapiclientlocation.disconnect();
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleapiclientlocation);
        }
        if(currentLocation != null) {

            //Updating marker
            if(currentmarker != null)
            {
                currentmarker.remove();
            }
            latitude = String.valueOf(currentLocation.getLatitude());
            longitude = String.valueOf(currentLocation.getLongitude());

            //Updating shared prefecerences
            ConnectionDetector.setDefaults("latitude",String.valueOf(currentLocation.getLatitude()),getApplicationContext());
            ConnectionDetector.setDefaults("longitude",String.valueOf(currentLocation.getLongitude()),getApplicationContext());

            updatecurrentmarker();
            startLocationUpdates();
        }
        else
        {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //Updating marker
        if(currentmarker != null)
        {
            currentmarker.remove();
        }
        currentLocation = location;
        latitude = String.valueOf(currentLocation.getLatitude());
        longitude = String.valueOf(currentLocation.getLongitude());
        updatecurrentmarker();
        Log.d(TAG, "Location changed to " + latitude + " " + longitude);

        //Updating shared preferences
        ConnectionDetector.setDefaults("latitude", String.valueOf(currentLocation.getLatitude()), getApplicationContext());
        ConnectionDetector.setDefaults("longitude", String.valueOf(currentLocation.getLongitude()), getApplicationContext());

    }

    @Override
    public void onConnectionSuspended(int cause) {

        showToast("Connection suspended");
        googleapiclientlocation.connect();

    }


    protected void showToast(String text) {

        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

    }


    ///////////////////////////////////////////////////////////////




    private AdapterView.OnItemClickListener autocompleteclicklistener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = adapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("Place Autocomplete", "Autocomplete item selected: " + item.description);
            Places.GeoDataApi.getPlaceById(googleapiclientplaces, item.placeId.toString())
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                final Place myPlace = places.get(0);
                                Log.i("Places AutoComplete", "Place found: " + myPlace.getLatLng());
                            }
                            places.release();
                        }
                    });
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleapiclientplaces, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            findDirections(Double.parseDouble(latitude),Double.parseDouble(longitude),12.9667, 77.5667, GMapV2Direction.MODE_DRIVING);

            Log.d("Place Autocomplete",item.placeId.toString());
            Log.i("Place Autocomplete", "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {

        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("Place Autocomplete", "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            Log.i("Place Autocomplete", "Place details received: " + place.getName());
            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id, CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e("Place Autocomplete", res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("Places Autocomplete", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }



    public class CardFrontFragment extends android.app.Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d("Fragment", "Showing front map fragment");
            if (view != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null)
                    parent.removeView(view);
            }
            try {
                view = inflater.inflate(R.layout.activity_maps_front, container, false);
            }
            catch (InflateException e) {
            }
            fragmentback = false;
            return view;
        }

    }





    private void flipCard() {

        if (fragmentback) {
            getFragmentManager().popBackStack();
            return;
        }
        fragmentback = true;

        FragmentManager fragmentManager = getSupportFragmentManager();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.card_flip_right_in, R.anim.card_flip_right_out,
                        R.anim.card_flip_left_in, R.anim.card_flip_left_out)
                .replace(R.id.content_frame, locationListViewFragment.newInstance())
                .addToBackStack(null)
                .commit();


    }

    @Override
    protected void onPause() {

        super.onPause();
        if (googleapiclientlocation.isConnected()) {
            stopLocationUpdates();
        }

    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {


        latitude = "0.0";
        longitude = "0.0";
        currentmarker = map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude))).title("Marker"));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void updatecurrentmarker()
    {
        if(updateposition) {
            currentmarker = map.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude))).title("Marker"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)))
                    .zoom(map.getCameraPosition().zoom)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask();
        asyncTask.execute(map);
    }

    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints)
    {
        Polyline newPolyline;
        GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.BLUE);
        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add(directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);
    }

    public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList>
    {
        public static final String USER_CURRENT_LAT = "user_current_lat";
        public static final String USER_CURRENT_LONG = "user_current_long";
        public static final String DESTINATION_LAT = "destination_lat";
        public static final String DESTINATION_LONG = "destination_long";
        public static final String DIRECTIONS_MODE = "directions_mode";
        private android.app.ActionBar activity;
        private Exception exception;
        private ProgressDialog progressDialog;

        public GetDirectionsAsyncTask()
        {

        }

        public void onPreExecute()
        {
            progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setMessage("Calculating directions");
//            progressDialog.show();
        }

        @Override
        public void onPostExecute(ArrayList result)
        {
            progressDialog.dismiss();
            if (exception == null)
            {
                handleGetDirectionsResult(result);
            }
            else
            {
                processException();
            }
        }

        @Override
        protected ArrayList doInBackground(Map<String, String>... params)
        {
            Map<String, String> paramMap = params[0];
            try
            {
                LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
                LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
                GMapV2Direction md = new GMapV2Direction();
                Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
                ArrayList directionPoints = md.getDirection(doc);
                return directionPoints;
            }
            catch (Exception e)
            {
                exception = e;
                return null;
            }
        }

        private void processException()
        {
            Log.d("sfds","sfs");
            //1 Toast.makeText(activity, activity.getString(R.string.error_when_retrieving_data), 3000).show();
        }
    }






    /////////////////////////////////////////////////////////////////



    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectItem(position /*, mDrawerItems.get(position - 1).getTag()*/);
        }
    }

    private void selectItem(int position/*, int drawerTag*/) {
        // minus 1 because we have header that has 0 position
        if (position < 1) {
            //Fragment fragment = locationMapViewFragment.newInstance();
            //commitFragment(fragment);//because we have header, we skip clicking on it
            return;
        }
        String drawerTitle = getString(mDrawerItems.get(position - 1).getTitle());
        Toast.makeText(this, "You selected " + drawerTitle + " at position: " + position, Toast.LENGTH_SHORT).show();

        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerItems.get(position - 1).getTitle());
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private class CommitFragmentRunnable implements Runnable {

        private Fragment fragment;

        public CommitFragmentRunnable(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void run() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }

    public void commitFragment(Fragment fragment) {
        //Using Handler class to avoid lagging while
        //committing fragment in same time as closing
        //navigation drawer
        mHandler.post(new CommitFragmentRunnable(fragment));
    }
    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }





}
