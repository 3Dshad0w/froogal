package co.froogal.froogal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.services.location_service;
import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.FloatLabeledEditText;

/**
 * Created by akhil on 10/3/15.
 */
public class SignUpActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //tag
    public static String TAG = "SignUpctivity";

    // Google+ Sign In Variables
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient google_api_client;
    private boolean mIntentInProgress;
    private boolean sign_in_clicked;
    private com.google.android.gms.common.SignInButton google_sign_in;
    protected Location currentLocation;
    protected LocationRequest locationrequest;
    Person.Name person_name;
    Person.Image person_image;
    String email="";
    String birthday = "";
    JSONObject json;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Facebook Sign In Variables
    CallbackManager callbackManager;
    private LoginButton loginButton;
    AccessToken token;
    JSONObject picture_object;
    JSONObject picture_data_object;
    private ProgressDialog fbDialog;
    String id = "";

    // User Function Object
    UserFunctions uf;

    // Basci utils Object
    basic_utils bu;


    /**
     *  JSON Response node names.
     **/


    private static String KEY_SUCCESS = "success";
    private static String KEY_UID = "uid";
    private static String KEY_FIRSTNAME = "fname";
    private static String KEY_LASTNAME = "lname";
    private static String KEY_MOBILE = "mobile";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ERROR = "error";

    /**
     * Defining layout items.
     **/
    ProgressDialog normal_dialog;
    FloatLabeledEditText inputFirstName;
    FloatLabeledEditText inputLastName;
    FloatLabeledEditText inputMobile;
    FloatLabeledEditText inputEmail;
    FloatLabeledEditText inputPassword;
    TextView btnRegister;
    SharedPreferences sharedpreferences;
    String first_name="";
    String last_name="";
    String image_url="";
    String ip_address="";
    String imei="";
    String registered_at = "";
    String registered_through = "s";
    String latitude = "";
    String longitude = "";
    String gcm_token = "";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SpannableString s = new SpannableString("SignUp");
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

        s.setSpan(myfont, 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * Defining all layout items
         **/
        // Basic utils intialise
        bu = new basic_utils(this);

        //Taking location from shared Preferences
        latitude = bu.get_defaults("latitude");
        longitude = bu.get_defaults("longitude");

        google_sign_in = (com.google.android.gms.common.SignInButton)findViewById(R.id.google_login);
        inputFirstName = (FloatLabeledEditText) findViewById(R.id.fname);
        inputLastName = (FloatLabeledEditText) findViewById(R.id.lname);
        inputEmail = (FloatLabeledEditText) findViewById(R.id.email);
        inputPassword = (FloatLabeledEditText) findViewById(R.id.pword);
        btnRegister = (TextView) findViewById(R.id.register);

        // Facebook button and click
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_login);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_birthday");

        // TODO Publish permissions to be taken

        // Facebook Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Showing progress bar
                fbDialog = new ProgressDialog(SignUpActivity.this);
                fbDialog.setTitle("Contacting Servers");
                fbDialog.setMessage("Logging in ...");
                fbDialog.setIndeterminate(false);
                fbDialog.setCancelable(false);
                fbDialog.show();

                token = AccessToken.getCurrentAccessToken();
                // Retrieving user data

                GraphRequest request = GraphRequest.newMeRequest(
                        token,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    Log.d(TAG, response.toString());

                                    // Modifying progressbar
                                    fbDialog.setMessage("Loading User Space");
                                    fbDialog.setTitle("Getting Data");

                                    if (object.has("first_name")) {
                                        first_name = object.getString("first_name");
                                    }
                                    if (object.has("last_name")) {
                                        last_name = object.getString("last_name");
                                    }
                                    if (object.has("picture")) {
                                        picture_object = object.getJSONObject("picture");
                                        if (picture_object.has("data")) {
                                            picture_data_object = picture_object.getJSONObject("data");
                                            if (picture_data_object.has("url")) {
                                                image_url = picture_data_object.getString("url");
                                            }
                                        }
                                    }
                                    if (object.has("email")) {
                                        email = object.getString("email");
                                    }
                                    if (object.has("id")) {
                                        id = object.getString("id");
                                    }
                                    ip_address = bu.get_defaults("ip_address");
                                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                    imei = telephonyManager.getDeviceId();
                                    if (latitude != "" && longitude != "") {

                                        // To get city name
                                        try {
                                            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                            List<Address> addresses = gcd.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                                            if (addresses.size() > 0) {
                                                registered_at = addresses.get(0).getLocality();
                                            }
                                        } catch (Exception e) {
                                            registered_at = "";
                                            Log.d(TAG, e.toString());
                                        }
                                    }
                                    registered_through = "f";
                                    if (object.has("birthday")) {
                                        birthday = object.getString("birthday");
                                    }

                                    // TODO firends list store after someone uses
                                    new process_login_facebook().execute();
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                    show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,friends,picture,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Cancelled");
                show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
            }

            @Override
            public void onError(FacebookException exception) {
                show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
                Log.d(TAG, exception.toString());
            }
        });


        // Google+ Sign In Code
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Plus.API)
                .addScope(new Scope("profile"))
                .build();
        createLocationRequest();

        // User Function object registration
        uf = new UserFunctions();

        // Google+ Sign In onClick
        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.google_login && !google_api_client.isConnecting()) {
                    sign_in_clicked = true;
                    // Show progress till activity result
                    normal_dialog = new ProgressDialog(SignUpActivity.this);
                    normal_dialog.setTitle("Google+ Accounts");
                    normal_dialog.setMessage("Fetching ... ");
                    normal_dialog.setIndeterminate(false);
                    normal_dialog.setCancelable(false);
                    normal_dialog.show();
                    google_api_client.connect();
                }
            }
        });



