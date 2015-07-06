package co.froogal.froogal.services;

import android.app.Service;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import co.froogal.froogal.library.JSONParser;
import co.froogal.froogal.util.basic_utils;

public class location_service extends Service implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, LocationListener {

    //tag
    public static String TAG = "location_service";

    //basic util object
    private basic_utils bf;

    // Variables Not Neccesarry
    private IBinder mBinder;

    // server variables
    private JSONParser jsonParser;
    private static String save_location_to_server_URL = "http://ec2-52-10-172-112.us-west-2.compute.amazonaws.com/";
    static InputStream is = null;
    static JSONObject jObj = null;
    private JSONArray send_data = null;
    static String json = "";
    private List<NameValuePair> params;


    //.Location variables
    protected GoogleApiClient googleapiclient;
    protected Location currentLocation;
    private String latitude;
    private String longitude;
    protected LocationRequest locationrequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
        bf = new basic_utils(getApplicationContext());
        Log.d(TAG,"Created");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Destroyed");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        googleapiclient.connect();
        return 1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected synchronized void buildGoogleApiClient() {

        googleapiclient = new GoogleApiClient.Builder(this)
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
        locationrequest.setPriority(LocationRequest.PRIORITY_NO_POWER);

    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(googleapiclient, locationrequest, this);

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleapiclient);
        }
        if(currentLocation != null) {

            latitude = String.valueOf(currentLocation.getLatitude());
            longitude = String.valueOf(currentLocation.getLongitude());

            save_location_to_server(latitude,longitude,"9");
            startLocationUpdates();
        }
        else
        {
            googleapiclient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;
        latitude = String.valueOf(currentLocation.getLatitude());
        longitude = String.valueOf(currentLocation.getLongitude());
        save_location_to_server(latitude,longitude, "9");

    }

    @Override
    public void onConnectionSuspended(int cause) {

        googleapiclient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        googleapiclient.connect();
    }

    public void save_location_to_server(String latitude, String longitude, String uid)
    {
        jsonParser = new JSONParser();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("latitude", latitude));
        params.add(new BasicNameValuePair("longitude", longitude));
        params.add(new BasicNameValuePair("uid", uid));

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    try {
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(save_location_to_server_URL);
                        httpPost.setEntity(new UrlEncodedFormEntity(params));
                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                is, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        is.close();
                        json = sb.toString();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result here1" + e.toString());
                    }

                    // try parse the string to a JSON object
                    try {
                        jObj = new JSONObject(json);
                    } catch (JSONException e) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}