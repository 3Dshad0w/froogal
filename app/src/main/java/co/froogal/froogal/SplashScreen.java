package co.froogal.froogal;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.text.DateFormat;
import java.util.Date;


public class SplashScreen extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    public static final String PREFS_NAME = "splashscreendatapreference";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private AddressResultReceiver mResultReceiver;

    protected String latitude;
    protected String longitude;
    protected String address;
    private static int SPLASH_TIME_OUT = 3000;
    protected boolean timeoutmainactivity = false;
    Context context = this;
    private ImageView mLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        buildGoogleApiClient();

        mLogo = (ImageView) findViewById(R.id.splashscreenup);
        animation2();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timeoutmainactivity = true;
            }
        }, SPLASH_TIME_OUT);

        mResultReceiver = new AddressResultReceiver(new Handler());


        if(Utils.locationCheck(getApplicationContext())) {

            latitude = Utils.getDefaults("latitude", getApplicationContext());
            longitude = Utils.getDefaults("longitude", getApplicationContext());
            address = Utils.getDefaults("address", getApplicationContext());

        }
        else
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog,  final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        Log.d("Activity", "onstart called");
        super.onStart();
        if (!Geocoder.isPresent()) {
            showToast("No geocoder available");
        }
        else {
            startIntentService();
        }
        mGoogleApiClient.connect();

    }

    @Override
    public void onResume() {
        Log.d("Activity", "onresume called");
        super.onResume();
        if (!Geocoder.isPresent()) {
            showToast("No geocoder available");
        }
        else {
            startIntentService();
        }
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }

    @Override
    protected void onPause() {
        Log.d("Activity", "onpause called");
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }

    }

    @Override
    protected void onStop() {
        Log.d("Activity","onstop called");
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        if(mCurrentLocation != null) {
            Utils.setDefaults("latitude",String.valueOf(mCurrentLocation.getLatitude()),getApplicationContext());
            Utils.setDefaults("longitude",String.valueOf(mCurrentLocation.getLongitude()),getApplicationContext());
            if (!Geocoder.isPresent()) {
                showToast("No geocoder available");
                Log.d("Location","GeoCoder not availabe !");
            }
            else {
                Log.d("Location", "Calling startIntentService !");
                startIntentService();
            }
            startLocationUpdates();
        }
        else
        {

            Log.d("Location","mlocation is null calling buildgoogleapiclient again !");
            buildGoogleApiClient();
        }
    }

    protected void startIntentService() {

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        startService(intent);

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("Location","Location changed to " + String.valueOf(mCurrentLocation.getLatitude()) + String.valueOf(mCurrentLocation.getLongitude()));
        mCurrentLocation = location;
        if (!Geocoder.isPresent()) {
            showToast("No geocoder available");
        }
        else {
            startIntentService();
        }
        Utils.setDefaults("latitude", String.valueOf(mCurrentLocation.getLatitude()),getApplicationContext());
        Utils.setDefaults("longitude", String.valueOf(mCurrentLocation.getLongitude()), getApplicationContext());
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        showToast("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());

    }

    @Override
    public void onConnectionSuspended(int cause) {

        showToast("Connection suspended");
        mGoogleApiClient.connect();

    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {

            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d("Location", "Recieved Address");
            address = resultData.getString(Constants.RESULT_DATA_KEY);
            if (resultCode == Constants.SUCCESS_RESULT) {
                Utils.setDefaults("address",address,getApplicationContext());
                showToast(address);
                Log.d("Location","Address correct ");
            }
            else
            {
                Log.d("Location","Address not correct !");
            }
            if (timeoutmainactivity) {
                if(Utils.checkInternetConnectivity(context)) {
                    Intent i = new Intent(SplashScreen.this, MapsActivity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Log.d("Internet","No Internet Connection !");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                    LayoutInflater inflater = SplashScreen.this.getLayoutInflater();
                    builder.setView(inflater.inflate(R.layout.dailogboxinternet, null))
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.d("Internet","Retry button pressed");
                                    if(Utils.checkInternetConnectivity(context)) {
                                        Intent i = new Intent(SplashScreen.this, MapsActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   dialog.cancel();
                                    finish();
                                    System.exit(0);
                                }
                            });
                    builder.create().show();
                }
            }

        }
    }

    protected void showToast(String text) {

        Toast.makeText(this, text, Toast.LENGTH_LONG).show();

    }

    private void animation2() {
        mLogo.setAlpha(1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

}