/**
 * Button which Switches back to the login screen on clicked
 **/



        /**
         * Register Button click event.
         * A Toast is set to alert when the fields are empty.
         * Another toast is set to alert Username must be 5 characters.
         **/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                clearErrors();

                boolean cancel = false;
                View focusView = null;

                // Store values at the time of the login attempt.
                String mEmail = inputEmail.getText().toString();
                String mFirstName = inputFirstName.getText().toString();
                String mLastName = inputLastName.getText().toString();
                String mPassword = inputPassword.getText().toString();



                // Check for a valid password.
                if (TextUtils.isEmpty(mPassword)) {
                    inputPassword.setError(getString(R.string.error_field_required));
                    focusView = inputPassword;
                    cancel = true;
                } else if (mPassword.length() < 4) {
                    inputPassword.setError(getString(R.string.error_invalid_password));
                    focusView = inputPassword;
                    cancel = true;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(mEmail)) {
                    inputEmail.setError(getString(R.string.error_field_required));
                    focusView = inputEmail;
                    cancel = true;
                } else if (!mEmail.contains("@") || !mEmail.contains(".")) {
                    inputEmail.setError(getString(R.string.error_invalid_email));
                    focusView = inputEmail;
                    cancel = true;
                }


                if (TextUtils.isEmpty(mFirstName)) {
                    inputFirstName.setError(getString(R.string.error_field_required));
                    focusView = inputFirstName;
                    cancel = true;
                }

                if (TextUtils.isEmpty(mLastName)) {
                    inputLastName.setError(getString(R.string.error_field_required));
                    focusView = inputLastName;
                    cancel = true;
                }
                /*
                if (TextUtils.isEmpty(mPhoneNumber)) {
                    inputMobile.setError(getString(R.string.error_field_required));
                    focusView = inputMobile;
                    cancel = true;
                }
                else if(mPhoneNumber.length() != 10) {
                    inputMobile.setError("Invalid PhoneNumber");
                    focusView = inputMobile;
                    cancel = true;
                }
                */

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    NetAsync(view);

                }

            }


        });
    }

    protected void createLocationRequest() {

        locationrequest = new LocationRequest();
        locationrequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    protected void onStop() {
        super.onStop();


        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {


        if (currentLocation == null) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(google_api_client);
        }
        if(currentLocation != null) {

            latitude = String.valueOf(currentLocation.getLatitude());
            longitude = String.valueOf(currentLocation.getLongitude());

            // Upating Shared preferences

            bu.set_defaults("latitude",latitude);
            bu.set_defaults("longitude",longitude);

        }
        if(sign_in_clicked) {

            // Retrieving additional information

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);

                // Code for extracting user data

                if (currentPerson.hasName()) {
                    person_name = currentPerson.getName();
                    if (person_name.hasFamilyName()) {
                        first_name = person_name.getFamilyName();
                    } else {
                        first_name = person_name.getMiddleName();
                    }
                    if (person_name.hasGivenName()) {
                        last_name = person_name.getGivenName();
                    }
                }
                if(currentPerson.hasId())
                {
                    id = currentPerson.getId();
                }

                // TODO see birthday
                if (currentPerson.hasBirthday()) {
                    birthday = currentPerson.getBirthday();
                }
                ip_address = bu.get_defaults("ip_address");
                if (currentPerson.hasImage()) {
                    person_image = currentPerson.getImage();
                    if (person_image.hasUrl()) {
                        image_url = person_image.getUrl();
                    }
                }
                email = Plus.AccountApi.getAccountName(google_api_client);
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();

                // To get city name
                try {
                    Geocoder gcd = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    if (addresses.size() > 0) {
                        registered_at = addresses.get(0).getLocality();
                    }
                } catch (Exception e) {
                    registered_at = "dontknow";
                    Log.d(TAG, e.toString());
                }

                registered_through = "g";
                // Store in database
                new process_login_google().execute();

            }
            else{
                Log.d("bitch", "here");
            }
        }

    }

    // Alert Dialogs

    @SuppressWarnings("deprecation")
    public void show_alert_dialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress) {
            if (sign_in_clicked && connectionResult.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try{
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    google_api_client.connect();
                }
            }
        }

    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            sign_in_clicked = false;
            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }
        else {
            callbackManager.onActivityResult(requestCode,responseCode, intent);
        }
    }

    // Google +  Process login

    private class process_login_google extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            normal_dialog.setTitle("Contacting Servers");
            normal_dialog.setMessage("Logging in ...");
            normal_dialog.setIndeterminate(false);
            normal_dialog.setCancelable(false);
            normal_dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            gcm_token = bu.get_defaults("gcm_token");
            json = uf.save_google_user_data_to_server(gcm_token,first_name,last_name,image_url,email,ip_address,imei,registered_through,latitude,longitude,registered_at,birthday, id);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {

                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){

                        normal_dialog.setMessage("Loading User Space");
                        normal_dialog.setTitle("Getting Data");

                        bu.set_defaults("email", email);
                        bu.set_defaults("password", "");
                        bu.set_defaults("fname", first_name);
                        bu.set_defaults("lname", last_name);
                        bu.set_defaults("image_url", image_url);
                        bu.set_defaults("ip_address", ip_address);
                        bu.set_defaults("registered_through", registered_through);
                        bu.set_defaults("registered_at", registered_at);
                        bu.set_defaults("mobile", json.getJSONObject("user").getString("mobile"));
                        bu.set_defaults("mobile_verified", json.getJSONObject("user").getString("mobile_verified"));
                        bu.set_defaults("birthday", birthday);
                        bu.set_defaults("special_id", id);
                        bu.set_defaults("uid", json.getJSONObject("user").getString("uid"));
                        normal_dialog.dismiss();
                        if(bu.get_defaults("mobile_verified").equals("no")) {
                            Intent intent = new Intent(getApplicationContext(), otp_activity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{

                        normal_dialog.dismiss();
                        show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
                show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
            }
        }
    }

    // Facebook Process login

    private class process_login_facebook extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            gcm_token = bu.get_defaults("gcm_token");
            json = uf.save_facebook_user_data_to_server(id,gcm_token,first_name, last_name, image_url, email, ip_address, imei, registered_through, latitude, longitude, registered_at, birthday);
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){
                        bu.set_defaults("email", email);
                        bu.set_defaults("password", "");
                        bu.set_defaults("fname", first_name);
                        bu.set_defaults("lname", last_name);
                        bu.set_defaults("image_url", image_url);
                        bu.set_defaults("ip_address", ip_address);
                        bu.set_defaults("registered_through", registered_through);
                        bu.set_defaults("registered_at", registered_at);
                        bu.set_defaults("mobile", json.getJSONObject("user").getString("mobile"));
                        bu.set_defaults("mobile_verified",json.getJSONObject("user").getString("mobile_verified"));
                        bu.set_defaults("birthday", birthday);
                        bu.set_defaults("special_id",id);
                        bu.set_defaults("uid", json.getJSONObject("user").getString("uid"));
                        fbDialog.dismiss();
                        if(bu.get_defaults("mobile_verified").equals("no"))
                        {
                            Intent intent = new Intent(getApplicationContext(), otp_activity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else{

                        show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
                    }
                }
            } catch (JSONException e) {
                show_alert_dialog(SignUpActivity.this, "Server Error", "Please try again later!");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();

    }


    /**
     * Async Task to check whether internet connection is working
     **/

    private class NetCheck extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(SignUpActivity.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){


/**
 * Gets current device state and checks for working internet connection by trying Google.
 **/
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                nDialog.dismiss();
                new ProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                showAlertDialog(SignUpActivity.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }
        }
    }


    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

        /**
         * Defining Process dialog
         **/
        private ProgressDialog pDialog;

        String email,password,fname,lname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inputPassword = (FloatLabeledEditText) findViewById(R.id.pword);
            fname = inputFirstName.getText().toString();
            lname = inputLastName.getText().toString();
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();
            pDialog = new ProgressDialog(SignUpActivity.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


            UserFunctions userFunction = new UserFunctions();
            Log.d("upjson", fname+lname+email+password);
            registered_through  ="e";
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            if(latitude != "" && longitude != "") {

                // To get city name
                try {
                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    if (addresses.size() > 0) {
                        registered_at = addresses.get(0).getLocality();
                    }
                } catch (Exception e) {
                    Log.d("haha", "City Name Call Failed");
                }
            }
            ip_address = bu.get_defaults("ip_address");
            gcm_token = bu.get_defaults("gcm_token");
            image_url = "";
            JSONObject json = userFunction.registerUser(fname, lname, email,password, registered_at, registered_through, imei, ip_address, image_url, longitude, latitude, gcm_token);
            Log.d("json", json.toString());
            return json;


        }
        @Override
        protected void onPostExecute(JSONObject json) {
            /**
             * Checks for success message.
             **/
            try {
                if (json.getString(KEY_SUCCESS) != null) {
                    Log.d("jsonhereinsignup", json.toString());
                    String res = json.getString(KEY_SUCCESS);

                    String red = json.getString(KEY_ERROR);

                    if(Integer.parseInt(res) == 1){
                        pDialog.setTitle("Getting Data");
                        pDialog.setMessage("Loading Info");



                        bu.set_defaults("email", email);
                        bu.set_defaults("password", password);
                        bu.set_defaults("fname", fname);
                        bu.set_defaults("lname", lname);
                        bu.set_defaults("image_url", "");
                        bu.set_defaults("ip_address", bu.get_defaults("ip_address"));
                        bu.set_defaults("registered_through", "e");
                        bu.set_defaults("registered_at", registered_at);
                        bu.set_defaults("mobile", json.getJSONObject("user").getString("mobile"));
                        Log.d(TAG,bu.get_defaults("mobile"));
                        // bu.set_defaults("mobile_verified", json.getJSONObject("user").getString("mobile_verified"));
                        // TODO Rohit Svk Remove this line. after akhil singh
                        bu.set_defaults("mobile_verified","no");
                        bu.set_defaults("birthday", "");
                        bu.set_defaults("special_id","");
                        bu.set_defaults("uid", json.getJSONObject("user").getString("uid"));

                        if(bu.get_defaults("mobile_verified").equals("no"))
                        {
                            Intent intent = new Intent(getApplicationContext(), otp_activity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }



                    }

                    else if (Integer.parseInt(red) ==2){
                        pDialog.dismiss();
                        showAlertDialog(SignUpActivity.this, "Error",
                                "User already exists.", false);
                        }
                    else if (Integer.parseInt(red) ==3){
                        pDialog.dismiss();
                        showAlertDialog(SignUpActivity.this, "Error",
                                "Invalid Email id.", false);

                    }

                    else {
                        pDialog.dismiss();
                        showAlertDialog(SignUpActivity.this, "Error",
                                "Error occured in registration.", false);

                    }
                }


                else{
                    pDialog.dismiss();
                    showAlertDialog(SignUpActivity.this, "Error",
                            "Error occured in registration.", false);

                }

            } catch (JSONException e) {
                e.printStackTrace();


            }
        }}
    public void NetAsync(View view){
        new NetCheck().execute();
    }

    private void clearErrors(){
       inputEmail.setError(null);
       inputFirstName.setError(null);
       inputLastName.setError(null);
       inputPassword.setError(null);

    }

}
